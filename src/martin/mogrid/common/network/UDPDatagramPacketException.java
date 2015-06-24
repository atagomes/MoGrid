
package martin.mogrid.common.network;

/**
 * @author luciana
 *
 * Created on 12/08/2005
 */
public class UDPDatagramPacketException extends Exception {

   /**
    * Comment for <code>serialVersionUID</code>
    */
   private static final long serialVersionUID = -7038244228128418683L;

   /**
   * Constructs a exception with the specified detail message
   * @param message message that detail the exception
   */
   public UDPDatagramPacketException(String message) {
      super(message);
   }
   
   /**
    * Constructs a exception with the specified cause and a 
    * detail message of (cause==null ? null : cause.toString()) (which 
    * typically contains the class and detail message of cause).
    * @param throwable objeto throwable aninhado
    */
   public UDPDatagramPacketException(Throwable throwable) {
      super(throwable);
   }
   
   /**
    * Constructs a exception with the specified detail message and cause
    * @param message message that detail the exception
    * @param cause the cause of exception
    */
   public UDPDatagramPacketException(String msg, Throwable cause) {
      super(msg, cause);
   }
}
