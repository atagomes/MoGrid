
package martin.mogrid.service.monitor;

/**
 * @author luciana
 *
 * Created on 26/06/2005
 */
public class ContextParserException extends Exception {

   /** Comment for <code>serialVersionUID</code> */
   private static final long serialVersionUID = -2915498395161523624L;

   /**
   * Constructs a ContextParserException with the specified detail message
   * @param message message that detail the exception
   */
   public ContextParserException(String message) {
      super(message);
   }
   
   /**
    * Constructs a ContextParserException with the specified cause and a 
    * detail message of (cause==null ? null : cause.toString()) (which 
    * typically contains the class and detail message of cause).
    * @param throwable objeto throwable aninhado
    */
   public ContextParserException(Throwable throwable) {
      super(throwable);
   }
   
   /**
    * Constructs a ContextParserException with the specified 
    * detail message and cause
    * @param message message that detail the exception
    * @param cause the cause of exception
    */
   public ContextParserException(String msg, Throwable cause) {
      super(msg, cause);
   }
}
