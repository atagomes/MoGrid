/*
 * Created on Oct 5, 2004 by Antonio Carlos Theophilo Costa Junior
 *
 */
package moca.service.monitor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * @author luciana
 */
public class SendingThread extends Thread {

   private Logger logger = Logger.getLogger(this.getClass());

   private boolean sending;

   private Properties conf;

   private List scans;

   private DatagramSocket socket;

   private DatagramPacket packet;

   public SendingThread(Properties conf, List scans) {
      this.conf = conf;
      this.scans = scans;
   }

   public void run() {
      try {
         socket = new DatagramSocket(null);
         InetSocketAddress cisAddress = new InetSocketAddress(conf
               .getProperty(MonitorSimulatorGUI.CIS_IP_PROP), Integer
               .parseInt(conf.getProperty(MonitorSimulatorGUI.CIS_PORT_PROP)));
         packet = new DatagramPacket(new byte[0], 0, 0, cisAddress);
      } catch (SocketException se) {
         logger.error("Erro ao abrir socket.", se);
      }
      int current = scans.size() - 1;
      int size = scans.size();
      setSending(true);
      int sleepInterval = Integer.parseInt(conf
            .getProperty(MonitorSimulatorGUI.SCAN_INTERVAL_PROP));

      while (isSending()) {
         // logger.debug("enviando scan " + (current = ++current % size) + ":" +
         // scans.get(current));
         try {
            current = ++current % size;
            logger.debug("enviando dados: " + scans.get(current));
            send((String) scans.get(current));
            logger.debug("dados enviados.");

            logger.debug("thread indo dormir por " + sleepInterval
                  + " milisegundos.");
            sleep(sleepInterval);
            logger.debug("thread acordada.");
         } catch (InterruptedException ie) {
            logger.error(ie);
         } catch (IOException ioe) {
            logger.error("Erro ao enviar dados ao CIS.", ioe);
         }
      }
   }

   /**
    * @return Returns the sending.
    * @uml.property name="sending"
    */
   public boolean isSending() {
      return sending;
   }

   /**
    * @param sending
    *           The sending to set.
    * @uml.property name="sending"
    */
   public void setSending(boolean sending) {
      this.sending = sending;
   }

   private void send(String line) throws IOException {
      byte[] buffer = line.getBytes();
      packet.setData(buffer);
      socket.send(packet);
   }
}
