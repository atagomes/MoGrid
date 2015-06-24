package martin.mogrid.p2pdl.protocol;

import java.net.SocketTimeoutException;

import martin.mogrid.p2pdl.protocol.message.P2PDPMessageInterface;



/**
 * @author luciana
 *
 * Created on 22/06/2005
 */
public interface P2PDPReceiverConnection extends P2PDPConnection {   
       
   public abstract int  getScanInterval();
   public abstract P2PDPMessageInterface receive() throws P2PDPConnectionException, SocketTimeoutException;        
        
}
