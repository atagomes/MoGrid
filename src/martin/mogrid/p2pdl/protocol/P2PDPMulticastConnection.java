package martin.mogrid.p2pdl.protocol;

/**
 * @author luciana
 *
 * Created on 12/08/2005
 */
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import martin.mogrid.common.network.NetworkUtil;
import martin.mogrid.common.network.UDPDatagramPacket;
import martin.mogrid.common.network.UDPDatagramPacketException;
import martin.mogrid.common.network.UDPDatagramSocket;
import martin.mogrid.p2pdl.protocol.message.P2PDPMessageInterface;

import org.apache.log4j.Logger;


//Canal para troca de mensagens entre COORDINATORS e COLLABORATORS (multicast)
public class P2PDPMulticastConnection extends P2PDPCoordinationConnection {
   
   //Manutencao do arquivo de log para debug
   private static final Logger logger = Logger.getLogger(P2PDPMulticastConnection.class);
 
   //Objetos associados a conexao 
   private DatagramSocket    multicastSenderSocket   = null;
   private MulticastSocket   multicastReceiverSocket = null; 
   private InetSocketAddress multicastAddress        = null;
   private InetSocketAddress multicastListenAddr     = null;    
   
   //Intervalo no qual o canal serah consultado 
   private int scanInterval = 1000;

   protected void configure(String address, int port) throws P2PDPConnectionException {
      if ( NetworkUtil.ipAddressIsNotValid(address) || NetworkUtil.portIsNotValid(port) ) {
         throw new P2PDPConnectionException("IP address and/or port are not valid. Port should be a value between 1024 and 65535.");         
      }
      //All class D IP addresses are multicast addresses. Class D IP addresses are those that
      //begin with 1110, that is, all addresses from 224.0.0.0 to 235.255.255.255.
      multicastAddress    = new InetSocketAddress(address, port);
      multicastListenAddr = new InetSocketAddress(port);
   }
   
   public void open() throws P2PDPConnectionException {    
      try {
         //SENDER CHANNEL
         multicastSenderSocket = new DatagramSocket(null);
         multicastSenderSocket.setSendBufferSize(UDPDatagramSocket.UDP_SENDER_BUFFER_SIZE);
         
         //RECEIVER CHANNEL
         /*
          * Don´t set the properties setBroadcast(false) and setSoTimeout()!!!
          * setBroadcast(false): you will not receive multicast messages
          * setSoTimeout()     : you will receive multiple multicast messages (? - bug)
          * 
          *  When one sends a message to a multicast group, all subscribing recipients  
          *  receive the message within the time-to-live range of the packet.
          *  
          *  setTimeToLive():
          *     Set the default time-to-live for multicast packets sent out on this 
          *     MulticastSocket in order to control the scope of the multicasts.
          *     The ttl must be in the range 0 <= ttl <= 255 or an IllegalArgumentException 
          *     will be thrown. 
          */
         multicastReceiverSocket = new MulticastSocket(multicastListenAddr);
         multicastReceiverSocket.joinGroup(multicastAddress.getAddress());
         multicastReceiverSocket.setReceiveBufferSize(UDPDatagramSocket.UDP_RECEIVER_BUFFER_SIZE);
         multicastReceiverSocket.setTimeToLive(UDPDatagramSocket.MCAST_TIME_TO_LIVE);
         
         logger.debug("P2PDP Multicast Connection [" + multicastAddress.toString() + "]");
         
   
      } catch (UnknownHostException uhe) {
         throw new P2PDPConnectionException(uhe);      
         
      }  catch (IOException ioe) {
         throw new P2PDPConnectionException(ioe);      
      }    
   }

   protected void setScanInterval(int scanInterval) {
      this.scanInterval = scanInterval;      
   }
   
   public int getScanInterval() {
      return scanInterval;
   }
   
   public void send(P2PDPMessageInterface msg) throws P2PDPConnectionException {
      
      if ( multicastSenderSocket == null ) {
         throw new P2PDPConnectionException("P2PDP Multicast Channel does not exist.");
      }
      
      DatagramPacket packet; 
      try {
         packet = UDPDatagramPacket.createSendPacket(multicastAddress);
         packet.setData(UDPDatagramPacket.convertObjectToByteArray(msg));
         multicastSenderSocket.send(packet);
         logger.debug("\n> P2PDP Multicast channel [send] " + msg.getMessageTypeStr());
         
      } catch (UDPDatagramPacketException UDPDPex) {
         throw new P2PDPConnectionException(UDPDPex);
               
      } catch (IOException ioex) {
         throw new P2PDPConnectionException(ioex);
      }         
   }
      
   public synchronized P2PDPMessageInterface receive() throws P2PDPConnectionException, SocketTimeoutException {
      DatagramPacket        packet  = UDPDatagramPacket.createReceivePacket(); 
      P2PDPMessageInterface message = null;
      
      try {
         multicastReceiverSocket.receive(packet);
         Object receivedData = UDPDatagramPacket.convertByteArrayToObject(packet.getData());
         if ( receivedData instanceof P2PDPMessageInterface ) {
            message = (P2PDPMessageInterface) receivedData;
            logger.debug("\n< P2PDP Multicast channel [receive] " + message.getMessageTypeStr());
         }
         return message;
         
      } catch (SocketTimeoutException stex) {
         throw new SocketTimeoutException();
          
      } catch (IOException ioex) {
         throw new P2PDPConnectionException(ioex);
          
      }  catch (NullPointerException npex) {
         throw new P2PDPConnectionException(npex); 
         
      } catch (UDPDatagramPacketException UDPDPex) {
         throw new P2PDPConnectionException(UDPDPex);
      }
   }   
   
   public void close() {
      if( multicastReceiverSocket != null ) {
         multicastReceiverSocket.close(); 
         multicastReceiverSocket = null;
         
         multicastAddress = null;
      } 
      if( multicastSenderSocket != null ) {
         multicastSenderSocket.close(); 
         multicastSenderSocket = null;
         
         multicastAddress = null; 
      } 
      logger.info("P2PDP Multicast Connection closed.");
   }

}
