
package martin.mogrid.service.monitor;

/**
 * @author luciana
 *
 * Created on 26/06/2005
 */
public class MonitorPropertiesException extends Exception {

   /** Comment for <code>serialVersionUID</code> */
   private static final long serialVersionUID = 3674818739684337431L;

   /**
   * Constructs a exception with the specified detail message
   * @param message message that detail the exception
   */
   public MonitorPropertiesException(String message) {
      super(message);
   }
   
   /**
    * Constructs a exception with the specified cause and a 
    * detail message of (cause==null ? null : cause.toString()) (which 
    * typically contains the class and detail message of cause).
    * @param throwable objeto throwable aninhado
    */
   public MonitorPropertiesException(Throwable throwable) {
      super(throwable);
   }
   
   /**
    * Constructs a exception with the specified detail message and cause
    * @param message message that detail the exception
    * @param cause the cause of exception
    */
   public MonitorPropertiesException(String msg, Throwable cause) {
      super(msg, cause);
   }
}
