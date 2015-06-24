/**
 * @author lslima
 * 
 * Created on 14/03/2006
 */

package martin.mogrid.tl.asl.bagoftask;

import java.util.Hashtable;

import martin.mogrid.common.resource.ResourceDescriptor;
import martin.mogrid.common.resource.ResourceIdentifier;
import martin.mogrid.common.util.SystemUtil;
import martin.mogrid.entity.collaborator.Collaborator;
import martin.mogrid.entity.collaborator.CollaboratorException;
import martin.mogrid.entity.dispatcher.TaskDispatcherFactory;
import martin.mogrid.p2pdl.api.DiscoveryCollaboratorFacade;
import martin.mogrid.p2pdl.api.MogridApplicationFacade;
import martin.mogrid.submission.protocol.TaskSubmissionListener;
import martin.mogrid.submission.task.bagoftask.protocol.BoTProtocol;
import martin.mogrid.tl.asl.AdaptationSublayer;

import org.apache.log4j.Logger;

public class BoTCollaboratorAdaptationSublayer extends AdaptationSublayer
                                               implements TaskSubmissionListener 
{
   
   //Manutencao do arquivo de log do servico
   private static final Logger logger = Logger.getLogger(BoTCollaboratorAdaptationSublayer.class);    
   
   //Armazena a associacao entre a string q representa o path do recurso local com o seu resID
   //<Key>   String (command complete path - Ex: Para java.exe -> C:\lng\java\jre\bin )
   //<Value> ResourceIdentifier   
   private Hashtable resources = null;      
   //Protocolo para transferencia de arquivo
   private BoTProtocol                 botSubmission      = null;   
   //App que atua como Listener da camada de descoberta 
   private MogridApplicationFacade     application        = null;   
   

   //TODO alterar a ASL, separar funcoes de descoberta, tendo assim uma 
   //     ASL para o colaborador e outra para o iniciador
   //     Util no caso de simulacoes e no perfil J2ME CLDC/MIDP
   //     Ver como se comporta o resultado das modificacoes feitas no 
   //     SINGLEHOP antes de adota-las no MULTIHOP     
   //Entidade MoGrid: COLLABORATOR 
   private DiscoveryCollaboratorFacade moGridCollaborator =  null; 

   
   public BoTCollaboratorAdaptationSublayer(MogridApplicationFacade app) {
      super();      
      
      resources    = new Hashtable();
      application  = app;                
      
      //COLLABORATOR
      try {
         //TODO nesse caso getInstance nao eh muito adequado pois reconfiguro alguns parametros, 
         //     o que afetaria a execucao de todas as demais classes que utilizassem essa instancia
         moGridCollaborator = Collaborator.getInstance();
         moGridCollaborator.setAdmissionController(new BoTAdmissionController());
          
      } catch (CollaboratorException cle) {
         logger.info("It was not possible to start Collaborator, application was interrupted.");
      
         logger.fatal("It was not possible to start Collaborator: " + cle.getMessage());
         logger.fatal("There are not MoGrid support for this application.");
         SystemUtil.abnormalExit();
      }   

      botSubmission = new BoTProtocol(this);      
   }    

   public void setTransferDelay(float s) {
      if ( moGridCollaborator!=null ) {
         moGridCollaborator.setTransferDelay(s);
      }
      logger.info("[ASL] Transfer Delay for each transmission was set to " + moGridCollaborator.getTransferDelay()); 
   }   

   // Configura os valores associados ao retardo de retransmissao da mensagem de requisicao (IReq), 
   // para minimizar as colisoes no meio sem fio (tempo em milisegundos)
   public void setForwardRequestDelay(int minForwardRequestDelay, int maxForwardRequestDelay) {
      if ( moGridCollaborator!=null ) {
         moGridCollaborator.setForwardRequestDelay(minForwardRequestDelay, maxForwardRequestDelay);
      }
      logger.info("[ASL] Minimum Forward Request Delay was set to " + moGridCollaborator.getMinForwardRequestDelay());
      logger.info("[ASL] Maximum Forward Request Delay was set to " + moGridCollaborator.getMaxForwardRequestDelay());        
   }
   
   public void setCollaborationLevel(float level) {
      if ( moGridCollaborator!=null ) {
         moGridCollaborator.setCollaborationLevel(level);
      }
      logger.info("[ASL] Collaboration Level was set to " + moGridCollaborator.getCollaborationLevel());       
   }
   
   public void registerTaskDispatcherFactory(TaskDispatcherFactory taskDispatcher) {
      botSubmission.registerTaskDispatcherFactory(taskDispatcher);
   }
   
   public void register(String identifier, String description, String[] keywords, String path) {
      ResourceDescriptor resDescriptor = new ResourceDescriptor(identifier, description, keywords, path);
      ResourceIdentifier resID         = moGridCollaborator.register(resDescriptor);
      logger.debug("reg: "+ resID.toString()+ " id: "+identifier+" path: "+ path);
      resources.put(path, resID);
   }
   
   public void deregisterFile(String path) {
      ResourceIdentifier resID = (ResourceIdentifier)resources.remove(path); 
      //LIXO logger.debug("dereg: "+ resID);    
      moGridCollaborator.deregister(resID);
   }
      
   
   public void finalize() {
      botSubmission.stop();
      moGridCollaborator.stop();
      
      SystemUtil.normalExit();
   }

   public void receiveTaskResolution(Object result) {
      if ( result != null )
      logger.debug("result... "+ result.toString());  
      
   }

}
