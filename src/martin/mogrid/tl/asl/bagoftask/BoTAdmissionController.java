/**
 * @author lslima
 * 
 * Created on 14/03/2006
 */

package martin.mogrid.tl.asl.bagoftask;

import java.util.Enumeration;

import martin.mogrid.common.resource.ResourceDescriptor;
import martin.mogrid.common.resource.ResourceIdentifier;
import martin.mogrid.common.resource.ResourceQuery;
import martin.mogrid.entity.collaborator.registry.ResourceRegistry;
import martin.mogrid.p2pdl.collaboration.criterias.AdmissionController;
import martin.mogrid.p2pdl.collaboration.criterias.AdmissionControllerException;
import martin.mogrid.service.monitor.DeviceContext;

public class BoTAdmissionController implements AdmissionController {

   private ResourceRegistry             resourceRegistry = null;
   private DeviceContext                deviceContext    = null;
   private ResourceQuery                resourceQuery    = null;                       

   public ResourceIdentifier admit() throws AdmissionControllerException {
      try {
         Enumeration key = resourceRegistry.keys(); 
         while ( key.hasMoreElements() ) {
            ResourceIdentifier resKey   = (ResourceIdentifier)key.nextElement(); 
            ResourceDescriptor resValue = resourceRegistry.get(resKey);
            
            if ( resValue.compareWith(resourceQuery) ) {
               return resKey;
            }                        
         }
      } catch (Exception ex) {
         throw new AdmissionControllerException(ex);       
      }
      return null;
   }

   
   public void registerResources(ResourceRegistry resourceRegistry) {
      this.resourceRegistry = resourceRegistry;  
   }

   public void setDeviceContext(DeviceContext deviceContext) {
      this.deviceContext = deviceContext;
   }

   public void setRequest(ResourceQuery resourceQuery) {
      this.resourceQuery = resourceQuery;
   }

}
