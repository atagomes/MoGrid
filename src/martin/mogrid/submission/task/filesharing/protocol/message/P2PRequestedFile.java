package martin.mogrid.submission.task.filesharing.protocol.message;

import java.io.File;

import martin.mogrid.p2pdl.api.RequestIdentifier;

public class P2PRequestedFile extends P2PFileSharingMessage {
   
   /** Comment for <code>serialVersionUID</code> */
   private static final long serialVersionUID = 1L;
   
   private File reqFile;
   
   //CONSTRUTOR
   public P2PRequestedFile(File reqFile, RequestIdentifier reqID) {
      super(reqID);
      this.reqFile = reqFile;
   }   
   
   //LEITURA   
   public File getRequestedFile() {
      return reqFile;
   }
   
   //ATRIBUICAO
   public void setRequetedFile(File reqFile) {
      this.reqFile = reqFile;   
   }
   
   //IMPRESSAO
   public String toString() {
      String messageStr = "[P2PRequestedFile]" +
                          "\nRequested File: " + reqFile +
                          super.toString();                          
         
      return messageStr;
   } 

   
}
