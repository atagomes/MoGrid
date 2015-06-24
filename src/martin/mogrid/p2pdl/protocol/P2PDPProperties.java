
package martin.mogrid.p2pdl.protocol;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import martin.mogrid.common.network.LocalHost;
import martin.mogrid.common.network.NetworkUtil;

import org.apache.log4j.Logger;



/**
 * @author lslima
 *
 * Created on 20/06/2005
 */
public class P2PDPProperties {
      
   //Manutencao do arquivo de log do servico
   private static final Logger logger = Logger.getLogger(P2PDPProperties.class);  
   
   private static final int    DEFAULT_SCAN_INTERVAL       = 1000;  // intervalo de leitura das mensagens do protocolo em milisegundos
   
   //Endereco e porta do coordenador e do iniciador
   //O valor da porta deve estar no intervalo (1024, 65535]      
   private static final int    DEFAULT_COORDINATOR_PORT    = 50010;                           // porta na qual o coordenador atende as requisicoes do iniciador
   private static final String DEFAULT_COORDINATOR_ADDRESS = LocalHost.getLocalHostAddress(); // endereco do coordenador
  
   private static final int    DEFAULT_INITIATOR_PORT      = 50020;                           // porta na qual o iniciador recebe a lista de respostas (colaboradores) do coordenador
   private static final String DEFAULT_INITIATOR_ADDRESS   = LocalHost.getLocalHostAddress(); // endereco do iniciador
   
   
   
   //Endereco (Multicast ou Broadcast), porta e intervalo de scan do protocolo de coordenacao
   //=> Multicast: Class D IP addresses are in the range 224.0.0.0 to 239.255.255.255, inclusive
   //              The address 224.0.0.0 is reserved and should not be used
   //=> Broadcast: 255.255.255.255 or <network address> + 255
   private static final String DEFAULT_COORDINATION_ADDRESS = "255.255.255.255"; // endereco multicast do protocolo de comunicacao
   //O valor da porta deve estar no intervalo (1024, 65535] 
   private static final int    DEFAULT_COORDINATION_PORT    = 50030;        // porta do protocolo de comunicacao
   //Classe default para o calculo de tempo de espera de envio de mensagens CRep
   private static final String DEFAULT_TIMERFUNCTION_CLASS = "martin.mogrid.p2pdl.collaboration.criterias.CollaborationReplyTimer";
   
   //Arquivo de propriedades do protocolo de coordenacao
   private static final String propsFile = System.getProperty("mogrid.home") + File.separator + "conf" + File.separator + "DiscoveryProtocol.properties";

   //Propriedades associadas ao coordenador e ao iniciador
   private static int    coordinatorScanInterval = DEFAULT_SCAN_INTERVAL;
   private static String coordinatorAddress      = DEFAULT_COORDINATOR_ADDRESS; 
   private static int    coordinatorPort         = DEFAULT_COORDINATOR_PORT;

   private static int    initiatorScanInterval = DEFAULT_SCAN_INTERVAL;
   private static String initiatorAddress      = DEFAULT_INITIATOR_ADDRESS; 
   private static int    initiatorPort         = DEFAULT_INITIATOR_PORT;   

   //Propriedades associadas ao protocolo de coordenacao
   private static int    coordinationScanInterval   = DEFAULT_SCAN_INTERVAL;    
   private static String coordinationAddress        = DEFAULT_COORDINATION_ADDRESS; 
   private static int    coordinationPort           = DEFAULT_COORDINATION_PORT;  
   
   //Class that implements the timer function to send collaboration replies (CReps)
   private static String timerFunctionClass = null;
 
   //Carrega propriedades do gerente de contexto com base no arquivo de propriedades   
   public static void load() {      
      try {
         Properties cpProperties = new Properties();
         
         FileInputStream programFileIn = new FileInputStream(propsFile);
         cpProperties.load( programFileIn );
         programFileIn.close();
         
         String property           = null;
         //coordenador
         property                  = cpProperties.getProperty("coordinator.scan");
         coordinatorScanInterval   = (property != null)                               ? Integer.parseInt(property) : DEFAULT_SCAN_INTERVAL;
         property                  = cpProperties.getProperty("coordinator.address");
         coordinatorAddress        = (property != null)                               ? property                   : DEFAULT_COORDINATOR_ADDRESS;
         property                  = cpProperties.getProperty("coordinator.port");   
         coordinatorPort           = (property != null && NetworkUtil.portIsValid(property)) ? Integer.parseInt(property) : DEFAULT_COORDINATOR_PORT;
         //iniciador
         property                  = cpProperties.getProperty("initiator.scan");
         initiatorScanInterval     = (property != null)                               ? Integer.parseInt(property) : DEFAULT_SCAN_INTERVAL;
         property                  = cpProperties.getProperty("initiator.address");
         initiatorAddress          = (property != null)                               ? property                   : DEFAULT_INITIATOR_ADDRESS;
         property                  = cpProperties.getProperty("initiator.port");   
         initiatorPort             = (property != null && NetworkUtil.portIsValid(property)) ? Integer.parseInt(property) : DEFAULT_INITIATOR_PORT;
         //protocolo de coordenacao 
         property                  = cpProperties.getProperty("coordination.scan");
         coordinationScanInterval  = (property != null)                               ? Integer.parseInt(property) : DEFAULT_SCAN_INTERVAL;
         property                  = cpProperties.getProperty("coordination.address");
         coordinationAddress       = (property != null)                               ? property                   : DEFAULT_COORDINATION_ADDRESS;
         property                  = cpProperties.getProperty("coordination.port");   
         coordinationPort          = (property != null && NetworkUtil.portIsValid(property)) ? Integer.parseInt(property) : DEFAULT_COORDINATION_PORT;        
         //Classe responsavel pelo calculo do tempo de espera no envio de mensagens CRep
         property                  = cpProperties.getProperty("coordination.timer.class");
         timerFunctionClass        = (property != null)                               ? property                   : DEFAULT_TIMERFUNCTION_CLASS;
         
      } catch (FileNotFoundException fnfe) {
         logger.warn("Coordination Protocol properties file not found: " + fnfe.getMessage() + ". Using default properties: " + showProperties());
         
      } catch (IOException ioe) {
         logger.warn("Error while acessing Coordination Protocol properties file: " + ioe.getMessage() + ". Using default properties: " + showProperties());
      }
   }    
   
