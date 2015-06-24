package martin.app.bagoftask.matrix;

import java.io.File;
import java.util.Random;
import java.util.Vector;

import martin.mogrid.common.logging.MoGridLog4jConfigurator;
import martin.mogrid.common.util.ProjectHeader;
import martin.mogrid.common.util.SystemUtil;
import martin.mogrid.p2pdl.api.MogridApplicationFacade;
import martin.mogrid.p2pdl.api.RequestIdentifier;
import martin.mogrid.submission.task.bagoftask.protocol.BoTDispatcherFactory;
import martin.mogrid.tl.asl.bagoftask.BoTAdaptationSublayer;
import martin.mogrid.tl.asl.bagoftask.BoTResult;
import martin.mogrid.tl.asl.bagoftask.BoTResultHistory;

import org.apache.log4j.Logger;


public class RunMatrixInitiatorCollaborator implements MogridApplicationFacade, Runnable { 

   //Manutencao do arquivo de log do servico
   private static final Logger logger = Logger.getLogger(RunMatrixInitiatorCollaborator.class); 
      
   private static final String BASEDIR = System.getProperty("mogrid.home") + File.separator + "MatrixFiles";
   private static final String RESOURCE_QUERY = "java";
   
   private String[] parameters;
   private Vector   matrixFiles;
   private File     matA;
   private File     matB;  
   private int      numJobs;
   private String   result;
   private Thread   requesterThread = null;   
   private int      requestInterval;

   protected BoTAdaptationSublayer gridJobDiscovery = null;  
   
   
   private RunMatrixInitiatorCollaborator(int numJobs, int requestInterval, String javaPath) {
      Matrix.setBaseDir(BASEDIR);         
      matrixFiles = new Vector();
      this.numJobs = numJobs;
      this.requestInterval = requestInterval;
      
      gridJobDiscovery = new BoTAdaptationSublayer(this);
      gridJobDiscovery.registerTaskDispatcherFactory(new BoTDispatcherFactory());
      
      if ( javaPath != null ) {      
          String fullPath = javaPath + File.separator + "java"; 
          gridJobDiscovery.register("java", "java", null, fullPath);
      }
      
      if( requesterThread == null ) {
         requesterThread = new Thread(this, "ContextListener");
         requesterThread.start();
      }      
   }
   

   public void run() {
      Thread myThread = Thread.currentThread();
      float timeTowait = requestInterval/2;
      while( requesterThread == myThread ) {         
         SystemUtil.sleep(timeTowait);  //Time to wait in seconds
         createMatrixes(numJobs);         
         sendJobRequest();             //Send a job request for MoGrid
         SystemUtil.sleep(timeTowait);  //Time to wait in seconds
      }
      
   }

   public void sendJobRequest() {
      String executable = "java";
      gridJobDiscovery.submitJobRequest(RESOURCE_QUERY, numJobs, numJobs, matrixFiles, executable, parameters); 
   }

   public void createMatrixes(int numJobs) {
      numJobs = Math.round(Math.abs(numJobs));
      
      Random rand = new Random();      
      // Random integers that range from from 0 to 20 -> we need [1, 20]
      int rowA    = numJobs;
      //rowA = ( rowA==0 ) ? 10 : rowA;
      int columnA = rand.nextInt(19) + 1;
      int rowB    = columnA;
      int columnB = rand.nextInt(19) + 1;
      //columnB = ( columnB==0 ) ? 10 : columnB;
      
      createMatrixes(rowA, columnA, rowB, columnB, 11);
   }  
   
   public void createMatrixes(int rowA, int columnA, int rowB, int columnB, int rand) {
      matA = Matrix.create(rowA, columnA, rand, "MatrixA"); 

      File[] linesA = Matrix.extractLines(matA);
      numJobs = linesA.length;  
      
      matB = Matrix.create(rowB, columnB, rand, "MatrixB");
      String matrix = matB.getName(); 
      
      File exec = new File(BASEDIR + File.separator + "matrix.jar");
      String execFile = exec.getName();      
           
      parameters = new String[numJobs];     
      for( int i=0; i<numJobs; i++ ) {
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
   

   public static void main(String[] args) {  
      MoGridLog4jConfigurator.configure();
      ProjectHeader.print();
      
      if ( args != null && args.length == 3 ) {
         int    numJobs  = Integer.parseInt(args[0]);
         int    interval = Integer.parseInt(args[1]);
         String javaPath = args[2];
         new RunMatrixInitiatorCollaborator(numJobs, interval, javaPath);
        
      } else if ( args != null && args.length == 2 ) {
         int    numJobs  = Integer.parseInt(args[0]);
         int    interval = Integer.parseInt(args[1]);
         new RunMatrixInitiatorCollaborator(numJobs, interval, null);
         
      } else {
         logger.info("Program Usage:");
         logger.info("java martin.app.gridjob.matrix.RunMatrixInitiator <numJobs> <request interval> <javaPath>");
         logger.info("\nWhere <numJobs> indicate the number of jobs to be executed. (Initiator)");
         logger.info("\nWhere <request interval> indicate the time in seconds for send requests (IReq messages). (Initiator)");
         logger.info("\nWhere <javaPath> points to java.exe dir. (Collaborator)");
         logger.info("<javaPath> example: /usr/java/jre/bin");
         logger.info("\n<javaPath> is optional, for APP run in Collaborator mode too.");
         SystemUtil.normalExit();
      }
   }
   
}
	

