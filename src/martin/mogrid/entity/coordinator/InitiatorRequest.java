package martin.mogrid.entity.coordinator;



public class InitiatorRequest {

   private CollaboratorReplyListScheduler cRepListScheduler = null;
   private int                            numMaxReplies     = 0;
   private int                            numReplies        = 0;
   
   public InitiatorRequest(CollaboratorReplyListScheduler cRepListScheduler, int numMaxReplies) {
      this.cRepListScheduler = cRepListScheduler;
      this.numMaxReplies     = numMaxReplies;
   }

   public CollaboratorReplyListScheduler getCRepListScheduler() {
      return cRepListScheduler;
   }
   
   public int getNumMaxReplies() {
      return numMaxReplies;
   }

   public void incNumRepliesListened() {
      numReplies++;
   }
   
   public boolean discardRequestScheduled() {
      if ( numReplies > numMaxReplies) {
         return true;
      }
      return false;
   }
   
}
