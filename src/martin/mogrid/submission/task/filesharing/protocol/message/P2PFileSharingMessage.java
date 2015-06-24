package martin.mogrid.submission.task.filesharing.protocol.message;

import martin.mogrid.p2pdl.api.RequestIdentifier;
import martin.mogrid.submission.protocol.message.TaskSubmissionMessage;

public abstract class P2PFileSharingMessage implements TaskSubmissionMessage {

   private RequestIdentifier reqID;
   

   public P2PFileSharingMessage(RequestIdentifier reqID) {
      this.reqID   = reqID;
   }   
   
   public RequestIdentifier getRequestIdentifier() {
      return reqID;
   }

   public void setRequestIdentifier(RequestIdentifier reqID) {
      this.reqID = reqID;   
   }

   //IMPRESSAO
   public String toString() {
      String messageStr = "\nRequestIdentifier: " + reqID;         
      return messageStr;
   } 
   
}
