package martin.mogrid.globus.service.mds;

public class ProxyMonitorException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public ProxyMonitorException() {
		super();
	}
	
	public ProxyMonitorException( String msg ) {
		super( msg );
	}
	
	public ProxyMonitorException( String msg, Throwable cause ) {
		super( msg, cause );
	}
	
	public ProxyMonitorException( Throwable cause ) {
		super( cause );
	}
}
