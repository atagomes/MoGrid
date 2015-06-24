/*
 * Created on 29/09/2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package martin.mogrid.p2pdl.collaboration.scheduler;

import martin.mogrid.p2pdl.api.RequestIdentifier;
import martin.mogrid.p2pdl.protocol.message.P2PDPCollaboratorReplyMessage;

public interface CollaborationReplyScheduler {      
  
     public abstract void    schedule(P2PDPCollaboratorReplyMessage cRepMessage, long timeToWait, int numMaxReplies);
     public abstract boolean containsCRepScheduled(RequestIdentifier reqID);
     public abstract void    incNumCRepOverheard(RequestIdentifier reqID);
     public abstract boolean canDiscardCRepScheduled(RequestIdentifier reqID);
     public abstract void    cancelSchedule(RequestIdentifier reqID);
   
}
