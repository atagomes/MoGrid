package martin.mogrid.simulation;

import martin.mogrid.common.logging.MoGridLog4jConfigurator;
import martin.mogrid.common.util.ProjectHeader;
import martin.mogrid.common.util.SystemUtil;

import org.apache.log4j.Logger;


public class RunMatrixCollaboratorNCTUNS { 

   //Manutencao do arquivo de log do servico
   private static final Logger logger = Logger.getLogger(RunMatrixCollaboratorNCTUNS.class); 
   
   
   protected BoTCollaboratorNCTUnsAdaptationSublayer gridJobDiscovery = null;  
   
   private RunMatrixCollaboratorNCTUNS(String javaPath) {    
      gridJobDiscovery = new BoTCollaboratorNCTUnsAdaptationSublayer();                   
      gridJobDiscovery.register("java", "java", null, javaPath);
   }
   
	private RunMatrixCollaboratorNCTUNS(String javaPath, float willingness, int gama, int minForwardRequestDelay, int maxForwardRequestDelay) {
      this(javaPath);
      gridJobDiscovery.setCollaborationLevel(willingness);
      gridJobDiscovery.setTransferDelay(gama);
      // Configura os valores associados ao retardo de retransmissao da mensagem de requisicao (IReq), 
      // para minimizar as colisoes no meio sem fio (tempo em milisegundos)
      gridJobDiscovery.setForwardRequestDelay(minForwardRequestDelay, maxForwardRequestDelay);  
	}

    public static void printHelp() {
       logger.info("Program Usage:");
       logger.info("java martin.app.gridjob.matrix.RunMatrixCollaboratorNCTUNS <javaPath> <willingness> <transferDelay> <minimumForwardRequestDelay> <maximumForwardRequestDelay>");
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
            float  willingness          = Float.parseFloat(args[1]);
            int    transferDelay        = Integer.parseInt(args[2]);
            int    minIReqForwardDelay  = Integer.parseInt(args[3]);
            int    maxIReqForwardDelay  = Integer.parseInt(args[4]);
            
            new RunMatrixCollaboratorNCTUNS(javaPath, willingness, transferDelay, minIReqForwardDelay, maxIReqForwardDelay);
            
         } catch (NumberFormatException nfex) {
            logger.info("NumberFormatException { javaPath: "+javaPath+" willingness: " +args[1]+ ", transferDelay: "+ args[2] +", minIReqForwardDelay: "+ args[3] +", maxIReqForwardDelay: "+ args[5]+" }", nfex);
            logger.info("Using default values to Collaborator parameters.\n\n\n");            
            printHelp();            
         }
         
      } else {
         printHelp();
      }
   }
   
}
	

