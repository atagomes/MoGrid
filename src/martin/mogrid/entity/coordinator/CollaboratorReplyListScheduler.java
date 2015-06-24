package martin.mogrid.entity.coordinator;

import java.util.Timer;
import java.util.TimerTask;

import martin.mogrid.common.network.LocalHost;
import martin.mogrid.common.util.SystemUtil;
import martin.mogrid.p2pdl.api.CollaboratorReply;
import martin.mogrid.p2pdl.api.CollaboratorReplyList;
import martin.mogrid.p2pdl.api.RequestIdentifier;
import martin.mogrid.p2pdl.protocol.P2PDPConnectionException;
import martin.mogrid.p2pdl.protocol.message.P2PDPCollaboratorReplyListMessage;
import martin.mogrid.p2pdl.protocol.message.P2PDPCollaboratorReplyMessage;

import org.apache.log4j.Logger;

public class CollaboratorReplyListScheduler {

    // Manutencao do arquivo de log para debug
    private static final Logger logger = Logger.getLogger(CollaboratorReplyListScheduler.class);
    private CollaboratorReplyListSchedulerChannel channel = null;
    private RequestIdentifier reqID = null;
    private CollaboratorReplyList cRepList = null;
    private Timer repListTimer = null;
    private String addrToSend = null;
    private int numMaxReplies = 0;

    public CollaboratorReplyListScheduler(CollaboratorReplyListSchedulerChannel channel, RequestIdentifier reqID, int numMaxReplies, long repDelayInSeconds, String addrToSend) {
        this.channel = channel;
        this.reqID = reqID;
        this.numMaxReplies = numMaxReplies;
        this.addrToSend = addrToSend;
        cRepList = new CollaboratorReplyList();

        //Garante que o valor do timeout eh positivo e converte para milisegundos
        long repDelayInMilliSeconds = Math.abs(SystemUtil.convertSecondsToMilliseconds(repDelayInSeconds));
        //Espera mais 20% para certificar que as mensagens cheguem ao colaborador em uma rede congestionada
        repDelayInMilliSeconds = (long) (repDelayInMilliSeconds * 1.20);
        //Cria um timer para envio da lista de CReps associado ao valor de MaxRepDelay da requisicao (IReq)
        repListTimer = new Timer();
        repListTimer.schedule(new CoordinationProtocolMessageTask(), repDelayInMilliSeconds);    //milliseconds
    }

    public boolean isNewReply(P2PDPCollaboratorReplyMessage message) {
        if (cRepList == null || !cRepList.containsValue(message.getDeviceIPAddress())) {
            return true;
        }
        return false;

    }

    public void mergeReplyList(P2PDPCollaboratorReplyMessage message) {
        if (cRepList == null) {
            return;
        }

        synchronized (cRepList) {
            //Verificar se a mensagem jah foi adicionada...
            if (isNewReply(message)) {
                CollaboratorReply cParsedRep = new CollaboratorReply(message);
                cRepList.addElement(cParsedRep);
                logger.trace("(+) Coordinator [" + LocalHost.getLocalHostAddress() + "] add CRep from [" + cParsedRep.getCollaboratorIPAddr() + "] [(px)" + cParsedRep.getCollaboratorProxyIPAddr() + "] [(ph)" + cParsedRep.getPreviousHop() + "] to CRepList - {" + reqID + "}");
            } else {
                logger.debug("! Coordinator [" + LocalHost.getLocalHostAddress() + "] not added CRep from [" + message.getDeviceIPAddress() + "] [(px)" + message.getProxyIPAddress() + "] [(ph)" + message.getGatewayIPAddress() + "] because it already is in CRepList - {" + reqID + "}");
            }

            if (cRepList.size() == numMaxReplies) {
                logger.debug("Coordinator [" + LocalHost.getLocalHostAddress() + "] finish CRep {" + reqID + "} resume in advance");
                sendCRepList();
            }
        }
    }

