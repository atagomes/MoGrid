
package martin.mogrid.p2pdl.api;

import martin.mogrid.common.context.ContextInformation;
import martin.mogrid.common.resource.ResourceQuery;

/**
 * @author luciana
 *
 * Created on 22/06/2005
 */
public interface DiscoveryInitiatorFacade {

   //Metodos da API de descoberta no INITIATOR (Discovery API)
   public abstract RequestProfile createRequestProfile(ContextInformation ctxtInfo, int numMaxReplies, long maxReplyDelay, int numHops);
   public abstract void           discover(ResourceQuery resourceQuery, RequestProfile reqProfile, DiscoveryApplicationFacade app);  
   
   public abstract void           stop();
}
