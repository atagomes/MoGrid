/*
 * @file MoGridLog4j.java
 * (C) COPYRIGHT 2005-2006, MARTIN\LNCC & PUC-Rio.
 * @author Luciana Lima.
 * @date August 01, 2006
 */

package martin.mogrid.common.logging;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.helpers.LogLog;


public class MoGridLog4j {

   private final  String MOGRID_LOGS          = System.getProperty("mogrid.home") + File.separator + "logs";
   private final  String DEFAULT_DATE_PATTERN = "'.'yyyy-MM-dd"; 
   private        Logger mogridLogger         = null;
      
   
   public MoGridLog4j(String className) {
      mogridLogger = Logger.getLogger( className );
   }
   
   public MoGridLog4j() {
      StackTraceElement callerTrace = new Throwable().getStackTrace()[1];
      String callerClassName = callerTrace.getClassName();
      mogridLogger = Logger.getLogger( callerClassName );
   }
   
   
   //all < TRACE < DEBUG < INFO < WARN < ERROR < FATAL < off   
   public void trace(String msg) {
      mogridLogger.trace(msg);
   }
   public void trace(String msg, Throwable t) {
      mogridLogger.trace(msg, t);
   }   

   public void debug(String msg) {
      mogridLogger.debug(msg);
   }
   public void debug(String msg, Throwable t) {
      mogridLogger.debug(msg, t);
   }

   public void info(String msg) {
      mogridLogger.info(msg);
   }
   public void info(String msg, Throwable t) {
      mogridLogger.info(msg, t);
   }

   public void warn(String msg) {
      mogridLogger.warn(msg);
   }
   public void warn(String msg, Throwable t) {
      mogridLogger.warn(msg, t);
   }

   public void error(String msg) {
      mogridLogger.error(msg);
   }
   public void error(String msg, Throwable t) {
      mogridLogger.error(msg, t);
   }

   public void fatal(String msg) {
      mogridLogger.fatal(msg);
   }
   public void fatal(String msg, Throwable t) {
      mogridLogger.fatal(msg, t);
   }
   
   

   public boolean isTraceEnabled() {
      return mogridLogger.isTraceEnabled();
   }
   
   public boolean isDebugEnabled() {
      return mogridLogger.isDebugEnabled();
   }

   public boolean isInfoEnabled() {
      return mogridLogger.isInfoEnabled();
   }

   public boolean isWarnEnabled() {
      return mogridLogger.isEnabledFor(Level.WARN);
   }

   public boolean isErrorEnabled() {
      return mogridLogger.isEnabledFor(Level.ERROR);
   }

   public boolean isFatalEnabled() {
      return mogridLogger.isEnabledFor(Level.FATAL);
   }   
     
   
   public void addDailyRollingFileAppender(String newDatePattern, String filename) throws IOException {
      if ( filename==null || filename.trim().equals("") ) { 
         throw new IOException();
      }
      
      Layout layout      = new PatternLayout("[%5p] (%d{DATE}) %c - %m%n");
      String filePath    = "";
      if (filename!=null) filePath = MOGRID_LOGS + File.separator + filename;
      else filePath = MOGRID_LOGS;
      
      if (filePath.indexOf("null")!=-1) {
          String toReplace = "null";
          if ("\\".equals(File.separator)) {
              toReplace += "\\";
          }
          toReplace += File.separator;
          filePath = filePath.replaceFirst(toReplace, "");         
      }
      
      String datePattern = DEFAULT_DATE_PATTERN;
      
      try {
         new SimpleDateFormat(newDatePattern);
         datePattern = newDatePattern;            
      } catch ( NullPointerException ex) {  
         LogLog.warn("Invalid Pattern Format, using default: " + ex.getMessage());
      } catch ( IllegalArgumentException ex ) {   
         LogLog.warn("Invalid Pattern Format, using default: " + ex.getMessage());
      }
      
      DailyRollingFileAppender appender = new DailyRollingFileAppender(layout, filePath, datePattern);
      appender.setAppend(true);     
      mogridLogger.addAppender(appender);     
   }

   public void addDailyRollingFileAppender(String filename) throws IOException {
      addDailyRollingFileAppender(DEFAULT_DATE_PATTERN, filename);
   }

}
