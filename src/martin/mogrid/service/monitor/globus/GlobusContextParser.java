package martin.mogrid.service.monitor.globus;

import martin.mogrid.service.monitor.ContextParser;
import martin.mogrid.service.monitor.ContextParserException;
import martin.mogrid.service.monitor.DeviceContext;

public class GlobusContextParser implements ContextParser {

	public DeviceContext parseMonitorData(Object data) throws ContextParserException {
      if ( data == null ) {
         throw new ContextParserException("Data received from Monitor is null.");
      }
      if ( !(data instanceof GlobusDeviceContext) ) {
         throw new ContextParserException("Data received from Monitor Parser in an invalid format.");
      }    
      
      DeviceContext devCtxt = (GlobusDeviceContext)data;
		return devCtxt;
	}
	
}
