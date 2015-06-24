package martin.mogrid.submission.protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;

import martin.mogrid.common.network.NetworkUtil;
import martin.mogrid.common.network.UDPDatagramPacket;
import martin.mogrid.common.network.UDPDatagramPacketException;
import martin.mogrid.common.network.UDPDatagramSocket;
import martin.mogrid.common.util.SystemUtil;
import martin.mogrid.submission.protocol.message.TaskSubmissionMessage;

import org.apache.log4j.Logger;



public class TaskSubmissionChannel {
   
   //Manutencao do arquivo de log para debug
   private static final Logger logger = Logger.getLogger(TaskSubmissionChannel.class);
    
   //Objetos associados a conexao
   private DatagramSocket    senderSocket    = null; 
   private DatagramSocket    receiverSocket  = null; 
   private InetSocketAddress senderAddress   = null;  //endereco de envio
   private InetSocketAddress receiverAddress = null;  //endereco de recepcao
   
   private String channelTitle = "Unicast Message Channel";      
   
   public void configure(InetSocketAddress addrToSend, InetSocketAddress addrToReceive, String title) throws TaskSubmissionException {
      if ( SystemUtil.strIsNotNull(title) ) {
         channelTitle = title;
      }
      //SENDER 
      if ( addrToSend != null ) {
         int    port = addrToSend.getPort();
         String addr = addrToSend.getHostName().toString();
         if ( NetworkUtil.portIsNotValid(port) || NetworkUtil.ipAddressIsNotValid(addr) ) {
            throw new TaskSubmissionException("IP address and/or Port to send message are not valid. Port should be a value between 1024 and 65535.");         
         }
         senderAddress = addrToSend;
      }
      
      //RECEIVER 
      if ( addrToReceive != null ) {
         int port = addrToReceive.getPort();
         if ( NetworkUtil.portIsNotValid(port) ) {
            throw new TaskSubmissionException("Port to receive message is not valid. Port should be a value between 1024 and 65535.");         
         }
         receiverAddress = addrToReceive;
      }           
   }
   
   public void open() throws TaskSubmissionException {      
      try {
         senderSocket = new DatagramSocket(null);
         senderSocket.setReuseAddress(true);
         //senderSocket.setSoTimeout(UDPDatagramSocket.UDP_SOCKET_TIMEOUT);
         senderSocket.setSendBufferSize(UDPDatagramSocket.UDP_SENDER_BUFFER_SIZE);
         senderSocket.setBroadcast(false);
         
         receiverSocket = new DatagramSocket(null);
         receiverSocket.setReuseAddress(true);
         //receiverSocket.setSoTimeout(UDPDatagramSocket.UDP_SOCKET_TIMEOUT);
         receiverSocket.setReceiveBufferSize(UDPDatagramSocket.UDP_RECEIVER_BUFFER_SIZE);
         receiverSocket.setBroadcast(false);
         receiverSocket.bind(receiverAddress);
         
         logger.debug("\n" + channelTitle + ": ");
         logger.debug("Port to receive [" + receiverAddress.getPort() + "]");
         if ( senderAddress != null ) {
            logger.debug("Address to send [" + senderAddress.getHostName() + ":" + senderAddress.getPort() + "]\n");
         }
         
      }  catch (IOException ioe) {
         throw new TaskSubmissionException(ioe);      
      }    
   }
   
   public void send(TaskSubmissionMessage msg) throws TaskSubmissionException {
      if ( senderSocket == null ) {
         throw new TaskSubmissionException(channelTitle + " does not exist.");
      }
      
      DatagramPacket packet;                      
      try {
         packet = UDPDatagramPacket.createSendPacket(senderAddress);
         packet.setData(UDPDatagramPacket.convertObjectToByteArray(msg));
         senderSocket.send(packet);
         
         //logger.debug("\n> " + channelTitle + " [sending] " + msg.getClass());
         
      } catch (UDPDatagramPacketException UDPDPex) {
         throw new TaskSubmissionException(UDPDPex);
         
      } catch (NullPointerException npex) {
         throw new TaskSubmissionException("Task Submission channel was interrupted." + npex); 
         
      } catch (IOException ioex) {
         throw new TaskSubmissionException(ioex);
      }         
   }
   
   public void send(TaskSubmissionMessage msg, String addrToSend) throws TaskSubmissionException {
      if ( senderSocket == null ) {
         throw new TaskSubmissionException(channelTitle + " does not exist.");
      }
      
      if ( NetworkUtil.ipAddressIsNotValid(addrToSend) ) {
         throw new TaskSubmissionException("IP address to send a message is not valid.");         
      }
      senderAddress = new InetSocketAddress(addrToSend, senderAddress.getPort());
      
      DatagramPacket packet;                      
      try {
         packet = UDPDatagramPacket.createSendPacket(senderAddress);
         packet.setData(UDPDatagramPacket.convertObjectToByteArray(msg));           
         senderSocket.send(packet);
         
         //logger.debug("\n> " + channelTitle + " [sending] to {"+senderAddress+"} a " + msg.getClass());
         
      } catch (UDPDatagramPacketException UDPDPex) {
         throw new TaskSubmissionException(UDPDPex);
               
      } catch (NullPointerException npex) {
         throw new TaskSubmissionException("Task Submission channel was interrupted." + npex); 
         
      } catch (IOException ioex) {
         throw new TaskSubmissionException(ioex);
      }         
   }   

   public TaskSubmissionMessage receive() throws TaskSubmissionException, SocketTimeoutException {
      DatagramPacket        packet = UDPDatagramPacket.createReceivePacket(); 
      TaskSubmissionMessage data   = null;
      
      try {
         receiverSocket.receive(packet);
         Object receivedData = UDPDatagramPacket.convertByteArrayToObject(packet.getData());
         if ( receivedData instanceof TaskSubmissionMessage ) {
            data = (TaskSubmissionMessage) receivedData; 
            //logger.debug("\n< " + channelTitle + " [receiving] " + data.getClass());
         }
         return data;
         
      } catch (SocketTimeoutException stex) {
         throw new SocketTimeoutException();
        
      } catch (IOException ioex) {
         throw new TaskSubmissionException(ioex);
          
      } catch (NullPointerException npex) {
         throw new TaskSubmissionException(channelTitle + " was interrupted.", npex); 
         
      } catch (UDPDatagramPacketException UDPDPex) {
         throw new TaskSubmissionException(UDPDPex);
      }
   }
      
   public boolean receiverIsClosed() {
      if ( receiverSocket == null ) {
         return true;
      }
      return receiverSocket.isClosed();
   }
   
   public boolean senderIsClosed() {
      if ( senderSocket == null ) {
         return true;
      }
      return senderSocket.isClosed();
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
