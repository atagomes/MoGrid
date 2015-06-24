package martin.mogrid.submission.protocol;

import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;

import martin.mogrid.submission.protocol.message.TaskSubmissionMessage;

import org.apache.log4j.Logger;


// Classe responsavel por ouvir o canal de compartilhamento
public abstract class TaskSubmissionProtocol implements Runnable { 
  
   //Manutencao do arquivo de log do servico
   private static final Logger logger = Logger.getLogger(TaskSubmissionProtocol.class);    
  
   private int    submissionPort; 
   private String title;
   
   //Protocolo para submissao de tarefas
   private TaskSubmissionChannel  connection           = null;
   private TaskSubmissionListener listener             = null;
   private Thread                 taskSubmissionThread = null; 
   
   
   
   public TaskSubmissionProtocol(String title, TaskSubmissionListener listener, int protPort) {
      this.title     = title;
      this.listener  = listener;      
      submissionPort = protPort;
      configure();
   }

   
   public TaskSubmissionProtocol(String title, TaskSubmissionListener listener) {
      TaskSubmissionProtocolProperties.load();
      this.title     = title;
      this.listener  = listener;  
      submissionPort = TaskSubmissionProtocolProperties.getSubmissionPort(); 
      configure();
   } 
   
   private void configure() {
      InetSocketAddress addrToSend    = new InetSocketAddress(submissionPort); 
      InetSocketAddress addrToReceive = new InetSocketAddress(submissionPort);
      try {   
         connection = new TaskSubmissionChannel();
         connection.configure(addrToSend, addrToReceive, title);
         connection.open(); 
      } catch (TaskSubmissionException e) {
         logger.error("There are not " + title + " support for this application." + e.getMessage());         
      }                     
      logger.info("Task Submission Channel created."); 
   }   
   
   public void start() { 
      if( taskSubmissionThread == null ) {
         taskSubmissionThread = new Thread(this, "TaskSubmission");
         taskSubmissionThread.start();
      }          
   }
    
   public synchronized void stop() { 
      if( connection != null ) {
         connection.close(); 
      } 
      taskSubmissionThread = null;
   }
   
   public boolean isAlive(Thread protocolThread) {
      return ( taskSubmissionThread == protocolThread ? true : false );
   }
      
   public void send(TaskSubmissionMessage msg, String addrToSend) {
      try {
         connection.send(msg, addrToSend);
         
      } catch (TaskSubmissionException e) {
         logger.error("Error sending Task Submission Request to " + addrToSend + "\n[ERROR] " + e.getMessage());
      }
   }
   
   public TaskSubmissionMessage receive() {
      TaskSubmissionMessage msg = null;
      try {
         msg = connection.receive();
         
      } catch (SocketTimeoutException stex) {
         //It doesn´t anything, exception was thow because a value >0 (0=infinite) was atributed a socket.SO_TIMEOUT
         //to evit that the receive method blocks until a datagram was received.
        
      } catch (TaskSubmissionException tsex) {
         if ( ! connection.receiverIsClosed() ) {
            logger.warn("It was not possible to receive Task Submission Message: " + tsex.getMessage());
         }
      }        
      return msg;
   }
   
   
   public void deliveryTaskResolution(Object taskResolution) {
      if ( listener != null ) {
         listener.receiveTaskResolution(taskResolution);
      }
   }
   
}
