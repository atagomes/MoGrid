
package martin.mogrid.common.network;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import martin.mogrid.common.util.SystemUtil;

import org.apache.log4j.Logger;

/**
 * @author luciana
 *
 * Created on 15/06/2005
 */
public class NetworkUtil {

   //Manutencao do arquivo de log do servico
   private static final Logger logger = Logger.getLogger(NetworkUtil.class); 
 
   
   public static boolean portIsValid(int port) {
      if( port >= 1024 && port <= 65535 ) {
         return true;
      }
      return false;
   }
   
   public static boolean portIsNotValid(int port) {
      return ( ! portIsValid(port) );
   }
   
   public static boolean portIsValid(String portStr) {
      if ( SystemUtil.strIsNotNull(portStr) ) {
         try {
            int port = Integer.parseInt(portStr);
            portIsValid(port);
            
         } catch (NumberFormatException nfe ) {
            return false;            
         }
      }
      return false;
   }

   public static boolean portIsNotValid(String portStr) {
      return ( ! portIsValid(portStr) );
   }
   
   public static boolean ipAddressIsValid(String address) {      
      if ( SystemUtil.strIsNotNull(address) ) {      
         try {
            if ( InetAddress.getByName(address) != null )
               return true;
            
         } catch (UnknownHostException e) {
            return false;
         }
      }
      return false;
   }

   public static boolean ipAddressIsNotValid(String address) { 
      return ( ! ipAddressIsValid(address) );
   }
   
   public static boolean inetSocketAddressIsValid(InetSocketAddress socketAddress) {            
      if ( socketAddress != null ) {   
         if ( ! socketAddress.isUnresolved() )
            return true;
      }
      return false;
   }

   public static boolean inetSocketAddressIsNotValid(InetSocketAddress socketAddress) { 
      return ( ! inetSocketAddressIsValid(socketAddress) );
   }
   
   public static InetAddress getInetAddressByName(String hostIdentification) {      
      if ( SystemUtil.strIsNotNull(hostIdentification) ) {      
         try {
            return InetAddress.getByName(hostIdentification);            
         } catch (UnknownHostException e) {
            return null;
         }
      }
      return null;
   }
   
   //Retorna o nome do host dada a sua representação em String (IP ou host name)
   //Ex: Util.getHostName("10.0.0.1") => "host.domain"
   //Ex: Util.getHostName("host")     => "host.domain"
   public static String getHostName(String IP) {
      String hostName = null;
      if ( SystemUtil.strIsNotNull(IP) ) {      
         try {
            if ( InetAddress.getByName(IP) != null ) 
               hostName = InetAddress.getByName(IP).getHostName();
            
         } catch (UnknownHostException e) {
            hostName = null;
         }
      }
      return hostName;
   }
   
}
