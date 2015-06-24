/*
 * Created on 23/10/2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package martin.mogrid.submission.task.bagoftask.protocol;

import martin.mogrid.entity.dispatcher.Task;
import martin.mogrid.submission.protocol.TaskSubmissionListener;
import martin.mogrid.submission.protocol.TaskSubmissionProtocol;

abstract class BotProtocolExecutor extends TaskSubmissionProtocol {

   public BotProtocolExecutor(String title, TaskSubmissionListener listener) {
      super(title, listener);
   }

   abstract public Object executeTask(Task task);

}
