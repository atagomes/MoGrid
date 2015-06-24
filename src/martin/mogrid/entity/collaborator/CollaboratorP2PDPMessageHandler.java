
package martin.mogrid.entity.collaborator;

import martin.mogrid.p2pdl.protocol.message.P2PDPCollaboratorReplyMessage;
import martin.mogrid.p2pdl.protocol.message.P2PDPMessageInterface;

/**
 * @author luciana
 *
 * Created on 17/06/2005
 */
public interface CollaboratorP2PDPMessageHandler {

   //COLLABORATOR <- COORDINATOR 
   public abstract void handlerInitiatorRequestMessage(P2PDPMessageInterface protMessage);
  
   //COLLABORATOR <- COLLABORATORS
   public abstract void handlerCollaboratorReplyMessage(P2PDPMessageInterface protMessage);
   
   //COLLABORATOR -> COORDINATOR
   public abstract void sendCollaboratorReplyMessage(P2PDPCollaboratorReplyMessage cRepMessage, long timeToWait, int numMaxReplies);
       
}
