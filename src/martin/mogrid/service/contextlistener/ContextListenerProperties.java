
package martin.mogrid.service.contextlistener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import martin.mogrid.common.network.LocalHost;
import martin.mogrid.common.network.NetworkUtil;
import martin.mogrid.common.util.SystemUtil;

import org.apache.log4j.Logger;



/**
 * @author lslima
 *
 * Created on 14/06/2005
 */
public class ContextListenerProperties {
      
   //Manutencao do arquivo de log do servico
   private static final Logger logger = Logger.getLogger(ContextListenerProperties.class);  
   
   //Porta e intervalo de scan do listener (PORT must be in the interval between 1024 and 65535) 
   private static final int DEFAULT_MONITOR_PORT          = 50000; // porta local que recebe contexto do monitor moca
   private static final int DEFAULT_MONITOR_SCAN_INTERVAL = 3000;  // intervalo de leitura do contexto recebido
   
   
   //Arquivo de propriedades do gerente de contexto
   private static final String propsFile        = System.getProperty("mogrid.home") + File.separator + "conf" + File.separator + "ContextListener.properties";
   
   //Propriedades associadas ao gerente de contexto
   private static int    monitorPort            = DEFAULT_MONITOR_PORT; 
   private static int    monitorScanInterval    = DEFAULT_MONITOR_SCAN_INTERVAL; 
   
   //MAC Address da maquina local
   private static String macAddress = null;
   
   //Class that implements the context information parser
   private static String parserClass = null;
   
   
   //Carrega propriedades do gerente de contexto com base no arquivo de propriedades   
   public static void load() {      
      try {
         Properties cmProperties = new Properties();
         FileInputStream programFileIn = new FileInputStream(propsFile);
         cmProperties.load( programFileIn );
         programFileIn.close();
         
         String property     = null; 
         property            = cmProperties.getProperty("monitor.mac.address");
         macAddress          = (property != null)                               ? property : "";   
         property            = cmProperties.getProperty("parser.class");
         parserClass         = (property != null)                               ? property : "";        
         property            = cmProperties.getProperty("monitor.port");
         monitorPort         = (property != null && NetworkUtil.portIsValid(property)) ? Integer.parseInt(property) : DEFAULT_MONITOR_PORT;        
         property            = cmProperties.getProperty("monitor.scan");
         monitorScanInterval = (property != null)                               ? Integer.parseInt(property) : DEFAULT_MONITOR_SCAN_INTERVAL;
       
         if ( parserClass=="" ) {
            if ( LocalHost.soIsLinux() ) {
               parserClass = "martin.mogrid.service.monitor.moca.linux.MoCALinuxContextParser";
            } else if ( LocalHost.soIsWindowsXP() ) {
               parserClass = "martin.mogrid.service.monitor.moca.windows.xp.MoCAWindowsXPContextParser";
            } else { 
               parserClass = "martin.mogrid.service.monitor.moca.MoCAContextParser";
            }
         }
      
      } catch (FileNotFoundException fnfe) {
         logger.warn("Context Listener properties file not found: " + fnfe.getMessage() + ". Using default properties: " + showProperties());
         
      } catch (IOException ioe) {
         logger.warn("Error while acessing context listener properties file: " + ioe.getMessage() + ". Using default properties: " + showProperties());
      }
   }    
   
   //Leitura das propriedades
   public static String getLocalMacAddress() {
      return macAddress;
   } 
   
   public static String getParserClass() {
      return parserClass;
   } 
   
   public static int getMonitorPort() {
      return monitorPort;
   } 

   public static int getMonitorScanInterval() {
      return monitorScanInterval;
   } 
   
   public static String getPropertiesFileName() {
      return propsFile;
   }
   

   //Salva propriedades do gerente de contexto no arquivo de propriedades
   public static void save() throws ContextListenerPropertiesException {
      try {
         Properties cmProperties = new Properties();

         cmProperties.setProperty( "monitor.mac.address" , macAddress                             );
         cmProperties.setProperty( "parser.class"        , parserClass                            );
         cmProperties.setProperty( "monitor.port"        , Integer.toString(monitorPort)          );
         cmProperties.setProperty( "monitor.scan"        , Integer.toString(monitorScanInterval)  );
          
         FileOutputStream programFileOut = new FileOutputStream(propsFile);
         cmProperties.store( programFileOut, "Context Listener Properties File" );
         programFileOut.close();
         logger.info("Context Listener properties file was saved."); 
      
      } catch (FileNotFoundException fnfe) {
         throw new ContextListenerPropertiesException("Context Listener properties file not found.", fnfe);
         
      } catch (IOException ioe) {
         throw new ContextListenerPropertiesException("Error while acessing context listener properties file.", ioe);
      }
   }
   
   //Atribuicao das propriedades
   public static void setLocalMacAddress(String macAddress) {    
      if ( SystemUtil.strIsNotNull(macAddress) )    
         ContextListenerProperties.macAddress = macAddress;
   }   
   public static void setParserClass(String parserClass) {    
      if ( SystemUtil.strIsNotNull(parserClass) )    
         ContextListenerProperties.parserClass = parserClass;
   }
   public static void setMonitorPort(int monitorPort) {    
      if ( NetworkUtil.portIsValid(monitorPort) )    
         ContextListenerProperties.monitorPort = monitorPort;
   }   
   
   public static void setMonitorScanInterval(int monitorScanInterval) {      
      ContextListenerProperties.monitorScanInterval = monitorScanInterval;
   }   

   
   public static String showProperties() {
      String p2pdpProperties = "";
      
      p2pdpProperties += "\nLocal MAC Address: " + macAddress;
      p2pdpProperties += "\nListener Parser Class: " + parserClass;
      p2pdpProperties += "\nListener Port: " + monitorPort;
      p2pdpProperties += "\nListener Scan Interval: " + monitorScanInterval;
      
      return p2pdpProperties;
   }
}
