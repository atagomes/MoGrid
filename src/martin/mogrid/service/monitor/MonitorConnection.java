package martin.mogrid.service.monitor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import martin.mogrid.common.network.NetworkUtil;
import martin.mogrid.common.network.UDPDatagramPacket;
import martin.mogrid.common.network.UDPDatagramPacketException;
import martin.mogrid.common.util.SystemUtil;

import org.apache.log4j.Logger;



public class MonitorConnection {
   
   //Manutencao do arquivo de log para debug
   private static final Logger logger = Logger.getLogger(MonitorConnection.class);
 
   //Objetos associados a conexao
   private DatagramSocket    senderSocket  = null;  
   private InetSocketAddress senderAddress = null;
   

   public void configure() throws MonitorConnectionException {
      MonitorProperties.load();
      
      String coordAddress = MonitorProperties.getListenerAddress();
      int coordPort       = MonitorProperties.getListenerPort();  
      
      if ( NetworkUtil.ipAddressIsNotValid(coordAddress) || NetworkUtil.portIsNotValid(coordPort) ) {
         throw new MonitorConnectionException("IP address and/or port are not valid. Port should be a value between 1024 and 65535.");         
      }
      senderAddress = new InetSocketAddress(coordAddress, coordPort); 

      try {
         senderSocket = new DatagramSocket(null);
      }  catch (IOException ioe) {
         throw new MonitorConnectionException(ioe);      
      }        
   }
 
   
   public int open() throws MonitorConnectionException {
      configure();
      
      try {
         senderSocket = new DatagramSocket(null);
         senderSocket.setBroadcast(false);
         
         logger.debug("Monitor Connection [" + senderAddress.getHostName() + ":" + senderAddress.getPort() + "]");    
         
      }  catch (IOException ioe) {
         throw new MonitorConnectionException(ioe);      
      }    
      
      return ( MonitorProperties.getScanInterval() );
   }

   public void send(DeviceContext msg) throws MonitorConnectionException {
      if ( senderSocket == null ) {
         throw new MonitorConnectionException("Monitor Channel does not exist.");
      }
      
      DatagramPacket packet;                      
      try {
         packet = UDPDatagramPacket.createSendPacket(senderAddress);
         packet.setData(UDPDatagramPacket.convertObjectToByteArray(msg));
         senderSocket.send( packet );
         logger.debug("> Monitor Channel - sending " + msg.toString());
         
      } catch (UDPDatagramPacketException UDPDPex) {
         throw new MonitorConnectionException(UDPDPex);
               
      } catch (NullPointerException npex) {
         throw new MonitorConnectionException("Monitor channel was interrupted." + npex); 
         
      } catch (IOException ioex) {
         throw new MonitorConnectionException(ioex);
      }         
   }

   public void send(String msg) throws MonitorConnectionException {
      if ( senderSocket == null ) {
         throw new MonitorConnectionException("Monitor Channel does not exist.");
      }
      
      if ( SystemUtil.strIsNull(msg) ) {
         logger.debug("!> Monitor Channel - data to send is null.");
         return;
      }

      DatagramPacket packet;                      
      try {
         byte[] data = msg.getBytes();
         
         packet = UDPDatagramPacket.createSendPacket(senderAddress);
         packet.setData( data );                 
         senderSocket.send(packet);
         
         logger.debug("> Monitor Channel - sending " + msg.toString());
         
      } catch (UDPDatagramPacketException UDPDPex) {
         throw new MonitorConnectionException(UDPDPex);
               
      } catch (NullPointerException npex) {
         throw new MonitorConnectionException("Monitor channel was interrupted." + npex); 
         
      } catch (IOException ioex) {
         throw new MonitorConnectionException(ioex);
      }         
   }
   
   public void close() {
      if( senderSocket != null ) {
         senderSocket.close();  
         senderSocket = null;
         
         senderAddress = null;
         logger.info("Monitor Connection closed.");
      } 
   }
}
