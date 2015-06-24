package martin.mogrid.entity.proxy.registry.globus;

import java.util.Enumeration;
import java.util.Hashtable;

import martin.mogrid.common.resource.ResourceIdentifier;

public class ProxyResourceRegistry {

   private Hashtable proxyResourceRegistry; 
   
   public static ProxyResourceRegistry proxy= null;
   
   private ProxyResourceRegistry() {
      proxyResourceRegistry = new Hashtable();
   }
      
   public boolean put(ResourceIdentifier key, ProxyResource value) {
      if ( key != null && value != null ) {
         proxyResourceRegistry.put(key, value);
         return true;
      }
      return false;
   }
   
   public ProxyResource get(ResourceIdentifier key) {
      ProxyResource resource = null;
      
      if ( key != null ) 
         resource = (ProxyResource) proxyResourceRegistry.get(key);
      
      return resource;
   }
   
   public ProxyResource remove(ResourceIdentifier key) {
      ProxyResource resource = null;
      if ( key != null )
         resource = (ProxyResource) proxyResourceRegistry.remove(key);
      
      return resource;
   }

   public boolean containsKey(ResourceIdentifier key) {
      return proxyResourceRegistry.containsKey(key);
   }
   
   public Enumeration keys() {
      return proxyResourceRegistry.keys();
   } 
      
   public Enumeration elements() {
      return proxyResourceRegistry.elements();
   } 
   
   public int size() {
      return proxyResourceRegistry.size();
   }

   public static ProxyResourceRegistry getInstance() {
      if( proxy ==  null ) {
         proxy = new ProxyResourceRegistry(); 
         return proxy;
      }
      return proxy;
   }  

}
