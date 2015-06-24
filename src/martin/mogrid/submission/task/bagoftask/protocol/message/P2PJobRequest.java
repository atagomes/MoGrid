package martin.mogrid.submission.task.bagoftask.protocol.message;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import martin.mogrid.common.network.LocalHost;
import martin.mogrid.common.network.UDPDatagramPacket;
import martin.mogrid.common.network.UDPDatagramPacketException;
import martin.mogrid.p2pdl.api.RequestIdentifier;

public class P2PJobRequest extends P2PBoTMessage {

   /** Comment for <code>serialVersionUID</code> */
   private static final long serialVersionUID = -3351077729729139127L;

   private Hashtable execFiles     = null; //Key: filename, Value: file in byteArray
   private String    executable    = null;
   private String    execArguments = null;
   private String    collabIPAddr  = null;
   

   // CONSTRUTOR
   public P2PJobRequest(RequestIdentifier jobRequestID, String reqIPAddress,
                        String collabIPAddr, int job, File[] files, String executable,
                        String execArguments) 
   {
      super(jobRequestID, reqIPAddress, job);
      this.executable    = executable;
      this.execArguments = execArguments;
      this.collabIPAddr  = collabIPAddr;
      this.execFiles     = convertFilesToByteArray(files);
   }

   // Na verdade nao existe um proxy, nesse caso, o endereco do proxy eh o
   // proprio endereco do colaborador
   public P2PJobRequest(RequestIdentifier jobRequestID, int job, File[] files,
                        String executable, String execArguments)
   {
      this(jobRequestID, LocalHost.getLocalHostAddress(), 
           LocalHost.getLocalHostAddress(), job, files, executable, execArguments);
   }

   // Existe um proxy (grade fixa ou grade movel infra-estruturada) e o seu
   // endereco difere do endereco do colaborador
   public P2PJobRequest(RequestIdentifier jobRequestID, String collabIPAddr,
                        int job, File[] files, String executable, String execArguments)
   {
      this(jobRequestID, LocalHost.getLocalHostAddress(), collabIPAddr, job,
           files, executable, execArguments);
   }

   private Hashtable convertFilesToByteArray(File[] files) {
      Hashtable filesInBytes = new Hashtable();
      for ( int i=0; i < files.length; i++ ) {
         try {
            byte[] file = UDPDatagramPacket.convertFileToByteArray(files[i]);
            filesInBytes.put(files[i].getName(), file);
           
         } catch (UDPDatagramPacketException e) { 
         }
      }    
      return filesInBytes;
   }

   // LEITURA   
   public Vector getFiles() {
      Vector   files     = new Vector(execFiles.size());
      int i = 0;
      Enumeration key = execFiles.keys();
      while (key.hasMoreElements()) {
         files.add( i++, (byte[])execFiles.get(key.nextElement()) );
      }
      return files;
   }
   
   public String[] getFilesName() {
      String[] filesName = new String[execFiles.size()];
      int i = 0;
      Enumeration key = execFiles.keys();
      while (key.hasMoreElements()) {
         filesName[i++] = (String) key.nextElement();
      }
      return filesName;
   }
   
   public String getExecutable() {
      return executable;
   }

   public String getExecArguments() {
      return execArguments;
   }

   public String getCollaboratorIPAddr() {
      return collabIPAddr;
   }

   
   // IMPRESSAO
   public String toString() {
      String messageStr = "[P2PJobRequest]" + super.toString() + 
                          "\nExec Call: " + executable + execArguments;
      //Print executable files name list
      messageStr += "\nFiles: ";
      Enumeration key = execFiles.keys(); 
      while ( key.hasMoreElements() ) {
         messageStr += (String)key.nextElement() + " ";
      }        
      messageStr += "\nCollaborator IP Address: " + collabIPAddr;
      
      return messageStr;
   }
   
}
