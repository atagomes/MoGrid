package martin.mogrid.p2pdl.collaboration.criterias;

import martin.mogrid.common.resource.ResourceIdentifier;
import martin.mogrid.common.resource.ResourceQuery;
import martin.mogrid.entity.collaborator.registry.ResourceRegistry;
import martin.mogrid.service.monitor.DeviceContext;

public interface AdmissionController {

   public abstract void registerResources(ResourceRegistry resourceRegistry);
   
   public abstract void setDeviceContext(DeviceContext deviceContext);
   public abstract void setRequest(ResourceQuery resourceQuery);
   
   public abstract ResourceIdentifier admit() throws AdmissionControllerException ;   
   
}
