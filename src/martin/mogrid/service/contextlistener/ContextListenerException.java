
package martin.mogrid.service.contextlistener;

/**
 * @author luciana
 *
 * Created on 22/06/2005
 */
public class ContextListenerException extends Exception {
   
   /** Comment for <code>serialVersionUID</code> */
   private static final long serialVersionUID = 7352461096467879031L;

   /**
   * Constructs a ContextListenerException with the specified detail message
   * @param message message that detail the exception
   */
   public ContextListenerException(String message) {
      super(message);
   }
   
   /**
    * Constructs a ContextListenerException with the specified cause and a 
    * detail message of (cause==null ? null : cause.toString()) (which 
    * typically contains the class and detail message of cause).
    * @param throwable objeto throwable aninhado
    */
   public ContextListenerException(Throwable throwable) {
      super(throwable);
   }
   
   /**
    * Constructs a ContextListenerException with the specified 
    * detail message and cause
    * @param message message that detail the exception
    * @param cause the cause of exception
    */
   public ContextListenerException(String msg, Throwable cause) {
      super(msg, cause);
   }

}
