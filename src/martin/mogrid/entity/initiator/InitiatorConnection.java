package martin.mogrid.entity.initiator;

import java.net.InetSocketAddress;

import martin.mogrid.common.network.NetworkUtil;
import martin.mogrid.p2pdl.protocol.P2PDPConnectionException;
import martin.mogrid.p2pdl.protocol.P2PDPProperties;
import martin.mogrid.p2pdl.protocol.P2PDPUnicastConnection;

import org.apache.log4j.Logger;


public class InitiatorConnection extends P2PDPUnicastConnection {

   //Manutencao do arquivo de log para debug
   private static final Logger logger = Logger.getLogger(InitiatorConnection.class);
   
   
   public InitiatorConnection() throws P2PDPConnectionException {
      super();
      P2PDPProperties.load();

      //SEND: INITIATOR -> COORDINATOR
      String coordAddress = P2PDPProperties.getCoordinatorAddress();
      int    coordPort    = P2PDPProperties.getCoordinatorPort();  
      if ( NetworkUtil.ipAddressIsNotValid(coordAddress) || NetworkUtil.portIsNotValid(coordPort) ) {
         throw new P2PDPConnectionException("IP address and/or port to send message are not valid. Port should be a value between 1024 and 65535.");         
      }
      InetSocketAddress senderAddress = new InetSocketAddress(coordAddress, coordPort); 

      //RECEIVE: INITIATOR <- COORDINATOR
      int    initPort    = P2PDPProperties.getInitiatorPort();  
      if ( NetworkUtil.portIsNotValid(initPort) ) {
         throw new P2PDPConnectionException("Port to send message is not valid. Port should be a value between 1024 and 65535.");         
      }
      InetSocketAddress receiverAddress = new InetSocketAddress(initPort);  
      
      configure(senderAddress, receiverAddress, "Initiator Message Channel");
   }

}
