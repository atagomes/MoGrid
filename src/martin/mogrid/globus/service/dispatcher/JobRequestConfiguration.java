 package martin.mogrid.globus.service.dispatcher;


import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.globus.ftp.GridFTPClient;
import org.globus.ftp.Session;
import org.globus.ftp.exception.ClientException;
import org.globus.ftp.exception.ServerException;
import org.globus.gram.GramJob;
import org.globus.io.gass.server.GassServer;
import org.globus.io.gass.server.JobOutputListener;
import org.globus.io.gass.server.JobOutputStream;
import org.gridforum.jgss.ExtendedGSSManager;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;

public abstract class JobRequestConfiguration {
   
   class JobRequestOutputListenerStdout implements JobOutputListener {

      public synchronized void  outputChanged( String output ) {
             stdoutReturn = output;
       }

      public void outputClosed() {
         
      }
      
   }
   
   class JobRequestOutputListenerStderr implements JobOutputListener {

      public synchronized void  outputChanged( String output ) {
          stderrReturn = output;
       }

      public void outputClosed() {
         
      }
      
   }
   
     private        GassServer       gassServer   = null; 
     private        String           gassURL      = null;
     private        JobOutputStream  stdoutStream = null;
     private        JobOutputStream  stderrStream = null;
     private        String           jobId        = null; 
     private        GSSCredential    cred         = null;
     private        String           stdoutReturn    = null;
     private        String           stderrReturn    = null;
     
    private static final Logger logger = Logger.getLogger(JobRequestConfiguration.class); 
   
    protected abstract void jobRequest( JobElements jobEl ) throws JobSubmitionException;
      
    protected boolean startGassServer() { 
       
       ExtendedGSSManager manager = (ExtendedGSSManager) ExtendedGSSManager.getInstance();
       
       try {
          cred = manager.createCredential(GSSCredential.INITIATE_AND_ACCEPT);
          }
        catch ( GSSException ee ) {
          logger.error("Error in credential definition!");
          return false;
        }
        
        if (gassServer != null)
          return true;
        try {
          gassServer = new GassServer( cred, 0 );
          gassURL = gassServer.getURL();
        }
        catch (Exception e) {
            logger.error("Error in GassServer initialization!");
            e.printStackTrace();
            return false;
        }
         gassServer.registerDefaultDeactivator();
       return true;
     }
    
    protected void initJobOutListeners() {
       
       if (stdoutStream != null)
          return;
        stdoutStream = new JobOutputStream( new JobRequestOutputListenerStdout() );
        stderrStream = new JobOutputStream( new JobRequestOutputListenerStderr() );
        jobId = String.valueOf( System.currentTimeMillis() );
         
        gassServer.registerJobOutputStream( "err-" + jobId, stderrStream );
        gassServer.registerJobOutputStream( "out-" + jobId, stdoutStream );
        return;
    }
 
    public String createRsl( JobElements jobElements ) {       
       String executable = jobElements.getExecutable();
       String arguments  = jobElements.getArguments();
       String srcDir     = jobElements.getJobId();
       String directory = "";
       
       if( srcDir != null)
          directory = "(directory="+srcDir+")";
       
       String args = "";
       
       if( arguments != null ) {
          args = " (arguments=" + arguments + ")";
       }
       
          
       
       String rsl = "&(executable=" + executable + ")" + directory + args +
                    " (stdout=" + gassURL + "/dev/stdout-" + jobId + ")" +
                    " (stderr=" + gassURL + "/dev/stderr-" + jobId + ")";    
       
       return rsl;    
    }
   
   public String getStdoutReturn(){
      return stdoutReturn;
   }
   
   public String getStderrReturn() {
      return stderrReturn;
   }
   
