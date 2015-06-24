package martin.mogrid.p2pdl.protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import martin.mogrid.common.network.NetworkUtil;
import martin.mogrid.common.network.UDPDatagramPacket;
import martin.mogrid.common.network.UDPDatagramPacketException;
import martin.mogrid.common.network.UDPDatagramSocket;
import martin.mogrid.common.util.SystemUtil;
import martin.mogrid.p2pdl.protocol.message.P2PDPMessageInterface;

import org.apache.log4j.Logger;



public abstract class P2PDPUnicastConnection extends P2PDPCoordinationConnection {

   //Manutencao do arquivo de log para debug
   private static final Logger logger = Logger.getLogger(P2PDPUnicastConnection.class);
 
   //Objetos associados a conexao
   private DatagramSocket    senderSocket    = null; 
   private DatagramSocket    receiverSocket  = null; 
   private InetSocketAddress senderAddress   = null;  //endereco de envio
   private InetSocketAddress receiverAddress = null;  //endereco de recepcao
   
   private String channelTitle = "Unicast Message Channel";      
   private int    scanInterval = 1000;
   
   public void configure(InetSocketAddress addrToSend, InetSocketAddress addrToReceive, String title) throws P2PDPConnectionException {
      if ( SystemUtil.strIsNotNull(title) ) {
         channelTitle = title;
      }
      //SENDER 
      if ( addrToSend != null ) {
         int    port = addrToSend.getPort();
         String addr = addrToSend.getHostName().toString();
         
         if ( NetworkUtil.portIsNotValid(port) || NetworkUtil.ipAddressIsNotValid(addr) ) {
            throw new P2PDPConnectionException("IP address and/or Port to send message are not valid. Port should be a value between 1024 and 65535.");         
         }
         senderAddress = addrToSend;
      }
      
      //RECEIVER 
      if ( addrToReceive != null ) {
         int port = addrToReceive.getPort();
         if ( NetworkUtil.portIsNotValid(port) ) {
            throw new P2PDPConnectionException("Port to receive message is not valid. Port should be a value between 1024 and 65535.");         
         }
         receiverAddress = addrToReceive;
      }           
   }
   
   public void open() throws P2PDPConnectionException {      
      try {
         //TODO Verificar o comportamento do socket: testar codigo abaixo
         // socket = new DatagramSocket(null); 
         // socket.setReuseAddress(true);
         // socket.bind(localSocketAddress);
         
         senderSocket = new DatagramSocket(null);
         senderSocket.setSendBufferSize(UDPDatagramSocket.UDP_SENDER_BUFFER_SIZE); 
         
         //receiverSocket = new DatagramSocket(receiverAddress);   
         receiverSocket = new DatagramSocket(null);
         receiverSocket.setReceiveBufferSize(UDPDatagramSocket.UDP_RECEIVER_BUFFER_SIZE);
         receiverSocket.setSoTimeout(UDPDatagramSocket.UDP_SOCKET_TIMEOUT);
         receiverSocket.bind(receiverAddress);
         
         logger.debug("\n" + channelTitle + ": ");
         logger.debug("Port to receive [" + receiverAddress.getPort() + "]");
         if ( senderAddress != null ) {
            logger.debug("Address to send [" + senderAddress.getHostName() + ":" + senderAddress.getPort() + "]\n");
         }
         
         scanInterval = P2PDPProperties.getCoordinationScanInterval();
         
      }  catch (IOException ioe) {
         throw new P2PDPConnectionException(ioe);      
      }    
   }

   public int getScanInterval() {
      return scanInterval;
   }
   
   public void send(P2PDPMessageInterface msg) throws P2PDPConnectionException {
      if ( senderSocket == null ) {
         throw new P2PDPConnectionException(channelTitle + " does not exist.");
      }
      
      DatagramPacket packet;                      
      try {
         packet = UDPDatagramPacket.createSendPacket(senderAddress);
         packet.setData(UDPDatagramPacket.convertObjectToByteArray(msg));
         senderSocket.send(packet);
         //logger.debug("\n> " + channelTitle + " [sending] " + msg.getMessageTypeStr());
         
      } catch (UDPDatagramPacketException UDPDPex) {
         throw new P2PDPConnectionException(UDPDPex);
               
      } catch (IOException ioex) {
         throw new P2PDPConnectionException(ioex);
      }         
   }

   public void send(P2PDPMessageInterface msg, String addrToSend) throws P2PDPConnectionException {
      if ( senderSocket == null ) {
         throw new P2PDPConnectionException(channelTitle + " does not exist.");
      }
      
      if ( NetworkUtil.ipAddressIsNotValid(addrToSend) ) {
         throw new P2PDPConnectionException("IP address to send a message is not valid.");         
      }
      senderAddress = new InetSocketAddress(addrToSend, senderAddress.getPort());
      
      DatagramPacket packet;                      
      try {
         packet = UDPDatagramPacket.createSendPacket(senderAddress);
         packet.setData(UDPDatagramPacket.convertObjectToByteArray(msg));
         senderSocket.send(packet);
         //logger.debug("\n> " + channelTitle + " [sending] " + msg.getMessageTypeStr());
         
      } catch (UDPDatagramPacketException UDPDPex) {
         throw new P2PDPConnectionException(UDPDPex);
               
      } catch (IOException ioex) {
         throw new P2PDPConnectionException(ioex);
      }         
   }   

   public synchronized P2PDPMessageInterface receive() throws P2PDPConnectionException {
      DatagramPacket        packet  = UDPDatagramPacket.createReceivePacket(); 
      P2PDPMessageInterface message = null;
      
      try {
         receiverSocket.receive(packet);
         Object receivedData = UDPDatagramPacket.convertByteArrayToObject(packet.getData());
         if ( receivedData instanceof P2PDPMessageInterface ) {
            message = (P2PDPMessageInterface) receivedData;
            //logger.debug("\n< " + channelTitle + " [receiving] " + message.getMessageTypeStr());
         }

         return message;
         
      } catch (IOException ioex) {
         throw new P2PDPConnectionException(ioex);
          
      } catch (UDPDatagramPacketException UDPDPex) {
         throw new P2PDPConnectionException(UDPDPex);
      } 
   }
      
   public void close() {
      if( senderSocket != null ) {
         senderSocket.close();  
         senderSocket = null;
         
         senderAddress   = null;
      }
      
      if( receiverSocket != null ) {
         receiverSocket.close();  
         receiverSocket = null;
         
         receiverAddress = null;
      }
      
      logger.info(channelTitle + " closed.");
   }

}
