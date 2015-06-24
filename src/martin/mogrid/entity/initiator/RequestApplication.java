package martin.mogrid.entity.initiator;

import java.util.Enumeration;
import java.util.Hashtable;

import martin.mogrid.p2pdl.api.DiscoveryApplicationFacade;
import martin.mogrid.p2pdl.api.RequestIdentifier;


public class RequestApplication {
   
   private Hashtable requestApplication; 

   public RequestApplication() {
      requestApplication = new Hashtable();
   }
      
   public boolean put(RequestIdentifier key, DiscoveryApplicationFacade value) {
      if ( key != null && value != null ) {
         requestApplication.put(key, value);
         return true;
      }
      return false;
   }
   
   public DiscoveryApplicationFacade get(RequestIdentifier key) {
      DiscoveryApplicationFacade value = null;
      
      if ( key != null ) 
         value = (DiscoveryApplicationFacade) requestApplication.get(key);
      
      return value;
   }
   
   public DiscoveryApplicationFacade remove(RequestIdentifier key) {
      DiscoveryApplicationFacade value = null;
      if ( key != null )
         value = (DiscoveryApplicationFacade) requestApplication.remove(key);
      
      return value;
   }

   public boolean containsKey(RequestIdentifier key) {
      return requestApplication.containsKey(key);
   }

   public boolean containsValue(DiscoveryApplicationFacade value) {
      return requestApplication.contains(value);
   }
   
   public Enumeration keys() {
      return requestApplication.keys();
   } 

   public Enumeration elements() {
      return requestApplication.elements();
   } 
   
   public int size() {
      return requestApplication.size();
   }  
}
