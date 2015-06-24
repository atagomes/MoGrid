/*
 * Created on 05/05/2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package martin.mogrid.submission.task.bagoftask.protocol;

import java.io.File;
import java.util.Vector;

import martin.mogrid.common.network.UDPDatagramPacket;
import martin.mogrid.common.network.UDPDatagramPacketException;
import martin.mogrid.entity.dispatcher.Task;
import martin.mogrid.entity.dispatcher.TaskDispatcher;
import martin.mogrid.entity.dispatcher.TaskExecutor;

import org.apache.log4j.Logger;

public class BoTDispatcher implements TaskDispatcher {
   
   //Service log maintenance
   private static final Logger logger = Logger.getLogger(BoTDispatcher.class);
   
   private static final String BASEDIR = System.getProperty("mogrid.home") + File.separator + "gridjob";
   
   public Object execute(Task task) {
      Vector   files     = task.getFiles();
      String[] filesName = task.getFilesName();
      String   execCall  = task.getExecutable() + task.getArguments();
      int      jobID     = task.getJobIdentifier();
      
      Object  jobResult  = null;
      boolean canExecute = false;

      String execDirPath = BASEDIR + File.separator + task.getRequestIdentifier()+"-"+jobID;
      File execDir = null;
      //Save the remote file(s)      
      if ( files != null && files.size() > 0 ) {        
         canExecute = true;         
         //Verify if execDir exist, if necessary create it
         execDir = new File(execDirPath);
         if ( ! execDir.exists() ) {  execDir.mkdirs();  } 
         //Convert bytes array to files 
         int size = files.size();
         for ( int i=0; i<size; i++ ) {   
            String file = execDirPath + File.separator + filesName[i];
            byte[] in = (byte[])files.elementAt(i);
            //logger.debug("file["+i+"] " + file+" size: "+in.length); 
            try {
               File execFile = UDPDatagramPacket.convertByteArrayToFile(in, file);               
               if ( execFile == null || !execFile.exists() ) {
                  canExecute = false;
                  break;
               }
               
            } catch (UDPDatagramPacketException e1) {
               logger.error("UDPDatagramPacketException: " + e1.getMessage());
            }        
         }
      }     
      
      if ( canExecute ) {
         logger.debug("* EXEC CALL: "+ execCall + " EXECDIR: "+execDir.getAbsolutePath());
         TaskExecutor taskExecutor = new TaskExecutor(jobID, execCall, execDirPath);
         jobResult = taskExecutor.exec(); 
         String resultStr = jobResult!=null ? jobResult.toString() : " NULL ";
         logger.debug("** RESULT {"+jobID+" - "+task.getRequestIdentifier()+"}: "+ resultStr);  
      }
      if (  execDir != null && execDir.exists() ) {
         //logger.debug("* DELETE EXEC DIR : "+ execDir.getAbsolutePath());  
         File[] dirFiles = execDir.listFiles();
         for ( int i=0; i<dirFiles.length; i++ ) {
            dirFiles[i].delete();
            //logger.debug("* DELETE file : "+ dirFiles[i].getAbsolutePath()); 
         }
         execDir.delete(); 
      }

      return jobResult;
   }

}
