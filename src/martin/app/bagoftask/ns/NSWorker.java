package martin.app.bagoftask.ns;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class NSWorker {
   
   private static DatagramPacket createSendPacket(InetAddress address, int port) {
      return new DatagramPacket(new byte[0], 0, 0, address, port);           
   }
   
   private static byte[] convertObjectToByteArray(Object object) {
      
      if( object == null ) { return null; }
   
      ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
      ObjectOutputStream    oOutStream = null;
      byte[]                sendBuf    = null;
      
      try {
         BufferedOutputStream bufferOut = new BufferedOutputStream(byteStream);
         oOutStream = new ObjectOutputStream(bufferOut); 
         oOutStream.flush();
         oOutStream.writeObject(object);
         oOutStream.flush();
         sendBuf = byteStream.toByteArray();
         oOutStream.close();
            
      } catch (IOException ioex) {
         ioex.printStackTrace();
      }

      return sendBuf;
   } 
   
   private static byte[] convertFileToByteArray(File file) {
   
      if( file == null ) { return null; }

      int             len     = (int)file.length();  
      byte[]          sendBuf = new byte[len];
      FileInputStream inFile  = null;
      try {
         inFile = new FileInputStream(file);         
         inFile.read(sendBuf, 0, len);  
         
      } catch (FileNotFoundException fnfex) {
         System.out.println("FileNotFoundException " + fnfex.getMessage());
            
      } catch (IOException ioex) {
         System.out.println("IOException " + ioex.getMessage());
      }
   
      return sendBuf;
   }  
   
   public void send(NSFileReceiveProperties[] files, String host) {
      DatagramSocket senderSocket;
      DatagramPacket packet;                      
      try {
         senderSocket = new DatagramSocket(null);
         packet = createSendPacket( InetAddress.getByName(host), 43100 );
         packet.setData(convertObjectToByteArray(files));
         senderSocket.send( packet );
         System.out.println( "Packet Sent to: " + host );
      } catch( Exception e) {
         e.printStackTrace();
      }
   }
   
   public static void main(String[] args) {
    
      NSFileReceiveProperties[] nsReceived = new NSFileReceiveProperties[2];
         
      System.out.println("Files: " + args[0] + ", " + args[1] );
         
      byte[] file1 = convertFileToByteArray( new File( args[0] ) );
      byte[] file2 = convertFileToByteArray( new File( args[1] ) );
         
      nsReceived[0] = new NSFileReceiveProperties( new File( args[0] ).getName(), file1);
      nsReceived[1] = new NSFileReceiveProperties( new File ( args[1] ).getName(), file2);
         
      new NSWorker().send(nsReceived,"ukko");
   }

}
