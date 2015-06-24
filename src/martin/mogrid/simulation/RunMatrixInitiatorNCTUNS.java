package martin.mogrid.simulation;

import java.io.File;
import java.util.Random;
import java.util.Vector;

import martin.app.bagoftask.matrix.Matrix;
import martin.mogrid.common.logging.MoGridLog4jConfigurator;
import martin.mogrid.common.util.ProjectHeader;
import martin.mogrid.common.util.SystemUtil;
import martin.mogrid.p2pdl.api.MogridApplicationFacade;
import martin.mogrid.p2pdl.api.RequestIdentifier;
import martin.mogrid.tl.asl.bagoftask.BoTResult;
import martin.mogrid.tl.asl.bagoftask.BoTResultHistory;

import org.apache.log4j.Logger;


public class RunMatrixInitiatorNCTUNS implements MogridApplicationFacade, Runnable { 

   //Manutencao do arquivo de log do servico
   private static final Logger logger = Logger.getLogger(RunMatrixInitiatorNCTUNS.class); 
     
   protected BoTInitiatorNCTUnsAdaptationSublayer gridJobDiscovery = null;   
   private static final String BASEDIR = System.getProperty("mogrid.home") + File.separator + "MatrixFiles";
   
   private String[] parameters;
   private Vector   matrixFiles;
   private File     matA;
   private File     matB;  
   private int      numRequests;
   private int      numCollabs;
   private int      numJobs;
   private String   result;
   private Thread   requesterThread = null;   
   private int      requestInterval;
   

   //<numRequests> <numMaxReplies> <numTasks> <replyDelay> <requestDiameter> <requestInterval>
   private RunMatrixInitiatorNCTUNS(int numRequests, int numCollabs, int numJobs, long maxRepDelay, int reqDiameter, int requestInterval) {
      Matrix.setBaseDir(BASEDIR);         
      matrixFiles = new Vector();

      this.numRequests = numRequests;
      this.numCollabs = numCollabs;
      this.numJobs  = numJobs;
      this.requestInterval = requestInterval;

      gridJobDiscovery = new BoTInitiatorNCTUnsAdaptationSublayer(this);
      gridJobDiscovery.setNumMaxReplies(numCollabs);
      
      if( requesterThread == null ) {
         requesterThread = new Thread(this, "ContextListener");
         requesterThread.start();
      }   
      gridJobDiscovery.setMaxReplyDelay(maxRepDelay);
      gridJobDiscovery.setRequestDiameter(reqDiameter);
   }

   public void run() {
      Thread myThread = Thread.currentThread();
      float timeTowait = requestInterval/2;
     
      int i=0;
      while( requesterThread == myThread ) {         
         SystemUtil.sleep(timeTowait);       //Time to wait in seconds
         if ( i < numRequests ) {
            //createMatrixes(numJobs);         
            sendJobRequest();                //Send a job request for MoGrid
         }
         i++;
         SystemUtil.sleep(timeTowait);       //Time to wait in seconds
      }    
   }

   
   public void sendJobRequest() {
      String executable = "java";
      gridJobDiscovery.submitJobRequest(numJobs, matrixFiles, executable, parameters); 
   }

   public void createMatrixes(int numJobs) {
      numJobs = Math.round(Math.abs(numJobs));
      
      Random rand = new Random();      
      // Random integers that range from from [0 to numJobs) -> we need [1, numJobs]
      int rowA    = numJobs;
      int columnA = rand.nextInt(numJobs-1) + 1;
      int rowB    = columnA;
      int columnB = rand.nextInt(numJobs-1) + 1;
      
      createMatrixes(rowA, columnA, rowB, columnB, 11);
   } 
   
   public void createMatrixes(int rowA, int columnA, int rowB, int columnB, int rand) {
      matA = Matrix.create(rowA, columnA, rand, "MatrixA"); 

      File[] linesA = Matrix.extractLines(matA);
      int rowsSize = linesA.length;  
      
      matB = Matrix.create(rowB, columnB, rand, "MatrixB");
      String matrix = matB.getName(); 
      
      File exec = new File(BASEDIR + File.separator + "matrix.jar");
      String execFile = exec.getName();      
           
      parameters = new String[rowsSize];     
      for( int i=0; i<rowsSize; i++ ) {
         String lineFileName = mountLineFileName(i);
         parameters[i] = mountJobArguments(matrix, lineFileName, execFile);
         File[] files  = { getFilebyName(linesA, lineFileName), matB, exec };
         matrixFiles.add(i, files);
      } 
   }   
   
