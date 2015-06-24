/**
 * @author lslima
 * 
 * Created on 14/03/2006
 */

package martin.mogrid.tl.asl.bagoftask;

import java.util.Hashtable;
import java.util.Vector;

import martin.mogrid.common.context.ContextInformation;
import martin.mogrid.common.resource.ResourceDescriptor;
import martin.mogrid.common.resource.ResourceQuery;
import martin.mogrid.common.util.SystemUtil;
import martin.mogrid.entity.dispatcher.TaskDispatcherFactory;
import martin.mogrid.entity.initiatorcoordinator.InitiatorCoordinator;
import martin.mogrid.entity.initiatorcoordinator.InitiatorCoordinatorException;
import martin.mogrid.p2pdl.api.CollaboratorReply;
import martin.mogrid.p2pdl.api.CollaboratorReplyList;
import martin.mogrid.p2pdl.api.DiscoveryApplicationFacade;
import martin.mogrid.p2pdl.api.DiscoveryInitiatorFacade;
import martin.mogrid.p2pdl.api.MogridApplicationFacade;
import martin.mogrid.p2pdl.api.RequestIdentifier;
import martin.mogrid.p2pdl.api.RequestProfile;
import martin.mogrid.p2pdl.collaboration.criterias.CollaborationSuitabilitySelector;
import martin.mogrid.submission.protocol.TaskSubmissionListener;
import martin.mogrid.submission.task.bagoftask.protocol.BoTProtocol;
import martin.mogrid.submission.task.bagoftask.protocol.message.P2PJobResult;
import martin.mogrid.tl.asl.AdaptationSublayer;

import org.apache.log4j.Logger;

