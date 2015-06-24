package martin.mogrid.entity.proxy.registry.globus;

import java.util.Enumeration;
import java.util.Hashtable;

import martin.mogrid.common.resource.ResourceDescriptor;

public class ProxyResource {
   
   private Hashtable proxyResource; 

   public ProxyResource() {
      proxyResource = new Hashtable();
   }
      
   public boolean put(String key, ResourceDescriptor value) {
      if ( key != null && value != null ) {
         proxyResource.put(key, value);
         return true;
      }
      return false;
   }
   
   public ResourceDescriptor get(String key) {
      ResourceDescriptor resource = null;
      
      if ( key != null ) {
         //resource = (ResourceDescriptor) proxyResource.get(key);
         resource = (ResourceDescriptor) proxyResource.get(key);
         if (resource == null)
            return null;
         resource = new ResourceDescriptor( resource.getIdentifier(), resource.getDescription(), resource.getKeywords(), resource.getPath() );
      }
      
      return resource;
   }
   
   public ResourceDescriptor remove(String key) {
      ResourceDescriptor resource = null;
      if ( key != null )
         resource = (ResourceDescriptor) proxyResource.remove(key);
      
      return resource;
   }

   public boolean containsKey(String key) {
      return proxyResource.containsKey(key);
   }
   
   public Enumeration keys() {
      return proxyResource.keys();
   } 
      
   public Enumeration elements() {
      return proxyResource.elements();
   } 
   
   public int size() {
      return proxyResource.size();
   }  


}
