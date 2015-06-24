/**
 * @author lslima
 * 
 * Created on 14/03/2006
 */

package martin.mogrid.tl.asl;

import martin.mogrid.common.logging.MoGridLog4jConfigurator;
import martin.mogrid.entity.dispatcher.TaskDispatcherFactory;

public abstract class AdaptationSublayer {

   public abstract void registerTaskDispatcherFactory(TaskDispatcherFactory taskDispatcherFactory);
   
   public AdaptationSublayer() {
      MoGridLog4jConfigurator.configure();
   }

}
