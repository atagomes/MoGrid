/*
 * Created on 20/10/2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package martin.mogrid.submission.task.bagoftask.protocol;

import martin.mogrid.entity.dispatcher.TaskDispatcher;
import martin.mogrid.entity.dispatcher.TaskDispatcherFactory;

public class BoTDispatcherFactory implements TaskDispatcherFactory {

   public TaskDispatcher createInstance() {
      return new BoTDispatcher();
   }

}
