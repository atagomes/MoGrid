
package martin.mogrid.p2pdl.protocol.message;

import martin.mogrid.common.network.Payload;
import martin.mogrid.p2pdl.api.RequestIdentifier;

/**
 * @author   luciana
 */
public interface P2PDPMessageInterface extends Payload {
   
   //Valores presentes na porcao de controle de uma mensagem do protocolo de coordenacao   
   //INITIATOR -> COORDINATOR -> COLLABORATORS
   public static final int CP_MSG_IREQ     = 0;  //iniciador aciona descoberta de recursos -> coordenador faz um broadcast/multicast da msg para os colaboradores
   //COLLABORATOR -> COORDINATOR
   public static final int CP_MSG_CREP     = 1;  //colaboradores disponiveis respondem
   //COORDINATOR -> INITIATOR
   public static final int CP_MSG_CREPLIST = 2;  //coordenador envia lista de servicos (recursos) disponiveis nos colaboradores 

  
   //Strings referentes as mensagens 
   public static final String CP_MSG_IREQ_STR     = "InitiatorRequest";    
   public static final String CP_MSG_CREP_STR     = "CollaboratorReply";   
   public static final String CP_MSG_CREPLIST_STR = "CollaboratorReplyList";   
   
   public static final String[] CP_MSG_STR = { 
      CP_MSG_IREQ_STR, CP_MSG_CREP_STR, CP_MSG_CREPLIST_STR
   };   

   public abstract int               getMessageType(); 
   public abstract String            getMessageTypeStr();   
   public abstract void              setMessageType(int msgType);
   public abstract RequestIdentifier getRequestIdentifier();
   public abstract String            getDeviceIPAddress();   
   public abstract void              setDeviceIPAddress(String devIPAddress);   
   public abstract String            getDeviceMACAddress();                         
   public abstract void              setDeviceMACAddress(String devMACAddress);
   
}
