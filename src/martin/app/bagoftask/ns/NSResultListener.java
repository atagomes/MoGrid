package martin.app.bagoftask.ns;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import martin.mogrid.common.network.UDPDatagramPacket;
import martin.mogrid.common.network.UDPDatagramPacketException;
import martin.mogrid.common.util.SystemUtil;

import org.apache.log4j.Logger;

public class NSResultListener extends Thread {

   private int                numOfJobs          = 0;
   private NSScriptProperties nsScriptProperties = null;
   private DatagramSocket     localSocket        = null;
   private int                fileReceivedCount  = 0;
   
   private static final String BASE_DIRECTORY;
   
   private static final Logger logger = Logger.getLogger(NSResultListener.class);
   
   static {
      BASE_DIRECTORY = "/home/bfbastos/workspace/MoGrid/NSFilesReceived";
   }
   
   public NSResultListener( int numOfJobs, NSScriptProperties nsScriptProperties ) {
      this.numOfJobs = numOfJobs;
      this.nsScriptProperties = nsScriptProperties;
      try {
         localSocket = new DatagramSocket(null);
      } catch (SocketException e) {
         logger.error(e);
      }
   } 
   
   private synchronized void receive() {
      
      DatagramPacket packet  = UDPDatagramPacket.createReceivePacket(); 
      Object         message = null;      
      
      try {
         if( !localSocket.isBound() )
            localSocket.bind(new InetSocketAddress(43100));
         localSocket.receive(packet);
         localSocket.setSoTimeout(12000); //Só começa a contar o timeoutdepois que receber o primeiro
         logger.info("*********File Received: " + fileReceivedCount++ );
         message = UDPDatagramPacket.convertByteArrayToObject(packet.getData());
            
      } catch (UDPDatagramPacketException e) {
         logger.error(e);
      } catch (IOException e) {
         logger.error(e);
         fileReceivedCount++;
         if( fileReceivedCount == numOfJobs )
            NSRoundsProcessor.processRounds( nsScriptProperties );
         return;
      } 
      NSFileReceiveProperties[] filesReceived = (NSFileReceiveProperties[]) message;
      for( int i = 0; i < filesReceived.length; i++ ) {
    	  try {
              UDPDatagramPacket.convertByteArrayToFile( filesReceived[i].getFile(), ( BASE_DIRECTORY + File.separator + filesReceived[i].getName() ).trim() );
           } catch (UDPDatagramPacketException e) {
              logger.error(e);
           }
      }
      if( fileReceivedCount == numOfJobs )
         NSRoundsProcessor.processRounds( nsScriptProperties );
   }
   
   public void run() {
      logger.info( "Starting NSResultListener thread: " + numOfJobs );
      int i = 0;
      while( i < numOfJobs ) {
    	 new Thread( new Runnable() {
    		 public void run() {
             SystemUtil.sleep(1000);  //Wait in milliseconds  
    			 receive();
    		 }
    	 }).start();
       i++;
    	 //Object obj = receive();
         //if( obj instanceof NSFinalizeMessage[] ) {
         //if( obj == null ) {
            //count++;
           // break;
         //} else {
           // vec.addElement( obj );
         //}
         //if( numOfJobs == count ) {
           // break;
        // }
      }
      
      /*logger.info( "Creating files received" );
      NSFileReceiveProperties[][] arrayFiles = new NSFileReceiveProperties[numOfJobs][];
      
      for( int i = 0; i < numOfJobs; i++ ) {
         arrayFiles[i] = (NSFileReceiveProperties[]) vec.elementAt(i);
      }
      for( int i = 0; i < numOfJobs; i++ ) {
         for( int j = 0; j < arrayFiles[i].length; j++ ) {
            try {
               //logger.info( "File received name: " + arrayFiles[i][j].getName() + "Length: " + arrayFiles[i].length );
               UDPDatagramPacket.convertByteArrayToFile( arrayFiles[i][j].getFile(), ( BASE_DIRECTORY + File.separator + arrayFiles[i][j].getName() ).trim() );
            } catch (UDPDatagramPacketException e) {
               e.printStackTrace();
            }
         }
      }      
   }
   
   public static void main(String[] args) {
      
   }*/
   }

}
