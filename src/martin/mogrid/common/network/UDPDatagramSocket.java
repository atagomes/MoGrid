/*
 * Created on 17/05/2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package martin.mogrid.common.network;

public class UDPDatagramSocket {
   //Technically, 64k (65535) is the maximum size of an IP datagram.
   public static final int IP_DATAGRAM_SIZE = 65500;
   
   //Configura o tamanho padrao do buffer do canal para o tamanho de 6 pacotes UDP
   //Tamanho default: 8192
   public static final int UDP_SENDER_BUFFER_SIZE   = IP_DATAGRAM_SIZE * 3;
   public static final int UDP_RECEIVER_BUFFER_SIZE = IP_DATAGRAM_SIZE * 6;
   
   //With this option set to a non-zero timeout, a call to receive() for this DatagramSocket will 
   //block for only this amount of time. If the timeout expires, a java.net.SocketTimeoutException 
   //is raised, though the DatagramSocket is still valid. The option must be enabled prior to entering 
   //the blocking operation to have effect. The timeout must be > 0. A timeout of zero is interpreted 
   //as an infinite timeout.
   public static final int UDP_SOCKET_TIMEOUT = 100;
   
   //Default time-to-live for multicast packets sent out on this MulticastSocket in order to control 
   //the scope of the multicasts. The ttl must be in the range  0 <= ttl <= 255.
   public static final int MCAST_TIME_TO_LIVE = 15;  //default value: 1
   
   //On IP networks, the IP address 255.255.255.255 (in binary, all 1s) is the general 
   //broadcast address. You can't use this address to broadcast a message to every user 
   //on the Internet because routers block it, so all you end up doing is broadcasting it 
   //to all hosts on your own network. 
   public static final String BROADCAST_ADDRESS = "255.255.255.255";
   
   //All class D IP addresses are multicast addresses. Class D IP addresses are those that
   //begin with 1110, that is, all addresses from 224.0.0.0 to 235.255.255.255.  
   //The address 224.0.0.0 is reserved and should not be used.
     
}
