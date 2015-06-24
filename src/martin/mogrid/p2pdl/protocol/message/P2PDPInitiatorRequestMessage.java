
package martin.mogrid.p2pdl.protocol.message;

import martin.mogrid.common.context.ContextInformation;
import martin.mogrid.common.resource.ResourceQuery;
import martin.mogrid.p2pdl.api.RequestProfile;

/**
 * @author   luciana
 */
public class P2PDPInitiatorRequestMessage extends P2PDPMessage {

   /** Comment for <code>serialVersionUID</code> */
   private static final long serialVersionUID = -1609612412542975154L;

   public static final int DEFAULT_REQ_DIAMETER = 0;  // 0:           single-hop (forward only to neighbors)
                                                      // 0 < n < 255: multi hop  (forward by n hops)
                                                      // 255:         multi-hop  (forward for all) 

   private ContextInformation  ctxtInfo;
   private ResourceQuery       resourceQuery;
   private long                maxReplyDelay;
   private int                 numMaxReplies;
   private int                 maxTTL = DEFAULT_REQ_DIAMETER;  // default: single-hop
 
   
   public P2PDPInitiatorRequestMessage() {
      super(CP_MSG_IREQ);
   }
   
   public P2PDPInitiatorRequestMessage(ResourceQuery resourceQuery, long maxReplyDelay, int numMaxReplies, int ttl, ContextInformation ctxtInfo) {
      super(CP_MSG_IREQ, resourceQuery.getRequestIdentifier());

      this.resourceQuery = resourceQuery;
      this.maxReplyDelay = maxReplyDelay;
      this.numMaxReplies = numMaxReplies;
      this.maxTTL        = ttl;       
      this.ctxtInfo      = ctxtInfo;
   } 
   
   public P2PDPInitiatorRequestMessage(ResourceQuery resourceQuery, RequestProfile reqProfile) {
      super(CP_MSG_IREQ, resourceQuery.getRequestIdentifier());
      
      this.resourceQuery = resourceQuery;
      this.maxReplyDelay = reqProfile.getNumMaxReplyDelay();
      this.numMaxReplies = reqProfile.getNumMaxReplies();
      this.maxTTL        = reqProfile.getRequestDiameter();
      this.ctxtInfo      = reqProfile.getContextInfo();
   }
   
   
   
   //LEITURA
   public long getMaxReplyDelay() {
      return maxReplyDelay;
   }
   
   public int getNumMaxReplies() {
      return numMaxReplies;
   }   

   public int getRequestDiameter() {
      return maxTTL;
   }

   public ContextInformation getContextInfo() {
      return ctxtInfo;
   }
   
   public ResourceQuery getResourceQuery() {
      return resourceQuery;
   }
      
   
   //ATRIBUICAO
   public void setMaxReplyDelay(long maxReplyDelay) {
      this.maxReplyDelay = maxReplyDelay;
   }
   
   public void setNumMaxReplies(int numMaxReplies) {
      this.numMaxReplies = numMaxReplies;
   }
      
   public void setContextInfo(ContextInformation ctxtInfo) {
      this.ctxtInfo = ctxtInfo;
   }
   
   public void setResourceQuery(ResourceQuery resourceQuery) {
      this.resourceQuery = resourceQuery;
   }
   
   
   
   //TESTE de encaminhamento   
   //-> Verifico o diametro da requisicao (TTL)
   public boolean canForwardMessage() {
      return ( (getHopCount() <= maxTTL) ? true : false );
   }
 
   
   
   //IMPRIME
   public String toString() {
      String messageStr = super.toString() +
                          "\nMaximum Reply Delay: " + maxReplyDelay +
                          "\nNumber Maximum of Replies: " + numMaxReplies + 
                          "\nRequest Diameter: " + maxTTL +   
                          resourceQuery.toString() +
                          ctxtInfo.toString();
         
      return messageStr;
   }     
   
}
