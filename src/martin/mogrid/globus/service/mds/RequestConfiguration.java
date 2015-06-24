package martin.mogrid.globus.service.mds;


import java.util.Enumeration;
import java.util.Vector;

import martin.mogrid.globus.service.authenticator.GridAuthentication;
import martin.mogrid.globus.service.authenticator.ProxyInit;

import org.apache.log4j.Logger;

public abstract class RequestConfiguration {

    //Manutencao do arquivo de log do servico
	private static final Logger logger = Logger.getLogger(RequestConfiguration.class);
	
	private static boolean isRunning;
   //private static GlobusResourceElements gsElements;
	
	static {
		isRunning = false;
	}

	public static String[] chooseGrid(){
	  	Vector hosts = new Vector();
	   proxyInit();
         
	  	ProxyMonitor proxyMonitor = GridAuthentication.createProxyMonitor();      
      GlobusResourceElements gsElements = null;
	   try {
           gsElements = proxyMonitor.getResource();
		} catch ( ProxyMonitorException e ) {
			e.printStackTrace();
		}
	   Enumeration keys = gsElements.keys();
	   while( keys.hasMoreElements() ){
	   	String host = (String)keys.nextElement();
	   	if ( proxyMonitor.machineTest( host ) ){
	   		hosts.add( host );
	   	}
	   }
	   hosts.trimToSize();
	   String[] hostTo = new String[hosts.size()];
	   for(int i =0; i<hosts.size();i++){
	   	hostTo[i] = (String)hosts.elementAt(i);
	   	logger.info("Grid Choosed: "+hostTo[i]);
	   }
	   return hostTo;
	}
	
	public static void proxyInit() {
      if( !isRunning ) {
         new ProxyInit().run();
         isRunning = true;
      }
      
	}
   
	/*public static JobElements createJobElements( String host, String arguments, String executable, File[] files ){
	 	JobElements jobEl = new JobElements();
	  	jobEl.setHost( host );
	   	jobEl.setArguments( arguments );
	   	jobEl.setExecutable( executable );
	   	jobEl.setFiles( files );
	   	return jobEl;
   }
	
	public static JobElements createJobElements( String host, String arguments, String executable, File file ){
	 	JobElements jobEl = new JobElements();
	  	jobEl.setHost( host );
	   	jobEl.setArguments( arguments );
	   	jobEl.setExecutable( executable );
	   	File[] files = new File[1];
	   	files[0] = file;
	   	jobEl.setFiles( files );
	   	return jobEl;
   }*/
}