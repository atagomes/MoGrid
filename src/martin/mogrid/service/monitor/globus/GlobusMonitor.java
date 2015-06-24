
package martin.mogrid.service.monitor.globus;


import java.net.InetAddress;

import martin.mogrid.common.logging.MoGridLog4jConfigurator;
import martin.mogrid.common.network.NetworkUtil;
import martin.mogrid.common.util.SystemUtil;
import martin.mogrid.globus.service.authenticator.GridAuthentication;
import martin.mogrid.globus.service.mds.GlobusResource;
import martin.mogrid.globus.service.mds.GlobusResourceElements;
import martin.mogrid.globus.service.mds.ProxyMonitor;
import martin.mogrid.globus.service.mds.ProxyMonitorException;
import martin.mogrid.globus.service.mds.RequestConfiguration;
import martin.mogrid.service.monitor.MonitorConnection;
import martin.mogrid.service.monitor.MonitorConnectionException;

import org.apache.log4j.Logger;


/**
 * @author luciana
 *
 * Created on 25/06/2005
 */
public class GlobusMonitor implements Runnable {
      
   //Manutencao do arquivo de log do servico
   private static final Logger logger = Logger.getLogger(GlobusMonitor.class); 
   
   //Informacoes para ativar a grade (Bruno)
   private ProxyMonitor       globusMonitor = null;

   //Objetos associados a execucao do listener
   private Thread            monitorThread             = null; 
   private int               monitorThreadScanInterval = 1000;
   private MonitorConnection listenerChannel           = null;  
 
   
   public GlobusMonitor() {
      //Configura o log4j de acordo com os parametros do arquivo de configuracao
      MoGridLog4jConfigurator.configure();
      //Canal de comunicacao UNICAST entre MONITOR e LISTENER 
      listenerChannel = new MonitorConnection();
      start();
   }
   
   private void start() {
      logger.info("\nStarting Globus Monitor...");
      
      try {            
         //Inicializacao da grade (Bruno)
         globusMonitor = GridAuthentication.createProxyMonitor();
         
         //Canal de comunicacao com o listener
         listenerChannel.open();
         
         if( monitorThread == null ) {
            monitorThread = new Thread(this, "GlobusMonitor");
            monitorThread.start();
         }         
         logger.info("Globus Monitor running.\n");
         //Atualiza a flag que indica o estado do monitor 
         
      } catch (MonitorConnectionException ex) {
         logger.info("It was not possible to create a Monitor Message Channel." );         
         logger.error("It was not possible to create a Monitor Message Channel: " + ex.getMessage());
         SystemUtil.abnormalExit();
      }   
   }
    
   public synchronized void stop() { 
      logger.info("Stopping Globus Monitor...");
      
      monitorThread.interrupt();
      monitorThread = null; 
      
      if( listenerChannel != null ) {
         listenerChannel.close(); 
         listenerChannel = null;
      } 
      
      logger.info("Globus Monitor sttoped.\n"); 
   }   

   public void run() {
      //Controle de execucao da thread monitor
      Thread myThread = Thread.currentThread();
      GlobusDeviceContext devContextInfo = null;
      
      String[] hostName = RequestConfiguration.chooseGrid();
      while( monitorThread == myThread ) {
         try {
            if ( listenerChannel != null ) {
                for ( int i=0; i<hostName.length;i++ ) {
                   devContextInfo = setDeviceContextInfo( hostName[i] );
                   if ( devContextInfo != null ) {
                      listenerChannel.send(devContextInfo);
                   }
               }
               hostName = RequestConfiguration.chooseGrid();
            }            
            SystemUtil.sleep(monitorThreadScanInterval);  //Wait in milliseconds
                    
         } catch (MonitorConnectionException ex) {
            logger.warn("It occurred some error receiving messages from coordinator.\n[ERROR] " + ex.getMessage());
            //Util.abnormalExit(); 
         } 
      }
   } 

   
   //Le info de contexto da grade fixa para envio (Bruno)
   public GlobusDeviceContext setDeviceContextInfo(String machine) {
      GlobusDeviceContext devCtxt = new GlobusDeviceContext();
      if ( machine != null ) {       
         //Retorna uma lista contendo nome da maquin, cpu e memoria
         GlobusResourceElements globusDeviceList = null;
      	try {
      		globusDeviceList = globusMonitor.getResource();
      	} catch ( ProxyMonitorException e ) {
            logger.warn(e.getMessage());
      	}
         if ( globusDeviceList != null ) { 
            GlobusResource globusDevice = globusDeviceList.get(machine);
            if ( globusDevice != null ) {         
               //TODO Implementar um metodo para retornar cada recurso de cada maquina
               //     pode ser passado atraves de um metodo que pega o nome da maquina
               InetAddress IP = null;
               IP = NetworkUtil.getInetAddressByName(machine);
               if ( IP != null ) {              
                  //Set device identification in context info 
                  devCtxt.setIPAddress(IP.getHostAddress());
                  logger.info("Host address = " + IP.getHostAddress());
                  devCtxt.setMacAddress("");
            
                  //Get available (free) resources in % value
                  float cpuFree = globusDevice.getFreeCpuPercent();
                  logger.info("Free CPU = " + cpuFree);
                  float memoryFree = globusDevice.getFreeMemoryPercent();
                  logger.info("Free Memory = " + memoryFree);
                  //SET available (free) Resources in % value
                  devCtxt.setCpuLevel(cpuFree);    
                  devCtxt.setMemoryLevel(memoryFree);
                  
                  //Get available (free) resources in absolute value
                  float cpuTotal = globusDevice.getFreeCpuHz();
                  logger.info("Free CPU (Hz) = "+cpuTotal);
                  float memoryTotal = globusDevice.getFreeMemoryKb();
                  logger.info("Free Memory = "+memoryTotal + "\n");      
                  //Set available (free) Resources in absolute value 
                  devCtxt.setCpuValue(cpuTotal);
                  devCtxt.setMemoryValue(memoryTotal);
                  
                  //Get the SO name
                  String soName = globusDevice.getSoName();
                  devCtxt.setSOName( soName );
               }
            }
         }
      }
      return devCtxt;
   }
   
   static public void main(String[] args) {  
      new GlobusMonitor();
   }
}
