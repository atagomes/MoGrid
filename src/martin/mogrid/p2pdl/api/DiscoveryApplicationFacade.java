
package martin.mogrid.p2pdl.api;


/**
 * @author luciana
 *
 * Created on 22/06/2005
 */
public interface DiscoveryApplicationFacade {

   //Metodos da API de descoberta na APLICACAO
   public abstract void receiveCReplyList(RequestIdentifier reqID, CollaboratorReplyList collabReplyList);  
   
}
