package martin.mogrid.globus.service.dispatcher;

import java.io.File;

public class JobElements {
  
    private String executable     = null;
    private File[] files          = null;
    private String arguments      = null;
    private String host           = null;
    private String jobId          = null;
    
    public JobElements() {
    	
    }
    public JobElements(String executable, File[] files, String arguments, String host) {
       this.executable = executable;
       this.files = files;
       this.arguments = arguments;
       this.host = host;
    }
    
    public JobElements(String executable, File[] files, String arguments, String host, String jobId) {
       this.executable = executable;
       this.files = files;
       this.arguments = arguments;
       this.host = host;
       this.jobId = jobId;
    }
       
    public void setExecutable( String executable ){
       this.executable = executable;
    }

    public String getExecutable(){
    	return executable.trim();
    }
  
    public void setFiles ( File[] files ) {
    	this.files = files;
    }

    public File[] getFiles(){
		return files;		
    }

	 public String getArguments() {
       return arguments;
    }
	
	 public void setArguments(String arguments) {
	    this.arguments = arguments;
	 }

	 public String getHost() {
	    return host;
	 }

	 public void setHost(String host) {
	    this.host = host;
	 }
   
	 public String toString() {
	    String filesString = "";
	    String toString = "\n[ JobElements ]" +
	                      "\nExecutable: " + executable +
                         "\nArguments: " + arguments +
                         "\nHost: " + host;
	    for( int i = 0; i < files.length - 1; i++ ) {
         filesString += files[i] + ", ";
	    }
      
       filesString += files[files.length - 1];
      
       return toString + "\nFiles: " + filesString +" \n[ JobElements ]\n";
	 }

    public String getJobId() {
       return jobId;
    }

    public void setJobId(String jobId) {
       this.jobId = jobId;
    }
}
