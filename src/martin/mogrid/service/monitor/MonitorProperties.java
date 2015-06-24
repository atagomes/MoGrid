
package martin.mogrid.service.monitor;

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
public class MonitorProperties {
      
   //Manutencao do arquivo de log do servico
   private static final Logger logger = Logger.getLogger(MonitorProperties.class);  
   
   //Endereco e porta do coordenador
   //O valor da porta deve estar no intervalo (1024, 65535]    
   private static final int    DEFAULT_LISTENER_PORT   = 50000;                           // porta na qual o coordenador atende as requisicoes do iniciador
   private static final String DEFAULT_LITENER_ADDRESS = LocalHost.getLocalHostAddress(); // endereco do coordenador
   private static final int    DEFAULT_SCAN_INTERVAL   = 3000;                            // intervalo de leitura das mensagens do protocolo em milisegundos         
   //Arquivo de propriedades do protocolo de coordenacao
   private static final String propsFile = System.getProperty("mogrid.home") + File.separator + "conf" + File.separator + "GlobusMonitor.properties";
   
   //Propriedades associadas ao coordenador
   private static String listenerAddress     = DEFAULT_LITENER_ADDRESS; 
   private static int    listenerPort        = DEFAULT_LISTENER_PORT; 
   private static int    monitorScanInterval = DEFAULT_SCAN_INTERVAL; 
  
   //Carrega propriedades do gerente de contexto com base no arquivo de propriedades   
   public static void load() { 	  
      try {
         Properties cpProperties = new Properties();
         FileInputStream programFileIn = new FileInputStream(propsFile);
         cpProperties.load( programFileIn );
         programFileIn.close();
         
         String property         = null;
         property                = cpProperties.getProperty("listener.address");
         listenerAddress         = (property != null)                                      ? property                   : DEFAULT_LITENER_ADDRESS;
         property                = cpProperties.getProperty("listener.port");   
         listenerPort            = (property != null && NetworkUtil.portIsValid(property)) ? Integer.parseInt(property) : DEFAULT_LISTENER_PORT;
         property                = cpProperties.getProperty("monitor.scan");
         monitorScanInterval     = (property != null)                                      ? Integer.parseInt(property) : DEFAULT_SCAN_INTERVAL;
                 
      } catch (FileNotFoundException fnfe) {
         logger.warn("Coordination Protocol properties file not found: " + fnfe);
         
      } catch (IOException ioe) {
         logger.warn("Error while acessing Coordination Protocol properties file: " + ioe);
      }
   }    
   
   //Leitura das propriedades
   public static String getListenerAddress() {
      return listenerAddress;
   }

   public static int getListenerPort() {      
      return listenerPort;
   }  
      
   public static int getScanInterval() {
      return monitorScanInterval;
   } 
      
   
   //Salva propriedades do gerente de contexto no arquivo de propriedades
   public static void save() throws MonitorPropertiesException {
      try {
         Properties cpProperties = new Properties();

         cpProperties.setProperty( "clitener.address"  , listenerAddress                       );
         cpProperties.setProperty( "listener.port"     , Integer.toString(listenerPort)        );
         cpProperties.setProperty( "monitor.scan"      , Integer.toString(monitorScanInterval) );
           
         FileOutputStream programFileOut = new FileOutputStream(propsFile);
         cpProperties.store( programFileOut, "Coordination Protocol Properties File" );
         programFileOut.close();
         logger.info("Coordination Protocol properties file was saved."); 
      
      } catch (FileNotFoundException fnfe) {
         throw new MonitorPropertiesException("Coordination Protocol properties file not found.", fnfe);
         
      } catch (IOException ioe) {
         throw new MonitorPropertiesException("Error while acessing Coordination Protocol properties file.", ioe);
      }
   }
   
   //Atribuicao das propriedades
   public static void setListenerAddress(String coordinatorAddress) {
      MonitorProperties.listenerAddress = coordinatorAddress;
   }
   
   public static void setListenerPort(int coordinatorPort) {    
      if ( NetworkUtil.portIsValid(coordinatorPort) )
         MonitorProperties.listenerPort = coordinatorPort;
   }  
   
   public static void setScanInterval(int coordinationScanInterval) {      
      MonitorProperties.monitorScanInterval = coordinationScanInterval;
   }   
   
}
