package martin.mogrid.service.monitor.moca.linux;

import martin.mogrid.service.monitor.DeviceContext;

public class MoCALinuxDeviceContext extends DeviceContext {
  
   /**
    * Parse data received from the moca.service.monitor:
    * Free CPU in %#Free CPU abs#Free Memory in %#Free Memory abs#MobileHost's IP#MobileHost's MASK#MobileHost's MacAddr
    * 
    * For instance: 245#20#264696#80#10.10.10.11#255.255.255.0#00:02:2D:A5:06:8C
    *
    * MoGrid Adaptation:
    * CPU Usage [0], Free Memory [2], IP [4], Mascara [5], and MAC [6] in % value
    * CPU Usage [1] and Free Memory [3] in abs value
    * 
    * @param monitorData - a data packet from monitor
    * @return a register with the device´s context information
    */
   
   /** Comment for <code>serialVersionUID</code> */
   private static final long serialVersionUID = -1209194020060001276L;
 
}
