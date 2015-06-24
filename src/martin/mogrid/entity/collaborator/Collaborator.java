package martin.mogrid.entity.collaborator;

import java.net.SocketTimeoutException;
import java.util.Enumeration;
import java.util.Random;

import martin.mogrid.common.context.MonitoredContext;
import martin.mogrid.common.network.LocalHost;
import martin.mogrid.common.resource.ResourceDescriptor;
import martin.mogrid.common.resource.ResourceIdentifier;
import martin.mogrid.common.util.MoGridClassLoader;
import martin.mogrid.common.util.MoGridClassLoaderException;
import martin.mogrid.common.util.SystemUtil;
import martin.mogrid.entity.collaborator.registry.RequestResource;
import martin.mogrid.entity.collaborator.registry.ResourceRegistry;
import martin.mogrid.p2pdl.api.DiscoveryCollaboratorFacade;
import martin.mogrid.p2pdl.api.RequestIdentifier;
import martin.mogrid.p2pdl.collaboration.criterias.AdmissionController;
import martin.mogrid.p2pdl.collaboration.criterias.AdmissionControllerException;
import martin.mogrid.p2pdl.collaboration.criterias.CollaboratorReplyTimerFunction;
import martin.mogrid.p2pdl.collaboration.scheduler.CollaborationReplySendingScheduler;
import martin.mogrid.p2pdl.collaboration.scheduler.InitiatorRequestToForwardScheduler;
import martin.mogrid.p2pdl.protocol.P2PDPConnectionException;
import martin.mogrid.p2pdl.protocol.P2PDPConnectionFactory;
import martin.mogrid.p2pdl.protocol.P2PDPCoordinationConnection;
import martin.mogrid.p2pdl.protocol.P2PDPProperties;
import martin.mogrid.p2pdl.protocol.message.P2PDPCollaboratorReplyMessage;
import martin.mogrid.p2pdl.protocol.message.P2PDPInitiatorRequestMessage;
import martin.mogrid.p2pdl.protocol.message.P2PDPMessageInterface;
import martin.mogrid.p2pdl.routing.ReversePath;
import martin.mogrid.p2pdl.routing.ReversePathScheduler;
import martin.mogrid.service.contextlistener.ContextListener;
import martin.mogrid.service.contextlistener.ContextListenerException;
import martin.mogrid.service.monitor.ContextParser;
import martin.mogrid.service.monitor.DeviceContext;

import org.apache.log4j.Logger;

/**
 * @author   luciana
 * 
 * Created on 26/07/2005
 */
