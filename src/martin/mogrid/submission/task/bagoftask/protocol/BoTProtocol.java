package martin.mogrid.submission.task.bagoftask.protocol;

import java.io.File;

import martin.mogrid.common.network.LocalHost;
import martin.mogrid.common.util.ThreadPool;
import martin.mogrid.common.util.SystemUtil;
import martin.mogrid.entity.dispatcher.Task;
import martin.mogrid.entity.dispatcher.TaskDispatcher;
import martin.mogrid.entity.dispatcher.TaskDispatcherFactory;
import martin.mogrid.p2pdl.api.RequestIdentifier;
import martin.mogrid.submission.protocol.TaskSubmissionListener;
import martin.mogrid.submission.task.bagoftask.protocol.message.P2PBoTMessage;
import martin.mogrid.submission.task.bagoftask.protocol.message.P2PJobRequest;
import martin.mogrid.submission.task.bagoftask.protocol.message.P2PJobResult;

import org.apache.log4j.Logger;

// Classe responsavel por ouvir o canal de compartilhamento
public class BoTProtocol extends BotProtocolExecutor {

   // Manutencao do arquivo de log do servico
   private static final Logger logger = Logger.getLogger(BoTProtocol.class);

   // Interface responsavel pelo redirecionamento de uma execucao:
   // > O proxy recebe a requisicao e encaminha para o colaborador correspondente
   protected TaskDispatcherFactory jobDispatcherFactory = null;
  
   //Pool de threads para controlar o tratamento das requisicoes/respostas
   private ThreadPool threadPool = null;
   
   private static final int MAX_THREADS = 40;
   
   private int THREAD_SLEEP_INTERVAL = 1000;
   
   
   public BoTProtocol(TaskSubmissionListener listener) {
      super("BoT Channel", listener);
      threadPool = new ThreadPool(MAX_THREADS);
      start();
   }
   
   public void registerTaskDispatcherFactory(TaskDispatcherFactory proxyJobDispatcherFactory) {
      this.jobDispatcherFactory = proxyJobDispatcherFactory;
   }

   public synchronized void sendRequest(RequestIdentifier reqID, int jobID,
         String collabAddr, String proxyAddr, File[] files, String executable,
         String execArguments) 
   {
      // Salva no corpo da mensagem o endereco do colaborador a quem a tarefa se destina
      P2PJobRequest jobRequest = new P2PJobRequest(reqID, collabAddr, jobID, files, executable, execArguments);
      // A tarefa eh enviada para o proxy do colaborador, caso nao exista um
      // proxy o endereco de proxyAddr eh o mesmo do colaborador (collabAddr)
      logger.debug("> BoT Channel <<"+LocalHost.getLocalHostAddress()+">> [sending] P2PJobRequest jobID(" + jobID+" - "+reqID+") to [" + proxyAddr+":"+collabAddr+"]");
      send(jobRequest, proxyAddr);
   }

   public void run() {
      Thread        myThread = Thread.currentThread();
      P2PBoTMessage data     = null;

      while ( isAlive(myThread) ) {                  
         data = (P2PBoTMessage) receive();         
         if ( data != null ) {  
            if ( data instanceof P2PJobResult ) {
               //When receive a Job Result message, handle it immediatly 
               try {
                  threadPool.execute( new P2PJobResultMessageHandler(data, this) );
               } catch (InterruptedException e) {
                  logger.warn("P2PJobResultMessageHandler: "+e.getMessage(), e);
               }
            } else if ( data instanceof P2PJobRequest ) {
               //When receive a Job Request message, handle it
               try {
                  threadPool.execute( new P2PJobRequestMessageHandler(data, this) );
               } catch (InterruptedException e) {
                  logger.warn("P2PJobRequestMessageHandler: "+e.getMessage(), e);
               }   
            } 
         }
         SystemUtil.sleep(THREAD_SLEEP_INTERVAL);  //Wait 1 seconds
      }
   }
   
   public Object executeTask(Task task) {
      Object result = null;
      if ( task != null ) {
         TaskDispatcher jobDispatcher = jobDispatcherFactory.createInstance();
         result = jobDispatcher.execute(task);
      }
      return result;
   }
   
}





