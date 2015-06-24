
package martin.mogrid.service.contextlistener;


import java.net.SocketTimeoutException;

import martin.mogrid.common.logging.MoGridLog4jConfigurator;
import martin.mogrid.common.util.MoGridClassLoader;
import martin.mogrid.common.util.MoGridClassLoaderException;
import martin.mogrid.common.util.SystemUtil;
import martin.mogrid.service.monitor.ContextParser;
import martin.mogrid.service.monitor.ContextParserException;
import martin.mogrid.service.monitor.DeviceContext;
import martin.mogrid.service.monitor.DeviceContextHistory;

import org.apache.log4j.Logger;



/**
 * @author   Luciana Lima
 */
public class ContextListener implements Runnable {
   
   //Manutencao do arquivo de log do servico
   private static final Logger logger = Logger.getLogger(ContextListener.class);   
   
   private Thread                     contextLstnrThread  = null;   
   private int                        scanInterval        = 0; 
   private ContextListenerConnection  ctxtListenerChannel = null;  
   private ContextParser              contextParser       = null;
   
   //Manutencao do historico das informacoes de contexto do(s) dispositivo(s)
   private DeviceContextHistory devContextHistory = null;
    
   //Garante uma instancia unica da classe
   private static ContextListener ctxtListenerInstance = null;
   
   /**
    * Get handle to the singleton
    * @return the singleton instance of this class.
    */
   public static synchronized ContextListener getInstance() throws ContextListenerException {      
      if ( ctxtListenerInstance == null ) {
         ctxtListenerInstance = new ContextListener();        
      }
      return ctxtListenerInstance;
   }
   
   /**
    * Creates a new instance of a Context Listener. 
    *      
    * @param monitorPort port in that the Context Listener will receive the monitor 
    * notifications 
    * @param scanIntervalInMilli interval in that the Context Listener will listener 
    * the monitor notifications (in milliseconds)    
    */
   private ContextListener() throws ContextListenerException {      
      devContextHistory   = new DeviceContextHistory();
      ctxtListenerChannel = new ContextListenerConnection();             
      start();   //Inicializa Thread para recepcao de pacotes do monitor             
   }
    
   private void start() throws ContextListenerException {        
      logger.info("Starting Context Listener...");
     
      try {
         //Inicializa o gerente de contexto
         ctxtListenerChannel.open();
       
         //Obtem o intervalo de scan
         scanInterval = ctxtListenerChannel.getScanInterval(); 
               
      } catch (ContextListenerException ex) {
         throw new ContextListenerException(ex);      
      }
      
      //Carrega do arquivo de configuracao do serviço ContextListener a classe responsavel pelo
      //parser das informacoes de contexto enviadas pelo monitor
      loadContextParserClass();
      
      if( contextLstnrThread == null ) {
         contextLstnrThread = new Thread(this, "ContextListener");
         contextLstnrThread.start();
      }
      
      logger.info("Context Listener running.\n");              
   }
    
   private void loadContextParserClass() throws ContextListenerException {
      ContextListenerProperties.load();      
      String parserClass = ContextListenerProperties.getParserClass();
      try {
         ContextParser ctxtParser = (ContextParser)MoGridClassLoader.load(parserClass).newInstance();         
         setContextParser( ctxtParser );
         logger.info("ContextListener is using " + parserClass);
      
      } catch (NullPointerException e) {
         throw new ContextListenerException("Context Information Parser class ["+parserClass+"] was not located.", e);
       
      } catch (InstantiationException e) {
         throw new ContextListenerException("Context Information Parser class ["+parserClass+"] was not located.", e);
         
      } catch (IllegalAccessException e) {
         throw new ContextListenerException("Context Information Parser class ["+parserClass+"] was not located.", e);
         
      } catch (MoGridClassLoaderException e) {
         throw new ContextListenerException("Context Information Parser class ["+parserClass+"] was not loaded.", e);        
      } 
   }
   
   public synchronized void stop() {  
      logger.info("Stopping Context Listener...");      
      
      contextLstnrThread.interrupt();
      try {
         contextLstnrThread.join(1000);      
      } catch (InterruptedException e) {
         logger.warn("Stopping Context Listener Thread: " + e.getMessage(), e); 
      } finally {
         contextLstnrThread = null; 
         if( ctxtListenerChannel != null ) {
            ctxtListenerChannel.close(); 
            ctxtListenerChannel = null;
         }        
         logger.info("Context Listener sttoped.");  
      }
   }   

   public void run() { 
      Thread myThread = Thread.currentThread();
      Object data     = null;
     
      while( contextLstnrThread == myThread ) {
         SystemUtil.sleep(scanInterval);  //Wait in milliseconds  
         
         try {             
            if ( ctxtListenerChannel != null ) {
               data = ctxtListenerChannel.receive();
               handlerMonitorData(data);
            }        
            
         } catch (SocketTimeoutException stex) {   
            //It doesn´t anything, exception was thow because a value > 0 (0=infinite) was atributed a socket.SO_TIMEOUT
            //to evit that the receive method blocks until a datagram was received.
            //logger.warn( stex.getMessage(), stex );
            
         } catch (ContextListenerException ex) {
            logger.warn( ex.getMessage(), ex );        
         } 
      }
   }  

   public void setContextParser(ContextParser contextParser) {
      this.contextParser = contextParser;
   }
   
   private void handlerMonitorData(Object data) throws ContextListenerException {   
      if ( data != null ) {
          try {
             DeviceContext devContext = contextParser.parseMonitorData(data);
             if ( devContext != null ) {
                //DEBUG 
                //logger.debug("KEY: "+ devContext.getKey());
                DeviceContext previousDevContext = devContextHistory.get(devContext.getKey());
                
                if ( previousDevContext != null ) {  
                   if ( ! devContext.getIPAddress().equalsIgnoreCase(previousDevContext.getIPAddress()) ) {
                      devContext.setIpChanged(true);
                   }
                   //DEBUG 
                   //logger.info("\ncurrent : "+ devContext.toString());
                   //logger.info("old : "+ previousDevContext.toString());
                   if ( ! devContext.toString().equalsIgnoreCase(previousDevContext.toString()) ){
                      //logger.info("Context Info received : "+ devContext.toString()); 
                      logger.trace(devContext.getCpuValue()+" "+devContext.getCpuLevel()+" "+devContext.getMemoryValue()+" "+devContext.getMemoryLevel()); 
                   }
                }  
                devContextHistory.put(devContext);
                
             }
             
          } catch (ContextParserException cpe) {
              throw new ContextListenerException("Error trying to parser the data received from Monitor: " + data.toString().trim() + ".\n[ * Probably the Monitor Service was not started or the Monitor Parser is incompatible with the Monitor Service * ]", cpe);         
          }
      }
   }
   
   public DeviceContextHistory getDeviceContextHistory() {
      return devContextHistory;
   }

   //A chave nesse caso diz respeito ao MACAddress
   public DeviceContext getDeviceContext(String key) {
      if ( devContextHistory != null ) {
         return devContextHistory.get(key);
         //return devContextHistory.remove(key);
      }
      return null;
   }
   
   //SOH PARA TESTES...
   public static void main(String[] args) throws ContextListenerException {
      MoGridLog4jConfigurator.configure();
      new ContextListener();       
   }
   
}
