package martin.app.bagoftask.matrix;

import java.io.File;

import martin.mogrid.common.logging.MoGridLog4jConfigurator;
import martin.mogrid.common.util.ProjectHeader;
import martin.mogrid.common.util.SystemUtil;
import martin.mogrid.p2pdl.api.MogridApplicationFacade;
import martin.mogrid.p2pdl.api.RequestIdentifier;
import martin.mogrid.submission.task.bagoftask.protocol.BoTDispatcherFactory;
import martin.mogrid.tl.asl.bagoftask.BoTCollaboratorAdaptationSublayer;

import org.apache.log4j.Logger;


public class RunMatrixCollaborator implements MogridApplicationFacade { 

   //Manutencao do arquivo de log do servico
   private static final Logger logger = Logger.getLogger(RunMatrixCollaborator.class);  
   
   protected BoTCollaboratorAdaptationSublayer gridJobDiscovery = null;   
   
   private RunMatrixCollaborator(String javaPath, float willingness, int transferDelay, int minForwardRequestDelay, int maxForwardRequestDelay) { 
      gridJobDiscovery = new BoTCollaboratorAdaptationSublayer(this);
      gridJobDiscovery.registerTaskDispatcherFactory(new BoTDispatcherFactory());  
      if ( javaPath != null ) { 
         String fullPath = javaPath + File.separator + "java";
         gridJobDiscovery.register("java", "java", null, fullPath);
      }
      // Configura a disponibilidade do colaborador em compartilhar os seus servicos
      gridJobDiscovery.setCollaborationLevel(willingness);
      gridJobDiscovery.setTransferDelay(transferDelay);
      // Configura os valores associados ao retardo de retransmissao da mensagem de requisicao (IReq), 
      // para minimizar as colisoes no meio sem fio (tempo em milisegundos)
      gridJobDiscovery.setForwardRequestDelay(minForwardRequestDelay, maxForwardRequestDelay);     
   }
   
   public void handleMogridResource(RequestIdentifier reqID, Object resource) {   
   }
   
   public static void printHelp() {
      logger.info("Program Usage:");
      logger.info("java martin.app.bagoftask.matrix.RunMatrixCollaborator <javaPath> <willingness> <transferDelay> <minimumForwardRequestDelay> <maximumForwardRequestDelay>");
      logger.info("\nWhere <javaPath> points to java.exe dir.");
      logger.info("<javaPath> example: /usr/java/jre/bin");   
      logger.info("\nWhere <willingness> indicate the the willingness to collaborate [0, 1]");
      logger.info("\nWhere <transferDelay> indicate the transfer delay at each transmission.");
      logger.info("\nWhere <minimumForwardRequestDelay> sets the minimum time, in milliseconds, that an IReq forward can be delayed.");
      logger.info("\nWhere <maximumForwardRequestDelay> sets the maximum time, in milliseconds, that an IReq forward can be delayed.");
                       
      SystemUtil.abnormalExit();
   }
   
   public static void main(String[] args) {  
      MoGridLog4jConfigurator.configure();
      ProjectHeader.print();
      
      if ( args != null && args.length == 5 ) {
         String javaPath = args[0];
         try {
            float willingness        = Float.parseFloat(args[1]);
            int   transferDelay      = Integer.parseInt(args[2]);
            int   minForwardReqDelay = Integer.parseInt(args[3]);
            int   maxForwardReqDelay = Integer.parseInt(args[4]);
            
            new RunMatrixCollaborator(javaPath, willingness, transferDelay, minForwardReqDelay, maxForwardReqDelay);            
         } catch (NumberFormatException nfex) {
            logger.info("NumberFormatException { javaPath: "+javaPath+" willingness: " +args[1]+ ", transferDelay: "+args[2]+", minIReqForwardDelay: "+ args[3] +", maxIReqForwardDelay: "+ args[4]+" }", nfex);
            logger.info("Incorrect values to initialize Collaborator.\n\n\n");
            printHelp();
         }
      } else {
         printHelp();
      }
   }
   
}
	

