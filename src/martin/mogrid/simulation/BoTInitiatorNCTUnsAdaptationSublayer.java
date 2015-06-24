/**
 * @author lslima
 * 
 * Created on 14/03/2006
 */

package martin.mogrid.simulation;

import java.util.Vector;

import martin.mogrid.common.context.ContextInformation;
import martin.mogrid.common.network.LocalHost;
import martin.mogrid.common.resource.ResourceQuery;
import martin.mogrid.common.util.SystemUtil;
import martin.mogrid.entity.dispatcher.TaskDispatcherFactory;
import martin.mogrid.entity.initiatorcoordinator.InitiatorCoordinator;
import martin.mogrid.entity.initiatorcoordinator.InitiatorCoordinatorException;
import martin.mogrid.p2pdl.api.CollaboratorReplyList;
import martin.mogrid.p2pdl.api.DiscoveryApplicationFacade;
import martin.mogrid.p2pdl.api.DiscoveryInitiatorFacade;
import martin.mogrid.p2pdl.api.MogridApplicationFacade;
import martin.mogrid.p2pdl.api.RequestIdentifier;
import martin.mogrid.p2pdl.api.RequestProfile;
import martin.mogrid.submission.protocol.TaskSubmissionListener;
import martin.mogrid.tl.asl.AdaptationSublayer;
import martin.mogrid.tl.asl.bagoftask.BoTRequest;
import martin.mogrid.tl.asl.bagoftask.BoTRequestHistory;

import org.apache.log4j.Logger;

public class BoTInitiatorNCTUnsAdaptationSublayer extends AdaptationSublayer
                                                  implements DiscoveryApplicationFacade,
                                                             TaskSubmissionListener
{
   
   //Manutencao do arquivo de log do servico
   private static final Logger logger = Logger.getLogger(BoTInitiatorNCTUnsAdaptationSublayer.class);    


   private final int     REQ_ATTEMPTS      =  0;
   private final String  RESOURCE_QUERY    =  "java";
   
   private long  MAX_REPLY_DELAY   =  18;      //in seconds
   private int   REQUEST_DIAMETER  =  255;     //max number of hops to be sent (0 = single hop; 0 < n < 255 = multihop controlled; 255: multihop to all)
   private int   NUM_MAX_REPLIES   =  1;       //max number of replies (CReps) for a IReq message
   
   private ContextInformation  ctxtInfo    =  null;
   private RequestProfile      reqProfile  =  null;
     
   //Armazena a associacao entre a requisicao, os arquivos, a chamada e as 
   //informacoes necessarias para regerar uma solicitacao de descoberta  
   private BoTRequestHistory        botRequests  =  null;
   //App que atua como Listener da camada de descoberta 
   private MogridApplicationFacade  application  =  null;   
   

   //TODO alterar a ASL, separar funcoes de descoberta, tendo assim uma 
   //     ASL para o colaborador e outra para o iniciador
   //     Util no caso de simulacoes e no perfil J2ME CLDC/MIDP
   //     Ver como se comporta o resultado das modificacoes feitas no 
   //     SINGLEHOP antes de adota-las no MULTIHOP     
   //Entidade MoGrid: INITIATOR
   private DiscoveryInitiatorFacade moGridInitiator = null; 

   
   public BoTInitiatorNCTUnsAdaptationSublayer(MogridApplicationFacade app) {
      super();      
      
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
      
      //File Sharing configurations
      //ContextInformation: conectividade, energia, cpu, memoria
      ctxtInfo = new ContextInformation(true, true, true, true, 1, 1, 3, 2);
   }    

   public void registerTaskDispatcherFactory(TaskDispatcherFactory taskDispatcherFactory) {
      // TODO Auto-generated method stub
   }
   
   public void setMaxReplyDelay(long timeInSeconds) {
      // MAX_REPLY_DELAY must be > 0
      MAX_REPLY_DELAY = Math.max(1, timeInSeconds);;
      logger.info("[ASL] MaxReplyDelay set to " + timeInSeconds);  
   }
   
   public void setRequestDiameter(int numHops) {
      // REQUEST_DIAMETER must be >= 0 && <=255 ) {
      REQUEST_DIAMETER = Math.max(0, Math.min(255, numHops));
      logger.info("[ASL] RequestDiameter set to " + numHops);
   }
   
   public void setNumMaxReplies(int numMaxReplies) {
      //NUM_MAX_REPLIES must be >= 1
      NUM_MAX_REPLIES = Math.max(1, numMaxReplies);
      logger.info("[ASL] NumMaxReplies set to " + NUM_MAX_REPLIES); 
   }
   
   
   public void submitJobRequest(int numJobs, Vector files, String executable, String[] execArguments) {
      reqProfile = moGridInitiator.createRequestProfile(ctxtInfo, NUM_MAX_REPLIES, MAX_REPLY_DELAY, REQUEST_DIAMETER);
      ResourceQuery resourceQuery = new ResourceQuery(RESOURCE_QUERY, ResourceQuery.CP_ALL);      
      moGridInitiator.discover(resourceQuery, reqProfile, this);
      
      //Recupera o identificador da requisicao (IReq) para controle de recepcao de respostas e possivel reenvio
      RequestIdentifier reqID = resourceQuery.getRequestIdentifier();  
      //Adiciona a requisicao ao historico de jobRequests para tratamento na recepcao de resposta
      //alem dos dados da requisicao para resubmissao, caso seja necessario
      BoTRequest req = new BoTRequest(REQ_ATTEMPTS, numJobs, NUM_MAX_REPLIES, files, executable, execArguments, resourceQuery, reqProfile);    
      botRequests.put(reqID, req);       
   }   
  
   //START - Metodos da interface AdaptationSublayer
   //> Metodo da interface TaskSubmissionListener
   public void receiveTaskResolution(Object msgResult) {
      logger.trace(msgResult);    
   }

   //Metodo da Interface DiscoveryApplicationFacade
   //Ao receber a lista de colaboradores distribui as tarefas
   public void receiveCReplyList(RequestIdentifier reqID, CollaboratorReplyList cRepList) {
      String receiveCRepListMsg = "";
      //IReq timer is handled by Coordinator when he is waiting for Collaborators replies (CReps)
      if ( botRequests.containsKey(reqID) && cRepList!=null ) {
         if ( cRepList.isEmpty() ) {
            receiveCRepListMsg = "< Initiator ["+ LocalHost.getLocalHostAddress()+"] received from Coordinator a EMPTY CReplyList for ReqID ["+reqID+"]"; 
            logger.debug(receiveCRepListMsg);
         } else {
            receiveCRepListMsg = "< Initiator ["+ LocalHost.getLocalHostAddress()+"] received from Coordinator a #"+cRepList.size()+" CReplyList for ReqID ["+reqID+"]";
            logger.debug(receiveCRepListMsg);
            if( reqID!=null && botRequests.containsKey(reqID) && cRepList!=null && cRepList.size() > 0 ) {
               BoTRequest jobReq     = botRequests.get(reqID);                
               int        numMaxReps = jobReq.getNumMaxReplies();
               int        numCollabs = cRepList.size();   
               
               receiveCRepListMsg = "(*) CReplyList {ReqID: "+reqID+"} -> NUM_MAX_REPLIES ["+numMaxReps+"], NUM_COLLABS ["+numCollabs+"]\n";
               logger.debug(receiveCRepListMsg);               
            }
         }   
      }
      receiveTaskResolution(receiveCRepListMsg);
   }           
   //END - Metodos da interface AdaptationSublayer

  
   
   public void finalize() {
      moGridInitiator.stop();
      
      SystemUtil.normalExit();
   }

}