public class Collaborator implements Runnable,
        DiscoveryCollaboratorFacade,
        CollaboratorP2PDPMessageHandler {

    //Manutencao do arquivo de log do servico
    private static final Logger logger = Logger.getLogger(Collaborator.class);
    //Garante uma instancia unica da classe
    private static Collaborator collaboratorListenerInstance = null;

    //Listener das informacoes de contexto do COLLABORATOR
    private ContextListener contextListener = null;

    //Lista de recursos locais registrados
    private ResourceRegistry resourceRegistry = null;
    //Lista de associacao entre recursos locais e requisicoes remotas
    private RequestResource requestResource = null; //TODO pensar em quando remover reqID...

    //Lista de requisicoes de colaboracao remotas (INITIATORs) sendo tratadas pelo COLLABORATOR
    //-> CRep enviadas aguardando por reconhecimento via REbroadcast
    private CollaborationReplySendingScheduler replySchedulerList = null;
    //Lista de requisicoes que devem ser encaminhadas para os demais nos da rede
    private InitiatorRequestToForwardScheduler requestToForwardSchedulerList = null;
    //Lista de CRep enviadas aguardando por reconhecimento via REbroadcast
    //private CollaborationReplyAcknowledmentScheduler cRepWaitingForAckList = null;
    //Tabela de roteamento indicando o proximo salto para cada INITIATOR (pendingList)
    private ReversePathScheduler reversePathTableScheduler = null;
    //Funcao para calcular o timeout de envio das respostas do COLLABORATOR
    private CollaboratorReplyTimerFunction replyTimer = null;
    //Controlador de admissao utilizado para definir a disponibilidade do recurso solicitado
    private AdmissionController admissionController = null;
    //Boa vontade do COLLABORATOR em participar (0, 1]
    private float collaborationLevel = 1;
    // Valor associado ao retardo de transferencia em cada transmissao (em milisegundos)
    private float transferDelay = 100;
    // Valores associados ao retardo de retransmissao da mensagem de requisicao (IReq), para minimizar 
    // as colisoes no meio sem fio (tempo em milisegundos)
    private int minForwardRequestDelay = 1;
    private int maxForwardRequestDelay = 10;
    // Objetos associados a execucao do listener
    private Thread collaboratorThread = null;
    private int collaboratorThreadScanInterval = 1000;
    //Canal de coordenacao do P2PDP (COLLABORATOR <--> COORDINATOR)
    private P2PDPCoordinationConnection coordinationChannel = null;
    //private String collabDescription = "Collaborator";
    private String[] localAddress = LocalHost.getLocalHostAddress().split("\\.");
    private int nodeID;
    private Random rand;
    //Garante que o SEED nao serah igual quando mais de um colaborador eh executado 
    //em uma mesma maquina (EX: simulador NCTUns)
    

    {
        try {
            nodeID = Integer.parseInt(localAddress[2]) + Integer.parseInt(localAddress[3]);
            rand = new Random(System.currentTimeMillis() * nodeID);
        //TODO otimizar tratamento de possivel excecao 
        } catch (Exception ex) {
            nodeID = Integer.parseInt(localAddress[3]);
            rand = new Random(System.currentTimeMillis() * nodeID);
        }
    }

    /**
     * Creates a new instance of a Collaborator Listener. 
     *      
     * @param coordinatorPort port in that the Collaborator Listener will receive the
     * broadcast requests and responses for tasks of coordinator
     * @param scanIntervalInMilli interval in that the Context Listener will listener the 
     * monitor notifications (in milliseconds)    
     */
    private Collaborator() throws CollaboratorException {
        resourceRegistry = new ResourceRegistry();
        reversePathTableScheduler = new ReversePathScheduler();
        requestResource = new RequestResource();

        //Carrega do arquivo de configuracao do protocolo de descoberta a classe responsavel pelo
        //calculo do timer de envio das respostas (mensagens CRep)
        loadCollaboratorReplyTimerFunctionClass();

        try {
            coordinationChannel = P2PDPConnectionFactory.create();
            replySchedulerList = new CollaborationReplySendingScheduler(coordinationChannel, reversePathTableScheduler);
            requestToForwardSchedulerList = new InitiatorRequestToForwardScheduler(coordinationChannel);
            //cRepWaitingForAckList         = new CollaborationReplyAcknowledmentScheduler(coordinationChannel);
            start();

        } catch (P2PDPConnectionException e) {
            logger.warn("Collaborator -> Coordination channel creation error: " + e.getMessage(), e);
            throw new CollaboratorException("It was not possible to create a Collaborator�s coordination channel. Collaborator initialization was interrupted.", e);

        } catch (ContextListenerException cle) {
            logger.warn("Collaborator -> Context Listener initialization error: " + cle.getMessage(), cle);
            throw new CollaboratorException("It was not possible to start Context Listener. Collaborator initialization was interrupted.", cle);
        }
    }

    private void loadCollaboratorReplyTimerFunctionClass() throws CollaboratorException {
        P2PDPProperties.load();
        String timerFunctionClass = P2PDPProperties.getTimerFunctionClass();
        try {
            CollaboratorReplyTimerFunction collabRepTimerFunction = (CollaboratorReplyTimerFunction) MoGridClassLoader.load(timerFunctionClass).newInstance();
            setCollaboratorReplyTimerFunction(collabRepTimerFunction);
            logger.info("Collaborator is using " + timerFunctionClass);

        } catch (NullPointerException e) {
            throw new CollaboratorException("Collaborator Reply TimerFunction class [" + timerFunctionClass + "] was not located: " + e.getMessage(), e);

        } catch (InstantiationException e) {
            throw new CollaboratorException("Collaborator Reply TimerFunction class [" + timerFunctionClass + "] was not located: " + e.getMessage(), e);

        } catch (IllegalAccessException e) {
            throw new CollaboratorException("Collaborator Reply TimerFunction class [" + timerFunctionClass + "] was not located: " + e.getMessage(), e);

        } catch (MoGridClassLoaderException e) {
            throw new CollaboratorException("Collaborator Reply TimerFunction class [" + timerFunctionClass + "] was not loaded: " + e.getMessage(), e);
        }
    }

    /**
     * Get handle to the singleton
     * @return the singleton instance of this class.
     */
    public static synchronized Collaborator getInstance() throws CollaboratorException {
        if (collaboratorListenerInstance == null) {
            collaboratorListenerInstance = new Collaborator();
        }
        return collaboratorListenerInstance;
    }

    private void start() throws P2PDPConnectionException, ContextListenerException {
        logger.info("Starting Collaborator...");

        contextListener = ContextListener.getInstance();
        if (coordinationChannel != null) {
            collaboratorThreadScanInterval = coordinationChannel.getScanInterval();
        }

        if (collaboratorThread == null) {
            collaboratorThread = new Thread(this, "CollaboratorListener");
            collaboratorThread.start();
        }

        logger.info("Collaborator running.\n");
    }

    public synchronized void stop() {
        logger.info("Stopping Collaborator...");

        if (contextListener != null) {
            contextListener.stop();
        }
        collaboratorThread.interrupt();
        try {
            collaboratorThread.join(collaboratorThreadScanInterval);
        } catch (InterruptedException e) {
            logger.warn("Stopping Collaborator Thread: " + e.getMessage(), e);
        } finally {
            collaboratorThread = null;
            if (coordinationChannel != null) {
                coordinationChannel.close();
                coordinationChannel = null;
            }
            logger.info("Collaborator sttoped.");
        }
    }

    public void run() {
        Thread myThread = Thread.currentThread();
        P2PDPMessageInterface msgReceived = null;
        while (collaboratorThread == myThread) {
            SystemUtil.sleep(collaboratorThreadScanInterval);  //Wait in milliseconds

            if (coordinationChannel != null) {
                try {
                    msgReceived = coordinationChannel.receive();
                    /*final P2PDPMessageInterface msgReceived = coordinationChannel.receive();
                    new Thread( new Runnable() {
                    public void run() {
                    handlerCoordinationMessage(msgReceived);
                    }
                    }).start();*/
                    handlerCoordinationMessage(msgReceived);
                } catch (SocketTimeoutException stex) {
                    //logger.warn("SocketTimeoutException: " + stex.getMessage(), stex);
                    //It doesn�t anything, exception was thow because a value >0 (0=infinite) was atributed a socket.SO_TIMEOUT
                    //to evit that the receive method blocks until a datagram was received.            
                } catch (P2PDPConnectionException ex) {
                    logger.warn("The Collaborator was interrupted or it occurred some error receiving datagram from coordinator: " + ex.getMessage(), ex);
                //Util.abnormalExit();
                }
            }
        }
    }

    /**
     * Parse protocol messages: 
     *
     * @param protMessage - a coordination protocol message
     */
    private void handlerCoordinationMessage(P2PDPMessageInterface protMessage) {
        if (protMessage == null) {
            logger.warn("Error at the Coordination Protocol Message handler: the message is null.");
            return;
        }

        int msgType = protMessage.getMessageType();
        switch (msgType) {
            case P2PDPMessageInterface.CP_MSG_IREQ:
                //String msgAddr = protMessage.getDeviceIPAddress();
                //Soh trato requisicoes que NAO foram originadas localmente
                //if ( ! LocalHost.isLocalHostAddress(msgAddr) )
                handlerInitiatorRequestMessage(protMessage);
                break;

            case P2PDPMessageInterface.CP_MSG_CREP:
                //Se eu enviei a resposta - gerei a resposta ou atuei como intermediario 
                //na sua difusao -, nao devo trata-la (meu proprio eco)
                //String lastHop = ((P2PDPCollaboratorReplyMessage)protMessage).getHopID();
                //if ( ! LocalHost.isLocalHostAddress(lastHop) )
                handlerCollaboratorReplyMessage(protMessage);
                break;

            default:
                break;
        }
    }

    //INICIO - Metodos da interface CollaboratorP2PDPMessageHandler   
    public void handlerInitiatorRequestMessage(P2PDPMessageInterface protMessage) {
        String localHostAddress = LocalHost.getLocalHostAddress();
        P2PDPInitiatorRequestMessage message = (P2PDPInitiatorRequestMessage) protMessage;
        RequestIdentifier reqID = message.getRequestIdentifier();

        //(1) Se a mensagem IReq nao for duplicada ela eh tratada pelo COLABORADOR 
        if (!isInitiatorRequestMessageDuplicate(reqID)) {
            String initiatorIPAddr = message.getDeviceIPAddress();
            logger.trace("");
            logger.trace("< Collaborator [" + localHostAddress + "] received from [" + initiatorIPAddr + "] [(ph)" + message.getHopID() + " : " + message.getHopCount() + "] " + message.getMessageTypeStr() + " message {ReqID: " + reqID + "}");

            //logger.trace("Collaborator received a NEW Initiator Request from [" + message.getDeviceIPAddress() + "]");

            //(2) Increase the hop count: it starts with 0 and goes to REQUEST_DIAMETER (RD)
            //Example: SOURCE (RD=2) -> 0 (HC=1) -> 1 (HC=2) -> 2 (HC=3) [~-> 3]
            message.incHopCount();

            //(3) Armazena na tabela de rotas o caminho reverso para a requisi��o (INICIADOR)         
            String returnPath = message.getHopID();
            int currenthopCount = message.getHopCount();
            int reqDiameter = message.getRequestDiameter();
            int numMaxReplies = message.getNumMaxReplies();
            long maxReplyDelay = message.getMaxReplyDelay();
            ReversePath revPathEntry = new ReversePath(returnPath, numMaxReplies, currenthopCount, reqDiameter);
            reversePathTableScheduler.addEntry(reqID, revPathEntry, currenthopCount, maxReplyDelay, transferDelay);

            //(4) Obtem contexto atual do dispositivo
            DeviceContext devContext = contextListener.getDeviceContext(localHostAddress);
            if (devContext == null) {
                logger.warn("Device Context information is null to " + localHostAddress + ". Maybe the Monitor service was not started.");
                SystemUtil.sleep(100);
                return;
            }
            //(5) Verifica a "boa vontade" (willingness) do COLABORADOR em atender a requisicoes (==0 -> nao QUER colaborar)
            if (collaborationLevel != 0) {
                //if ( WillingnessFunction.getValue(devContext, collaborationLevel) != 0 ) { // EM TESTE!!!
                //(6) Executa o controle de admissao
                //(6.1) Configura o Controle de Admissao em funcao do contexto atual
                admissionController.setDeviceContext(devContext);
                admissionController.setRequest(message.getResourceQuery());
                //(6.2) COLABORADOR usa o controle de admissao para ver se tem o recurso requisitado
                ResourceIdentifier localResourceID = null;
                try {
                    localResourceID = admissionController.admit();
                } catch (AdmissionControllerException acex) {
                    logger.error("Admission Controller Exception: " + acex.getMessage(), acex);
                }

                //(7) Responde a requisicao caso tenha o recurso solicitado localmente 
                if (localResourceID != null) {
                    //(7.1) Calcula o tempo que deve esperar em funcao da sua "suitability" antes de enviar a resposta
                    replyTimer.configure(devContext, message, collaborationLevel, transferDelay);
                    long timeToWait = replyTimer.getTimeout(); //in milliseconds               

                    MonitoredContext monitoredCtxt = replyTimer.getMonitoredContext();
                    //(7.2) Monta a mensagem de resposta, escalona o seu envio e aguarda confirmacao
                    ResourceDescriptor resDescription = resourceRegistry.get(localResourceID);
                    P2PDPCollaboratorReplyMessage cRepMessage = new P2PDPCollaboratorReplyMessage(reqID, monitoredCtxt, localResourceID, resDescription, initiatorIPAddr, returnPath);
                    //(7.2.1) Escalona o envio da resposta (cRepMessage) para timeTowait
                    sendCollaboratorReplyMessage(cRepMessage, timeToWait, numMaxReplies);
                    logger.info("Collaborator [" + localHostAddress + "] CAN collaborate: " + localResourceID.getResourceIdentifier());
                    logger.info("Collaborator can reply in " + timeToWait + " milliseconds (MaxReplyDelay = " + SystemUtil.convertSecondsToMilliseconds(maxReplyDelay) + " milliseconds)\n");

                    //TODO Adicionar na proxima versao com os devidos testes e correcoes      
                    //(7.3) Escalona espera pelo reconhecimento da mensagem enviada (6.2.1), atraves da escuta do encaminhamento e mensagens pelos nos intermediarios
                    //se gateway == initiatorIPAddr, nao agendo reenvio
                    //waitForAcknonledment(cRepMessage, currenthopCount, timeToWait, maxReplyDelay);  

                    //(7.4) Armazena na lista local a associacao entre o ID da requisicao e do recurso
                    requestResource.put(reqID, localResourceID);

                } else {
                    logger.info("Collaborator [" + localHostAddress + "] CANNOT collaborate (request id : " + reqID + ")\n");
                    logger.debug("Collaborator [" + localHostAddress + "] CANNOT Collaborate (request id : " + reqID + ").");
                }
            } else {
                logger.info("Collaborator [" + localHostAddress + "] does NOT WANT collaborate (request id : " + reqID + ")\n");
                logger.debug("Collaborator [" + localHostAddress + "] does NOT WANT Collaborate (request id : " + reqID + ").");
            }

            //(8) Encaminha a requisicao para a vizinhanca utilizando broadcast local 
            forwardInitiatorRequestMessage(message);

        } else {
            //logger.trace("Collaborator received a DUPLICATED Initiator Request from [" + message.getDeviceIPAddress() + "]");
            logger.trace("< Collaborator [" + localHostAddress + "] received from [" + message.getDeviceIPAddress() + "] [(ph)" + message.getHopID() + " : " + message.getHopCount() + "] a duplicate " + message.getMessageTypeStr() + " message and Collaborator discarded it {ReqID: " + reqID + "}");
        }
    }

    //TODO: TESTAR!!! 
    //Verifico se a mensagem de requisicao (IReq) jah foi tratada pelo COLABORADOR
    private boolean isInitiatorRequestMessageDuplicate(RequestIdentifier reqID) {
        return reversePathTableScheduler.containsEntry(reqID);
    }

    //TODO Melhorar o calculo do tempo de envio do ack...
   /*private void waitForAcknonledment(P2PDPCollaboratorReplyMessage cRepMessage, int currenthopCount, long timeToWait, long maxReplyDelay) {
    //Altera o gateway para o default (broadcast) soh se a mensagem nao receber ACK
    P2PDPCollaboratorReplyMessage replyMessage = cRepMessage;
    replyMessage.setDefaultGatewayIPAddress();
    long timeToWaitForAck = Math.round( timeToWait + ((maxReplyDelay - timeToWait)*1f/currenthopCount) );
    timeToWaitForAck = timeToWaitForAck > maxReplyDelay ? maxReplyDelay : timeToWaitForAck;
    logger.info("Collaborator wait for ACK in " + SystemUtil.convertMillisecondsToSeconds(timeToWaitForAck) + " seconds (Timer to Send = "+ timeToWait+" seconds; MaxReplyDelay = " + maxReplyDelay + " seconds)");      
    cRepWaitingForAckList.schedule(replyMessage, timeToWaitForAck);
    }*/
    private synchronized void forwardInitiatorRequestMessage(P2PDPInitiatorRequestMessage message) {
        //OBS:  The hop count is increased in handlerInitiatorRequestMessage() 
        //(1) Verify if can forward message: HOP_COUNT <= REQUEST_DIAMETER
        if (message.canForwardMessage()) {
            //try {
            //(2) Change the identification of the node sending out the message
            message.setHopID(LocalHost.getLocalHostAddress());
            //(3) Forward message in local broadcast (coordination channel)
            if (coordinationChannel != null) {
                //TODO otimizar! Alteracao para evitar colisao no meio sem fio 
                //Returns a pseudorandom, uniformly distributed int value between 0 (inclusive) and the 
                //specified value (exclusive), drawn from this random number generator's sequence.
                // -> I need [minDelay, maxDelay], I have [0, maxDelay), then => MAX(minDelay, rand(0, maxDelay+1))
                long timeToWait = Math.max(minForwardRequestDelay, rand.nextInt(maxForwardRequestDelay + 1));
                requestToForwardSchedulerList.schedule(message, timeToWait);
                logger.trace("> Collaborator [" + message.getHopID() + "] scheduled a " + message.getMessageTypeStr() + " message from [" + message.getDeviceIPAddress() + "] to be forwarded in " + timeToWait + " milliseconds - {ReqID: " + message.getRequestIdentifier() + "}");
            }
        //logger.trace(">> Collaborator ["+ LocalHost.getLocalHostAddress()+"] forwarded a " + message.getMessageTypeStr() + " message {ReqID: " + message.getRequestIdentifier()+"} (current hop: " + message.getHopCount() + "/"+message.getRequestDiameter()+")");            

        // } catch (P2PDPConnectionException e) {
        //    logger.error("!>> Collaborator ["+ LocalHost.getLocalHostAddress()+"] could not forward a "+message.getMessageTypeStr() + " message {ReqID: " + message.getRequestIdentifier()+"}: " + e.getMessage(), e);                       
        // } 
        }
    }

    //TODO melhorar a l�gica... principalmente o teste para encaminhamento
    public synchronized void handlerCollaboratorReplyMessage(P2PDPMessageInterface protMessage) {
        P2PDPCollaboratorReplyMessage message = (P2PDPCollaboratorReplyMessage) protMessage;
        RequestIdentifier reqID = message.getRequestIdentifier();
        String myAddress = LocalHost.getLocalHostAddress();
        String collabAddr = message.getDeviceIPAddress();

        if (reqID != null && collabAddr != null) {

            //(*) Verify if the CRep message is your own REPLY - for ACK
            if (isMyOwnCollabReplyMessage(message)) {
                //(1) Se a mensagem CRep recebida corresponde a alguma enviada anteriormente pelo 
                //    COLABORADOR, eh caracterizado um reconhecimento positivo da CRep (ACK)          
                //    Cancela o envio da mensagem em BROADCAST (nao direcionado) para TODOS os vizinhos
            /*if ( cRepWaitingForAckList.containsCRepScheduled(reqID) ) {
                cRepWaitingForAckList.cancelSchedule(reqID);
                }*/
                logger.debug("!>> Collaborator [" + myAddress + "] discard a duplicate " + message.getMessageTypeStr() + " message from [" + collabAddr + "(" + message.getHopID() + ")] {ReqID: " + reqID + "}");

            } else if (!isCollabReplyMessageDuplicate(reqID, collabAddr)) { // CRep message is NOT DUPLICATE                      

                //logger.trace("Collaborator received a Collaborator Reply from [" + message.getDeviceIPAddress() + "]");
                logger.trace("< Collaborator [" + myAddress + "] received from [" + collabAddr + "] [(ph)" + message.getHopID() + " : " + message.getHopCount() + "] a " + message.getMessageTypeStr() + " message {ReqID: " + reqID + "} - ResID: " + message.getResourceIdentifier());
                //(2) Se o COLABORADOR estah atendendo a mesma requisicao verifica se ela pode ser suprimida ou nao
                if (replySchedulerList != null && replySchedulerList.containsCRepScheduled(reqID)) {
                    replySchedulerList.incNumCRepOverheard(reqID);
                    if (replySchedulerList.canDiscardCRepScheduled(reqID)) {
                        replySchedulerList.cancelSchedule(reqID);
                    }
                }

                //(3) Verify if exist references to Request ID in ReversePathTable
                ReversePath reversePath = null;
                if (reversePathTableScheduler.containsEntry(reqID)) {
                    reversePath = reversePathTableScheduler.getEntry(reqID);
                }

                //(4) Entrega a mensagem CRep para o modulo de encaminhamento que vai fazer as verificacoes
                //    necessarias para encaminhar a mensagem (broadcast normal ou broadcast direcionado)
                forwardCollaboratorReplyMessage(message, reversePath);
            }
        } else {
            logger.warn("!>> Collaborator [" + myAddress + "] discard a not identified " + message.getMessageTypeStr() + " message from [" + collabAddr + "(" + message.getHopID() + ")] {ReqID: " + reqID + "}");
        }
    }


    //Verify if the CRep message is your own REPLY in LOOP (duplicate)
    private boolean isMyOwnCollabReplyMessage(P2PDPCollaboratorReplyMessage message) {
        //Verifica se a origem da msg eh ele mesmo, ou seja, eh o eco do seu proprio broadcast enviado pelo DGW      
        return (LocalHost.isLocalHostAddress(message.getDeviceIPAddress()));
    }

    //Como adiciono o IP do colaborador qdo contabilizo qtas CRep jah recebi, posso consultar a crep para detectar se jah tratei a msg, ou seja, se ela eh duplicada
    private boolean isCollabReplyMessageDuplicate(RequestIdentifier reqID, String collabAddr) {
        boolean status = false;

        ReversePath reversePath = reversePathTableScheduler.getEntry(reqID);
        if (reversePath != null) {
            status = reversePath.replyIsDuplicate(collabAddr);
        }
        return status;
    }

    // Faz o encaminhamento da mensagem CRep para os nos intermediarios, verificando se deve ser
    // utilizado broadcast normal ou broadcast direcionado         
    private synchronized void forwardCollaboratorReplyMessage(P2PDPCollaboratorReplyMessage message, ReversePath reversePath) {
        String gatewayIPAddr = message.getGatewayIPAddress();
        String collabAddress = LocalHost.getLocalHostAddress();
        //(2) Forward message (coordination channel) ONLY if COLLABORATOR is the message�s gateway OR if the message was send to DEFAULT_GATEWAY 
        if (LocalHost.isLocalHostAddress(gatewayIPAddr) || message.sendToDefaultGateway()) {
            //(2.1) Atualize the value of the last sender nodeID to COLLABORATOR identification
            message.setHopID(collabAddress);
            //(2.2) Increase the message hop count
            message.incHopCount();
            //Exception generated when the message was send: coordinationChannel.send(message)                      
            try {
                //(2.3) If Collaborator is in the reply�s ReturnPath, verify if the number of replies (CRep messages) for this Request (reqID) already was reach
                if (reversePath != null) {
                    RequestIdentifier reqID = message.getRequestIdentifier();
                    //(2.3.1) If the sender is in the same hop that me then I DISCART the reply (CRep)
                    if (reversePath.canDiscardReply()) {
                        //logger.trace("Collaborator discarded a CRep from [" + message.getDeviceIPAddress() + "]");
                        logger.trace("!>> Collaborator [" + collabAddress + "] discard a CollaboratorReply message from [" + message.getDeviceIPAddress() + "], the node is in the same HOP that me - {ReqID: " + reqID + "}");

                    //(2.3.2) The node overhears replies and atualize its replyCount in PendingList            
                    } else {
                        reversePath.decReplyCount();
                        //Suppress by Vicinity...
                        if (reversePath.canForwardReply()) {
                            if (coordinationChannel != null) {
                                //(2.3.3)-> Send message in directed ("direcionado") broadcast to next hop
                                reversePath.addCollab(message.getDeviceIPAddress()); // Adiciono o collab para quem envio na pending list para controlar as duplicadas                     

                                message.setGatewayIPAddress(reversePath.getReturnPath());

                                coordinationChannel.send(message);
                                logger.trace(">> Collaborator [" + collabAddress + "] forwarded to [" + message.getInitiatorIPAddress() + "] [(nh)" + reversePath.getReturnPath() + "] a " + message.getMessageTypeStr() + " message from [" + message.getDeviceIPAddress() + "] {ReqID: " + message.getRequestIdentifier() + "}");
                            //logger.trace("Collaborator forwarded to [" + message.getInitiatorIPAddress() + "] [(nh)" + reversePath.getReturnPath() + "] a " + message.getMessageTypeStr() + " message from [" + message.getDeviceIPAddress() + "]");
                            } else {
                                logger.error("!>> Collaborator [" + collabAddress + "] could not forward a " + message.getMessageTypeStr() + " message from [" + message.getDeviceIPAddress() + "] {ReqID: " + reqID + "}, problems in coordination channel");
                            }
                        } else {
                            logger.trace("(-) Collaborator [" + collabAddress + "] suppressed a CollaboratorReply message from [" + message.getDeviceIPAddress() + "(" + message.getHopID() + ")] - {ReqID: " + reqID + "}");
                        //logger.trace("Collaborator suppressed a CollaboratorReply message from [" + message.getDeviceIPAddress() + "(" + message.getHopID() + ")]");
                        }
                    }
                } else { //else of (2.3): 
                    //(2.4) Send message in local broadcast (for ALL neighbors)

                    if (coordinationChannel != null) {
                        message.setDefaultGatewayIPAddress();
                        coordinationChannel.send(message);
                        logger.trace(">> Collaborator [" + collabAddress + "] forwarded to ALL [" + message.getGatewayIPAddress() + "] a " + message.getMessageTypeStr() + " message {ReqID: " + message.getRequestIdentifier() + "} from [" + message.getDeviceIPAddress() + "(" + message.getHopID() + ")]");
                    } else {
                        logger.error("!>> Collaborator [" + collabAddress + "] could not forward a " + message.getMessageTypeStr() + " message {ReqID: " + message.getRequestIdentifier() + "}");
                    }
                }
            } catch (P2PDPConnectionException e) {
                logger.error("!>> Collaborator [" + collabAddress + "] could not forward a " + message.getMessageTypeStr() + " message {ReqID: " + message.getRequestIdentifier() + "}: " + e.getMessage(), e);
            }
        } else {
            //else of (2): Discard message ...
            logger.trace("!>> Collaborator [" + collabAddress + "] discard a " + message.getMessageTypeStr() + " message from [" + message.getDeviceIPAddress() + "(" + message.getHopID() + ")], Collaborator is not the default gateway [" + gatewayIPAddr + "] to request {ReqID: " + message.getRequestIdentifier() + "}");
        }
    }

    public void sendCollaboratorReplyMessage(P2PDPCollaboratorReplyMessage cRepMessage, long timeToWait, int numMaxReplies) {
        // Escalona envio da mensagem de resposta
        replySchedulerList.schedule(cRepMessage, timeToWait, numMaxReplies);
        logger.trace("> Collaborator [" + cRepMessage.getDeviceIPAddress() + "] [(px)" + cRepMessage.getProxyIPAddress() + ")] schedule to sent in " + timeToWait + " milliseconds a " + cRepMessage.getMessageTypeStr() + " message to [" + cRepMessage.getInitiatorIPAddress() + "] - Return Path [" + cRepMessage.getGatewayIPAddress() + "] {ReqID: " + cRepMessage.getRequestIdentifier() + "} - ResID: " + cRepMessage.getResourceIdentifier());
    }
    //FIM - Metodos da interface CollaboratorP2PDPMessageHandler


    //INICIO - Metodos da interface DiscoveryCollaboratorFacade
    public ResourceIdentifier register(ResourceDescriptor resourceDescriptor) {
        if (resourceDescriptor == null) {
            logger.warn("The registration was not executed, the resource descriptor is null.");
            return null;
        }

        ResourceIdentifier resID = new ResourceIdentifier(resourceDescriptor.getIdentifier());
        resourceRegistry.put(resID, resourceDescriptor);

        logger.debug("Collaborator [" + LocalHost.getLocalHostAddress() + "] registered resource " + resID.getResourceIdentifier() + ".");
        logger.debug(resourceDescriptor.toString());

        return resID;
    }

    public void deregister(ResourceIdentifier resID) {
        resourceRegistry.remove(resID);

        logger.debug("Collaborator [" + LocalHost.getLocalHostAddress() + "] deregistered resource " + resID.getResourceIdentifier() + ".");

        if (requestResource.containsValue(resID)) {
            RequestIdentifier[] reqID = requestResource.getKeysFromValue(resID);
            for (int i = 0; i < reqID.length; i++) {
                replySchedulerList.cancelSchedule(reqID[i]);
            //TODO TRAS - talvez deva avisar ao coordenador e/ou iniciador 
            //            que o recurso nao estah mais disponivel
            }
        }
    }

    public void deregisterAll() {
        try {
            for (Enumeration resKey = resourceRegistry.keys(); resKey.hasMoreElements();) {
                ResourceIdentifier resID = (ResourceIdentifier) resKey;
                deregister(resID);

                logger.debug("Collaborator [" + LocalHost.getLocalHostAddress() + "] deregistered all resources.");
            }
        } catch (Exception ex) {
            logger.error("The resources deregistration was not executed.");
        }
    }

    public void setAdmissionController(AdmissionController admController) {
        admController.registerResources(resourceRegistry);
        admissionController = admController;
    }

    public void setCollaborationLevel(float w) {
        if (w > 1) {
            w = 1.0f;
        } else if (w < 0) {
            w = 0.0f;
        }
        //else if ( w <= 0 ) { w = 0.1f; }

        this.collaborationLevel = w;   // willingness

    }

    public void setTransferDelay(float gama) {
        if (gama <= 0) {
            gama = 2;
        }
        transferDelay = gama;
    }

    public void setForwardRequestDelay(int minForwardDelay, int maxForwardDelay) {
        if (minForwardDelay > 0) {
            minForwardRequestDelay = minForwardDelay;
        }
        if (maxForwardDelay > 0 && maxForwardDelay > minForwardDelay) {
            maxForwardRequestDelay = maxForwardDelay;
        }
    }

    public void setCollaboratorReplyTimerFunction(CollaboratorReplyTimerFunction collabRepTimerFunction) {
        replyTimer = collabRepTimerFunction;
    }

    public void setContextParser(ContextParser contextParser) {
        contextListener.setContextParser(contextParser);
    }

    public float getCollaborationLevel() {
        return collaborationLevel;
    }

    public float getTransferDelay() {
        return transferDelay;
    }

    public int getMinForwardRequestDelay() {
        return minForwardRequestDelay;
    }

    public int getMaxForwardRequestDelay() {
        return maxForwardRequestDelay;
    }
    //FIM - Metodos da interface DiscoveryCollaboratorFacade
}

