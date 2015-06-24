package martin.app.bagoftask.matrix;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Vector;

import martin.app.bagoftask.matrix.gui.RunMatrixGUI;
import martin.mogrid.common.logging.MoGridLog4jConfigurator;
import martin.mogrid.common.util.MoGridString;
import martin.mogrid.common.util.ProjectHeader;
import martin.mogrid.p2pdl.api.MogridApplicationFacade;
import martin.mogrid.p2pdl.api.RequestIdentifier;
import martin.mogrid.submission.task.bagoftask.protocol.BoTDispatcherFactory;
import martin.mogrid.tl.asl.bagoftask.BoTAdaptationSublayer;
import martin.mogrid.tl.asl.bagoftask.BoTResult;
import martin.mogrid.tl.asl.bagoftask.BoTResultHistory;

import org.apache.log4j.Logger;


public class RunMatrix implements MogridApplicationFacade { 

   //Manutencao do arquivo de log do servico
   private static final Logger logger = Logger.getLogger(RunMatrix.class);  
   
   
   private static final String BASEDIR = System.getProperty("mogrid.home") + File.separator + "MatrixFiles";
   private static final String RESOURCE_QUERY = "java";
   
   private String[]     parameters;
   private Vector       matrixFiles;
   private File         matA;
   private File         matB;  
   private int          numJobs;
   private String       result;
   
   private BoTAdaptationSublayer gridJobDiscovery = null;
   private RunMatrixGUI          runMatrixGUI     = null;
   
   
   //TODO mudar passagem de parametros
	public RunMatrix() {      
      Matrix.setBaseDir(BASEDIR);        
      gridJobDiscovery = new BoTAdaptationSublayer(this);
      gridJobDiscovery.registerTaskDispatcherFactory(new BoTDispatcherFactory());
      gridJobDiscovery.setCollaborationLevel(1);
      gridJobDiscovery.setTransferDelay(10); // em milisegundos
      
      runMatrixGUI     = new RunMatrixGUI(this);    
      matrixFiles      = new Vector();
	}
   
   public void registerCompilers(String identifier, String path) {
      //String[] keywords = {"java", "jre", "1.5.0_06"};
      //gridJobDiscovery.register("java", "Java 2 Platform Standard Edition 5.0", keywords, "C:\\lng\\java\\jre\\bin");      
      if ( path != null ) { 
         String fullPath = path + File.separator + identifier;
         gridJobDiscovery.register(identifier, null, null, fullPath);
      }
   }

   public void createMatrixes(int rowA, int columnA, int rowB, int columnB, int rand) throws NumberFormatException {
      if ( rowA <=0 || columnA <=0 || rowB <=0 || columnB <=0 ) {
         runMatrixGUI.println("Invalid values informed for row/columns: A["+rowA+","+columnA+"] B["+rowB+","+columnB+"]\n"); 
         throw new NumberFormatException();
      }
      if ( columnA != rowB ) {
         runMatrixGUI.println("The column value of Matrix A must be equal to the row value of Matrix B: A["+rowA+","+columnA+"] B["+rowB+","+columnB+"]\n"); 
         throw new NumberFormatException();
      }
      
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

   public void createMatrices(int rowA, int columnA, int rowB, int columnB) throws NumberFormatException {
      createMatrixes(rowA, columnA, rowB, columnB, 10);
   }   
   
   private void removeFilesGenerated() {
      File execDir = new File(BASEDIR);
      //logger.debug("* DELETE EXEC DIR FILES : "+ execDir.getAbsolutePath());  
      if ( execDir != null && execDir.exists() ) {
         String matrixJar = BASEDIR + File.separator + "matrix.jar";
         File[] dirFiles = execDir.listFiles();
         for ( int i=0; i<dirFiles.length; i++ ) {
            if ( matrixJar.equals(dirFiles[i].getAbsolutePath()) ) { continue; }            
            dirFiles[i].delete();
            //logger.debug("* DELETE file : "+ dirFiles[i].getAbsolutePath()); 
         }
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
   
   public String readMatrixA() {
      return readResultFile(matA);
   }
   
   public String readMatrixB() {
      return readResultFile(matB);
   }
   
   public String readMatrixAB() {
      return result;      
   }
   
   public void sendJobRequest() {
      String executable = "java";
      gridJobDiscovery.submitJobRequest(RESOURCE_QUERY, numJobs, numJobs, matrixFiles, executable, parameters); 
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
         runMatrixGUI.showMatrixAB();
      }      
   }	
   

   private synchronized String readResultFile(File result) {
      String resultStr = ""; 
      
      if ( result==null || !result.exists() ) {
         return resultStr;
      }
 
      try {
         FileInputStream stream = new FileInputStream(result);
         BufferedReader  in     = new BufferedReader (new InputStreamReader(stream));
         
         //Continue to read lines while there are still some left to read  
         String line = null;
         Vector matrixStr = new Vector();
         while ( (line = in.readLine()) != null ) {
            if ( ! line.startsWith("Line") ) { continue; }
            matrixStr.add(line);
         }

         int size = matrixStr.size();
         String[] matrixPrt = new String [size];
         
         Iterator matrixStrIt = matrixStr.iterator();
         while ( matrixStrIt.hasNext() ) {
            String   matrixStrLine   = (String)matrixStrIt.next();
            //String[] matrixStrFields = matrixStrLine.trim().split("=");
            String[] matrixStrFields = MoGridString.split(matrixStrLine.trim(), "=");
            if ( matrixStrFields != null && matrixStrFields.length == 2 ) {
               String lineNum = matrixStrFields[0].replaceFirst("Line", "");
               int num = Integer.parseInt(lineNum);
               String lineStr = matrixStrFields[1].replaceAll(",", ", ");
               matrixPrt[num] = lineStr; 
            }
         }
         
         size = matrixPrt.length; 
         for ( int i=0; i<size; i++ ) {
            resultStr += matrixPrt[i] + "\n";            
         }
         in.close();
         
      } catch (FileNotFoundException e) {
        logger.error("File with result not found: " + result.getAbsolutePath() + "\n[ERROR] " + e.getMessage());

      } catch (IOException e) {
         logger.error("It was not possible to read the file with job execution result: " + result.getAbsolutePath() + "\n[ERROR] " + e.getMessage());
      }

      return resultStr;
   }
   
   public void finalize() {   
      removeFilesGenerated();   
      gridJobDiscovery.finalize();
   }
      
   //Execucao da app
   public static void main(String[] args) {  
      MoGridLog4jConfigurator.configure();
      ProjectHeader.print();
      
      new RunMatrix();
   }
   
}
	

