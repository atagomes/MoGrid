/*
 * Created on 20/09/2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package moca.service.monitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * @author ney
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MonitorProperties extends Properties {

   /** Comment for <code>serialVersionUID</code> */
   private static final long serialVersionUID = 1L;

   private static final Logger logger = Logger.getLogger(MonitorProperties.class);

   private static final String DEFAULT_CIS_SERVER_HOST = "localhost";

   private static final int DEFAULT_CIS_MONITOR_PORT = 55010;

   private static final int DEFAULT_SCAN_INTERVAL = 1000;

   public MonitorProperties() {
      super();
      // Configura o log4j de acordo com os parametros do arquivo de
      // configuracao
      String log4jFile = System.getProperty("log4j.configuration");
      if (log4jFile != null) {
         PropertyConfigurator.configure(log4jFile);
      }

      try {
         String monPropsFile = System.getProperty("monitor.home")
               + File.separator + "conf" + File.separator
               + "MonitorSimulator.properties";
         load(new FileInputStream(monPropsFile));
      } catch (FileNotFoundException fnfe) {
         logger.error("Monitor properties file not found.", fnfe);
      } catch (IOException ioe) {
         logger.error("Error while acessing monitor properties file.", ioe);
      }
   }

   public String getCisServerHost() {
      String cisServerHost = getProperty("cis.server.host");
      cisServerHost = (cisServerHost != null) ? cisServerHost : DEFAULT_CIS_SERVER_HOST;
      return cisServerHost;
   }

   public int getCisMonitorPort() {
      String temp = getProperty("cis.monitor.port");
      int cisMonitorPort = (temp != null) ? Integer.parseInt(temp)
            : DEFAULT_CIS_MONITOR_PORT;
      return cisMonitorPort;
   }

   public int getScanInterval() {
      String temp = getProperty("monitor.scanInterval");
      int scanInterval = (temp != null) ? Integer.parseInt(temp)
            : DEFAULT_SCAN_INTERVAL;
      return scanInterval;
   }

   public boolean getRepeating() {
      String temp = getProperty("monitor.repeating");
      boolean repeating = (temp != null) ? new Boolean(temp).booleanValue()
            : false;
      return repeating;
   }

   public List getScanFiles() {
      int i = 0;
      List fileNames = new ArrayList();
      while (true) {
         i++;
         String fileName = getProperty("file" + i);
         if (fileName != null) {
            fileNames.add(fileName);
         } else {
            break;
         }
      }
      return fileNames;
   }

   public List getScanIntervals() {
      int i = 0;
      List scanIntervals = new ArrayList();
      while (true) {
         i++;
         String interval = getProperty("interval" + i);
         if (interval != null) {
            scanIntervals.add(new Integer(interval));
         } else {
            break;
         }
      }
      return scanIntervals;
   }
}
