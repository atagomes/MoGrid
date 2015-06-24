package martin.mogrid.entity.coordinator;

import java.net.InetSocketAddress;

import martin.mogrid.common.network.NetworkUtil;
import martin.mogrid.p2pdl.protocol.P2PDPConnectionException;
import martin.mogrid.p2pdl.protocol.P2PDPProperties;
import martin.mogrid.p2pdl.protocol.P2PDPUnicastConnection;

import org.apache.log4j.Logger;



public class CoordinatorConnection extends P2PDPUnicastConnection {

   //Manutencao do arquivo de log para debug
   private static final Logger logger = Logger.getLogger(CoordinatorConnection.class);
   
   public CoordinatorConnection() throws P2PDPConnectionException { 
      P2PDPProperties.load();
      
      //SEND: COORDINATOR -> INITIATOR  
      int initPort = P2PDPProperties.getInitiatorPort();  
      if ( NetworkUtil.portIsNotValid(initPort) ) {
         throw new P2PDPConnectionException("Port to send message is not valid. Port should be a value between 1024 and 65535.");         
      }
      InetSocketAddress senderAddress = new InetSocketAddress(initPort);

      //RECEIVE: COORDINATOR <- INITIATOR
      int coordPort  = P2PDPProperties.getCoordinatorPort();  
      if ( NetworkUtil.portIsNotValid(coordPort) ) {
         throw new P2PDPConnectionException("Port to send message is not valid. Port should be a value between 1024 and 65535.");         
      }
      InetSocketAddress receiverAddress = new InetSocketAddress(coordPort);  
      
      configure(senderAddress, receiverAddress, "Coordinator Message Channel");      
     
   }

}
