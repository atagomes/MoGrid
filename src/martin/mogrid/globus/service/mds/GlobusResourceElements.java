package martin.mogrid.globus.service.mds;

import java.util.Enumeration;
import java.util.Hashtable;

public class GlobusResourceElements {
   
	private Hashtable globusResource;
	
	public GlobusResourceElements(){
		 globusResource = new Hashtable();
	}

	public boolean put( GlobusResource gResource ) {
		if( gResource != null ) {
			globusResource.put( gResource.getHostName(), gResource );
			return true;
		}
		return false;
	}

	public GlobusResource get( String key ){
		if( key != null ){
			return (GlobusResource)globusResource.get( key );
		}
		return null;
	}
	
	public Enumeration keys(){
		return globusResource.keys();
	}
}
