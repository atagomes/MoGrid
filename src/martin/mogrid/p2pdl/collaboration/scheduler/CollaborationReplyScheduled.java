package martin.mogrid.p2pdl.collaboration.scheduler;

import java.util.Timer;

public class CollaborationReplyScheduled {     

   private Timer replyScheduled;
   private int   numReplies;
   
   public CollaborationReplyScheduled(Timer repScheduled, int numMaxReplies) {
      this.replyScheduled = repScheduled;
      this.numReplies     = numMaxReplies;
   }
   
   public Timer getReplyScheduled() {
      return replyScheduled;
   }
   
   public boolean removeReplyScheduled() {
      if ( replyScheduled!= null ) {
         replyScheduled.cancel(); 
         replyScheduled = null;
         return true;
      }
      return false;
   }
   
   public void incNumRepliesListened() {
      // numReplies is set to MAX_NUM_REPLIES waited then inc number of replies listened 
      // corresponds to decrease the numReplies (MAX:MAX_NUM_REPLIES -> MIN:0)
      numReplies--;
   }

   //First increment numReplies
   public boolean canDiscardReplyScheduled() {
      //EX: Num Max Replies = 2:
      //    ->CRep1: 2-1 =  1 > 0  wait to send myCRep
      //    ->CRep2: 1-1 =  0 > 0  discard myCRep
      return ( numReplies > 0 ? false : true ); 
   }
   
   // Necessario reimplementar os metodos equals() e hashCode() pois a classe eh usada como 
   // chave em Hashtable
   public boolean equals(Object obj) {
      // Step 1: this.equals(null) should return false
      if ( obj == null ) {
         return false;
      }
      // Step 2: Perform an == test (reflexive)
      if ( this == obj ) { 
          return true;
      }
      // Step 3: Instance of check
      if( obj instanceof CollaborationReplyScheduled ) { 
         // Step 4: For each important field, check to see if they are equal
         // For primitives use ==
         // For objects use equals() but be sure to also handle the null case first
         CollaborationReplyScheduled remoteRequest = (CollaborationReplyScheduled) obj;           
         if ( replyScheduled.equals(remoteRequest.replyScheduled) && 
              numReplies == remoteRequest.numReplies ) 
         {
            return true;
         }
         return false;
      } 
      return false;
   }

   public int hashCode() {
      // Always return the same value for each object because you always return the 
      // same value for all objects. You also return identical hashCode values when
      // 2 objects test as equals because you always return identical hashCode values. 
      // There is no requirement to return different hashCode values when
      // two objects test as not equal.
      
      //Turn the object's fields into a string, concatenate the strings,
      //and return the resulting hashcode multiply with prime
      String concat = replyScheduled.toString() + numReplies;
      return concat.hashCode()*3;
   }
   
}
