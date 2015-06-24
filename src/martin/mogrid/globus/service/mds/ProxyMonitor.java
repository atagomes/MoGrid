package martin.mogrid.globus.service.mds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchResult;

import org.apache.log4j.Logger;
import org.globus.ftp.GridFTPClient;

public class ProxyMonitor {
   
   private CpuInformation cpuObj;
   private MemoryInformation memoryObj;
   private OperationalSystemInformation osObj;
	private String host;    
   private String baseDN;
    
   private static final Logger logger = Logger.getLogger( ProxyMonitor.class );

	public ProxyMonitor( String host, String baseDN ){
		cpuObj = new CpuInformation();
		memoryObj = new MemoryInformation();
      osObj = new OperationalSystemInformation();
		this.host=host;
		this.baseDN=baseDN;
	}
	
	private Hashtable getJndiInfo() {
		String qop     = "auth-conf, auth";
		int port       = 2135;
		int version    = 3;
		
		if ( host == null ) {
			logger.error( "Host GIIS not specified" );
			System.exit(1);
		}
		   	
		Hashtable env = new Hashtable();    
		env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
		env.put("java.naming.ldap.version", String.valueOf(version));
		env.put(Context.PROVIDER_URL, "ldap://"+ host + ":" + port);
		env.put(Context.SECURITY_AUTHENTICATION,"simple");     
		env.put("javax.security.sasl.client.pkgs","org.globus.mds.gsi.jndi");
		env.put("javax.security.sasl.qop", qop);
      
		return env;
	}

	public GlobusResourceElements getResource() throws ProxyMonitorException {
		
		GlobusResourceElements resourceReturn = null;
		String filter  = "(objectclass=MdsHost)";
		Hashtable env = getJndiInfo();

		DirContext ctx = null;
	        

		try {
         ctx = new InitialDirContext(env);
			NamingEnumeration results = ctx.search(baseDN, filter, null);
			SearchResult si;
			Attributes attrs;
		
			ArrayList globusResource = new ArrayList();
				
			String auxHost;
			while ( results.hasMoreElements() ) {
				si = (SearchResult)results.next(); 
				attrs = si.getAttributes();
				StringTokenizer st = new StringTokenizer(si.getName(),"=");
				st.nextToken();
				StringTokenizer st1 = new StringTokenizer(st.nextToken(),",");
				
				auxHost=st1.nextToken();
				            
				float cpuFreeHz = cpuObj.getFreeCpuHz( attrs );
				float cpuFreePercent = cpuObj.getFreeCpuPercent( attrs );
				float memoryFreeKb = transKb( memoryObj.getFreeMemoryMb( attrs ) );
				float memoryFreePercent = memoryObj.getFreeMemoryPercent( attrs );
            String soName = osObj.getSOName( attrs );
				globusResource.add( new GlobusResource( auxHost, cpuFreePercent, memoryFreePercent, cpuFreeHz, memoryFreeKb, soName ) );
			}
			
		   	resourceReturn = getMoreResource( globusResource );
      } catch (NamingException e) {
          throw new ProxyMonitorException( "Error when getting resource information", e );
		} finally {			
		   if (ctx != null) {
				try {
               ctx.close();
            } catch (NamingException e) {
               throw new ProxyMonitorException( "Error when closing the context information object", e );
            } 
		
				
		   }
	   }
	  return resourceReturn;
	}
	
	private float transKb( float memoryFreeKb ) {
		float mem =  memoryFreeKb * 1024;
		return mem;
	}
	
	private GlobusResourceElements getMoreResource( ArrayList globusResource ) {
		GlobusResourceElements gsElements = new GlobusResourceElements();
		for( int i = 0; i < globusResource.size(); i++ ) {
			( (GlobusResource)globusResource.get(i) ).calculatePercent();
		}
		
		Collections.sort( globusResource, new GlobusResourceComparator() );
		
		for( int i = 0; i < globusResource.size(); i++ ) {
			gsElements.put( (GlobusResource)globusResource.get(i) );
		}
    	return gsElements;
	}

	public Vector getMachinesName() throws ProxyMonitorException {
		Vector hosts = null;
		String filter  = "(objectclass=MdsHost)";
		Hashtable env = getJndiInfo();

        DirContext ctx = null;
        try {
			ctx = new InitialDirContext(env);
            NamingEnumeration results = ctx.search(baseDN, filter, null);
		    SearchResult si;
			while (results.hasMoreElements()) {
				si = (SearchResult)results.next(); 
				StringTokenizer st = new StringTokenizer(si.getName(),"=");
				st.nextToken();
				StringTokenizer st1 = new StringTokenizer(st.nextToken(),",");
				hosts.add(st1.nextToken());		
	        }
        }catch(Exception e){
        	throw new ProxyMonitorException( "Error when getting the machines names", e );
        }
		return hosts;
	}
         	
	public boolean machineTest( String host ) {
      GridFTPClient machine = null;
		try{
		  machine = new GridFTPClient(host,2811);
        machine.authenticate( null );
		}catch( Exception e ){
			return false;
		} finally {
			try{				
			    machine.close();
			}catch(Exception ee){}
		}
		return true;
	}
	
	class GlobusResourceComparator implements Comparator {
		public int compare( Object o1, Object o2 ) {
			
			GlobusResource gResource1 = (GlobusResource)o1;
			GlobusResource gResource2 = (GlobusResource)o2;
			
			float percent  = gResource1.getPercent() - gResource2.getPercent();
			return (int) percent;
		}
	}
}