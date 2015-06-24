/**
 * @author lslima
 * 
 * Created on 15/03/2006
 */

package martin.mogrid.tl.asl.bagoftask;

import java.util.Vector;

import martin.mogrid.p2pdl.api.RequestIdentifier;


public class BoTResultHistory {

   private RequestIdentifier jobRequestID = null;
   private Vector            gridJobResultHistory;
   
   public BoTResultHistory(RequestIdentifier jobRequestID) {
      this.jobRequestID    = jobRequestID;
      gridJobResultHistory = new Vector();
   }
   

   public RequestIdentifier getJobRequestID() {
      return jobRequestID;
   }
   
   public void addElement(BoTResult result) {
      gridJobResultHistory.add(result);
   }
   
   public BoTResult getElementAt(int position) {
      return (BoTResult)gridJobResultHistory.elementAt(position);
   }
   
   public int size() {
      return gridJobResultHistory.size();  
   }

   public boolean isEmpty() {
      return gridJobResultHistory.isEmpty();  
   }
   
   public int containsJobRequest(String collabAddr, int jobID) {
      for ( int i=0; i<gridJobResultHistory.size(); i++ ) {
         BoTResult gridJobRes = (BoTResult)gridJobResultHistory.elementAt(i);
         String collab = gridJobRes.getCollaboratorIPAddr();
         int    job    = gridJobRes.getJobID();
         if ( collabAddr.equalsIgnoreCase(collab) && jobID==job ) { 
            return i;
         }
      }
      return -1;
   }
   
   public boolean canProcess() {
      for ( int i=0; i<gridJobResultHistory.size(); i++ ) {
         BoTResult gridJobRes = (BoTResult)gridJobResultHistory.elementAt(i);
         Object collab = gridJobRes.getResult();
         if ( collab == null ) { 
            return false;
         }
      }
      return true;
   }
   
}