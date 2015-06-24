
package martin.mogrid.entity.initiator;

import martin.mogrid.common.resource.ResourceQuery;
import martin.mogrid.p2pdl.api.RequestProfile;
import martin.mogrid.p2pdl.protocol.message.P2PDPMessageInterface;

/**
 * @author luciana
 *
 * Created on 17/06/2005
 */
public interface InitiatorP2PDPMessageHandler {

   //INITIATOR <- COORDINATOR -> 
   public abstract void handlerCollaboratorReplyListMessage(P2PDPMessageInterface protMessage);
   
   //INITIATOR -> COORDINATOR 
   public abstract void sendInitiatorRequestMessage(RequestProfile reqProfile, ResourceQuery resourceQuery);
        
}
