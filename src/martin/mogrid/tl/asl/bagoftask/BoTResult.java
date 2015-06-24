/**
 * @author lslima
 * 
 * Created on 22/03/2006
 */

package martin.mogrid.tl.asl.bagoftask;

public class BoTResult {
   
   private int     jobRequestID;
   private String  collaboratorIPAddr = null;
   private Object  jobResult          = null;
   
   public BoTResult(int jobRequestID, String collaboratorIPAddr, Object jobResult) {
      this.jobRequestID       = jobRequestID;
      this.collaboratorIPAddr = collaboratorIPAddr;
      this.jobResult          = jobResult;
   }
   
   public int getJobID(){
      return jobRequestID;
   }
   
   public String getCollaboratorIPAddr() {
      return collaboratorIPAddr;
   }
   
   public Object getResult() {
      return jobResult;
   }
   
   
   public void setResult(Object jobResult) {
      this.jobResult = jobResult;
   }

}

