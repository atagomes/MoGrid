     
package martin.mogrid.entity.coordinator;


import java.net.SocketTimeoutException;

import martin.mogrid.common.network.LocalHost;
import martin.mogrid.common.util.SystemUtil;
import martin.mogrid.p2pdl.api.RequestIdentifier;
import martin.mogrid.p2pdl.protocol.P2PDPConnectionException;
import martin.mogrid.p2pdl.protocol.P2PDPConnectionFactory;
import martin.mogrid.p2pdl.protocol.P2PDPCoordinationConnection;
import martin.mogrid.p2pdl.protocol.message.P2PDPCollaboratorReplyMessage;
import martin.mogrid.p2pdl.protocol.message.P2PDPInitiatorRequestMessage;
import martin.mogrid.p2pdl.protocol.message.P2PDPMessageInterface;

import org.apache.log4j.Logger;



/**
 * @author luciana
 * 
 * Created on 19/08/2005
 */
public class Coordinator implements Runnable,
                                    CoordinatorP2PDPMessageHandler {

   //Manutencao do arquivo de log do servico
   private static final Logger logger = Logger.getLogger(Coordinator.class);
   
   
   //Objetos associados a execucao do listener
   private Thread                       coordinatorThread              = null; 
   private int                          coordinatorThreadScanInterval  = 1000;
   private int                          coordinationThreadScanInterval = 1000;
   private P2PDPCoordinationConnection  coordinationChannel            = null;
   private CoordinatorConnection        coordinatorChannel             = null;  

   //Lista de requisicoes (INITIATORs) sendo tratadas pelo COORDINATOR
   private PendingInitiatorRequests iRequests = null;  
   
   //Garante uma instancia unica da classe
   private static Coordinator coordinatorInstance = null;
   
   
   private Coordinator() throws CoordinatorException {      
      try {
         coordinatorChannel = new CoordinatorConnection();
         coordinationChannel = P2PDPConnectionFactory.create();
         
      } catch (P2PDPConnectionException ex) {
         throw new CoordinatorException("It was not possible to create a Coordinator´s channel. Coordinator initialization was interrupted.", ex);
      } 

      iRequests = new PendingInitiatorRequests();      
      start();
   }
   
   /**
   * Get handle to the singleton
   * @return the singleton instance of this class.
   */
   public static synchronized Coordinator getInstance() throws CoordinatorException {
      if ( coordinatorInstance == null ) {
         coordinatorInstance = new Coordinator();
      }
      return coordinatorInstance;
   }   
   
   private void start() {
      logger.info("Starting Coordinator...");
      
      try {
         //canal de comunicacao com o coordenador
         coordinatorChannel.open();
         coordinatorThreadScanInterval = coordinatorChannel.getScanInterval();
         
      } catch (P2PDPConnectionException e) {
         logger.info("Coordinator was interrupted.");
         logger.fatal("It was not possible to create a Coordinator-Initiator Channel: " + e.getMessage());
         SystemUtil.abnormalExit();
      }  
      
      //Recupera o intervalo de espera do canal de coorden     
      coordinationThreadScanInterval = coordinationChannel.getScanInterval();
      
      if( coordinatorThread == null ) {
         coordinatorThread = new Thread(this, "CoordinatorListener");
         coordinatorThread.start();
      }     
      
      logger.info("Coordinator running.");
   }
   
   public void stop() {
      logger.info("Stopping Coordinator...");      
      
      coordinatorThread.interrupt();
      try {
         coordinatorThread.join(1000);      
      } catch (InterruptedException e) {      
      } finally {
         coordinatorThread = null;
         if( coordinatorChannel != null ) {
            coordinatorChannel.close(); 
            coordinatorChannel = null;
         } 
         if( coordinationChannel != null ) {
            coordinationChannel.close(); 
            coordinationChannel = null;
         } 
         logger.info("Coordinator sttoped.");
      } 
   }   
   
   public void run() { 
      Thread                myThread    = Thread.currentThread();
      P2PDPMessageInterface msgReceived = null;
      
      int scanInterval = (coordinatorThreadScanInterval + coordinationThreadScanInterval) / 2;
      while( coordinatorThread == myThread ) {
         SystemUtil.sleep(scanInterval, "It occured some error at sleeping the Coordinator Thread");
        
         try {
            //Canal de mensagem com o INITIATOR
            msgReceived = coordinatorChannel.receive();
            handlerInitiatorMessage(msgReceived);   
            
            //Canal de mensagem com os COLLABORATORS
            msgReceived = coordinationChannel.receive();
            handlerCoordinationMessage(msgReceived);
            
         } catch (P2PDPConnectionException ex) {
            logger.warn("It occurred some error receiving messages from P2PDP: " + ex.getMessage());
            //Util.abnormalExit();   
            
         } catch (SocketTimeoutException stex) {
               //It doesn´t anything, exception was thow because a value >0 (0=infinite) was atributed a socket.SO_TIMEOUT
               //to evit that the receive method blocks until a datagram was received.            
         }
         
      }
   }  
   
   //O Coordinator deve tratar mensagens IReq oriundas de Initiators e ignorar
   //mensagens IReq reenviadas por ele mesmo (o que causaria um loop de REenvio de IReqs)
   private void handlerInitiatorMessage(P2PDPMessageInterface protMessage) {
      if ( protMessage != null ) {
         int msgType = protMessage.getMessageType();
         
         if ( msgType == P2PDPMessageInterface.CP_MSG_IREQ ) {                
            handlerInitiatorRequestMessage(protMessage);
         }  
      
      } else {
         logger.warn("Error at the Coordinator-Initiator Message handler: the message is null.");
      }
   }   
   
   private void handlerCoordinationMessage(P2PDPMessageInterface protMessage) {
      logger.error("Coord Recebeu mensagem..." ); 
      
      if ( protMessage != null ) {
         int msgType = protMessage.getMessageType();            

         logger.info("< Coordinator [] received " + protMessage.getMessageTypeStr());
         if ( msgType == P2PDPMessageInterface.CP_MSG_CREP ) {
            handlerCollaboratorReplyMessage(protMessage);
         }  
      
      } else {
         logger.warn("Error at the Coordination Protocol Message handler: the message is null.");
      }
   }   
   
   
   //START - Metodos da interface CoordinatorP2PDPMessageHandler
   public void handlerInitiatorRequestMessage(P2PDPMessageInterface protMessage) {
      P2PDPInitiatorRequestMessage message       = (P2PDPInitiatorRequestMessage) protMessage;
      RequestIdentifier            reqID         = message.getRequestIdentifier();    
      long                         maxRepDelay   = message.getMaxReplyDelay();
      int                          numMaxReplies = message.getNumMaxReplies();
      String                       initiatorAddr = message.getDeviceIPAddress();
      
      logger.info("< Coordinator [] received " + message.getMessageTypeStr());
      
      try {
         coordinationChannel.send(message);
         logger.info("> Coordinator [] sent " + message.getMessageTypeStr());
         
         CollaboratorReplyListScheduler cRepMerger = new CollaboratorReplyListScheduler(this, reqID, numMaxReplies, maxRepDelay, initiatorAddr);         
         InitiatorRequest iReq = new InitiatorRequest(cRepMerger, numMaxReplies);
         iRequests.put(reqID, iReq);
         
      } catch (P2PDPConnectionException e) {
         logger.error("\nIt was not possible to send message ["+message.getMessageTypeStr()+"].\n[ERROR] " + e.getMessage());            
      }      
   }

   public void handlerCollaboratorReplyMessage(P2PDPMessageInterface protMessage) {
      P2PDPCollaboratorReplyMessage message = (P2PDPCollaboratorReplyMessage) protMessage;
      RequestIdentifier             reqID   = message.getRequestIdentifier(); 
   
      logger.info("\n< [coordinator] receive " + message.getMessageTypeStr());      
      
      if ( iRequests.containsKey(reqID) ) {
         InitiatorRequest req    = iRequests.get(reqID);
         String  msgMAC = message.getDeviceMACAddress();
         if ( ! LocalHost.isLocalMacAddress(msgMAC) ) {
            req.incNumRepliesListened();
         }
         if ( req.discardRequestScheduled() ) { 
            iRequests.remove(reqID);
            logger.info(reqID.toString()+" [coordinator] discard request scheduled " + reqID +"\n" );
            return;
         }
         //A cada requisicao (reqID) estah associada uma lista de respostas
         CollaboratorReplyListScheduler cRepListScheduler = req.getCRepListScheduler();
         //A cada CRep recebida (message) eh feito um merge de todas as respostas (CReps)
         cRepListScheduler.mergeReplyList(message);
      }       
   }  

   public void sendCollaboratorReplyListMessage(P2PDPMessageInterface msg, String addr) throws P2PDPConnectionException {
      coordinatorChannel.send(msg, addr);
   }
   //END - Metodos da interface CoordinatorP2PDPMessageHandler
   
   
   static public void main(String[] args) throws CoordinatorException {      
      Coordinator.getInstance();
   }
   
}
