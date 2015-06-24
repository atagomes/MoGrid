/*
 * Created on 02/10/2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package martin.mogrid.p2pdl.collaboration.criterias;

import martin.mogrid.common.context.MonitoredContext;
import martin.mogrid.p2pdl.protocol.message.P2PDPInitiatorRequestMessage;
import martin.mogrid.service.monitor.DeviceContext;

public interface CollaboratorReplyTimerFunction {
   
   public abstract void configure(DeviceContext devCtxt, P2PDPInitiatorRequestMessage iReqMessage, float willingness, float gamaTransferDelay);
   public abstract MonitoredContext getMonitoredContext();
   public abstract long getTimeout(); //in milliseconds

}
