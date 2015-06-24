package martin.mogrid.entity.collaborator.registry;

import java.util.Enumeration;
import java.util.Hashtable;

import martin.mogrid.common.resource.ResourceDescriptor;
import martin.mogrid.common.resource.ResourceIdentifier;



public class ResourceRegistry { 
   
   private Hashtable resourceRegistry; 

   public ResourceRegistry() {
      resourceRegistry = new Hashtable();
   }
      
   public boolean put(ResourceIdentifier key, ResourceDescriptor value) {
      if ( key != null && value != null ) {
         resourceRegistry.put(key, value);
         return true;
      }
      return false;
   }
   
   public ResourceDescriptor get(ResourceIdentifier key) {
      ResourceDescriptor resource = null;
      
      if ( key != null ) 
         resource = (ResourceDescriptor) resourceRegistry.get(key);
      
      return resource;
   }
   
   public ResourceDescriptor remove(ResourceIdentifier key) {
      ResourceDescriptor resource = null;
      if ( key != null )
         resource = (ResourceDescriptor) resourceRegistry.remove(key);
      
      return resource;
   }

   public boolean containsKey(ResourceIdentifier key) {
      return resourceRegistry.containsKey(key);
   }
   
   public Enumeration keys() {
      return resourceRegistry.keys();
   } 
      
   public Enumeration elements() {
      return resourceRegistry.elements();
   } 
   
   public int size() {
      return resourceRegistry.size();
   }  
}
