package martin.mogrid.entity.collaborator.registry;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import martin.mogrid.common.resource.ResourceIdentifier;
import martin.mogrid.p2pdl.api.RequestIdentifier;


public class RequestResource {
   
   private Hashtable requestResource; 

   public RequestResource() {
      requestResource = new Hashtable();
   }
      
   public boolean put(RequestIdentifier key, ResourceIdentifier value) {
      if ( key != null && value != null ) {
         requestResource.put(key, value);
         return true;
      }
      return false;
   }
   
   public ResourceIdentifier get(RequestIdentifier key) {
      ResourceIdentifier resource = null;
      
      if ( key != null ) 
         resource = (ResourceIdentifier) requestResource.get(key);
      
      return resource;
   }
   
   public ResourceIdentifier remove(RequestIdentifier key) {
      ResourceIdentifier resource = null;
      if ( key != null )
         resource = (ResourceIdentifier) requestResource.remove(key);
      
      return resource;
   }

   public boolean containsKey(RequestIdentifier key) {
      return requestResource.containsKey(key);
   }

   public boolean containsValue(ResourceIdentifier value) {
      return requestResource.contains(value);
   }
   
   public Enumeration keys() {
      return requestResource.keys();
   } 

   public Enumeration elements() {
      return requestResource.elements();
   } 
   
   public RequestIdentifier[] getKeysFromValue(ResourceIdentifier value) {
      Vector ensureKey = new Vector();  
      for(Enumeration e=requestResource.keys(); e.hasMoreElements();) {
         RequestIdentifier  key = (RequestIdentifier) e.nextElement();
         ResourceIdentifier val = (ResourceIdentifier) requestResource.get(key);
         if ( val.equals(value) ) {
            ensureKey.add(key);        
         }
      }
      RequestIdentifier[] keys = (RequestIdentifier[]) ensureKey.toArray();
      return keys;
   }
   
   public int size() {
      return requestResource.size();
   }  
}
