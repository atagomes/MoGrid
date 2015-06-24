package martin.mogrid.service.monitor;


public interface ContextParser {

   public abstract DeviceContext parseMonitorData(Object data) throws ContextParserException;
   
}
