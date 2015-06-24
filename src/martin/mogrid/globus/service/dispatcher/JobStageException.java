package martin.mogrid.globus.service.dispatcher;

public class JobStageException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public JobStageException() {
		super();
	}
	
	public JobStageException( String msg ) {
		super( msg );
	}
	
	public JobStageException( String msg, Throwable cause ) {
		super( msg, cause );
	}
	
	public JobStageException( Throwable cause ) {
		super( cause );
	}

}
