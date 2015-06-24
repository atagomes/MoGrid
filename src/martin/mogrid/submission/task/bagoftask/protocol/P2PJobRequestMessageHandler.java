/*
 * Created on 23/10/2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package martin.mogrid.submission.task.bagoftask.protocol;

import java.util.Vector;

import martin.mogrid.common.network.LocalHost;
import martin.mogrid.entity.dispatcher.Task;
import martin.mogrid.p2pdl.api.RequestIdentifier;
import martin.mogrid.submission.task.bagoftask.protocol.message.P2PBoTMessage;
import martin.mogrid.submission.task.bagoftask.protocol.message.P2PJobRequest;
import martin.mogrid.submission.task.bagoftask.protocol.message.P2PJobResult;

import org.apache.log4j.Logger;

public class P2PJobRequestMessageHandler implements Runnable {

   // Manutencao do arquivo de log do servico
   private static final Logger logger = Logger.getLogger(P2PJobRequestMessageHandler.class);
 
   private P2PJobRequest        msgJobRequest         = null;
   private BotProtocolExecutor  botSubmissionProtocol = null;
   private Thread               requestThread         = null;
   
   public P2PJobRequestMessageHandler(P2PBoTMessage data, BotProtocolExecutor botSubmissionProtocol) {
      this.msgJobRequest = (P2PJobRequest) data;
      this.botSubmissionProtocol = botSubmissionProtocol;   
   }

   public void run() {
      //Recupera infos referentes a requisicao remota para o processamento da tarefa e envio de resultado
      int      jobID          = msgJobRequest.getJobID();
      String   executable     = msgJobRequest.getExecutable();
      String   execArguments  = msgJobRequest.getExecArguments();
      Vector   files          = msgJobRequest.getFiles();
      String[] filesName      = msgJobRequest.getFilesName();
      String   collabIPAddr   = msgJobRequest.getCollaboratorIPAddr();
      RequestIdentifier reqID = msgJobRequest.getRequestID();      
      
      logger.debug("< BoT Channel <<"+LocalHost.getLocalHostAddress()+">> receiving from ["+msgJobRequest.getSenderIPAddr()+"] a P2PJobRequest {JobID: "+jobID+" - ReqID: "+ reqID +"}");

      Object result   = null;      
      Task   gridTask = null;
      if ( LocalHost.isLocalHostAddress(collabIPAddr) ) {
         // Executa a tarefa remota localmente
         gridTask = new Task(reqID, jobID, collabIPAddr, executable, execArguments, files, filesName);        
      } else {
         // Chama listener externo (collabIPAddr), encaminhando a execucao da tarefa remota
         gridTask = new Task(reqID, jobID, collabIPAddr, executable, execArguments, files, filesName);
      }
      result = botSubmissionProtocol.executeTask(gridTask);

      // Envia o resultado da execucao da tarefa remota
      P2PJobResult jobResult = new P2PJobResult(reqID, collabIPAddr, jobID, result);
      String       reqIPAddr = msgJobRequest.getSenderIPAddr();
      
      String resultStr = result!=null ? result.toString() : " NULL ";
      logger.debug("> BoT Channel <<"+LocalHost.getLocalHostAddress()+">> - Collaborator ["+collabIPAddr+"] sending P2PJobResult {JobID: "+jobID+" - ReqID: "+ reqID +"} to ["+reqIPAddr+"] (RESULT: " + resultStr +")");      
      botSubmissionProtocol.send(jobResult, reqIPAddr);  
   }
}
