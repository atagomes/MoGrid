
package martin.mogrid.service.monitor.moca.linux;

import martin.mogrid.common.network.LocalHost;
import martin.mogrid.common.util.MoGridString;
import martin.mogrid.common.util.SystemUtil;
import martin.mogrid.service.monitor.ContextParser;
import martin.mogrid.service.monitor.ContextParserException;
import martin.mogrid.service.monitor.DeviceContext;




/**
 * @author luciana
 * copyright Vagner Sacramento
 *
 * Created on 15/06/2005
 */
public class MoCALinuxContextParser implements ContextParser {      
   
   /**
    * Parse data received from the moca.service.monitor:
    * Free CPU in %#Free CPU abs#Free Memory in %#Free Memory abs
    * 
    * For instance: 245#20#264696#80
    *
    * MoGrid Adaptation:
    * CPU Usage [0], Free Memory [2],    (in %   value) 
    * CPU Usage [1] and Free Memory [3]  (in abs value)
    * 
    * @param monitorData - a data packet from monitor
    * @return a register with the device´s context information
    */
   
   public DeviceContext parseMonitorData(Object data) throws ContextParserException {   
      if ( data == null ) {
         throw new ContextParserException("Data received from Monitor is null.");
      }
      if ( !(data instanceof String) ) {
         throw new ContextParserException("Data received from Monitor in an invalid format.");
      }
      
      
      String monitorData = (String) data;      
      if ( SystemUtil.strIsNotNull(monitorData) ) {
         MoCALinuxDeviceContext deviceContext = new MoCALinuxDeviceContext();  // Store the device´s context information
         
         //String[] contextTokens = monitorData.split("#");              // Context information tokens
         String[] contextTokens = MoGridString.split(monitorData, "#");  // Context information tokens
        
         if ( contextTokens != null && contextTokens.length == 4 ) {
            try {                   
               //Mogrid Adaptation: problemas com Memory e Connectivity
               //o primeiro eh disponibilizado em Kb e o segundo em um
               //valor maluco que nao sabemos ainda como tratar
               
               deviceContext.setTimestamp();
               deviceContext.setEnergyLevel(100);
               deviceContext.setConnectivityLevel(100); 
               
               deviceContext.setCpuLevel(Integer.parseInt(contextTokens[0]));
               deviceContext.setCpuValue(Integer.parseInt(contextTokens[1]));
               deviceContext.setMemoryLevel(Float.parseFloat(contextTokens[2]));
               deviceContext.setMemoryValue(Float.parseFloat(contextTokens[3]));
               
               deviceContext.setIPAddress(LocalHost.getLocalHostAddress());
               deviceContext.setNetworkMask("");  
               deviceContext.setMacAddress("");
               //if ( Util.strIsNotNull(contextTokens[6]) ) {
               //   deviceContext.setMacAddress(contextTokens[6].toUpperCase());
               //}
 
            } catch (NumberFormatException nfe) {
               throw new ContextParserException(nfe);
            }
         } else {
            throw new ContextParserException("Data received from Monitor is not in a valid format.");
         }
         return deviceContext;
         
      } else {        
         throw new ContextParserException("Data received from Monitor is null.");         
      }
   }  
   
}
