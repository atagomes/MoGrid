package martin.mogrid.tl.asl.filesharing;

import java.io.File;
import java.util.Hashtable;

import martin.mogrid.common.context.ContextInformation;
import martin.mogrid.common.resource.ResourceDescriptor;
import martin.mogrid.common.resource.ResourceIdentifier;
import martin.mogrid.common.resource.ResourceQuery;
import martin.mogrid.common.util.MoGridString;
import martin.mogrid.common.util.SystemUtil;
import martin.mogrid.entity.collaborator.Collaborator;
import martin.mogrid.entity.collaborator.CollaboratorException;
import martin.mogrid.entity.dispatcher.TaskDispatcherFactory;
import martin.mogrid.entity.initiatorcoordinator.InitiatorCoordinator;
import martin.mogrid.entity.initiatorcoordinator.InitiatorCoordinatorException;
import martin.mogrid.p2pdl.api.CollaboratorReply;
import martin.mogrid.p2pdl.api.CollaboratorReplyList;
import martin.mogrid.p2pdl.api.DiscoveryApplicationFacade;
import martin.mogrid.p2pdl.api.DiscoveryCollaboratorFacade;
import martin.mogrid.p2pdl.api.DiscoveryInitiatorFacade;
import martin.mogrid.p2pdl.api.MogridApplicationFacade;
import martin.mogrid.p2pdl.api.RequestIdentifier;
import martin.mogrid.p2pdl.api.RequestProfile;
import martin.mogrid.service.monitor.moca.MoCAContextParser;
import martin.mogrid.submission.protocol.TaskSubmissionListener;
import martin.mogrid.submission.task.filesharing.protocol.FileSharingProtocol;
import martin.mogrid.submission.task.filesharing.protocol.message.P2PRequestedFile;
import martin.mogrid.tl.asl.AdaptationSublayer;

import org.apache.log4j.Logger;


