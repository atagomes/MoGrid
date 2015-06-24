
package martin.mogrid.p2pdl.collaboration.criterias;

/**
 * @author luciana
 *
 * Created on 22/06/2005
 */
public class AdmissionControllerException extends Exception {

   /** Comment for <code>serialVersionUID</code> */
   private static final long serialVersionUID = -5349471457041344000L;

   /**
   * Constructs a exception with the specified detail message
   * @param message message that detail the exception
   */
   public AdmissionControllerException(String message) {
      super(message);
   }
   
   /**
    * Constructs a exception with the specified cause and a 
    * detail message of (cause==null ? null : cause.toString()) (which 
    * typically contains the class and detail message of cause).
    * @param throwable objeto throwable aninhado
    */
   public AdmissionControllerException(Throwable throwable) {
      super(throwable);
   }
   
   /**
    * Constructs a exception with the specified detail message and cause
    * @param message message that detail the exception
    * @param cause the cause of exception
    */
   public AdmissionControllerException(String msg, Throwable cause) {
      super(msg, cause);
   }

}
