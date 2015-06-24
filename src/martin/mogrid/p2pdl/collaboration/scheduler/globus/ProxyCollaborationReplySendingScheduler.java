/*
 * Created on 29/09/2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package martin.mogrid.p2pdl.collaboration.scheduler.globus;

import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import martin.mogrid.common.network.LocalHost;
import martin.mogrid.p2pdl.api.RequestIdentifier;
import martin.mogrid.p2pdl.collaboration.scheduler.CollaborationReplyScheduler;
import martin.mogrid.p2pdl.protocol.P2PDPConnectionException;
import martin.mogrid.p2pdl.protocol.P2PDPSenderConnection;
import martin.mogrid.p2pdl.protocol.message.P2PDPCollaboratorReplyMessage;

import org.apache.log4j.Logger;

public class ProxyCollaborationReplySendingScheduler implements CollaborationReplyScheduler {        
      // Manutencao do arquivo de log para debug
      private static final Logger logger = Logger.getLogger(ProxyCollaborationReplySendingScheduler.class);
       
      private P2PDPSenderConnection channel      = null;
      private Hashtable             scheduleList = null;

      
      public ProxyCollaborationReplySendingScheduler(P2PDPSenderConnection coordinationChannel) {
         this.channel = coordinationChannel;
         scheduleList = new Hashtable();
      }      
      
      public void schedule(P2PDPCollaboratorReplyMessage cRepMessage, long msgDelay, int numMaxReplies) {
         RequestIdentifier reqID = cRepMessage.getRequestIdentifier();
         if ( reqID != null ) {
            //Garante que o valor do timeout eh positivo
            msgDelay = Math.abs(msgDelay);
            Timer msgTimer = new Timer();
            msgTimer.schedule(new SendReplyTask(cRepMessage, msgTimer), msgDelay); //milliseconds   
            //Como a requisicao pode ser atendida por varios colaboradores (PROXY) a hashtable de 
            //requisicoes remotas passa a apontar para um Vetor de RemoteRequest (ProxyRemoteRequest)
            ProxyCollaborationReplyScheduled scheduledReply = null;
            if ( scheduleList.containsKey(reqID) ) {
               Object value = scheduleList.get(reqID);
               if ( value!=null && value instanceof ProxyCollaborationReplyScheduled ) {
                  scheduledReply = (ProxyCollaborationReplyScheduled)value;
               }
            } else {
               scheduledReply = new ProxyCollaborationReplyScheduled(numMaxReplies);
            }
            if ( scheduledReply != null ) {
               scheduledReply.addElement(msgTimer);
               scheduleList.put(reqID, scheduledReply); 
            }    
         }
      }
      
      public boolean containsCRepScheduled(RequestIdentifier key) {
         return scheduleList.containsKey(key);
      }
      
      public ProxyCollaborationReplyScheduled getCRepScheduled(RequestIdentifier key) {
         return (ProxyCollaborationReplyScheduled)scheduleList.get(key);
      } 

      public void incNumCRepOverheard(RequestIdentifier key) {
         ProxyCollaborationReplyScheduled request = (ProxyCollaborationReplyScheduled)scheduleList.get(key);
         request.incNumRepliesListened();
      }
      
      public boolean canDiscardCRepScheduled(RequestIdentifier key) {
         ProxyCollaborationReplyScheduled request = (ProxyCollaborationReplyScheduled)scheduleList.get(key);         
         return request.discardReplyScheduled();
      }
      
      //Remove all replies scheduled for a specific REQID
      public void cancelSchedule(RequestIdentifier key) {
         if ( scheduleList.containsKey(key) ) {
            ProxyCollaborationReplyScheduled reply = (ProxyCollaborationReplyScheduled)scheduleList.remove(key);
            logger.trace("!> Collaborator Proxy [" + LocalHost.getLocalHostAddress() + "] discard reply to {ReqID: " + key + "}");                                       
            Timer[] timerToRemove = reply.elements();
            for ( int i=0; i<timerToRemove.length; i++ ) {
               if ( timerToRemove[i] != null ) { 
                  timerToRemove[i].cancel(); 
                  timerToRemove[i] = null;
               }
            }
         }
      }
      
      
      
      class SendReplyTask extends TimerTask {
         private P2PDPCollaboratorReplyMessage message    = null;
         private Timer                         replyTimer = null;
         
         public SendReplyTask(P2PDPCollaboratorReplyMessage cRepMessage, Timer replyTimer) {
            this.message    = cRepMessage;
            this.replyTimer = replyTimer;
         }
         
         public void run() {
            try {
               //Send scheduled reply
               channel.send(message); 
               logger.trace("> Proxy Collaborator ["+ message.getProxyIPAddress() +"] sent in name of Collaborator ["+ message.getDeviceIPAddress()+"] a " + message.getMessageTypeStr() + " message to ["+message.getInitiatorIPAddress()+"] [(ph)"+message.getGatewayIPAddress()+"] {ReqID: "+ message.getRequestIdentifier()+ "}" );        
                           
            } catch (P2PDPConnectionException cpex) {
               logger.debug("!> Proxy Collaborator ["+ message.getProxyIPAddress() +"] CANNOT sent in name of Collaborator ["+ message.getDeviceIPAddress()+"] a " + message.getMessageTypeStr() + " message to ["+message.getInitiatorIPAddress()+" [(ph)"+message.getGatewayIPAddress()+"] {ReqID: "+ message.getRequestIdentifier()+ "}");                  
            } 

            //(2) Terminate the timer thread created for the Task
            if ( replyTimer != null ) { 
               replyTimer.cancel(); 
               replyTimer = null;
            }
         }
      }

   }
