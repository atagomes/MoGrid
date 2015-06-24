package martin.mogrid.entity.proxy.globus;

import java.util.Enumeration;

import martin.mogrid.common.resource.ResourceDescriptor;
import martin.mogrid.common.resource.ResourceIdentifier;
import martin.mogrid.common.resource.ResourceQuery;
import martin.mogrid.entity.collaborator.registry.ResourceRegistry;
import martin.mogrid.entity.proxy.registry.globus.ProxyResource;
import martin.mogrid.entity.proxy.registry.globus.ProxyResourceRegistry;
import martin.mogrid.p2pdl.collaboration.criterias.AdmissionController;
import martin.mogrid.p2pdl.collaboration.criterias.AdmissionControllerException;
import martin.mogrid.service.contextlistener.ContextListener;
import martin.mogrid.service.contextlistener.ContextListenerException;
import martin.mogrid.service.monitor.DeviceContext;
import martin.mogrid.service.monitor.DeviceContextHistory;
import martin.mogrid.service.monitor.globus.GlobusDeviceContext;

import org.apache.log4j.Logger;

public class ProxyAdmissionController implements AdmissionController {
   
   //Manutencao do arquivo de log do servico
   private static final Logger logger = Logger.getLogger(ProxyAdmissionController.class);

   private ResourceRegistry             resourceRegistry = null;
   private GlobusDeviceContext          deviceContext    = null;
   private ResourceQuery                resourceQuery    = null;

   /* Para passar no controle de admissao no PROXY eh preciso que apenas
    * UMA maquina tenha o recurso. 
    * As maquinas que receberao as tarefas serao escolhidas na classe ProxyCollaborator
    * que eh a responsavel por enviar os jobs.
    * */
   public ResourceIdentifier admit() throws AdmissionControllerException {
      ProxyResourceRegistry resourceRegistry = ProxyResourceRegistry.getInstance();
      Enumeration key = resourceRegistry.keys();
      
      if( resourceQuery.isMultipleResourceRequest() ) { 
         //int ctrl = 0;
         ResourceIdentifier resourceID = new ResourceIdentifier();
         
         String match[] = resourceQuery.getResourceToMatchArray();
           
         int numOfResources = match.length;
         
         ContextListener contextListener = null;
         try {
            contextListener = ContextListener.getInstance();
         } catch (ContextListenerException e) {
            e.printStackTrace();
         }
         
         DeviceContextHistory history  = contextListener.getDeviceContextHistory();
         Enumeration          machines = history.keys();
         
         while( machines.hasMoreElements() ) {
            int   ctrl = 0;
            String IP   = machines.nextElement().toString();
            while ( key.hasMoreElements() ) {
               ResourceIdentifier resKey   = (ResourceIdentifier)key.nextElement(); 
               ProxyResource proxyResource = (ProxyResource) resourceRegistry.get(resKey);
               ResourceDescriptor resValue = (ResourceDescriptor) proxyResource.get(IP);
               
               if( resValue == null) {
                  continue;
               }
               
               for( int j = 0; j < numOfResources; j++ ) {
                  if( resValue.compareWith( new ResourceQuery( match[j], resourceQuery.getWhereClausule() ) ) ) {
                     ctrl++;
                     resourceID.addResourceIdentifier( resKey );
                     if( ctrl == numOfResources ) {
                        return resourceID;
                     }
                     break;
                  }
               }
            }    
         }
         
         /*while ( key.hasMoreElements() ) {
            ResourceIdentifier resKey   = (ResourceIdentifier)key.nextElement(); 
            ProxyResource proxyResource = (ProxyResource) resourceRegistry.get(resKey);
            Enumeration keys = proxyResource.keys();
            while ( keys.hasMoreElements() ) {
               String IP = (String)keys.nextElement();
               ResourceDescriptor resValue = (ResourceDescriptor) proxyResource.get(IP);
               
               for( int i = 0; i < numOfResources; i++ ) {
                  if( resValue.compareWith( new ResourceQuery( match[i], resourceQuery.getWhereClausule() ) ) ) {
                     ctrl++;
                     resourceID.addResourceIdentifier( resKey );
                     if( ctrl == numOfResources ) {
                        return resourceID;
                     }
                  }
               }
            }
         }*/         
      } else {      
         while ( key.hasMoreElements() ) {
            ResourceIdentifier resKey   = (ResourceIdentifier)key.nextElement(); 
            ProxyResource proxyResource = (ProxyResource) resourceRegistry.get(resKey);
            Enumeration keys = proxyResource.keys();
            while ( keys.hasMoreElements() ) {
               String IP = (String)keys.nextElement();
               ResourceDescriptor resValue = (ResourceDescriptor) proxyResource.get(IP);
                           
                     
               if ( resValue.compareWith(resourceQuery) ) {
                  return resKey;
               }                        
            }
         }
      }
      return null;
   }

   
   public void registerResources(ResourceRegistry resourceRegistry) {
      this.resourceRegistry = resourceRegistry;  
   }

   public void setDeviceContext(DeviceContext deviceContext) {
      this.deviceContext = (GlobusDeviceContext)deviceContext;
   }

   public void setRequest(ResourceQuery resourceQuery) {
      this.resourceQuery = resourceQuery;
   }
}
