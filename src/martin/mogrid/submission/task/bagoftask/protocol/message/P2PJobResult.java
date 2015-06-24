package martin.mogrid.submission.task.bagoftask.protocol.message;

import martin.mogrid.common.network.LocalHost;
import martin.mogrid.p2pdl.api.RequestIdentifier;


public class P2PJobResult extends P2PBoTMessage {
   
   /** Comment for <code>serialVersionUID</code> */
   private static final long serialVersionUID = -8460040490173007766L;

   private Object jobResult;
   
   //CONSTRUTOR   
   public P2PJobResult(RequestIdentifier requestID, String collabIPAddr, int job, Object jobResult) {
      super(requestID, collabIPAddr, job);
      this.jobResult = jobResult;
   }
   
   public P2PJobResult(RequestIdentifier jobRequestID, int job, Object jobResult) {
      this(jobRequestID, LocalHost.getLocalHostAddress(), job, jobResult);
   }   
   
   
   //LEITURA   
   public Object getJobResult() {
      return jobResult;
   }
   
   //ATRIBUICAO   
   public void setJobResult(Object jobResult) {
      this.jobResult = jobResult;   
   }
   
   //IMPRESSAO
   public String toString() {
      String messageStr = "[P2PJobResult]" +
                           super.toString()  + 
                          "\nJob Result: "   + jobResult!=null ? jobResult.toString() : " NULL "; 
         
      return messageStr;
   } 
   
}