   private File getFilebyName(File[] files, String fileName) {
      if ( files == null ) { return null; }      

      File file = null;   
      int length = files.length;
      for ( int i=0; i<length; i++ ) {
         if ( files[i].getName().equals(fileName) ) {  // equals -> Unix-like OSs are case sensitive
            file = files[i];
            break;
         }
      }
      return file;
   }  

   private String mountLineFileName(int line) {
      return "Line"+line+".properties";
   }

   //Montando as opcoes da linha de comando (java) a ser executada:
   //Exemplo: (java) -Dline=Line0.properties -Dmatrix=MatrixB.properties -jar gridTask.jar 
   private String mountJobArguments(String matrixFile, String linFile, String execFile) {
      String params = "";
      params += " \"-Dline="   + linFile    + "\""; 
      params += " \"-Dmatrix=" + matrixFile + "\"";   
      params += " -jar "       + execFile;
      
      return params;
   }  
   
  
   public void handleMogridResource(RequestIdentifier reqID, Object resource) {
      if ( resource != null && resource instanceof BoTResultHistory) { 
         result = "";
         BoTResultHistory resultHistory = (BoTResultHistory)resource;
         int numResults = resultHistory.size();
         String[] line = new String[numResults];
         for ( int job=0; job<numResults; job++ ) {
            BoTResult gridResult = resultHistory.getElementAt(job);
            Object objResult = gridResult.getResult();
            if ( objResult != null ) {
               if ( objResult instanceof String ) {             
                  line[job] = (String)objResult;
                  String lineStr = line[job].trim().replaceAll(" ", ", ");
                  result += lineStr + "\n";               
               }  
            }
         }
         logger.info("MatrixAB:\n" + result.trim());
         logger.trace("[MatrixAB] - Result for request " + reqID + ":\n" + result.trim());
      }      
   }  
   
   private static void printHelp() {
      logger.info("Program Usage:");
      logger.info("java martin.app.gridjob.matrix.RunMatrixInitiator <numRequests> <requestInterval> <numMaxReplies> <numTasks> <maxReplyDelay> <requestDiameter>");
      logger.info("\nWhere <numRequests> indicate the number of requests that must be sent.");
      logger.info("\nWhere <requestDiameter> indicate the diameter of request (number of hops that IReq will traverse).");
      logger.info("\nWhere <requestInterval> indicate the time in seconds for send requests (IReq messages).");
      logger.info("\nWhere <numMaxReplies> indicate the maximum number of replies (CReps) for each IReq message.\n");
      logger.info("\nWhere <numTasks> indicate the number of tasks to be executed in each request.\n");
      logger.info("\nWhere <maxReplyDelay> indicate the maximum time to wait for replies.");

      SystemUtil.abnormalExit();
   }

   public static void main(String[] args) {  
      MoGridLog4jConfigurator.configure();
      ProjectHeader.print();
      
      //<numRequests> <numMaxReplies> <numTasks> <replyDelay> <requestDiameter> <requestInterval>
      if ( args != null && args.length == 6 ) {
         try {
            int    numRequests    = Integer.parseInt(args[0]);
            int    numCollabs     = Integer.parseInt(args[1]);
            int    numTasks       = Integer.parseInt(args[2]);
            long   maxReplyDelay  = Long.parseLong(args[3]);  
            int    reqDiameter    = Integer.parseInt(args[4]);
            int    reqInterval    = Integer.parseInt(args[5]);
          
            new RunMatrixInitiatorNCTUNS(numRequests, numCollabs, numTasks, maxReplyDelay, reqDiameter, reqInterval);
         
         } catch (NumberFormatException nfex) {
            logger.info("NumberFormatException { numRequests: " +args[0]+", numCollabs: " +args[1]+ " numTasks: "+args[2]+" maxReplyDelay: "+ args[3] +" reqDiameter: "+ args[4] + ", requestInterval: "+ args[5]+" }: "+ nfex.getMessage());
            logger.info("It is not possible execute the application, parameters incorrect.\n\n\n");
            printHelp();
         }
         
      } else {
         printHelp();
      }
   }
   
}
	

