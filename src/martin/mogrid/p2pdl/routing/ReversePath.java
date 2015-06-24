/*
 * Created on 18/08/2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package martin.mogrid.p2pdl.routing;

import java.util.Vector;



public class ReversePath { 
   
   private String  returnPath  = null;  // Next-hop to reach (Initiator address)
   private Vector  collabsAddr = null;  // List of all collabs tha replied the request
   private int     replyCount  = 0;     // Number of replies listened + sent for the requestID
   private int     numHops     = 0;     // Number of hops to reqID originator
   private int     reqDiameter = 0;     // Max number of hops that a request can be forward
    
   //TODO trocar todos os tipos INT associados a NumberOfReplies e RepliesCount to SHORT
   //OBS: The short data type is a 16-bit signed two's complement integer. 
   //     It has a minimum value of -32,768 and a maximum value of 32,767 (inclusive).
   
   public ReversePath(String nextHop, int replyCount, int numHops, int reqDiameter) {
      this.returnPath  = nextHop;
      this.replyCount  = replyCount;  // initialized with the REQUEST_DIAMETER
      this.numHops     = numHops;
      this.reqDiameter = reqDiameter;
      collabsAddr      = new Vector();
   }

   public boolean replyIsDuplicate(String collabAddr) {
      return collabsAddr.contains(collabAddr);
   }

   public void addCollab(String collabAddr) {
      collabsAddr.add(collabAddr);
   }

   public void decReplyCount() {
      replyCount--;
   }

   //First 'decrement' replyCount
   public boolean canForwardReply() {
      //EX: Num Max Replies = 2:
      //    ->CRep1: 2-1 =  1 >= 0  >>CRep
      //    ->CRep2: 1-1 =  0 >= 0  >>CRep
      //    ->CRep3: 0-1 = -1 >= 0 !>>CRep
      //
      //    numHops = reqDiameter: nodes are direct neighbors of initiator
      //return ( (( replyCount >= 0 ) && ( numHops >= reqDiameter )) ? true : false );
      return ( replyCount >= 0 ? true : false );
   }

   public boolean canDiscardReply() {
      //EX: numHops = reqDiameter: nodes are direct neighbors of initiator
      return ( numHops >= reqDiameter ? true : false );
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
