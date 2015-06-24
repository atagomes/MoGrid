/*
 * Created on 29/09/2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package martin.mogrid.p2pdl.collaboration.scheduler;

import java.util.Timer;
import java.util.TimerTask;

import martin.mogrid.p2pdl.api.RequestIdentifier;
import martin.mogrid.p2pdl.protocol.P2PDPConnectionException;
import martin.mogrid.p2pdl.protocol.P2PDPSenderConnection;
import martin.mogrid.p2pdl.protocol.message.P2PDPInitiatorRequestMessage;

import org.apache.log4j.Logger;

public class InitiatorRequestToForwardScheduler {      
  
   // Manutencao do arquivo de log para debug
   private static final Logger logger = Logger.getLogger(InitiatorRequestToForwardScheduler.class);      

   private  P2PDPSenderConnection channel = null;

   
   public InitiatorRequestToForwardScheduler(P2PDPSenderConnection coordinationChannel) {
      this.channel = coordinationChannel;
   }
   

   public void schedule(P2PDPInitiatorRequestMessage iReqMessage, long timeToWait) {
      //Garante que o valor do timeout eh positivo
      timeToWait = Math.abs(timeToWait);
      
      //(1) Set a new timer task to FORWARD a IReq message in the timer specified (entryTTL) if no ACK was receive          
      Timer msgTimer = new Timer();
      msgTimer.schedule(new ForwardRequestTask(iReqMessage, msgTimer), timeToWait); //milliseconds 
    }
 

   
   class ForwardRequestTask extends TimerTask {
      private P2PDPInitiatorRequestMessage message;
      private Timer                        msgTimer;
      
      public ForwardRequestTask(P2PDPInitiatorRequestMessage iReqMessage, Timer msgTimer) {
         this.message = iReqMessage;
         this.msgTimer = msgTimer;
      }
      
      public void run() {
         RequestIdentifier reqID = message.getRequestIdentifier();
         try {
            //(1) Send scheduled request
            channel.send(message); 
            logger.trace("> Collaborator [" + message.getHopID()+"] forwarded a " + message.getMessageTypeStr() + " message from ["+message.getDeviceIPAddress() + "] {ReqID: "+ reqID+ "}");                                  
         } catch (P2PDPConnectionException cpex) {
            logger.debug("!> Collaborator [" + message.getHopID()+"] cannot forwarded a " + message.getMessageTypeStr() + " message from ["+message.getDeviceIPAddress() + "] {ReqID: "+ reqID+ "}");
            logger.warn("!> Collaborator [" + message.getHopID()+"] cannot forwarded a " + message.getMessageTypeStr() + " message from ["+message.getDeviceIPAddress() + "] {ReqID: "+ reqID+ "}", cpex);
         } 

         //(2) Terminate the timer thread created for the Task
         if ( msgTimer!=null ) {
            msgTimer.cancel();
            msgTimer = null;
         }
         
      }
   }
   
}
