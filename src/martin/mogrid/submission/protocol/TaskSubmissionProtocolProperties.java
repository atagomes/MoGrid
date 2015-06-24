/**
 * @author lslima
 *
 * Created on 20/10/20056
 */

package martin.mogrid.submission.protocol;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import martin.mogrid.common.network.NetworkUtil;

import org.apache.log4j.Logger;



public class TaskSubmissionProtocolProperties {
      
   //Manutencao do arquivo de log do servico
   private static final Logger logger = Logger.getLogger(TaskSubmissionProtocolProperties.class);   
   //Porta do protocolo de submissao : O valor da porta deve estar no intervalo (1024, 65535]      
   private static final int DEFAULT_SUBMISSION_PORT = 60010;   // porta na qual as submissoes de tarefa sao atendidas   
   //Arquivo de propriedades do protocolo de coordenacao
   private static final String propsFile = System.getProperty("mogrid.home") + File.separator + "conf" + File.separator + "SubmissionProtocol.properties";
   //Propriedades associadas ao protocolo de submissao de tarefas
   private static int submissionPort = DEFAULT_SUBMISSION_PORT;

   
   //Carrega propriedades do gerente de contexto com base no arquivo de propriedades   
   public static void load() {      
      try {
         Properties tspProperties = new Properties();
         FileInputStream programFileIn = new FileInputStream(propsFile);
         tspProperties.load( programFileIn );
         programFileIn.close();
         
         String property  = null;
         property         = tspProperties.getProperty("submission.port");   
         submissionPort   = (property != null && NetworkUtil.portIsValid(property)) ? Integer.parseInt(property) : DEFAULT_SUBMISSION_PORT;
         
      } catch (FileNotFoundException fnfe) {
         logger.warn("Task Submission Protocol properties file not found: " + fnfe.getMessage() + ". Using default properties: " + showProperties());
         
      } catch (IOException ioe) {
         logger.warn("Error while acessing Task Submission Protocol properties file: " + ioe.getMessage() + ". Using default properties: " + showProperties());
      }
   }
   
   
   //Salva propriedades do gerente de contexto no arquivo de propriedades
   public static void save() throws TaskSubmissionProtocolPropertiesException {
      try {
         Properties tspProperties = new Properties();
         tspProperties.setProperty( "submission.port" , Integer.toString(submissionPort)          );
               
         FileOutputStream programFileOut = new FileOutputStream(propsFile);
         tspProperties.store( programFileOut, "Task Submission Protocol Properties File" );
         programFileOut.close();
         logger.info("Task Submission Protocol properties file was saved."); 
      
      } catch (FileNotFoundException fnfe) {
         throw new TaskSubmissionProtocolPropertiesException("Task Submission Protocol properties file not found.", fnfe);
         
      } catch (IOException ioe) {
         throw new TaskSubmissionProtocolPropertiesException("Error while acessing Task Submission Protocol properties file.", ioe);
      }
   }
   

   
   //Leitura das propriedades
   public static int getSubmissionPort() {      
      return submissionPort;
   } 

   //Atribuicao das propriedades
   public static void setSubmissionPort(int coordinatorPort) {    
      if ( NetworkUtil.portIsValid(coordinatorPort) )
         TaskSubmissionProtocolProperties.submissionPort = coordinatorPort;
   } 
   
   //Imprime
   public static String showProperties() {
      String tspProperties = "\nTask Submission Port: " + submissionPort;      
      return tspProperties;
   }
   
}
