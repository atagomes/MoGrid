/*
 * Created on 15/09/2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package martin.mogrid.p2pdl.collaboration.scheduler;

import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import martin.mogrid.common.util.SystemUtil;
import martin.mogrid.p2pdl.api.RequestIdentifier;
import martin.mogrid.p2pdl.protocol.P2PDPConnectionException;
import martin.mogrid.p2pdl.protocol.P2PDPSenderConnection;
import martin.mogrid.p2pdl.protocol.message.P2PDPCollaboratorReplyMessage;

import org.apache.log4j.Logger;

public class CollaborationReplyAcknowledmentScheduler {
   // Manutencao do arquivo de log para debug
   private static final Logger logger = Logger.getLogger(CollaborationReplyAcknowledmentScheduler.class);
   
   private  P2PDPSenderConnection channel      = null;
   private  Hashtable             scheduleList = null;

   
   public CollaborationReplyAcknowledmentScheduler(P2PDPSenderConnection channel) {
      this.channel = channel;
      scheduleList = new Hashtable();
   }
   
   public void schedule(P2PDPCollaboratorReplyMessage cRepMessage, long timeToWait) {
      RequestIdentifier reqID = cRepMessage.getRequestIdentifier();      
      //Garante que o valor do timeout eh positivo
      timeToWait = Math.abs(timeToWait);
      
      //(1) Set a new timer task to send CRep message in the timer specified (entryTTL)
      Timer ackTimer = new Timer();      
      ackTimer.schedule(new SendReplyTask(cRepMessage), SystemUtil.convertSecondsToMilliseconds(timeToWait)); //milliseconds
      scheduleList.put(reqID, ackTimer);      
      logger.debug("? Waiting " + timeToWait + " seconds for ACK to CRep [" + reqID + "] message...");      
   }

   public void cancelSchedule(RequestIdentifier key) {
      if ( scheduleList.contains(key) ) {
         Timer timerToRemove = (Timer)scheduleList.remove(key);
         if ( timerToRemove != null ) { 
            timerToRemove.cancel(); 
            timerToRemove = null;
         }
      }
   }
   
   public boolean containsCRepScheduled(RequestIdentifier key) {
      return scheduleList.containsKey(key);
   }
   
   
   class SendReplyTask extends TimerTask {
      private P2PDPCollaboratorReplyMessage message;
      
      public SendReplyTask(P2PDPCollaboratorReplyMessage cRepMessage) {
         this.message = cRepMessage;
      }
      
      public void run() {
         RequestIdentifier reqID = message.getRequestIdentifier();
         try {
            //(1) Send scheduled reply
            channel.send(message); 
            logger.trace("> Collaborator [" + message.getDeviceIPAddress()+"] [(px)"+ message.getProxyIPAddress()+"] sent a " + message.getMessageTypeStr() + " message to ["+message.getInitiatorIPAddress()+"] [(nh)"+message.getGatewayIPAddress()+"] {ReqID: "+ reqID+ "} - ResID: " + message.getResourceIdentifier());                                  
         } catch (P2PDPConnectionException cpex) {
            logger.debug("!> Collaborator [" + message.getDeviceIPAddress()+"] [(px)"+message.getProxyIPAddress()+"] CANNOT sent a " + message.getMessageTypeStr() + " message to  ["+message.getInitiatorIPAddress()+"] [(nh)"+message.getGatewayIPAddress()+"] {ReqID: "+ reqID+ "} - ResID: " + message.getResourceIdentifier());                 
         } 

         //(2) Terminate the timer thread created for the Task
         cancelSchedule(reqID);
      }
   }

}
