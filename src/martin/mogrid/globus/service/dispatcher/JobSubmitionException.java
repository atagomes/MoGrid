package martin.mogrid.globus.service.dispatcher;

public class JobSubmitionException extends Exception {

	private static final long serialVersionUID = 1L;

	public JobSubmitionException() {
		super();
	}
	
	public JobSubmitionException( String msg ) {
		super( msg );
	}
	
	public JobSubmitionException( String msg, Throwable cause ) {
		super( msg, cause );
	}
	
	public JobSubmitionException( Throwable cause ) {
		super( cause );
	}

}
