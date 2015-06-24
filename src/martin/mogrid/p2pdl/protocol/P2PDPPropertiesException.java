
package martin.mogrid.p2pdl.protocol;

/**
 * @author luciana
 *
 * Created on 26/06/2005
 */
public class P2PDPPropertiesException extends Exception {

   /** Comment for <code>serialVersionUID</code> */
   private static final long serialVersionUID = -6021305856830597658L;

   /**
   * Constructs a exception with the specified detail message
   * @param message message that detail the exception
   */
   public P2PDPPropertiesException(String message) {
      super(message);
   }
   
   /**
    * Constructs a exception with the specified cause and a 
    * detail message of (cause==null ? null : cause.toString()) (which 
    * typically contains the class and detail message of cause).
    * @param throwable objeto throwable aninhado
    */
   public P2PDPPropertiesException(Throwable throwable) {
      super(throwable);
   }
   
   /**
    * Constructs a exception with the specified detail message and cause
    * @param message message that detail the exception
    * @param cause the cause of exception
    */
   public P2PDPPropertiesException(String msg, Throwable cause) {
      super(msg, cause);
   }
}
