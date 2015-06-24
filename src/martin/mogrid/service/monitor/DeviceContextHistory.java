
package martin.mogrid.service.monitor;

import java.util.Enumeration;
import java.util.Hashtable;

import martin.mogrid.common.util.SystemUtil;




/**
 * @author luciana
 *
 * Created on 15/06/2005
 */
public class DeviceContextHistory {
   
   private Hashtable deviceContextHistory; 

   public DeviceContextHistory() {
      deviceContextHistory = new Hashtable();
   }
   
   public boolean put(DeviceContext devContext) {
      if ( devContext != null ) {
         String key = devContext.getKey(); 
         if ( SystemUtil.strIsNotNull(key) ) {
            deviceContextHistory.put(key, devContext);
            return true;
         }
      }
      return false;
   }
   
   public DeviceContext get(String key) {
      if ( SystemUtil.strIsNotNull(key) ) 
         return ( (DeviceContext)deviceContextHistory.get(key) );
      
      return null;
   }
   
   public DeviceContext remove(String key) {
      if ( SystemUtil.strIsNotNull(key) )
         return ( (DeviceContext) deviceContextHistory.remove(key) );    
      
      return null;  
   }

   public boolean containsKey(String key) {
      return deviceContextHistory.containsKey(key);
   }
   
   public Enumeration keys() {
      return deviceContextHistory.keys();
   } 
   
   public int size() {
      return deviceContextHistory.size();
   }  

}
