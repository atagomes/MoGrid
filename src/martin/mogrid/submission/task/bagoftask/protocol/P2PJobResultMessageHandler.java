/*
 * Created on 23/10/2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package martin.mogrid.submission.task.bagoftask.protocol;

import martin.mogrid.submission.protocol.TaskSubmissionProtocol;
import martin.mogrid.submission.task.bagoftask.protocol.message.P2PBoTMessage;
import martin.mogrid.submission.task.bagoftask.protocol.message.P2PJobResult;

import org.apache.log4j.Logger;

public class P2PJobResultMessageHandler implements Runnable {

   // Manutencao do arquivo de log do servico
   private static final Logger logger = Logger.getLogger(P2PJobResultMessageHandler.class);
   
   private P2PJobResult           msgJobResult          = null;
   private TaskSubmissionProtocol botSubmissionProtocol = null;
   private Thread                 resultThread          = null;
   
   public P2PJobResultMessageHandler(P2PBoTMessage data, TaskSubmissionProtocol botSubmissionProtocol) {      
      this.msgJobResult          = (P2PJobResult) data;
      this.botSubmissionProtocol = botSubmissionProtocol; 
   }

   public void run() {
      logger.debug("< BoT Channel [receiving] P2PJobResult {JobID: "+msgJobResult.getJobID()+" - ReqID: "+ msgJobResult.getRequestID()+"} (RESULT: "+msgJobResult.getJobResult()+")");      
      botSubmissionProtocol.deliveryTaskResolution(msgJobResult);       
   }
   
}
