
package martin.mogrid.entity.initiator;


import martin.mogrid.common.context.ContextInformation;
import martin.mogrid.common.network.LocalHost;
import martin.mogrid.common.resource.ResourceQuery;
import martin.mogrid.common.util.SystemUtil;
import martin.mogrid.entity.coordinator.Coordinator;
import martin.mogrid.entity.coordinator.CoordinatorException;
import martin.mogrid.p2pdl.api.DiscoveryApplicationFacade;
import martin.mogrid.p2pdl.api.DiscoveryInitiatorFacade;
import martin.mogrid.p2pdl.api.RequestIdentifier;
import martin.mogrid.p2pdl.api.RequestProfile;
import martin.mogrid.p2pdl.protocol.P2PDPConnectionException;
import martin.mogrid.p2pdl.protocol.message.P2PDPCollaboratorReplyListMessage;
import martin.mogrid.p2pdl.protocol.message.P2PDPInitiatorRequestMessage;
import martin.mogrid.p2pdl.protocol.message.P2PDPMessageInterface;

import org.apache.log4j.Logger;



/**
 * @author luciana
 *
 * Created on 25/06/2005
 */
public class Initiator implements Runnable,
                                  DiscoveryInitiatorFacade,
                                  InitiatorP2PDPMessageHandler {
      
   //Manutencao do arquivo de log do servico
   private static final Logger logger = Logger.getLogger(Initiator.class);    

   //Objetos associados a execucao do listener
   private Thread              initiatorThread             = null; 
   private int                 initiatorThreadScanInterval = 1000;
   private InitiatorConnection initiatorChannel            = null;  
   
   //Lista de associacao das requisicoes locais
   private RequestApplication requestApplication = null;
   
   //Garante uma instancia unica da classe
   private static Initiator initiatorInstance = null;
   
   //Obtem uma instancia do coordenador
   private Coordinator coordinator = null;
   
   private Initiator() throws InitiatorException {
      requestApplication = new RequestApplication();
      
      //Canal de comunicacao UNICAST entre INITIATOR e COLLABORATOR 
      try {
         initiatorChannel = new InitiatorConnection();
      } catch (P2PDPConnectionException e) {
         throw new InitiatorException("It was not possible to create a Initiator´s channel. Initiator initialization was interrupted.", e);     
      }
      start();
   }

   /**
    * Get handle to the singleton
    * @return the singleton instance of this class.
    */
   public static Initiator getInstance() throws InitiatorException {
      if ( initiatorInstance == null ) {
         initiatorInstance = new Initiator();
      }
      return initiatorInstance;
   }   
   
   private void start() {
      logger.info("Starting Initiator...");
      
      try {
         //canal de comunicacao com o coordenador         
         initiatorChannel.open();
         initiatorThreadScanInterval = initiatorChannel.getScanInterval();
         
         if( initiatorThread == null ) {
            initiatorThread = new Thread(this, "InitiatorListener");
            initiatorThread.start();
         }         
         
      } catch (P2PDPConnectionException e) {
         logger.info("Initiator was interrupted.");
         logger.fatal("It was not possible to create a Initiator-Coordinator Channel.\n[ERROR] " + e.getMessage());                  
         SystemUtil.abnormalExit();
      }  
      
      try {
         coordinator = Coordinator.getInstance();
      } catch (CoordinatorException e) {
         logger.info("Initiator was interrupted.");
         logger.fatal("It was not possible to instantiate a Coordinator entity.\n[ERROR] " + e.getMessage());         
         SystemUtil.abnormalExit();
      }
      
      logger.info("Initiator running.");
   }
    
   public synchronized void stop() {  
      logger.info("Stopping Initiator...");
      
      initiatorThread.interrupt();
      try {
         initiatorThread.join(1000);      
      } catch (InterruptedException e) {      
      } finally {
         initiatorThread = null; 
         if( initiatorChannel != null ) {
            initiatorChannel.close(); 
            initiatorChannel = null;
         }     
         logger.info("Initiator sttoped."); 
      }       
   }   

   public void run() { 
      Thread                myThread    = Thread.currentThread();
      P2PDPMessageInterface msgReceived = null;
      
      while( initiatorThread == myThread ) {
         try {
            SystemUtil.sleep(initiatorThreadScanInterval, "It occured some error at sleeping the InitiatorListener.");  //Wait in milliseconds
            
            if ( initiatorChannel != null ) {
               msgReceived = initiatorChannel.receive();
               handlerCoordinatorMessage(msgReceived); 
            }
                               
         } catch (P2PDPConnectionException ex) {
            logger.warn("The InitiatorListener was interrupted or it occurred some error receiving messages from coordinator.\n[ERROR] " + ex.getMessage());
            //Util.abnormalExit(); 
         } 
      }
   }  

   private void handlerCoordinatorMessage(P2PDPMessageInterface protMessage) {
      if ( protMessage != null ) {
         int msgType = protMessage.getMessageType();   
         if ( msgType == P2PDPMessageInterface.CP_MSG_CREPLIST ) {
            handlerCollaboratorReplyListMessage(protMessage);               
         }  
      } else {
         logger.warn("Error at the Coordination Protocol Message handler: the message is null.");
      }
   }   

   //START - Metodos da interface InitiatorP2PDPMessageHandler  
   public void sendInitiatorRequestMessage(RequestProfile reqProfile, ResourceQuery resourceQuery) {            
      P2PDPInitiatorRequestMessage message = new P2PDPInitiatorRequestMessage(resourceQuery, reqProfile);
     
      logger.trace("< Initiator ["+ LocalHost.getLocalHostAddress()+"] sent a "+ message.getMessageTypeStr() + " {ReqID: "+ message.getRequestIdentifier()+"}");         

      try {
         initiatorChannel.send(message);
      } catch (P2PDPConnectionException e) {
         logger.error("It was not possible to send message [" + message.getMessageTypeStr() + "].\n[ERROR] " + e.getMessage(), e);
      }
   }

   public void handlerCollaboratorReplyListMessage(P2PDPMessageInterface protMessage) {
      P2PDPCollaboratorReplyListMessage message = (P2PDPCollaboratorReplyListMessage) protMessage;
      logger.trace("< Initiator ["+ LocalHost.getLocalHostAddress()+"] received from [" + message.getDeviceIPAddress() + "] a "+ message.getMessageTypeStr() + " message {ReqID: "+ message.getRequestIdentifier()+"}");         
     
      RequestIdentifier reqID = message.getRequestIdentifier();
      if ( requestApplication.containsKey(reqID) ) {
         DiscoveryApplicationFacade appRequest = requestApplication.remove(reqID);
         appRequest.receiveCReplyList(reqID, message.getRepList());       
      } else {
         logger.warn("Initiator received CRepList from ["+message.getDeviceIPAddress()+"] but it had not registered the {ReqID: "+ reqID+"}");
      } 
   }
   //END - Metodos da interface InitiatorP2PDPMessageHandler

   
   //INICIO - Metodos da interface DiscoveryInitiatorFacade   
   public RequestProfile createRequestProfile(ContextInformation ctxtInfo, int numMaxReplies, long maxReplyDelay, int requestDiameter) {
      return ( new RequestProfile(ctxtInfo, numMaxReplies, maxReplyDelay, requestDiameter) );
   }
   
   public void discover(ResourceQuery resourceQuery, RequestProfile reqProfile, DiscoveryApplicationFacade app) {
      sendInitiatorRequestMessage(reqProfile, resourceQuery); 
      requestApplication.put(resourceQuery.getRequestIdentifier(), app);
   }   
   //FIM - Metodos da interface DiscoveryInitiatorFacade
   
}
