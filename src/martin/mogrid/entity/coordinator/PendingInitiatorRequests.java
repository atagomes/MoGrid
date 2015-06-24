package martin.mogrid.entity.coordinator;

import java.util.Enumeration;
import java.util.Hashtable;

import martin.mogrid.p2pdl.api.RequestIdentifier;



public class PendingInitiatorRequests {
   
   private Hashtable pendingRequests; 

   public PendingInitiatorRequests() {
      pendingRequests = new Hashtable();
   }
      
   public boolean put(RequestIdentifier key, InitiatorRequest value) {
      if ( key != null && value != null ) {
         pendingRequests.put(key, value);
         return true;
      }
      return false;
   }
   
   public InitiatorRequest get(RequestIdentifier key) {
      InitiatorRequest resource = null;
      
      if ( key != null ) 
         resource = (InitiatorRequest) pendingRequests.get(key);
      
      return resource;
   }
   
   public InitiatorRequest remove(RequestIdentifier key) {
      InitiatorRequest resource = null;
      if ( key != null )
         resource = (InitiatorRequest) pendingRequests.remove(key);
      
      return resource;
   }
   
   public boolean containsKey(Object key) {
      return pendingRequests.containsKey(key);
   }
   
   public Enumeration keys() {
      return pendingRequests.keys();
   } 

   public Enumeration elements() {
      return pendingRequests.elements();
   } 
   
   public int size() {
      return pendingRequests.size();
   }  
}
