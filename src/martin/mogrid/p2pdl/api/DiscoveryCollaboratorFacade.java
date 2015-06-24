
package martin.mogrid.p2pdl.api;

import martin.mogrid.common.resource.ResourceDescriptor;
import martin.mogrid.common.resource.ResourceIdentifier;
import martin.mogrid.p2pdl.collaboration.criterias.AdmissionController;
import martin.mogrid.p2pdl.collaboration.criterias.CollaboratorReplyTimerFunction;
import martin.mogrid.service.monitor.ContextParser;

/**
 * @author luciana
 *
 * Created on 22/06/2005
 */
public interface DiscoveryCollaboratorFacade {

   //Metodos da API de descoberta no Collaborator (Discovery API)
   public abstract ResourceIdentifier register(ResourceDescriptor resourceDescriptor);
   public abstract void               deregister(ResourceIdentifier resID);
   public abstract void               deregisterAll();
   
   public abstract void               setCollaborationLevel(float collaborationLevel);                  // Willingness
   public abstract void               setForwardRequestDelay(int minForwardDelay, int maxForwardDelay); // Delay to forward IReq messages, defines a randomic value in the interval [min, max]
   public abstract void               setTransferDelay(float transferDelay);                            // S (tunning parameter representing transfer delay at each transmission)
   
   public abstract float              getCollaborationLevel();         // Willingness
   public abstract int                getMinForwardRequestDelay();     // Min value to request forward delay 
   public abstract int                getMaxForwardRequestDelay();     // Max value to request forward delay 
   public abstract float              getTransferDelay();              // S (tunning parameter representing transfer delay at each transmission)
   
   public abstract void               setAdmissionController(AdmissionController admissionController);
   
   public abstract void               setCollaboratorReplyTimerFunction(CollaboratorReplyTimerFunction collabRepTimerFunction);      
   public abstract void               setContextParser(ContextParser contextParser); 
   
   public abstract void               stop();
  
}
