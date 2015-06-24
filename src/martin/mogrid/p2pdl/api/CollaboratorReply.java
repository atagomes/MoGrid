package martin.mogrid.p2pdl.api;

import martin.mogrid.common.context.MonitoredContext;
import martin.mogrid.common.network.Payload;
import martin.mogrid.common.resource.ResourceDescriptor;
import martin.mogrid.common.resource.ResourceIdentifier;
import martin.mogrid.p2pdl.protocol.message.P2PDPCollaboratorReplyMessage;

public class CollaboratorReply implements Payload {
   
   /** Comment for <code>serialVersionUID</code> */
   private static final long serialVersionUID = -415171436672245126L;

   private P2PDPCollaboratorReplyMessage cRepMessage;
   
   public CollaboratorReply(P2PDPCollaboratorReplyMessage message) {
      cRepMessage = message;
   }

   
   //LEITURA   
   public String getCollaboratorProxyIPAddr() {
      return cRepMessage.getProxyIPAddress();
   }
   
   public String getCollaboratorIPAddr() {
      return cRepMessage.getDeviceIPAddress();
   }

   public String getCollaboratorMACAddr() {
      return cRepMessage.getDeviceMACAddress();
   }

   public String getPreviousHop() {
      return cRepMessage.getHopID();
   }
   
   public MonitoredContext getCollaboratorContext() {
      return cRepMessage.getMonitoredContext();
   }
   
   public ResourceIdentifier getCollaboratorResourceIdentifier() {
      return cRepMessage.getResourceIdentifier();
   }
   
   public ResourceDescriptor getCollaboratorResourceDescriptor() { 
      return cRepMessage.getResourceDescriptor();
   }
   
   
   // IMPRESSAO
   public String toString() {
      String messageStr =  "\nDevice IP Address: "   + getCollaboratorIPAddr() + 
                           "\nDevice MAC Address: "  + getCollaboratorMACAddr() + 
                           "\nProxy IP Address: "    + getCollaboratorProxyIPAddr() + 
                           "\nPrevius Hop: "         + getPreviousHop() + 
                           //Monitored Context
                           getCollaboratorContext().toString()  +     
                           //Resource Identifier
                           getCollaboratorResourceIdentifier().toString() +     
                           "\nResource Descriptor: " + getCollaboratorResourceDescriptor().toString(); 
         
      return messageStr;
   } 
   
}
