/*
 * Created on 05/05/2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package martin.mogrid.entity.dispatcher;

import java.util.Vector;

import martin.mogrid.p2pdl.api.RequestIdentifier;

public class Task {
   
   //proxy dispatcher arguments
   private String            collabAddr;    
   private RequestIdentifier reqID;
   private int               jobID;
   //common arguments
   private String            executable; 
   private String            execArguments; 
   private Vector            files;
   private String[]          filesName; 
   
   public Task(RequestIdentifier reqID, int jobID, String collabAddr, String executable, String execArguments, Vector files, String[] filesName) {
      this.reqID         = reqID;
      this.jobID         = jobID;
      this.collabAddr    = collabAddr;
      this.executable    = executable;
      this.execArguments = execArguments;
      this.files         = files;
      this.filesName     = filesName;
   }   
   
   public String getCollabAddress() {
      return collabAddr;
   }

   public RequestIdentifier getRequestIdentifier() {
      return reqID;
   }
   
   public int getJobIdentifier() {
      return jobID;
   }
   
   public String getExecutable() {
      //return parserExecutable(executable);
      return executable;
   }
   
   public String getArguments() {
      return execArguments;
   }
   
   public Vector getFiles() {
      return files;
   }
   
   public String[] getFilesName() {
      return filesName;
   }
   
}
