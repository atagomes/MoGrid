package martin.mogrid.p2pdl.api;

import martin.mogrid.common.context.ContextInformation;

public class RequestProfile {      
   
   private ContextInformation ctxtInfo;
   private int                numMaxReplies;    // 0: infinito
   private long               maxReplyDelay;    // 0: infinito
   private int                requestDiameter;  // 0:           single-hop (forward only to neighbors)
                                                // 0 < n < 255: multi hop  (forward by n hops)
                                                // 255:         multi-hop  (forward for all)   
   
   
   public RequestProfile(ContextInformation ctxtInfo, int numMaxReplies, long maxReplyDelay, int diameter) {
      this.ctxtInfo        = ctxtInfo;      
      this.numMaxReplies   = numMaxReplies;
      this.maxReplyDelay   = maxReplyDelay;
      this.requestDiameter = diameter;
   }
   
   //LEITURA
   public ContextInformation getContextInfo() {
      return ctxtInfo;
   }
   
   public int getNumMaxReplies() {
      return numMaxReplies;
   }
   
   public long getNumMaxReplyDelay() {
      return maxReplyDelay;
   }

   public int getRequestDiameter() {
      return requestDiameter;
   }
   
   //ATRIBUICAO
   public void setContextInfo(ContextInformation ctxtInfo) {
      this.ctxtInfo = ctxtInfo;
   }
   
   public void setNumMaxReplies(int numMaxReplies) {
      this.numMaxReplies = numMaxReplies;
   }
   
   public void setNumMaxReplyDelay(long maxReplyDelay) {
      this.maxReplyDelay = maxReplyDelay;
   }

   public void setRequestDiameter(int numHops) {
      this.requestDiameter = numHops;
   }
   
   //IMPRIME
   public String toString() {
      String messageStr = "\nMaximum Reply Delay: " + maxReplyDelay +
                          "\nNumber Maximum of Replies: " + numMaxReplies + 
                          "\nRequest Diameter: " + requestDiameter + 
                          ctxtInfo.toString();
         
      return messageStr;
   }
}
