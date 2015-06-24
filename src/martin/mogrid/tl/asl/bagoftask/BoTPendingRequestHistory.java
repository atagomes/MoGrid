
package martin.mogrid.tl.asl.bagoftask;

import java.util.Enumeration;
import java.util.Hashtable;

import martin.mogrid.p2pdl.api.RequestIdentifier;


/**
 * @author luciana
 *
 * Created on 15/06/2005
 */
public class BoTPendingRequestHistory {
   
   private Hashtable gridJobPendingReqHistory;
   
   public BoTPendingRequestHistory() {
      gridJobPendingReqHistory = new Hashtable();
   }
   
   public boolean put(RequestIdentifier key, BoTPendingRequest value) {
      if ( value != null ) { 
          gridJobPendingReqHistory.put(key, value);
          return true;
      }
      return false;
   }
   
   public BoTPendingRequest get(RequestIdentifier key) {
      if ( key != null ) 
         return ( (BoTPendingRequest)gridJobPendingReqHistory.get(key) );
      
      return null;
   }
   
   public BoTPendingRequest remove(RequestIdentifier key) {
      if ( key != null )
         return ( (BoTPendingRequest) gridJobPendingReqHistory.remove(key) );    
      
      return null;  
   }

   public boolean containsKey(RequestIdentifier key) {
      return gridJobPendingReqHistory.containsKey(key);
   }
   
   public Enumeration keys() {
      return gridJobPendingReqHistory.keys();
   } 
   
   public int size() {
      return gridJobPendingReqHistory.size();
   }  

}
