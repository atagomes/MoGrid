package martin.mogrid.submission.task.filesharing.protocol.message;

import martin.mogrid.common.network.LocalHost;
import martin.mogrid.common.resource.ResourceIdentifier;
import martin.mogrid.p2pdl.api.RequestIdentifier;

public class P2PRequestFile extends P2PFileSharingMessage {

   /**
    * Comment for <code>serialVersionUID</code>
    */
   private static final long serialVersionUID = 1L;

   private String reqIPAddress;

   private ResourceIdentifier resID;

   //CONSTRUTOR
   public P2PRequestFile(ResourceIdentifier request, RequestIdentifier reqID) {
      super(reqID);
      this.reqIPAddress = LocalHost.getLocalHostAddress();
      this.resID = request;
   }

   //LEITURA   
   public String getRequesterIPAddress() {
      return reqIPAddress;
   }

   public ResourceIdentifier getResourceIdentifier() {
      return resID;
   }

   //ATRIBUICAO
   public void setRequeterIPAddress(String devIPAddress) {
      this.reqIPAddress = devIPAddress;
   }

   public void setResourceIdentifier(ResourceIdentifier request) {
      this.resID = request;
   }

   //IMPRESSAO
   public String toString() {
      String messageStr = "[P2PRequestFile]" + "\nIP Address: " + reqIPAddress
            + "\nRequest: " + resID + super.toString();

      return messageStr;
   }

}
