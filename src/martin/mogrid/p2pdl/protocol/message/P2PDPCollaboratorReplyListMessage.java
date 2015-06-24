
package martin.mogrid.p2pdl.protocol.message;

import martin.mogrid.p2pdl.api.CollaboratorReplyList;
import martin.mogrid.p2pdl.api.RequestIdentifier;

/**
 * @author lslima
 * 
 * Created on 13/07/2005
 */
public class P2PDPCollaboratorReplyListMessage extends P2PDPMessage {

   /** Comment for <code>serialVersionUID</code> */
   private static final long serialVersionUID = 638930048412661219L;
   
   private CollaboratorReplyList repList = null;
   
   public P2PDPCollaboratorReplyListMessage() {
      super(CP_MSG_CREPLIST);
   }
   
   public P2PDPCollaboratorReplyListMessage(RequestIdentifier requestIdentifier, CollaboratorReplyList repList) {
      super(CP_MSG_CREPLIST, requestIdentifier);
      this.repList = repList;
   }
   
   //LEITURA
   public CollaboratorReplyList getRepList() {
      return repList;
   }
   
   //ATRIBUICAO
   public void setRepList(CollaboratorReplyList repList) {
      this.repList = repList;
   }
   
   //IMPRIME
   public String toString() {
      String messageStr = super.toString() + repList.toString(); 
         
      return messageStr;
   }     
}
