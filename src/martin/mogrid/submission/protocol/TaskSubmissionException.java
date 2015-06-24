
package martin.mogrid.submission.protocol;



/**
 * @author luciana
 *
 * Created on 12/08/2005
 */
public class TaskSubmissionException extends Exception {

   /**
    * Comment for <code>serialVersionUID</code>
    */
   private static final long serialVersionUID = 1L;

   /**
   * Constructs a exception with the specified detail message
   * @param message message that detail the exception
   */
   public TaskSubmissionException(String message) {
      super(message);
   }
   
   /**
    * Constructs a exception with the specified cause and a 
    * detail message of (cause==null ? null : cause.toString()) (which 
    * typically contains the class and detail message of cause).
    * @param throwable objeto throwable aninhado
    */
   public TaskSubmissionException(Throwable throwable) {
      super(throwable);
   }
   
   /**
    * Constructs a exception with the specified detail message and cause
    * @param message message that detail the exception
    * @param cause the cause of exception
    */
   public TaskSubmissionException(String msg, Throwable cause) {
      super(msg, cause);
   }
}
