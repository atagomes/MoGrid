/**
 * @author lslima
 * 
 * Created on 10/04/2006
 */

package martin.mogrid.entity.dispatcher.globus;


import java.io.File;
import java.util.Vector;

import martin.mogrid.common.network.UDPDatagramPacket;
import martin.mogrid.common.network.UDPDatagramPacketException;
import martin.mogrid.entity.dispatcher.Task;
import martin.mogrid.entity.dispatcher.TaskDispatcher;
import martin.mogrid.globus.service.dispatcher.JobElements;
import martin.mogrid.globus.service.dispatcher.JobRequestControler;

import org.apache.log4j.Logger;

public class ProxyCollaboratorDispatcher implements TaskDispatcher {
  
   //Manutencao do arquivo de log do servico
   private final Logger logger = Logger.getLogger(this.getClass()); 
   private Object jobReturn;
   
   // Enviar a tarefa para a grade fixa e retornar o resultado para
   // o iniciador da grade movel
   public Object execute(Task task) {
      logger.debug( "Execute: " + task.getRequestIdentifier() );
      logger.debug( "Collaborator: " + task.getCollabAddress() );
      Vector   bytesArray = task.getFiles();
      String[] filesName  = task.getFilesName();
      String reqID = task.getRequestIdentifier().getRequestIdentifier();
      File directory = new File( reqID );
      if( !directory.exists() )
         directory.mkdir();
      

      //Recupera os arquivos relacionados a execucao da tarefa
      int size = filesName.length;
      File[] files = new File[size];
      synchronized( files ) {
         if ( bytesArray!=null && filesName!=null && size > 0 ) {
            for ( int i=0; i<size; i++ ) {   
               String fileName = filesName[i];
               byte[] bytes = (byte[])bytesArray.elementAt(i);
               logger.debug("file["+i+"] " + fileName+" size: "+bytes.length); 
               try {
                  files[i] = UDPDatagramPacket.convertByteArrayToFile(bytes, directory + File.separator + fileName);              
               } catch (UDPDatagramPacketException e1) {
                  //Caso ocorra erro ao recuperar algum arquivo, nao eh possivel submeter a tarefa para a grade
                  logger.warn("UDPDatagramPacketException: " + e1.getMessage());
                  logger.debug("Retorno Null");
                  return null;
               }        
            }
         }
      }

      // Submete a tarefa para a grade fixa
      String executable    = task.getExecutable();
      String execArguments = task.getArguments();
      String collabAddr    = task.getCollabAddress();   
      
      //JobRequest  job         = new JobRequest();
      Thread jobThread = null;
      JobRequestControler job = new JobRequestControler();
      String jobId = task.getRequestIdentifier().toString();
      JobElements jobElements = new JobElements(executable, files,execArguments,  collabAddr, jobId );
      job.setJobElements( jobElements );
      
      logger.debug( "Proxy Dispatcher Thread..." );
      jobThread = new Thread( job );
      jobThread.start();
      
      synchronized( jobThread ) {
      //synchronized( job ) {
         while( !job.getStatus() ) {
            try {
               jobThread.wait( 300 );
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
         }
      }
      
      /*try {
         job.jobRequest( jobElements );
      } catch (JobSubmitionException e) {
         e.printStackTrace();
      }*/
      
      logger.debug(" Resquest Identifier: "+ task.getRequestIdentifier() +" Retorno:" + job.getReturn());      
      //deleteFiles( directory, files );
      jobReturn = job.getReturn();
      return job.getReturn();
   }
  
   public static void main(String[] args) {
      new ProxyCollaboratorDispatcher();
   }
   
   private void deleteFiles( File directory, File[] files ) {
      //files = directory.listFiles();
      for( int i = 0; i < files.length; i++ ) {
         files[i].delete();
      }
      if( directory.listFiles() == null )
         directory.delete();
   }
   
   public Object getJobReturn() {
      return jobReturn;
   }
   
}

