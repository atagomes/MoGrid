package martin.mogrid.globus.service.dispatcher;

import org.apache.log4j.Logger;
import org.globus.gram.GramException;
import org.globus.gram.GramJob;
import org.globus.gram.GramJobListener;
import org.ietf.jgss.GSSException;


public final class JobRequestControler extends JobRequestConfiguration implements Runnable { 
	
	class JobRequestMogridListener implements GramJobListener {

		 public void statusChanged(GramJob job) {
			if (job.getStatusAsString().compareToIgnoreCase("DONE") == 0) {
				state = AFTERRUN;
			}
	    }
		
	}
		
	 private static final int   INRUN        = 0;
	 private static final int   AFTERRUN     = 1;
	 private volatile int       state        = INRUN;
    private JobElements          jobElements  = null;
    private String               jobReturn    = null;
    private boolean             status       = false;
	  
    private static final Logger logger = Logger.getLogger( JobRequestControler.class );
	 
   
    public void setJobElements( JobElements jobEl ) {
       jobElements = jobEl;
    }
    
    public void jobRequest( JobElements jobEl ) throws JobSubmitionException {
      
       logger.info( "Starting job resquest in host: " + jobEl.getHost() );
      
		
       GramJob job = null;		
       if ( !startGassServer() )		 
          throw new JobSubmitionException( "Couldn't start the gass server" );		  
       initJobOutListeners();
	      
       try {		
          copy( jobEl );	
       } catch ( JobStageException e ) { 
          throw new JobSubmitionException( e );	
       }
       	    
       String rsl = createRsl( jobEl );	    
       logger.info( "Jop request RSL: " + rsl );
	      	    
       allowExecution( jobEl );
           
       try {
          if( rsl == null )	    	 
             throw new GramException();	     
          job = new GramJob( rsl );
          job.setCredentials( null );        
          job.addListener( new JobRequestMogridListener() );	    
            job.request( jobEl.getHost(), false );
         } catch ( GramException e ) {
            throw new JobSubmitionException("Erro in submiting the job" ); 
         } catch ( GSSException e ) {
            throw new JobSubmitionException("Credential problems" ); 
         }	  			
	    synchronized (this) {
	      try {  
	        while (state != AFTERRUN ) {
	      	  if(job.getStatusAsString().compareToIgnoreCase("FAILED") == 0 ){
	       		  throw new JobSubmitionException( "Job failed" );
	       	  }
	          wait(300);
	        }
	      }catch (InterruptedException e){
	    	  throw new JobSubmitionException( "Job interrupted" );
	      }
	    }
       logger.info( "Job result received: " + getStdoutReturn() );
	    //deleteJob( jobEl );	
       status = true;
	   }

   public void run() {
      try {
         jobRequest( jobElements );
      } catch (JobSubmitionException e) {
         e.printStackTrace();
      }
      jobReturn = getStdoutReturn();
   }
   
   public String getReturn() {
      return jobReturn;
   }

   public boolean getStatus() {
      return status;
   }
}


