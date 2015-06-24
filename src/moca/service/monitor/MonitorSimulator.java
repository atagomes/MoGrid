/*
 * Created on May 3, 2004
 * File: MonitorSimulator.java
 * Author: Fernando Ney da Costa Nascimento <fernando_ney@yahoo.com.br, ney@{natalnet.br, inf.puc-rio.br}>
 *                                            
 */
package moca.service.monitor;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author Fernando Ney da Costa Nascimento <ney@inf.puc-rio.br>
 */
public class MonitorSimulator {

   private static final Logger logger = Logger.getLogger(MonitorSimulator.class);
   private int scanInterval;
   private boolean repeating;
   private InetSocketAddress cisAddress;
   private List scanFiles;
   private List scanIntervals;
   private DatagramSocket socket;
   private DatagramPacket packet;

   
   public MonitorSimulator() throws SocketException {
      MonitorProperties props = new MonitorProperties();
      scanInterval = props.getScanInterval();
      repeating = props.getRepeating();
      cisAddress = new InetSocketAddress(props.getCisServerHost(), props.getCisMonitorPort());
      scanFiles = props.getScanFiles();
      scanIntervals = props.getScanIntervals();

      for (int i = 0; i < scanFiles.size();) {
         try {
            new BufferedReader(new FileReader((String) scanFiles.get(i)));
            i++;
         } catch (FileNotFoundException e) {
            logger.warn("File Not Found:" + scanFiles.get(i), e);
            scanFiles.remove(i);
            scanIntervals.remove(i);
         }
      }

      socket = new DatagramSocket(null);
      packet = new DatagramPacket(new byte[0], 0, 0, cisAddress);

      logger.info("Monitor simulator started:");
      logger.info("Cis Server:" + cisAddress);
      logger.info("Scan Files:");
      for (int i = 0; i < scanFiles.size(); i++) {
         logger.info("  " + scanFiles.get(i));
      }
   }

   public void run() throws IOException {
      String line;

      if (scanFiles.size() == 0) {
         return;
      }

      do {
         for (int i = 0; i < scanFiles.size(); i++) {
            logger.debug("Sending scans from file: " + scanFiles.get(i));
            BufferedReader source = new BufferedReader(new FileReader((String) scanFiles.get(i)));
            int number = ((Integer) scanIntervals.get(i)).intValue() / scanInterval;

            for (int j = 0; j < number; j++) {
               line = source.readLine();
               // Se chegou ao fim do arquivo
               if (line == null) {
                  source = new BufferedReader(new FileReader((String) scanFiles.get(i)));
                  line = source.readLine();
               }
               logger.debug(line);
               send(line);
               sleep(scanInterval);
            }
         }
      } while (repeating);
   }

   private void send(String line) throws IOException {
      packet.setData(convertObjectToByteArray(line));
      //packet.setData(line.getBytes());
      socket.send(packet);
   }

   private void sleep(int scanInterval) {
      try {
         Thread.sleep(scanInterval);
      } catch (InterruptedException e) {
         logger.error("", e);
      }
   }

   public static byte[] convertObjectToByteArray(Object object) {
      if (object == null)
         return null;

      ByteArrayOutputStream byteStream = new ByteArrayOutputStream(6 * 1024);
      ObjectOutputStream os = null;
      byte[] sendBuf = null;
      try {
         os = new ObjectOutputStream(new BufferedOutputStream(byteStream));
         // Flushes the stream. This will write any buffered output bytes and
         // flush
         // through to the underlying stream.
         os.flush();
         // and use writeObject method to put object inside it.
         os.writeObject(object);
         // write the object to the byte array.
         os.flush();
         // Obtain the byte array from serializable object.
         sendBuf = byteStream.toByteArray();
         os.close();

      } catch (IOException ioex) {
         return null;
      }

      return sendBuf;
   }

   public static void main(String args[]) throws IOException, FileNotFoundException {
      MonitorSimulator simulator = new MonitorSimulator();
      simulator.run();
   }
}