   public void copy( File[] files, String hostTo, String jobId ) throws JobStageException {
       
       boolean isConnected = false;
       String dir = null;
       String host     = null;
       GassServer gass;
       try {
          gass = new GassServer();
          host = gass.getHostname();
       } catch (IOException e) {
          throw new JobStageException("Couldn't get the host name", e );
       }
       
       logger.info( "Starting file stage" );
       
       for(int i=0;i<files.length;i++) {          
          GridFTPClient origin      = null;
          GridFTPClient destination = null;
          
          try {             
             logger.info( "Origin: " + host );
             origin = new GridFTPClient( host, 2811 );
             origin.authenticate(null);
             origin.setType(Session.TYPE_IMAGE);
              
             logger.info( "Destination: " + hostTo );
             destination = new GridFTPClient(hostTo,2811);
             destination.authenticate(null);
             
             dir = destination.getCurrentDir();
             if ( jobId != null )
                dir += File.separator + jobId ;
             if( !destination.exists( dir ) )
                destination.makeDir( dir );
             if( dir.equals("/") ) { //Quando o uduário não tem home dir a / é default
                logger.warn( "Couldn't find the user home dir: " + dir);
                throw new IOException( "Couldn't find the user home dir" );
             }
             destination.setType(Session.TYPE_IMAGE);
              
             String file = files[i].getName();
             String remoteSrcFile = files[i].getAbsolutePath();
             String remoteDstFile = dir+File.separator+file;           
              
             logger.info( "Origin file "+remoteSrcFile );
             logger.info( "Destination file: "+remoteDstFile );
             
             if( destination.exists(remoteDstFile) ) {
                logger.info( "File already exists!" );
                continue;
             }
             origin.transfer( remoteSrcFile, destination, remoteDstFile, false, null );
             isConnected = true;
             
           } catch ( ServerException e ) {
              throw new JobStageException( "Problems in the server", e );
           } catch ( IOException e ) {
              throw new JobStageException( "Problems while copying the file", e );
           } catch ( ClientException e ) {
              throw new JobStageException( "Problems in the client", e );
           } finally {
              try {
                 if( isConnected ) {
                    if ( origin != null )  
                       origin.close();
                    if ( destination != null ) 
                       destination.close();
                 }
            } catch (ServerException e) {
               throw new JobStageException( "Couldn't close the server", e );
            } catch (IOException e) {
               throw new JobStageException( e );
            }
          }           
       }
       logger.info( "File stage successful" );
   }

   public void copy( JobElements jobEl ) throws JobStageException {
      File[] files = jobEl.getFiles();
      String hostTo   = jobEl.getHost();
      String jobId = jobEl.getJobId();
      
      copy( files, hostTo, jobId );     
   }
   
   public void allowExecution( File[] files, String host, String jobId ) throws JobSubmitionException {
       
      logger.info( "Starting file permission change" );
      GramJob job        = null;
      String directory = ""; 
      
      if ( jobId != null) {
         directory = "(directory="+jobId +")";         
      }
      for( int i = 0; i < files.length; i++ ) {
         
         String rsl = "&(executable =/bin/chmod)" +directory+
                      "(arguments= \"+x\" " + files[i].getName() + ")";
         
         logger.info( "Permission RSL: "+rsl );
         
         job = new GramJob( rsl );
     
         try {
            job.request( host, false );
         }catch( Exception e ) { 
            throw new JobSubmitionException ("Error in changing the permition of the job", e );       
         }
      }
      logger.info( "Permission change successful" );
   }
   
   public void allowExecution( JobElements jobEl ) throws JobSubmitionException {
      String  host       = jobEl.getHost();
      File[] files = jobEl.getFiles();
      String jobId = jobEl.getJobId();
      
      allowExecution( files, host, jobId ); 
   }
   
   public void deleteFilesStaged( JobElements jobElements ) throws JobSubmitionException {
      logger.info( "Starting file remotion" );
      File[] files = jobElements.getFiles();
      String host = jobElements.getHost();
      
      String args = "(arguments=";
      for( int i = 0; i < files.length; i++ ) {
         args += "\"" + files[i].getName() + "\"" + " ";
      }
      
      String rsl = "&(executable=/bin/rm)" + args + ")";      
      GramJob job = new GramJob( rsl );
      try {
         job.request( host, false );
      } catch ( Exception e ) {
         throw new JobSubmitionException( "Erron in deleting the job", e );
      }
      logger.info( "File remotion successful" );
   }
}