public class BoTInitiatorAdaptationSublayer extends AdaptationSublayer
                                   implements DiscoveryApplicationFacade,
                                              TaskSubmissionListener
{
   
   //Manutencao do arquivo de log do servico
   private static final Logger logger = Logger.getLogger(BoTInitiatorAdaptationSublayer.class);    


   private static final int     REQ_ATTEMPTS      =  0;
   private static final String  RESOURCE_QUERY    =  "java";
   
   private long                 MAX_REPLY_DELAY   =  18;      //in seconds
   private int                  REQUEST_DIAMETER  =  0;       //max number of hops to be sent (0 = single hop; 0 < n < 255 = multihop controlled; 255: multihop to all) 
   private ContextInformation   ctxtInfo          =  null;
   private RequestProfile       reqProfile        =  null;
   
   //Associa os resultados recebidos a uma dada requisicao
   //<Key>   RequestIdentifier
   //<Value> GridJobResultHistory
   private Hashtable results = null;     
   //Armazena a associacao entre a requisicao, os arquivos, a chamada e as 
   //informacoes necessarias para regerar uma solicitacao de descoberta  
   private BoTRequestHistory           botRequests        = null;
   //Protocolo para transferencia de arquivo
   private BoTProtocol                 botSubmission      = null;   
   //App que atua como Listener da camada de descoberta 
   private MogridApplicationFacade     application        = null;   
   

   //TODO alterar a ASL, separar funcoes de descoberta, tendo assim uma 
   //     ASL para o colaborador e outra para o iniciador
   //     Util no caso de simulacoes e no perfil J2ME CLDC/MIDP
   //     Ver como se comporta o resultado das modificacoes feitas no 
   //     SINGLEHOP antes de adota-las no MULTIHOP     
   //Entidade MoGrid: INITIATOR
   private DiscoveryInitiatorFacade    moGridInitiator    =  null; 

   
   public BoTInitiatorAdaptationSublayer(MogridApplicationFacade app) {
      super();      
      
      results      = new Hashtable();
      botRequests  = new BoTRequestHistory();
      application  = app;                
      
      try {
         moGridInitiator = InitiatorCoordinator.getInstance();  
         
      } catch (InitiatorCoordinatorException icex) {
         logger.info("It was not possible to start Initiator-Coordinator, application was interrupted.");
         
         logger.fatal("It was not possible to start Initiator-Coordinator: " + icex.getMessage());
         logger.fatal("There are not MoGrid support for this application.");
         SystemUtil.abnormalExit();
      }  
      botSubmission = new BoTProtocol(this);
      
      //File Sharing configurations
      //ContextInformation: conectividade, energia, cpu, memoria
      //ctxtInfo = new ContextInformation(true, true, true, true, 1, 1, 3, 2);
      //ctxtInfo = new ContextInformation(false, false, true, true, 0, 0, 4, 1);
      ctxtInfo = new ContextInformation(false, false, true, false, 0, 0, 1, 0);
   }    

   public void setMaxReplyDelay(long timeInSeconds) {
      if ( timeInSeconds>0 ) {
         MAX_REPLY_DELAY = timeInSeconds;
         logger.info("[ASL] MaxReplyDelay was set to " + timeInSeconds);  
      }
   }
   
   public void setRequestDiameter(int numHops) {
      if ( numHops >= 0 && numHops <=255 ) {
         REQUEST_DIAMETER = numHops;
         logger.info("[ASL] RequestDiameter was set to " + numHops); 
      }
   }
   
   public void registerTaskDispatcherFactory(TaskDispatcherFactory taskDispatcherFactory) {
      botSubmission.registerTaskDispatcherFactory(taskDispatcherFactory);
   }
   
   public void submitJobRequest(int numMaxReplies, int numJobs, Vector files, String executable, String[] execArguments) {
      reqProfile = moGridInitiator.createRequestProfile(ctxtInfo, numMaxReplies, MAX_REPLY_DELAY, REQUEST_DIAMETER);
      ResourceQuery resourceQuery = new ResourceQuery(RESOURCE_QUERY, ResourceQuery.CP_ALL);      
      moGridInitiator.discover(resourceQuery, reqProfile, this);
      
      //Recupera o identificador da requisicao (IReq) para controle de recepcao de respostas e possivel reenvio
      RequestIdentifier reqID = resourceQuery.getRequestIdentifier();  
      //Adiciona a requisicao ao historico de jobRequests para tratamento na recepcao de resposta
      //alem dos dados da requisicao para resubmissao, caso seja necessario
      BoTRequest req = new BoTRequest(REQ_ATTEMPTS, numJobs, numMaxReplies, files, executable, execArguments, resourceQuery, reqProfile);    
      botRequests.put(reqID, req);
      
      logger.info("[ASL] JobRequest: NumMaxReplies was set to " + numMaxReplies + ", NumJobs was set to " + numJobs);
   }
   
   private void resubmitJobRequest(RequestIdentifier reqID) {
      if ( botRequests.containsKey(reqID) ) {
         //Mantem o mesmo identificador de requisicao
         BoTRequest request = botRequests.get(reqID);
         if ( request.canSubmit() ) {
            logger.debug("> Resubmit Job Request "+reqID);
            RequestProfile reqProfile    = request.getRequestProfile();
            ResourceQuery  resourceQuery = request.getResourceQuery();
            moGridInitiator.discover(resourceQuery, reqProfile, this);
         } else {
            //TODO Remove da lista e notifica a aplicacao
            logger.info("! InitiatorRequest message ["+reqID+"] does not have reply.\n");
            logger.debug("!< Job Request ["+reqID+"] does not have reply.");
            botRequests.remove(reqID);
         }
         
      }    
   }  
   
   //TODO Refazer a resubmissao de tarefas: soh estah sendo detectada qdo o resultado chega nulo
   //     caso ele nao chegue ela nao eh feita e a aplicacao fica aguardando, sem receber notificacao
   private void resubmitTask(RequestIdentifier reqID) {
      if ( botRequests.containsKey(reqID) ) {
         BoTRequest     pendingRequest = botRequests.get(reqID);
         ResourceQuery  resourceQuery  = pendingRequest.getResourceQuery();  
         RequestProfile reqProfile     = pendingRequest.getRequestProfile();         
         reqProfile.setNumMaxReplies(1); 
         moGridInitiator.discover(resourceQuery, reqProfile, this);  
      }
   }   

   
   //START - Metodos da interface AdaptationSublayer
   //> Metodo da interface TaskSubmissionListener
   public synchronized void receiveTaskResolution(Object msgResult) {
      //Se a hashtable que associa os resultados recebidos a uma dada requisicao
      //estiver nula, entao nao foi registrada nenhuma requisicao
      //A mensagem-resultado tb nao pode ser nula
      if ( results == null || msgResult == null ) { return; }      
      
      //TODO Preciso definir um temporizador para tarefa, senao fica eternamente pendente esperando
      //     pelo resultado sem resubmete-la
      P2PJobResult      jobResult = (P2PJobResult)msgResult;
      RequestIdentifier reqID     = jobResult.getRequestID();
      
      if ( results.containsKey(reqID) ) {    
         Object  result       = jobResult.getJobResult();
         int     jobID        = jobResult.getJobID();
         String  collabIPAddr = jobResult.getSenderIPAddr();
         
         BoTResultHistory history = (BoTResultHistory)results.get(reqID);
         int element = history.containsJobRequest(collabIPAddr, jobID);

         BoTRequest jobReq = botRequests.get(reqID);
         if ( jobReq != null && !jobReq.isGridJobFinish(jobID)) {
            if ( result != null ) {
               //o colaborador e a tarefa existem, 'element' representa o seu indice no vetor
               if ( element >= 0 ) { 
                  history.getElementAt(element).setResult(result);
                  //Atualiza o historico de jobRequests com o novo status da tarefa (executada),
                  //evitando que ela seja executada novamente em caso de resubmissao      
                  jobReq.setGridJobStatus(jobID, BoTRequest.FINISH);
                  
                  //se jah recebeu todas as respostas as devolve ordenadamente
                  if ( history.canProcess() ) { 
                     application.handleMogridResource(reqID, history);
                     botRequests.remove(reqID);
                  }
               }
   
            //Caso o resultado da execucao seja invalido, resubmete requisicao para MoGrid para o reenvio da tarefa
            } else {
               jobReq.setGridJobStatus(element, BoTRequest.PENDING);
               logger.debug("> resubmit task["+jobID+"] with the same reqID " + reqID);
               resubmitTask(reqID);
            }
         } else {
            logger.debug("-> Task ["+reqID+" : "+ jobID+"] was already received.");            
         }
      }
   }

   //Metodo da Interface DiscoveryApplicationFacade
   //Ao receber a lista de colaboradores distribui as tarefas
   public void receiveCReplyList(RequestIdentifier reqID, CollaboratorReplyList cRepList) {      
      //IReq timer is handled by Coordinator when he is waiting for Collaborators replies (CReps)
      if ( botRequests.containsKey(reqID) && cRepList!=null ) {
         if ( cRepList.isEmpty() ) {
            logger.debug("> receiveCReplyList IS EMPTY...");
            //resubmitJobRequest(reqID);
         } else {
            logger.debug("> receiveCReplyList - handle it");
            handleCReplyList(reqID, cRepList);            
         }   
      }      
   }           
   //END - Metodos da interface AdaptationSublayer

   private void handleCReplyList(RequestIdentifier reqID, CollaboratorReplyList cRepList) {
      if( reqID!=null && botRequests.containsKey(reqID) && cRepList!=null && cRepList.size() > 0 ) {
         BoTResultHistory jobResult     = new BoTResultHistory(reqID);
         BoTRequest       jobReq        = botRequests.get(reqID); 
         String           executable    = jobReq.getExecutable(); 
         String[]         execArguments = jobReq.getExecArguments();
         int              numJobs       = jobReq.getNumJobs();
         int              numMaxReplies = jobReq.getNumMaxReplies();
         int              numCollabs    = cRepList.size();

         logger.trace("(*) CReplyList {ReqID: "+reqID+"} -> NUM_MAX_REPLIES ["+numMaxReplies+"], NUM_COLLABS ["+numCollabs+"]\n");
         
         //Caso o numero de tarefas seja maior do que o de colaboradores, ordena a lista 
         //de respostas de acordo com a disponibilidade de recursos de cada colaborador
         //comparativamente aos demais
         if ( numJobs > numCollabs) {
            CollaborationSuitabilitySelector avaliator = new CollaborationSuitabilitySelector(cRepList);
            cRepList = avaliator.sortRepliesByContext();
         }
         
         //Distribui a execucao em funcao do numero de tarefas/numero de colaboradores
         for ( int job=0; job<numJobs; job++ ) {
            if (jobReq.isGridJobFinish(job) || jobReq.isGridJobRunning(job) ) {
               continue;
            }
            //Se o numero de tarefas eh maior que o de colaboradores a distribuicao de tarefas eh 
            //feita de forma circular entre os colaboradores, o que possibilita que um colaborador
            //receba mais de uma tarefa
            //Ex: numJobs=4, numCollabs=3: 
            //    (job%numCollabs) 0%3=0; 1%3=1; 2%3=2; 3%3=0; 4%3=1
            //Distribui��o: collabs 0 e 1 receberam 2 tarefas cada, collab 2 apenas 1
            //TODO: Melhorar pol�tica de redistribuicao, considerar o melhor contexto (mais
            //      energia, mais cpu, etc.)
            int collab = job % cRepList.size();
            CollaboratorReply cRep = cRepList.get(collab);
            
            String collabAddress = cRep.getCollaboratorIPAddr();
            ResourceDescriptor resDescriptor = cRep.getCollaboratorResourceDescriptor();
          
            String exec      = executable;   
            String arguments = execArguments[job];   
            if ( resDescriptor!=null ) {
               exec = resDescriptor.getPath();
            } else {
               logger.debug("Resource Descriptor IS NULL from ["+collabAddress+"] to ["+reqID+"]");
            }

            botSubmission.sendRequest(reqID, job, collabAddress, cRep.getCollaboratorProxyIPAddr(), jobReq.getJobFiles(job), exec, arguments);            
            jobReq.setGridJobStatus(job, BoTRequest.RUNNING);
            //guarda as requiscoes (esperando as respostas - null) em uma ordem que garanta a montagem do resultado
            jobResult.addElement(new BoTResult(job, collabAddress, null));
         }
         results.put(reqID, jobResult);
      }
   }   
   
   
   public void finalize() {
      botSubmission.stop();
      moGridInitiator.stop();
      
      SystemUtil.normalExit();
   }
}
