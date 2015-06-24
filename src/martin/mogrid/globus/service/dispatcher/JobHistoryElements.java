package martin.mogrid.globus.service.dispatcher;


import java.util.Hashtable;

public class JobHistoryElements {
	
	private Hashtable jobHistoryElement;
	
	public JobHistoryElements(){
		jobHistoryElement = new Hashtable();
	}

	public boolean put( String jobID, JobElements value ){
		if( value != null ){
			jobHistoryElement.put( jobID, value );
			return true;
		}
		return false;
	}

	public JobElements get( String jobID ){
		if(jobID != null){
			return ( JobElements ) jobHistoryElement.get(jobID);
		}
		return null;
	}
}
