package martin.mogrid.service.contextlistener;

/**
 * @author luciana
 *
 * Created on 05/09/2005
 */
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import martin.mogrid.common.network.NetworkUtil;
import martin.mogrid.common.network.UDPDatagramPacket;
import martin.mogrid.common.network.UDPDatagramPacketException;
import martin.mogrid.common.network.UDPDatagramSocket;

import org.apache.log4j.Logger;



//Canal para escuta do servico monitor pelo Context Listener 
public class ContextListenerConnection {
      
   //Manutencao do arquivo de log para debug
   private static final Logger logger = Logger.getLogger(ContextListenerConnection.class);
   
   //Objetos associados a conexao
   private DatagramSocket    localSocket        = null;  
   private InetSocketAddress localSocketAddress = null;
   private int               scanInterval       = 0;
   
   /**
    * Get handle to the singleton
    * @return the singleton instance of this class.
    */     
   public void open() throws ContextListenerException {
      ContextListenerProperties.load();
      
      scanInterval = ContextListenerProperties.getMonitorScanInterval();
      int monitorPort = ContextListenerProperties.getMonitorPort();
      
      if ( NetworkUtil.portIsNotValid(monitorPort) ) {
         throw new ContextListenerException("Port is not valid. Port should be a value between 1024 and 65535.");         
      }
      localSocketAddress = new InetSocketAddress(monitorPort);

      try {
         localSocket = new DatagramSocket(null); 
         localSocket.setReuseAddress(true);
         localSocket.setBroadcast(false);
         localSocket.setSoTimeout(UDPDatagramSocket.UDP_SOCKET_TIMEOUT); //not blocking receive                   
         localSocket.bind(localSocketAddress);   
         
         logger.debug("Context Listener Connection [localhost:" + monitorPort + "]");

      }  catch (SocketException se) {
         throw new ContextListenerException(se);      
      }    
   }
  
   public int getScanInterval() { 
      return scanInterval;
   }
  
   public Object receive() throws ContextListenerException, SocketTimeoutException {
      DatagramPacket packet  = UDPDatagramPacket.createReceivePacket(); 
      Object         message = null;

      if( localSocket != null ) {
         try {
            localSocket.receive(packet);               
            message = UDPDatagramPacket.convertByteArrayToObject(packet.getData());
            
            
         } catch (SocketTimeoutException stex) {
            throw new SocketTimeoutException();         
         
         } catch (IOException ioex) { //SocketException handled by IOException
            throw new ContextListenerException(ioex);            
            
         } catch (NullPointerException npex) {
            throw new ContextListenerException("Context Listener channel was interrupted." + npex);
            
         } catch (UDPDatagramPacketException udpex) {
            throw new ContextListenerException(udpex);
         } 
      }
      
      return message;
   }
   
   public void close() {
      if( localSocket != null ) {
         localSocket.close(); 
         localSocket = null;
         
         localSocketAddress = null;
      } 
      logger.info("Context Listener Connection closed.");
   }

}  