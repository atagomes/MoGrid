/*
 * Created on 29/09/2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package martin.mogrid.p2pdl.collaboration.scheduler;

import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import martin.mogrid.common.network.LocalHost;
import martin.mogrid.p2pdl.api.RequestIdentifier;
import martin.mogrid.p2pdl.protocol.P2PDPConnectionException;
import martin.mogrid.p2pdl.protocol.P2PDPSenderConnection;
import martin.mogrid.p2pdl.protocol.message.P2PDPCollaboratorReplyMessage;
import martin.mogrid.p2pdl.routing.ReversePath;
import martin.mogrid.p2pdl.routing.ReversePathScheduler;

import org.apache.log4j.Logger;

public class CollaborationReplySendingScheduler implements CollaborationReplyScheduler {   
   
   // Manutencao do arquivo de log para debug
   private static final Logger logger = Logger.getLogger(CollaborationReplySendingScheduler.class);      

   private P2PDPSenderConnection channel;
   private ReversePathScheduler  reversePathScheduler;
   private Hashtable             scheduleList;

   
   public CollaborationReplySendingScheduler(P2PDPSenderConnection coordinationChannel, ReversePathScheduler reversePathScheduler) {
      this.channel = coordinationChannel;
      this.reversePathScheduler = reversePathScheduler;
      scheduleList = new Hashtable();
   }

   
   //-> numMaxReplies is used to controll the overheard CReps (normal schedule send)
   public void schedule(P2PDPCollaboratorReplyMessage cRepMessage, long timeToWait, int numMaxReplies) {
      RequestIdentifier reqID = cRepMessage.getRequestIdentifier();
      //Garante que o valor do timeout eh positivo
      timeToWait = Math.abs(timeToWait);
      
      //Set a new timer task to schedule a CRep message sent in the timer specified (timeToWait)
      if ( timeToWait > 0 ) {
         Timer msgTimer = new Timer();
         msgTimer.schedule(new SendReplyTask(cRepMessage), timeToWait); //milliseconds 
         CollaborationReplyScheduled reply = new CollaborationReplyScheduled(msgTimer, numMaxReplies);        
         scheduleList.put(reqID, reply);
      } else { // send message immediately
         sendCRepScheduled(reqID, cRepMessage); 
      }
   }   

   public void incNumCRepOverheard(RequestIdentifier key) {
      CollaborationReplyScheduled reply = (CollaborationReplyScheduled)scheduleList.get(key);  
      if ( reply != null )
         reply.incNumRepliesListened();
   }
   
   public boolean canDiscardCRepScheduled(RequestIdentifier key) {
      CollaborationReplyScheduled reply = (CollaborationReplyScheduled)scheduleList.get(key); 
      if ( reply == null ) { return false; }
      return reply.canDiscardReplyScheduled();
   }

   public boolean containsCRepScheduled(RequestIdentifier key) {
      return scheduleList.containsKey(key);
   }
   
   public void cancelSchedule(RequestIdentifier key) {
      if ( scheduleList.containsKey(key) ) {
         CollaborationReplyScheduled reply = (CollaborationReplyScheduled)scheduleList.remove(key);
         if ( reply != null ) {
            reply.removeReplyScheduled(); 
            logger.trace("(-) Collaborator [" + LocalHost.getLocalHostAddress() + "] suppressed ITS proper CollaboratorReply message - {ReqID: " + key+"}");            
         }
      }
   }
   
   private void finalizeSchedule(RequestIdentifier key) {
      if ( scheduleList.containsKey(key) ) {
         CollaborationReplyScheduled reply = (CollaborationReplyScheduled)scheduleList.remove(key);         
         if ( reply != null )
            reply.removeReplyScheduled(); 
      }
   } 
   
   private void sendCRepScheduled(RequestIdentifier key, P2PDPCollaboratorReplyMessage message) {    
      try {
         //(1) Send scheduled reply
         channel.send(message);          
         //(2) Notify collaborator if exist references to Request ID in ReversePathTable
         ReversePath reversePath = null;
         if ( reversePathScheduler.containsEntry(key)) {
            reversePath = reversePathScheduler.getEntry(key);
            reversePath.decReplyCount();
            reversePath.addCollab(message.getDeviceIPAddress()); // Adiciono o collab para quem envio na pending list para controlar as duplicadas                                                   
         }
         logger.trace("> Collaborator [" + message.getDeviceIPAddress() +"] [(px)"+message.getProxyIPAddress()+"] sent a " + message.getMessageTypeStr() + " message to ["+message.getInitiatorIPAddress()+"] [(nh)"+message.getGatewayIPAddress()+"] {ReqID: "+ key+ "}");                                                             
      } catch (P2PDPConnectionException cpex) {
         logger.debug("!> Collaborator [" +  message.getDeviceIPAddress() +"] [(px)"+message.getProxyIPAddress()+"] CANNOT sent a " + message.getMessageTypeStr() + " message to  ["+message.getInitiatorIPAddress()+"] [(nh)"+message.getGatewayIPAddress()+"] {ReqID: "+ key+ "}");                 
         logger.warn("!> Collaborator [" +  message.getDeviceIPAddress() +"] [(px)"+message.getProxyIPAddress()+"] CANNOT sent a " + message.getMessageTypeStr() + " message to  ["+message.getInitiatorIPAddress()+"] [(nh)"+message.getGatewayIPAddress()+"] {ReqID: "+ key+ "}", cpex);
      } 
   }
   
   
   class SendReplyTask extends TimerTask {
      private P2PDPCollaboratorReplyMessage message;
      
      public SendReplyTask(P2PDPCollaboratorReplyMessage cRepMessage) {
         this.message = cRepMessage;
      }
      
      public void run() {
         RequestIdentifier reqID = message.getRequestIdentifier();         
         //(1) Send scheduled reply and notify collaborator if exist references to Request ID in ReversePathTable
         sendCRepScheduled(reqID, message);
         //(2) Terminate the timer thread created for the Task
          finalizeSchedule(reqID);
      }
   }
   
}