    /*
    public void mergeReplyList(P2PDPCollaboratorReplyMessage message) {
    if ( cRepList == null ) { return; }
    
    synchronized ( cRepList ) {
    CollaboratorReply cParsedRep = new CollaboratorReply(message);      
    cRepList.addElement(cParsedRep);    
    logger.trace("(+) Coordinator [" + LocalHost.getLocalHostAddress() + "] add CRep {"+reqID+"} from ["+cParsedRep.getCollaboratorIPAddr()+"->"+cParsedRep.getPreviusHop()+"] to CRepList");
    
    if ( cRepList.size() == numMaxReplies ) {
    logger.debug("Coordinator [" + LocalHost.getLocalHostAddress() + "] finish CRep {"+reqID+"} resume in advance");      
    sendCRepList();
    }
    }
    }*/
    public Timer getRepListTimer() {
        return repListTimer;
    }

    private void clearRepList() {
        if (cRepList != null) {
            cRepList.clear();
            cRepList = null;
        }
    }

    private void clearRepListTimer() {
        if (repListTimer != null) {
            repListTimer.cancel();        //Terminate the timer thread
            repListTimer = null;
        }
    }

    private void sendCRepList() {
        //Evita que mensagens CRep sejam adicionadas depois que a tarefa tenha sido executada e antes que o timer seja finalizado
        synchronized (cRepList) {
            if (!cRepList.isEmpty()) {
                P2PDPCollaboratorReplyListMessage repListMessage = new P2PDPCollaboratorReplyListMessage(reqID, cRepList);
                try {
                    //When the Initiator timer (IReq.maxReplyDelay) expires the coordinator sent the CRepList
                    //The coordinator can, in some times, not receive any CRep from Collaborators, then he sent a 
                    //CRepList empty and Initiator will handle this case
                    logger.trace("> Coordinator [" + LocalHost.getLocalHostAddress() + "] sent scheduled message P2PDPCollaboratorReplyListMessage {ReqID: " + reqID + "}");
                    channel.sendCollaboratorReplyListMessage(repListMessage, addrToSend);
                } catch (P2PDPConnectionException cex) {
                    logger.trace("!> Coordinator [" + LocalHost.getLocalHostAddress() + "] cannot sent scheduled P2PDPCollaboratorReplyListMessage message {ReqID: " + reqID + "}");
                    logger.error("!> Coordinator [" + LocalHost.getLocalHostAddress() + "] cannot sent scheduled P2PDPCollaboratorReplyListMessage message {ReqID: " + reqID + "}: " + cex.getMessage(), cex);
                }
            } else {
                logger.trace("!> Coordinator [" + LocalHost.getLocalHostAddress() + "] cannot sent P2PDPCollaboratorReplyListMessage message because it is EMPTY {ReqID: " + reqID + "}");
            }
            clearRepList();
            clearRepListTimer();
        }
    }

    class CoordinationProtocolMessageTask extends TimerTask {
        /*String addrToSend = null;
        
        public CoordinationProtocolMessageTask(String addrToSend) {
        this.addrToSend = addrToSend;
        }
        
        public void run() {
        P2PDPCollaboratorReplyListMessage repListMessage = new P2PDPCollaboratorReplyListMessage(reqID, cRepList);
        //if ( !cRepList.isEmpty() ) {
        try { 
        //When the Initiator timer (IReq.maxReplyDelay) expires the coordinator sent the CRepList
        //The coordinator can, in some times, not receive any CRep from Collaborators, then he send a 
        //CRepList empty and Initiator will handle this case
        logger.trace("> Coordinator [" + LocalHost.getLocalHostAddress() + "] sent scheduled " + repListMessage.getMessageTypeStr()+" message {ReqID: " + repListMessage.getRequestIdentifier()+"}");            
        channel.sendCollaboratorReplyListMessage(repListMessage, addrToSend);           
        } catch (P2PDPConnectionException cex) {
        logger.error("!> Coordinator [" + LocalHost.getLocalHostAddress() + "] cannot sent scheduled " + repListMessage.getMessageTypeStr() + " message {ReqID: "+ repListMessage.getRequestIdentifier()+"}: " + cex.getMessage(), cex);         
        }
        //} else {
        // logger.trace("!> Coordinator [" + LocalHost.getLocalHostAddress() + "] cannot sent " + repListMessage.getMessageTypeStr() + " message because it is EMPTY {ReqID: "+ repListMessage.getRequestIdentifier()+"}");         
        //}
        
        if ( repListTimer != null ) {
        repListTimer.cancel();   //Terminate the timer thread
        }
        }*/

        public void run() {
            sendCRepList();
        }
    }
}
