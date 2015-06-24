/**
 * @author lslima
 * 
 * Created on 22/03/2006
 */

package martin.mogrid.tl.asl.bagoftask;

import java.io.File;
import java.util.Vector;

import martin.mogrid.common.resource.ResourceQuery;
import martin.mogrid.p2pdl.api.RequestProfile;

public class BoTRequest {
   
   public static final String WAITING = "waiting";
   public static final String RUNNING = "running";
   public static final String PENDING = "pending";
   public static final String FINISH  = "finish";

   private int      reqSubmissions; 
   private int      numJobs; 
   private int      numMaxReplies; 
   private Vector   files; 
   private String   executable;
   private String[] execArguments;
   
   //Values needs for request resubmission
   private ResourceQuery  resourceQuery = null;
   private RequestProfile reqProfile    = null;
   
   private String[] jobPending;
   
   public BoTRequest(int reqSubmissions, int numJobs, int numMaxReplies, Vector files, String executable, String[] execArguments, ResourceQuery resourceQuery, RequestProfile reqProfile) {
      this.reqSubmissions = reqSubmissions;
      this.numJobs        = numJobs;
      this.numMaxReplies  = numMaxReplies;
      this.files          = files;
      this.executable     = executable;
      this.execArguments  = execArguments;
      this.resourceQuery  = resourceQuery;
      this.reqProfile     = reqProfile;
      
      jobPending = new String [numJobs];
      for ( int i=0; i<numJobs; i++ ) {
         jobPending[i] = WAITING;
      }
   }
   
   /*public BoTRequest(int reqSubmissions, int numJobs, Vector files, String executable, String[] execArguments, ResourceQuery resourceQuery, RequestProfile reqProfile) {
      this(reqSubmissions, numJobs, numJobs, files, executable, execArguments, resourceQuery, reqProfile);       
   }*/
   
   //When consulted it decreases the number of resubmissions in each sending
   public boolean canSubmit() {
      if ( (reqSubmissions--) > 0 ) {
         return true;
      }
      return false;
   }
   
   public int getNumJobs() {
      return numJobs;
   }

   public int getNumMaxReplies() {
      return numMaxReplies;
   }
   
   public File[] getJobFiles(int job) {
      File [] jobFiles = null;
      if ( files != null && job >= 0 && job < files.size() ) {         
         jobFiles = (File[])files.get(job);
      }
      return jobFiles;
   }
   
   public String[] getExecArguments() {
      return execArguments;
   }

   public String getExecutable() {
      return executable;
   }
   
   public ResourceQuery getResourceQuery() {
      return resourceQuery;
   }
   
   public RequestProfile getRequestProfile() {
      return reqProfile;
   }
 
   
   
   public void setGridJobStatus(int index, String status) {
      jobPending[index] = status;
   }

   public String getGridJobStatus(int index) {
      return jobPending[index];
   }
   public boolean isGridJobPending(int index) {
      return jobPending[index].equalsIgnoreCase(PENDING);
   }
   public boolean isGridJobWaiting(int index) {
      return jobPending[index].equalsIgnoreCase(WAITING);
   }
   public boolean isGridJobRunning(int index) {
      return jobPending[index].equalsIgnoreCase(RUNNING);
   }
   public boolean isGridJobFinish(int index) {
      return jobPending[index].equalsIgnoreCase(FINISH);
   }
   
}