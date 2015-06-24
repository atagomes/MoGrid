/*
 * Created on 27/04/2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package martin.mogrid.entity.coordinator;

import martin.mogrid.p2pdl.protocol.P2PDPConnectionException;
import martin.mogrid.p2pdl.protocol.message.P2PDPMessageInterface;

public interface CollaboratorReplyListSchedulerChannel {

   public abstract void sendCollaboratorReplyListMessage(P2PDPMessageInterface msg, String initiatorAddr) throws P2PDPConnectionException;   
   
}
