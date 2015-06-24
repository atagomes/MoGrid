package martin.mogrid.p2pdl.protocol;

import martin.mogrid.p2pdl.protocol.message.P2PDPMessageInterface;



/**
 * @author luciana
 *
 * Created on 22/06/2005
 */
public interface P2PDPSenderConnection extends P2PDPConnection {  
   
   public abstract void send(P2PDPMessageInterface msg) throws P2PDPConnectionException;
        
}
