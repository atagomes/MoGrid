
package martin.mogrid.common.network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;


/**
 * @author luciana
 *
 * Created on 22/06/2005
 */
public class UDPDatagramPacket {
   
   //The absolute maximum datagram packet size is 65507, dictated by the maximum IP packet 
   //size of 65535 less 20 bytes for the IP header and 8 bytes for the UDP header.
   private static final byte[] UDP_BUFFER        = new byte[UDPDatagramSocket.IP_DATAGRAM_SIZE];    //limite do UDP: 64 Kbyte
   private static final int    UDP_BUFFER_LENGTH = UDP_BUFFER.length;  //limite do UDP: 64 Kbyte
     

   //When running MoGrid in Linux-like plataforms the UDP_BUFFER is filled with memory values,
   //then it is need to initialize UDP_BUFFER with blank spaces
   private static byte[] getUDPBuffer() {
      for ( int i=0; i<UDP_BUFFER_LENGTH; i++ ) {
         UDP_BUFFER[i] = ' ';
      }
      return UDP_BUFFER;       
   }
   
   public static DatagramPacket createReceivePacket() {
      return new DatagramPacket(getUDPBuffer(), UDP_BUFFER_LENGTH);       
   }

   public static DatagramPacket createSendPacket(InetAddress address, int port) {
      return new DatagramPacket(new byte[0], 0, 0, address, port);           
   }
   
   public static DatagramPacket createSendPacket(InetSocketAddress socketAddress) throws UDPDatagramPacketException {
      /*
       //Construtor nao suportado no perfil Personal Profile do J2ME CDC
       try {
         return new DatagramPacket(new byte[0], 0, 0, socketAddress);
         
      } catch (SocketException se) {
         throw new UDPDatagramPacketException(se);
      } */  
      return createSendPacket(socketAddress.getAddress(), socketAddress.getPort());
   }
   
   /**
    * Transform an array of bytes into Object.
    * 
    * @param bytesReceived
    * Array of bytes what you want to unserialize into a Object class
    * @return A object unserialized from a array of bytes.
    */
   public static Object convertByteArrayToObject(byte[] bytesArray)
      throws UDPDatagramPacketException
   {
      if( bytesArray == null ) { return null; }
      
      //Step 1. Create a ByteArrayInputStream object, called, for example, byteStream         
      ByteArrayInputStream byteStream     = new ByteArrayInputStream(bytesArray);
      ObjectInputStream    oInStream      = null;
      Object               objectReceived = null;
      
      try {
         BufferedInputStream bufferIn = new BufferedInputStream(byteStream);

         //Caso os dados transportados via DatagramPacket nao tenham sido convertidos para
         //um array de bytes via UDPDatagramPacket.convertObjectToByteArray() ocorre um
         //erro na criacao de um novo ObjectInputStream, nesse caso a camada superior
         //recebe como retorno uma flag que indica que o objeto nao pode ser deserializado
         //=> Por exemplo, no caso de uma String s, byte[] b = s.getBytes()
         try {
            //Step 2. Construct an ObjectInputStream, say oInStream, using byteStream.            
            oInStream = new ObjectInputStream(bufferIn);
         
         //new ObjectInputStream(InputStream in) throws:
         //   StreamCorruptedException - if the stream header is incorrect 
         //   IOException              - if an I/O error occurs while reading stream header 
         //   SecurityException        - if untrusted subclass illegally overrides security-sensitive methods 
         //   NullPointerException     - if in is null
         }  catch (StreamCorruptedException scex) {         
            //O objeto nao pode ser deserializado
            //O array de bytes eh convertido em uma String e encaminhado para o nivel superior
            //=> Por exemplo, no caso de uma String s, byte[] b = s.getBytes() (envio de dados)
            objectReceived = new String(bytesArray, 0, bytesArray.length); 
            return objectReceived; 
         }
         
         objectReceived = oInStream.readObject();
         oInStream.close();
         
      } catch (IOException ioex) {
         throw new UDPDatagramPacketException(ioex);
         
      } catch (ClassNotFoundException cnfex) {
         throw new UDPDatagramPacketException(cnfex);         
      } 

      // return the object what have been unserialized.
      return objectReceived;
   }
   
   /**
    * Convert an Object into an Array of Bytes
    * 
    * @param object  -- object to be converted in bytes
    * @return
    * @throws IOException
    */
   public static byte[] convertObjectToByteArray(Object object) 
      throws UDPDatagramPacketException 
   {
      //Step 1. Object need to be a serializable by implementing serializable interface.
      if( object == null ) { return null; }

      //Step 2. Create a ByteArrayOutputStream object, called, for example, byteStream  
      ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
      ObjectOutputStream    oOutStream = null;
      byte[]                sendBuf    = null;
      
      try {
         //Step 3. Construct an ObjectOutputStream, say oOutStream, using byteStream.
         BufferedOutputStream bufferOut = new BufferedOutputStream(byteStream);
         oOutStream = new ObjectOutputStream(bufferOut);
         //Flushes the stream. 
         //This will write any buffered output bytes and flush through to the underlying stream.
         oOutStream.flush();
         //Step 4. Write the object object to byteStream by calling the method writeObject() of oOutStream.
         oOutStream.writeObject(object);
         oOutStream.flush();
         //Step 5. Retrieve the byte array buffer from byteStream, using its method toByteArray()
         sendBuf = byteStream.toByteArray();
         oOutStream.close();
            
      } catch (IOException ioex) {
         throw new UDPDatagramPacketException(ioex);
      }

      return sendBuf;
   } 
   
   
   /**
    * Transform an array of bytes into File.
    * 
    * @param bytesReceived
    * Array of bytes what you want to unserialize into a Object class
    * @return A object unserialized from a array of bytes.
    */
   public static File convertByteArrayToFile(byte[] bytesArray, String fileName) 
      throws UDPDatagramPacketException 
   {
      if( bytesArray == null ) { return null; }
      
      int  len        = bytesArray.length;
      File sourceFile = new File(fileName); 
      try {
         FileOutputStream file = new FileOutputStream(sourceFile);
         // For efficiency wrap it in a BufferedOutputStream.         
         BufferedOutputStream output = new BufferedOutputStream(file); 
         output.flush();
         // Now write the entire byte array to the file.         
         output.write(bytesArray, 0, len); 
         output.flush();
         // Close the output stream to make sure that all of the bytes are written
         // to the file.         
         output.close();
         
      } catch (FileNotFoundException fnfex) {
         throw new UDPDatagramPacketException(fnfex);
           
      } catch (IOException ioex) {
         throw new UDPDatagramPacketException(ioex);   
      }
      
      return sourceFile;
   }   


   /**
    * Convert an Object into an Array of Bytes
    * 
    * @param object  -- object to be converted in bytes
    * @return
    * @throws IOException
    */
   public static byte[] convertFileToByteArray(File file) 
      throws UDPDatagramPacketException 
   {
      if( file == null ) { return null; }

      int             len     = (int)file.length();  
      byte[]          sendBuf = new byte[len];
      FileInputStream inFile  = null;
      try {
         inFile = new FileInputStream(file);         
         inFile.read(sendBuf, 0, len);  
         
      } catch (FileNotFoundException fnfex) {
         System.out.println("FileNotFoundException " + fnfex.getMessage());
         throw new UDPDatagramPacketException(fnfex);
            
      } catch (IOException ioex) {
         System.out.println("IOException " + ioex.getMessage());
         throw new UDPDatagramPacketException(ioex);
      }

      return sendBuf;
   }  
   
}
