/*
 * Created on 17/05/2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package martin.mogrid.p2pdl.protocol;

import java.net.InetAddress;

import martin.mogrid.common.network.NetworkUtil;

import org.apache.log4j.Logger;

public class P2PDPConnectionFactory {

   //Manutencao do arquivo de log do servico
   private static final Logger logger = Logger.getLogger(P2PDPConnectionFactory.class);
   
   public static final int UNICAST_CONNECTION   = 0;
   public static final int MULTICAST_CONNECTION = 1;
   public static final int BROADCAST_CONNECTION = 2;
   
   
   public static P2PDPCoordinationConnection create() throws P2PDPConnectionException {
      P2PDPProperties.load();
       
      String coordAddr    = P2PDPProperties.getCoordinationAddress();
      int    coordPort    = P2PDPProperties.getCoordinationPort();  
      int    scanInterval = P2PDPProperties.getCoordinationScanInterval();

      InetAddress coordAddress = null;   
      if ( NetworkUtil.ipAddressIsNotValid(coordAddr) || NetworkUtil.portIsNotValid(coordPort) ) {
         throw new P2PDPConnectionException("IP address and/or port are not valid. Port should be a value between 1024 and 65535.");
      } else {
         coordAddress = NetworkUtil.getInetAddressByName(coordAddr);
      }
      
      int type = BROADCAST_CONNECTION;
      if ( coordAddress.isMulticastAddress() ) {
         type = MULTICAST_CONNECTION;
      }

      P2PDPCoordinationConnection connection = null;
      switch (type) {
         case ( UNICAST_CONNECTION ) :            
            break;
            
         case ( MULTICAST_CONNECTION ) :            
            P2PDPMulticastConnection multicastConnection = new P2PDPMulticastConnection();
            multicastConnection.setScanInterval(scanInterval);
            multicastConnection.configure(coordAddr, coordPort);
            multicastConnection.open();
            connection = multicastConnection;
            logger.info("A Coordination channel is created like an instance of P2PDPMulticastConnection");
            break;
         
         case ( BROADCAST_CONNECTION ) :          
            P2PDPBroadcastConnection broadcastConnection = new P2PDPBroadcastConnection();
            broadcastConnection.setScanInterval(scanInterval);
            broadcastConnection.configure(coordAddr, coordPort);
            broadcastConnection.open();            
            connection = broadcastConnection;
            logger.info("A Coordination channel is created like an instance of P2PDPBroadcastConnection");
            break;
      }
      
      return connection;
   }   

}
