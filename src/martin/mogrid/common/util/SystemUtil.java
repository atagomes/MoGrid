
package martin.mogrid.common.util;

import org.apache.log4j.Logger;

/**
 * @author luciana
 *
 * Created on 15/06/2005
 */
public class SystemUtil {

   //Manutencao do arquivo de log do servico
   private static final Logger logger = Logger.getLogger(SystemUtil.class); 
   
   private static final long SEC_MILLI = 1000L;
   
   public static boolean strIsNotNull(String str) {      
      if ( str != null && !str.trim().equals("") ) {
         return true;
      }
      return false;
   }
   
   public static boolean strIsNull(String str) {            
      return ( ! strIsNotNull(str) );
   }
   
   public static float convertSecondsToMilliseconds(float timeInSec) {
      float milliseconds = timeInSec * SEC_MILLI;      
      return milliseconds;
   }
   
   public static long convertSecondsToMilliseconds(long timeInSec) {
      long milliseconds = timeInSec * SEC_MILLI;      
      return milliseconds;
   }

   public static long convertFloatToLong(float timeInSec) {
      float milliseconds = Math.round(timeInSec); 
      long newLong = 0;
      try {
         String milliInFloat = new String(Float.toString(milliseconds));
         String[] floatPoint = milliInFloat.split("\\.");
         newLong = Long.valueOf(floatPoint[0]).longValue();
      } catch (NumberFormatException nfex) {
         logger.warn("Trying convert time (milliseconds) in float to long: "+nfex);
      }
      return newLong;
   }
   
   public static long convertMillisecondsToSeconds(long timeInMilli) {
      long seconds = timeInMilli / SEC_MILLI;      
      return seconds;
   }
   

   public static void sleep(double milliseconds) {
      try {         
         Thread.sleep( Math.round(milliseconds) ); 
      } catch (InterruptedException ie) { 
         //Ignore it
         //logger.warn("It occured some error sleeping Thread: " + ie.getMessage(), ie); 
      }
   }

   public static void sleep(double milliseconds, String warnMsg) {
      try {         
         Thread.sleep( Math.round(milliseconds) ); 
      } catch (InterruptedException ie) {
         logger.warn(warnMsg);
      }
   }
   
   public static void abnormalExit() {
      exit(1, 1000);
   }
   
   public static void normalExit() {
      exit(0, 1000);
   }
   
   private static void exit(int status, int time) {
      try {
         Thread.sleep(time);
      } catch (InterruptedException e) {
      }
      System.exit(status);
   }
   

   public static String getStackTraceFirstLine(Exception e) {
      String stackTrace = "["+e.getClass()+"]";
      StackTraceElement[] error = e.getStackTrace();
      if ( error.length > 0 ) { stackTrace += ": "+error[0].toString(); }
      return stackTrace;
   }
   

   public static String getStackTrace(Exception e) {
      String stackTrace = "["+e.getClass()+"]";
      StackTraceElement[] error = e.getStackTrace();
      if ( error.length > 0 ) { stackTrace += ": "; }
      for (int i=0; i< error.length; i++){
         stackTrace += error[i].toString();
      }
      return stackTrace;
   }
   
}
