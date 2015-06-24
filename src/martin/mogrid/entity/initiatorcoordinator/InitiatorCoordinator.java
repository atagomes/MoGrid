package martin.mogrid.entity.initiatorcoordinator;

import java.net.SocketTimeoutException;

import martin.mogrid.common.context.ContextInformation;
import martin.mogrid.common.network.LocalHost;
import martin.mogrid.common.resource.ResourceQuery;
import martin.mogrid.common.util.SystemUtil;
import martin.mogrid.entity.coordinator.CollaboratorReplyListScheduler;
import martin.mogrid.entity.coordinator.InitiatorRequest;
import martin.mogrid.entity.coordinator.PendingInitiatorRequests;
import martin.mogrid.entity.initiator.RequestApplication;
import martin.mogrid.p2pdl.api.CollaboratorReplyList;
import martin.mogrid.p2pdl.api.DiscoveryApplicationFacade;
import martin.mogrid.p2pdl.api.DiscoveryInitiatorFacade;
import martin.mogrid.p2pdl.api.RequestIdentifier;
import martin.mogrid.p2pdl.api.RequestProfile;
import martin.mogrid.p2pdl.protocol.P2PDPConnectionException;
import martin.mogrid.p2pdl.protocol.P2PDPConnectionFactory;
import martin.mogrid.p2pdl.protocol.P2PDPCoordinationConnection;
import martin.mogrid.p2pdl.protocol.message.P2PDPCollaboratorReplyListMessage;
import martin.mogrid.p2pdl.protocol.message.P2PDPCollaboratorReplyMessage;
import martin.mogrid.p2pdl.protocol.message.P2PDPInitiatorRequestMessage;
import martin.mogrid.p2pdl.protocol.message.P2PDPMessageInterface;

import org.apache.log4j.Logger;

/**
 * @author luciana
 *
 * Created on 25/06/2005
 */
