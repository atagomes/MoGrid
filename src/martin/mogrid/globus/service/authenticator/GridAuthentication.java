package martin.mogrid.globus.service.authenticator;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import martin.mogrid.globus.service.mds.ProxyMonitor;

public class GridAuthentication {

	private  static String hostGiis   = null; 
	private  static String hostDn     = null;
	private  static String passPhrase = null;
   
   static {
      hostGiis   = "grade02.lncc.br"; 
      hostDn     = "mds-vo-name=GIIS-ComCiDis, o=grid";
      passPhrase = "";
   }
	
	public static String getHostGiis() {
		return hostGiis; 
	}
	
	public static String getHostDn() {
		return hostDn;
	}
	
	public static String getPassPhrase() {
		JPasswordField jPass = new JPasswordField (10 );
		JOptionPane.showMessageDialog( null,jPass );
		passPhrase = String.valueOf( jPass.getPassword() );
		return passPhrase;
	}
	
    public static void setHostDn( String hostDn ) {
		GridAuthentication.hostDn = hostDn;
    }
	
	public static void setHostGiis( String hostGiis ) {
		GridAuthentication.hostGiis = hostGiis;
	}
		
	public static void setPassPhrase( String passPhrase ) {
		GridAuthentication.passPhrase = passPhrase;
	}

    public static ProxyMonitor createProxyMonitor() {
        return new ProxyMonitor( hostGiis, hostDn );
    }
}