   //Leitura das propriedades  
   public static String getCoordinatorAddress() {
      return coordinatorAddress;
   }

   public static int getCoordinatorPort() {      
      return coordinatorPort;
   } 
   
   public static int getCoordinatorScanInterval() {
      return coordinatorScanInterval;
   } 
   
   public static String getInitiatorAddress() {
      return initiatorAddress;
   }

   public static int getInitiatorPort() {      
      return initiatorPort;
   }  
   
   public static int getInitiatorScanInterval() {
      return initiatorScanInterval;
   } 
   
   public static String getCoordinationAddress() {
      return coordinationAddress;
   }

   public static int getCoordinationPort() {      
      return coordinationPort;
   }    
   
   public static int getCoordinationScanInterval() {
      return coordinationScanInterval;
   } 
   
   public static String getTimerFunctionClass() {
      return timerFunctionClass;
   }
      
   
   //Salva propriedades do gerente de contexto no arquivo de propriedades
   public static void save() throws P2PDPPropertiesException {
      try {
         Properties cpProperties = new Properties();

         cpProperties.setProperty( "coordinator.scan"         , Integer.toString(coordinatorScanInterval)  );         
         cpProperties.setProperty( "coordinator.address"      , coordinatorAddress                         );
         cpProperties.setProperty( "coordinator.port"         , Integer.toString(coordinatorPort)          );
         
         cpProperties.setProperty( "initiator.scan"           , Integer.toString(initiatorScanInterval)    );
         cpProperties.setProperty( "initiator.address"        , initiatorAddress                           );
         cpProperties.setProperty( "initiator.port"           , Integer.toString(initiatorPort)            );
         
         cpProperties.setProperty( "coordination.scan"        , Integer.toString(coordinationScanInterval) );
         cpProperties.setProperty( "coordination.address"     , coordinationAddress                        );
         cpProperties.setProperty( "coordination.port"        , Integer.toString(coordinationPort)         );
        
         cpProperties.setProperty( "coordination.timer.class" , timerFunctionClass                         );
                    
         FileOutputStream programFileOut = new FileOutputStream(propsFile);
         cpProperties.store( programFileOut, "Coordination Protocol Properties File" );
         programFileOut.close();
         logger.info("Coordination Protocol properties file was saved."); 
      
      } catch (FileNotFoundException fnfe) {
         throw new P2PDPPropertiesException("Coordination Protocol properties file not found.", fnfe);
         
      } catch (IOException ioe) {
         throw new P2PDPPropertiesException("Error while acessing Coordination Protocol properties file.", ioe);
      }
   }
   
   //Atribuicao das propriedades
   public static void setCoordinatorAddress(String coordinatorAddress) {
      P2PDPProperties.coordinatorAddress = coordinatorAddress;
   }
   
   public static void setCoordinatorPort(int coordinatorPort) {    
      if ( NetworkUtil.portIsValid(coordinatorPort) )
         P2PDPProperties.coordinatorPort = coordinatorPort;
   } 
   
   public static void setCoordinatorScanInterval(int coordinatorScanInterval) {      
      P2PDPProperties.coordinatorScanInterval = coordinatorScanInterval;
   }  
   
   public static void setInitiatorAddress(String initiatorAddress) {
      P2PDPProperties.initiatorAddress = initiatorAddress;
   }
   
   public static void setInitiatorPort(int initiatorPort) {    
      if ( NetworkUtil.portIsValid(initiatorPort) )
         P2PDPProperties.initiatorPort = initiatorPort;
   }  
   
   public static void setInitiatorScanInterval(int initiatorScanInterval) {      
      P2PDPProperties.initiatorScanInterval = initiatorScanInterval;
   }  
   
   public static void setCoordinationAddress(String coordinationAddress) {
      P2PDPProperties.coordinationAddress = coordinationAddress;
   }
   
   public static void setCoordinationPort(int coordinationPort) {    
      if ( NetworkUtil.portIsValid(coordinationPort) )
         P2PDPProperties.coordinationPort = coordinationPort;
   }    
   
   public static void setCoordinationScanInterval(int coordinationScanInterval) {      
      P2PDPProperties.coordinationScanInterval = coordinationScanInterval;
   }   
  
   
   public static String showProperties() {
      String p2pdpProperties = "";
      
      p2pdpProperties += "\nCoordination Scan Interval: " + coordinationScanInterval;
      p2pdpProperties += "\nCoordination Address: " + coordinationAddress + ":" + coordinationPort;
      p2pdpProperties += "\nCoordinator Scan Interval: " + coordinatorScanInterval;
      p2pdpProperties += "\nCoordinator Address: " + coordinatorAddress + ":" + coordinatorPort;
      p2pdpProperties += "\nInitiator Scan Interval: " + initiatorScanInterval;
      p2pdpProperties += "\nInitiator Address: " + initiatorAddress + ":" + initiatorPort;
      p2pdpProperties += "\nCoordination Timer Function Class: " + timerFunctionClass;
     
      return p2pdpProperties;
   }
}
