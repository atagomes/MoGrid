package martin.mogrid.tl.asl.proxy;

import java.util.Hashtable;

import martin.mogrid.common.resource.ResourceIdentifier;
import martin.mogrid.common.util.SystemUtil;
import martin.mogrid.entity.dispatcher.TaskDispatcherFactory;
import martin.mogrid.entity.proxy.globus.ProxyAdmissionController;
import martin.mogrid.entity.proxy.globus.ProxyCollaborator;
import martin.mogrid.entity.proxy.globus.ProxyCollaboratorException;
import martin.mogrid.entity.proxy.registry.globus.ProxyResourceDescriptor;
import martin.mogrid.p2pdl.api.DiscoveryCollaboratorFacade;
import martin.mogrid.p2pdl.api.MogridApplicationFacade;
import martin.mogrid.submission.protocol.TaskSubmissionListener;
import martin.mogrid.submission.task.bagoftask.protocol.BoTProtocol;
import martin.mogrid.tl.asl.AdaptationSublayer;

import org.apache.log4j.Logger;

public class ProxyAdaptationSublayer extends AdaptationSublayer implements TaskSubmissionListener {
   
   //Manutencao do arquivo de log do servico
   private static final Logger logger = Logger.getLogger(ProxyAdaptationSublayer.class);    

   //Armazena a associacao entre a string q representa o path do recurso local com o seu resID
   //<Key>   String (command complete path - Ex: Para java.exe -> C:\lng\java\jre\bin )
   //<Value> ResourceIdentifier   
   private Hashtable resources = null;   
  
   //Protocolo para transferencia de arquivo
   private BoTProtocol                 botSubmission      = null;   
   //App que atua como Listener da camada de descoberta 
   private MogridApplicationFacade     application        = null;   
   //Entidade MoGrid: COLLABORATOR  
   private DiscoveryCollaboratorFacade moGridCollaborator =  null; 

   
   public ProxyAdaptationSublayer(MogridApplicationFacade app) {
      super();      
      
      resources    = new Hashtable();
      application  = app;                
           
      try {
         //TODO nesse caso getInstance nao eh muito adequado pois reconfiguro alguns parametros, 
         //     o que afetaria a execucao de todas as demais classes que utilizassem essa instancia
         //moGridCollaborator = ProxyCollaborator.getInstance();
         moGridCollaborator = ProxyCollaborator.getInstance();
         moGridCollaborator.setAdmissionController(new ProxyAdmissionController());
         moGridCollaborator.setCollaborationLevel(1);  
         moGridCollaborator.setTransferDelay(10);
          
      //} catch (ProxyCollaboratorException cle) {
      } catch ( ProxyCollaboratorException cle) {
         logger.info("It was not possible to start Collaborator, application was interrupted.");
      
         logger.fatal("It was not possible to start Collaborator: " + cle.getMessage());
         logger.fatal("There are not MoGrid support for this application.");
         SystemUtil.abnormalExit();
      }   
       
      botSubmission = new BoTProtocol(this);
   }    

   public void registerTaskDispatcherFactory(TaskDispatcherFactory taskDispatcherFactory) {
      botSubmission.registerTaskDispatcherFactory(taskDispatcherFactory);
   }
   
   public ResourceIdentifier register(String identifier, String description, String[] keywords, String linuxPath, String unixPath) {
      ProxyResourceDescriptor resDescriptor = new ProxyResourceDescriptor(identifier, description, keywords, linuxPath, unixPath);
      ResourceIdentifier resID         = moGridCollaborator.register(resDescriptor);
      logger.debug("reg: "+ resID.toString()+ " id: "+identifier+" Linux path: "+ linuxPath + " Unix path: "+ unixPath);
      resources.put(identifier, resID);
      return resID;
   }
   
   // A chave aqui é o identifier e não o path
   public void deregisterFile(String identifier) {
      ResourceIdentifier resID = (ResourceIdentifier)resources.remove(identifier); 
      //LIXO logger.debug("dereg: "+ resID);    
      moGridCollaborator.deregister(resID);
   }
   

   //START - Metodos da classe abstrata AdaptationSublayer
   //> Metodo da interface TaskSubmissionListener
   public void receiveTaskResolution(Object msgResult) {
   }     
   //END - Metodos da classe abstrata AdaptationSublayer

   
   public void finalize() {
      botSubmission.stop();
      moGridCollaborator.stop();
      
      SystemUtil.normalExit();
   }

}