public class FileSharingAdaptationSublayer extends AdaptationSublayer
                                           implements DiscoveryApplicationFacade,
                                                      TaskSubmissionListener
{
   
   //Manutencao do arquivo de log do servico
   private static final Logger logger = Logger.getLogger(FileSharingAdaptationSublayer.class);    

   private static final int   NUM_MAX_REPLIES   =  1;
   private static final long  MAX_REPLY_DELAY   =  20;    //in seconds
   private static final int   REQUEST_DIAMETER  =  0;     //max number of hops to be sent (0 = single hop; 0 < n < 255 = multihop controlled; 255: multihop to all) 
   private ContextInformation ctxtInfo          =  null;
   private RequestProfile     reqProfile        =  null;
   
   //Armazena a associacao entre a string q representa o path do recurso local com o seu resID   
   private Hashtable audioResources = null;
   
   //Associa os resultados recebidos a uma dada requisicao
   //<Key>   RequestIdentifier
   //<Value> ExecutionState
   private Hashtable results = null; 
   //ExecutionState
   private static final String WAITING = "waiting";
   private static final String FINISH  = "finish";
   
   //Protocolo para transferencia de arquivo
   FileSharingProtocol fileSharing = null;
   
   //App que atua como Listener da camada de descoberta 
   MogridApplicationFacade application = null;
   
   //Entidades MoGrid: INITIATOR e COLLABORATOR 
   private DiscoveryInitiatorFacade    moGridInitiator    =  null; 
   private DiscoveryCollaboratorFacade moGridCollaborator =  null; 

         
   public FileSharingAdaptationSublayer(MogridApplicationFacade app) {
      super();
      
      results         = new Hashtable();
      audioResources  = new Hashtable();
      application     = app;

      try {
         moGridInitiator = InitiatorCoordinator.getInstance();  
         
      } catch (InitiatorCoordinatorException icex) {
         logger.info("It was not possible to start Initiator-Coordinator, application was interrupted.");
         
         logger.fatal("It was not possible to start Initiator-Coordinator: " + icex.getMessage());
         logger.fatal("There are not MoGrid support for this application.");
         SystemUtil.abnormalExit();
      } 
            
      try {
         //TODO nesse caso getInstance nao eh muito adequado pois reconfiguro alguns parametros
         moGridCollaborator = Collaborator.getInstance();
         moGridCollaborator.setAdmissionController(new FileSharingAdmissionController());
         moGridCollaborator.setCollaborationLevel(1);  
         moGridCollaborator.setTransferDelay(10);  // em milisegundos
         moGridCollaborator.setContextParser(new MoCAContextParser());
          
      } catch (CollaboratorException cle) {
         logger.info("It was not possible to start Collaborator, application was interrupted.");                
         
         logger.error("It was not possible to start Collaborator: " + cle.getMessage());
         logger.error("There are not MoGrid support for this application.");
         SystemUtil.abnormalExit();
      }   
     
      fileSharing = new FileSharingProtocol(this, audioResources);
      
      //File Sharing configurations
      ctxtInfo   = new ContextInformation(true, true, false, false, 1, 1, 0, 0);
      reqProfile = moGridInitiator.createRequestProfile(ctxtInfo, NUM_MAX_REPLIES, MAX_REPLY_DELAY, REQUEST_DIAMETER);      
   }


   public void registerTaskDispatcherFactory(TaskDispatcherFactory taskDispatcherFactory) {
      // TODO Auto-generated method stub
   }
   
   public void getFile(String fileQuery) {
      ResourceQuery resourceQuery = new ResourceQuery(fileQuery, ResourceQuery.CP_CONTAINS);
      moGridInitiator.discover(resourceQuery, reqProfile, this);
   }
   
   public void registerFile(long length, long duration, String description, String[] keywords, String path) {
      String identifier = null;
      File audioFile = new File(path);
      if (! audioFile.isFile() ) {
         return;
      }
      //String[] file = audioFile.getName().split("\\.");
      String[] file = MoGridString.split(audioFile.getName(), "\\.");
      identifier = file[0];
      
      ResourceDescriptor resDescriptor = new ResourceDescriptor(length, duration, identifier, description, keywords, path);
      ResourceIdentifier resID         = moGridCollaborator.register(resDescriptor);
      //LIXO 
      //logger.debug("* path: "+ path+ " ** reg: "+ resID+" - "+resDescriptor.toString());
      audioResources.put(resID, path);
   }

   public void registerFile(String subject) {
      String[] keywords = { "MPB", "Rio de Janeiro" };
      registerFile(0, 0, null, keywords, subject); 
      //registerFile(0, 0, null, null, subject);
   }
   
   public void deregisterFile(String subject) {
      ResourceIdentifier resID = (ResourceIdentifier)audioResources.remove(subject); 
      //LIXO 
      //logger.debug("dereg: "+ subject+" - "+resID);    
      moGridCollaborator.deregister(resID);
   }

   //Metodo da Interface DiscoveryApplicationFacade
   public void receiveCReplyList(RequestIdentifier reqID, CollaboratorReplyList cRepList) {
      if( cRepList != null && cRepList.size() > 0 ) {
         for ( int key=0; key<cRepList.size(); key++ ) {
            CollaboratorReply cRep = cRepList.get(key);
            logger.debug("< Collab["+key+"] - "+ " {ReqID: "+ reqID+"} => {ResID: "+cRep.getCollaboratorResourceIdentifier()+"}");
            fileSharing.requestFile(cRep, reqID);           
         }
         results.put(reqID, WAITING);
      }
   }   

   public void receiveTaskResolution(Object audioFiles) {
      P2PRequestedFile requestedFile = (P2PRequestedFile) audioFiles;
      RequestIdentifier reqID = requestedFile.getRequestIdentifier();
      if ( results.containsKey(reqID) ) {
         if ( results.get(reqID).equals(WAITING) ) { 
            results.put(reqID, FINISH);
            File audioFile = requestedFile.getRequestedFile();
            //logger.debug("< ["+reqID+"] Receive File: "+ audioFile.getAbsolutePath());
            application.handleMogridResource(reqID, audioFile);
            
         } if ( results.get(reqID).equals(FINISH) ) { 
            //logger.debug("-> Audio Request {ReqID: "+reqID+"} was already received."); 
         }         
      }
   }
   
   public void finalize() {
      fileSharing.stop();
      moGridInitiator.stop();
      moGridCollaborator.stop();
   }
    
}
