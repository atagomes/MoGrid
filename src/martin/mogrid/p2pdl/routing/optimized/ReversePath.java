/*
 * Created on 18/08/2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package martin.mogrid.p2pdl.routing.optimized;


public class ReversePath {
   
   private String  returnPath  = null;  // Next-hop to reach (Initiator address)
   private int     replyCount  = 0;     // Number of replies listened + sent for the requestID
   private int     numHops     = 0;     // Number of hops to reqID originator
    
   //TODO trocar todos os tipos INT associados a NumberOfReplies e RepliesCount to SHORT
   //OBS: The short data type is a 16-bit signed two's complement integer. 
   //     It has a minimum value of -32,768 and a maximum value of 32,767 (inclusive).
   
   public ReversePath(String nextHop, int replyCount, int numHops) {
      this.returnPath  = nextHop;
      this.replyCount  = replyCount;  // initialized with the REQUEST_DIAMETER
      this.numHops     = numHops;
   }
   

   public void decReplyCount() {
      replyCount--;
   }
   
   public boolean canForwardReply() {
      return ( replyCount >= 0 ? true : false );
   }
   
   public String getReturnPath() {
      return returnPath;
   }
   
   public int getReplyCount() {
      return replyCount;
   }  
   
   public int getNumHops() {
      return numHops;
   }  
   
   
   public String toString() {
      String reversePathEntrySTR = "";
      reversePathEntrySTR += "Return Path: " + returnPath + ", ";
      reversePathEntrySTR += "Reply Count: " + replyCount + ", ";
      reversePathEntrySTR += "Number of Hops: " + numHops;
      
      return reversePathEntrySTR;      
   }
   
}
