
package martin.mogrid.tl.asl.bagoftask;

import java.util.Enumeration;
import java.util.Hashtable;

import martin.mogrid.p2pdl.api.RequestIdentifier;


/**
 * @author luciana
 *
 * Created on 15/06/2005
 */
public class BoTRequestHistory {
   
   private Hashtable gridJobReqHistory;
   
   public BoTRequestHistory() {
      gridJobReqHistory = new Hashtable();
   }
   
   public boolean put(RequestIdentifier key, BoTRequest value) {
      if ( value != null ) { 
          gridJobReqHistory.put(key, value);
          return true;
      }
      return false;
   }
   
   public BoTRequest get(RequestIdentifier key) {
      if ( key != null ) 
         return ( (BoTRequest)gridJobReqHistory.get(key) );
      
      return null;
   }
   
   public BoTRequest remove(RequestIdentifier key) {
      if ( key != null )
         return ( (BoTRequest) gridJobReqHistory.remove(key) );    
      
      return null;  
   }

   public boolean containsKey(RequestIdentifier key) {
      return gridJobReqHistory.containsKey(key);
   }
   
   public Enumeration keys() {
      return gridJobReqHistory.keys();
   } 
   
   public int size() {
      return gridJobReqHistory.size();
   }  

}
