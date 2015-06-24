package martin.mogrid.submission.task.filesharing.protocol;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import martin.mogrid.common.network.FileCopy;
import martin.mogrid.common.resource.ResourceIdentifier;
import martin.mogrid.common.util.SystemUtil;
import martin.mogrid.p2pdl.api.CollaboratorReply;
import martin.mogrid.p2pdl.api.RequestIdentifier;
import martin.mogrid.submission.protocol.TaskSubmissionListener;
import martin.mogrid.submission.protocol.TaskSubmissionProtocol;
import martin.mogrid.submission.task.filesharing.protocol.message.P2PFileSharingMessage;
import martin.mogrid.submission.task.filesharing.protocol.message.P2PRequestFile;
import martin.mogrid.submission.task.filesharing.protocol.message.P2PRequestedFile;

import org.apache.log4j.Logger;


// Classe responsavel por ouvir o canal de compartilhamento
public class FileSharingProtocol extends TaskSubmissionProtocol { 
  
   //Manutencao do arquivo de log do servico
   private static final Logger logger = Logger.getLogger(FileSharingProtocol.class);    

   //Dados relativos do protocolo de compartilhamento de arquivos
   private Hashtable files = null;
      
   public FileSharingProtocol(TaskSubmissionListener listener, Hashtable files) {
      super("File Sharing Channel", listener);   
      this.files = files;
      
      start();
   }  
   
   
   public void run() {
      Thread                myThread = Thread.currentThread();
      P2PFileSharingMessage data     = null;
   
      while( isAlive(myThread) ) {
         SystemUtil.sleep(1000); //1 second  
         
         data = (P2PFileSharingMessage)receive();
         
         if ( data != null) {            
            if ( data instanceof P2PRequestFile ) {
               P2PRequestFile message = (P2PRequestFile) data;
               ResourceIdentifier request = message.getResourceIdentifier();
               //logger.debug("< Receive a P2PRequestFile "+request);
               //logger.debug("Contains requested file... "+request+": "+files.containsKey(request) );
               if ( files.containsKey(request) ) {
                  String filePath = (String) files.get(request);
                  File audioFile = new File(filePath);
                  //logger.debug("audioFile exists... "+audioFile+": "+audioFile.exists());
                  if ( audioFile.exists() ) {
                     RequestIdentifier reID = message.getRequestIdentifier();
                     P2PRequestedFile reqFile = new P2PRequestedFile(audioFile, reID);
                     send(reqFile, message.getRequesterIPAddress());                    
                  }
               }
        
            } else if ( data instanceof P2PRequestedFile ) {
               P2PRequestedFile requestedFile = (P2PRequestedFile) data;
               File remoteFile = requestedFile.getRequestedFile();
               
               if ( remoteFile!= null && remoteFile.exists() ) {
                  String filePath = System.getProperty("mogrid.home") + File.separator + "download" + File.separator + remoteFile.getName();
                  //logger.debug("** Remote File: "+remoteFile.getAbsolutePath());
                  //logger.debug("New Local File: "+filePath);
                  File localFile = new File(filePath);                  
   
                  try {
                     localFile = FileCopy.copy(remoteFile, localFile, true);
                     if ( localFile != null && localFile.exists() ) {
                        requestedFile.setRequetedFile(localFile);
                        deliveryTaskResolution(requestedFile);      
                     }  
                  } catch (IOException e) {
                     logger.error("It was not possible copy Remote File locally: " + e.getMessage());
                  }
               }
            }
         }
      }
   } 
   
   public void requestFile(CollaboratorReply cRep, RequestIdentifier reqID) {
      P2PRequestFile request = new P2PRequestFile(cRep.getCollaboratorResourceIdentifier(), reqID);
      String collabAddr = cRep.getCollaboratorIPAddr();
      send(request, collabAddr); 
   }
      
}