public class InitiatorCoordinator implements Runnable,
        DiscoveryInitiatorFacade,
        InitiatorCoordinatorP2PDPMessageHandler {
    //Manutencao do arquivo de log do servico
    private static final Logger logger = Logger.getLogger(InitiatorCoordinator.class);    //Objetos associados a execucao do listener
    private Thread initiatorCoordThread = null;
    private int initiatorCoordThreadScanInterval = 1000;
    private P2PDPCoordinationConnection coordinationChannel = null;    //Lista de requisicoes (INITIATORs) sendo tratadas pelo COORDINATOR
    private PendingInitiatorRequests iRequests = null;    //Lista de associacao das requisicoes locais
    private RequestApplication requestApplication = null;    //Garante uma instancia unica da classe
    private static InitiatorCoordinator initiatorCoordInstance = null;

    private InitiatorCoordinator() throws InitiatorCoordinatorException {
        requestApplication = new RequestApplication();

        //Canal de comunicacao (P2P coordination) entre COORDINATOR (INITIATOR-COORDINATOR) e COLLABORATORS 
        try {
            coordinationChannel = P2PDPConnectionFactory.create();
        } catch (P2PDPConnectionException ex) {
            logger.info(ex.getMessage());
            throw new InitiatorCoordinatorException("It was not possible to create a Coordination Channel. Initiator-Coordinator initialization was interrupted.", ex);
        }

        iRequests = new PendingInitiatorRequests();
        start();
    }

    /**
     * Get handle to the singleton
     * @return the singleton instance of this class.
     */
    public static synchronized InitiatorCoordinator getInstance() throws InitiatorCoordinatorException {
        if (initiatorCoordInstance == null) {
            initiatorCoordInstance = new InitiatorCoordinator();
        }
        return initiatorCoordInstance;
    }

    private void start() {
        logger.info("Starting Initiator-Coordinator...");

        if (initiatorCoordThread == null) {
            initiatorCoordThread = new Thread(this, "InitiatorCoordinatorListener");
            initiatorCoordThread.start();
        }

        logger.info("Initiator-Coordinator running.\n");
    }

    public synchronized void stop() {
        logger.info("Stopping Initiator-Coordinator...");

        initiatorCoordThread.interrupt();
        try {
            initiatorCoordThread.join(1000);
        } catch (InterruptedException e) {
            logger.warn("Stopping Initiator-Coordinator Thread: " + e.getMessage(), e);
        } finally {
            initiatorCoordThread = null;
            if (coordinationChannel != null) {
                coordinationChannel.close();
                coordinationChannel = null;
            }
            logger.info("Initiator-Coordinator sttoped.");
        }
    }

    public void run() {
        Thread myThread = Thread.currentThread();
        P2PDPMessageInterface msgReceived = null;

        while (initiatorCoordThread == myThread) {
            SystemUtil.sleep(initiatorCoordThreadScanInterval);  //Wait in milliseconds

            if (coordinationChannel != null) {
                try {
                    msgReceived = coordinationChannel.receive();

                } catch (SocketTimeoutException stex) {
                    //It doesn�t anything, exception was thow because a value >0 (0=infinite) was atributed a socket.SO_TIMEOUT
                    //to evit that the receive method blocks until a datagram was received.
                } catch (P2PDPConnectionException ex) {
                    logger.warn("The Initiator-Coordinator Listener was interrupted or it occurred some error receiving messages from coordinator: " + ex.getMessage(), ex);
                //Util.abnormalExit(); 
                }

                if (msgReceived != null) {
                    int msgType = msgReceived.getMessageType();
                    //Soh trata mensagens CREP pois os papeis de INI e COORD foram fundidos, desse modo
                    //as msgs IREQ sao enviadas "diretamente" para os COLLABS e a msg CREPLIST eh entregue
                    //diretamente ao INI
                    if (msgType == P2PDPMessageInterface.CP_MSG_CREP) {
                        //logger.info("< Coordinator [" + LocalHost.getLocalHostAddress() + "] received a " + msgReceived.getMessageTypeStr() );                              
                        handlerCollaboratorReplyMessage(msgReceived);
                    }
                } else {
                    //Ignore...
                    //logger.warn("Error at the Coordination Protocol Message handler: the message is null.");
                }
            }
        }
    }

    //START - Metodos da interface InitiatorCoordinatorP2PDPMessageHandler      
    public void sendInitiatorRequestMessage(RequestProfile reqProfile, ResourceQuery resourceQuery) {
        P2PDPInitiatorRequestMessage message = new P2PDPInitiatorRequestMessage(resourceQuery, reqProfile);

        RequestIdentifier reqID = message.getRequestIdentifier();
        long maxRepDelay = message.getMaxReplyDelay();
        int numMaxReplies = message.getNumMaxReplies();
        String initiatorAddr = message.getDeviceIPAddress();

        try {
            logger.trace("> Initiator-Coordinator [" + LocalHost.getLocalHostAddress() + "] sent a " + message.getMessageTypeStr() + " message {ReqID: " + reqID + "}");
            logger.info("> Initiator-Coordinator [" + LocalHost.getLocalHostAddress() + "] sent a " + message.getMessageTypeStr() + " message {ReqID: " + reqID + "}");
            coordinationChannel.send(message);

            CollaboratorReplyListScheduler cRepMerger = new CollaboratorReplyListScheduler(this, reqID, numMaxReplies, maxRepDelay, initiatorAddr);
            InitiatorRequest iReq = new InitiatorRequest(cRepMerger, numMaxReplies);
            iRequests.put(reqID, iReq);

        } catch (P2PDPConnectionException e) {
            logger.error("!> Initiator-Coordinator [" + LocalHost.getLocalHostAddress() + "] not sent a " + message.getMessageTypeStr() + " message {ReqID: " + reqID + "}: " + e.getMessage());
        }
    }

    public void handlerCollaboratorReplyMessage(P2PDPMessageInterface protMessage) {
        P2PDPCollaboratorReplyMessage message = (P2PDPCollaboratorReplyMessage) protMessage;
        RequestIdentifier reqID = message.getRequestIdentifier();

        //logger.trace("< Coordinator ["+ LocalHost.getLocalHostAddress()+"] received from [" + message.getDeviceIPAddress() + "] a " + message.getMessageTypeStr() + " {ReqID: " + reqID+"} - ResID: " + message.getResourceIdentifier());      

        if (iRequests.containsKey(reqID)) {
            InitiatorRequest req = iRequests.get(reqID);
            //A cada requisicao (reqID) estah associada uma lista de respostas
            CollaboratorReplyListScheduler cRepListScheduler = req.getCRepListScheduler();

            //Verifico se jah nao recebi uma resposta do mesmo colaborador antes...
            if (cRepListScheduler.isNewReply(message)) {
                req.incNumRepliesListened();
                if (req.discardRequestScheduled()) {
                    //Por enquanto nao devo remover para poder detectar quais respostas EXTRAS foram recebidas e, consequentemente, descartadas
                    //iRequests.remove(reqID);               
                    logger.trace("(-) Coordinator [" + LocalHost.getLocalHostAddress() + "] discard message reply from [" + message.getDeviceIPAddress() + "] [(px)" + message.getProxyIPAddress() + "] [(ph)" + message.getGatewayIPAddress() + "] {ReqID: " + reqID + "}\n");
                    return;
                }
                //logger.info("< Coordinator["+message.getAddresses()+"] ["+ LocalHost.getLocalHostAddress()+"] received from [" + message.getDeviceIPAddress() + "] [(px)"+message.getProxyIPAddress()+"] [(ph)"+ message.getGatewayIPAddress()+"] a " + message.getMessageTypeStr() + " {ReqID: " + reqID+"}");      
                logger.info("< Coordinator [" + LocalHost.getLocalHostAddress() + "] received from [" + message.getDeviceIPAddress() + "] [(px)" + message.getProxyIPAddress() + "] [(ph)" + message.getGatewayIPAddress() + "] a " + message.getMessageTypeStr() + " {ReqID: " + reqID + "}");

                //A cada CRep recebida (message) eh feito um merge de todas as respostas (CReps)
                cRepListScheduler.mergeReplyList(message);
            }
        }
    }

    public void sendCollaboratorReplyListMessage(P2PDPMessageInterface protMessage, String initiatorAddr) {
        P2PDPCollaboratorReplyListMessage message = (P2PDPCollaboratorReplyListMessage) protMessage;

        logger.info("< Initiator [" + LocalHost.getLocalHostAddress() + "] received from [" + message.getDeviceIPAddress() + "] a " + message.getMessageTypeStr());
        CollaboratorReplyList list = message.getRepList();
        String addresses = "$";
        for (int i = 0; i < list.size() - 1; i++) {
            addresses += list.get(i).getCollaboratorIPAddr() + ",";
        }
        addresses += list.get(list.size() - 1).getCollaboratorIPAddr() + "$";
        logger.trace("< Initiator [" + LocalHost.getLocalHostAddress() + "] received from [" + message.getDeviceIPAddress() + "] a " + message.getMessageTypeStr() + " {ReqID: " + message.getRequestIdentifier() + "}" + " {List: " + addresses + "}");
        RequestIdentifier reqID = message.getRequestIdentifier();
        if (requestApplication.containsKey(reqID)) {
            DiscoveryApplicationFacade appRequest = requestApplication.remove(reqID);
            appRequest.receiveCReplyList(reqID, message.getRepList());
        } else {
            logger.warn("Initiator received a CollaboratorReplyList from [" + message.getDeviceIPAddress() + "] but it had not registered the {ReqID: " + reqID + "}");
        }
    }
    //END - Metodos da interface InitiatorCoordinatorP2PDPMessageHandler   
    //INICIO - Metodos da interface DiscoveryInitiatorFacade   
    public RequestProfile createRequestProfile(ContextInformation ctxtInfo, int numMaxReplies, long maxReplyDelay, int requestDiameter) {
        return (new RequestProfile(ctxtInfo, numMaxReplies, maxReplyDelay, requestDiameter));
    }

    public void discover(ResourceQuery resourceQuery, RequestProfile reqProfile, DiscoveryApplicationFacade app) {
        sendInitiatorRequestMessage(reqProfile, resourceQuery);
        requestApplication.put(resourceQuery.getRequestIdentifier(), app);
    }    //FIM - Metodos da interface DiscoveryInitiatorFacade   
}
