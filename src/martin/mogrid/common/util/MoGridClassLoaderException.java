
package martin.mogrid.common.util;

/**
 * @author luciana
 *
 * Created on 22/06/2005
 */
public class MoGridClassLoaderException extends Exception {

   /** Comment for <code>serialVersionUID</code> */
   private static final long serialVersionUID = -6614851211791944713L;

   /**
   * Constructs a exception with the specified detail message
   * @param message message that detail the exception
   */
   public MoGridClassLoaderException(String message) {
      super(message);
   }
   
   /**
    * Constructs a exception with the specified cause and a 
    * detail message of (cause==null ? null : cause.toString()) (which 
    * typically contains the class and detail message of cause).
    * @param throwable objeto throwable aninhado
    */
   public MoGridClassLoaderException(Throwable throwable) {
      super(throwable);
   }
   
   /**
    * Constructs a exception with the specified detail message and cause
    * @param message message that detail the exception
    * @param cause the cause of exception
    */
   public MoGridClassLoaderException(String msg, Throwable cause) {
      super(msg, cause);
   }

}
