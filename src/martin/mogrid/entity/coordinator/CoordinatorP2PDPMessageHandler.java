
package martin.mogrid.entity.coordinator;

import martin.mogrid.p2pdl.protocol.message.P2PDPMessageInterface;

/**
 * @author luciana
 *
 * Created on 17/06/2005
 */
public interface CoordinatorP2PDPMessageHandler extends CollaboratorReplyListSchedulerChannel {

   //COORDINATOR <- INITIATOR
   public abstract void handlerInitiatorRequestMessage(P2PDPMessageInterface protMessage);

   //COORDINATOR <- COLLABORATOR
   public abstract void handlerCollaboratorReplyMessage(P2PDPMessageInterface protMessage);
   
   //COORDINATOR -> INITIATOR
   //CollaboratorReplyListSchedulerClient.sendCollaboratorReplyListMessage(P2PDPMessageInterface msg, String initiatorAddr);
   
}
