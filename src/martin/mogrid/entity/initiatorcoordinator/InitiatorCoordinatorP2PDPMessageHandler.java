/*
 * Created on 19/04/2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package martin.mogrid.entity.initiatorcoordinator;

import martin.mogrid.common.resource.ResourceQuery;
import martin.mogrid.entity.coordinator.CollaboratorReplyListSchedulerChannel;
import martin.mogrid.p2pdl.api.RequestProfile;
import martin.mogrid.p2pdl.protocol.message.P2PDPMessageInterface;

public interface InitiatorCoordinatorP2PDPMessageHandler extends CollaboratorReplyListSchedulerChannel {

   //INITIATOR-COORDINATOR -> COLLABORATORS 
   public abstract void sendInitiatorRequestMessage(RequestProfile reqProfile, ResourceQuery resourceQuery);

   //INITIATOR-COORDINATOR <- COLLABORATOR
   public abstract void handlerCollaboratorReplyMessage(P2PDPMessageInterface protMessage);
  
   //COORDINATOR -> INITIATOR
   //CollaboratorReplyListSchedulerClient.sendCollaboratorReplyListMessage(P2PDPMessageInterface msg, String initiatorAddr);
   
}
