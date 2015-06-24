package martin.mogrid.submission.task.bagoftask.protocol.message;

import martin.mogrid.p2pdl.api.RequestIdentifier;
import martin.mogrid.submission.protocol.message.TaskSubmissionMessage;

public abstract class P2PBoTMessage implements TaskSubmissionMessage {

   private RequestIdentifier jobRequestID = null;
   private String            srcIPAddr    = null;
   private int               job;
   
   public P2PBoTMessage(RequestIdentifier jobRequestID, String srcIPAddr, int job) {
      this.jobRequestID = jobRequestID;
      this.srcIPAddr    = srcIPAddr;
      this.job          = job;
   }
   
   
   public void setRequestID(RequestIdentifier jobRequestID) {
      this.jobRequestID = jobRequestID;
   }
   
   public void setSenderIPAddr(String srcIPAddr) {
      this.srcIPAddr = srcIPAddr;   
   }

   public void setJobID(int job) {
      this.job = job;   
   }
   
   
   
   public RequestIdentifier getRequestID() {
      return jobRequestID;
   }
   
   public String getSenderIPAddr() {
      return srcIPAddr;
   } 
   
   public int getJobID() {
      return job;
   }
   
   
   public String toString() {
      String messageStr = "\nJob Request ID: "    + jobRequestID + 
                          "\nSource IP Address: " + srcIPAddr + 
                          "\nJob ID: "            + job; 
         
      return messageStr;
   } 

}
