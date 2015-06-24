package martin.app.bagoftask.ns;

import java.io.File;
import java.util.Vector;

import martin.mogrid.entity.dispatcher.globus.ProxyCollaboratorDispatcherFactory;
import martin.mogrid.p2pdl.api.MogridApplicationFacade;
import martin.mogrid.p2pdl.api.RequestIdentifier;
import martin.mogrid.tl.asl.simulation.NSAdaptationSublayer;

public class NSMaster implements MogridApplicationFacade {
 
   private static final File RUN_ROUNDS_SCRIPT;
   private static final File NS_WORKER;
   
   //private NSScriptProperties[] nsProperties;
   private NSScriptProperties   nsScriptProperties; 
   private NSAdaptationSublayer gridJobDiscovery;
   private int                  numOfJobs;
   
   static {
      RUN_ROUNDS_SCRIPT = new File ( "NSFiles/run_rounds.sh" );
      NS_WORKER         = new File( "NSFiles/worker.jar" );
   }
   
   public NSMaster(NSScriptProperties nsScriptProperties) {      
      numOfJobs = NSScriptSender.getNumOfJobs( nsScriptProperties );
      this.nsScriptProperties = nsScriptProperties;
      //nsProperties = new NSScriptProperties[ numOfJobs ];
      
      //initNSProperties( nsProperties );
      
      gridJobDiscovery = new NSAdaptationSublayer(this);
      gridJobDiscovery.registerTaskDispatcherFactory(new ProxyCollaboratorDispatcherFactory());
      gridJobDiscovery.setCollaborationLevel(1);
      gridJobDiscovery.setTransferDelay(10);
   }
   
   /*private void initNSProperties( NSScriptProperties[] nsProperties ) {
      for( int i = 0; i < numOfJobs; i++ ) {
         nsProperties[i] = NSScriptProperties.generateScriptSingleRoundProperties( nsScriptProperties, ( i + 1 ) );
      }
   }*/
   
   private Vector getFiles( ) {
      //File[] files = new File[2];
      Vector vec = new Vector();      
      for( int i = 0; i < numOfJobs; i ++ ) {
         vec.addElement( new File[] { nsScriptProperties.getSimScriptPath(), RUN_ROUNDS_SCRIPT, NS_WORKER } );
      }
      //files[0] =  nsProperties[0].getSimScriptPath();
      //files[1] = RUN_ROUNDS_SCRIPT;
      //vec.addElement ( files );
      return vec;
   }
   
   /*private String[] getArgs() {
      String[] args = new String[numOfJobs];
      for( int i = 0; i < numOfJobs; i ++ ) {
         args[i] = nsScriptProperties.getArguments();
      }
      return args;
   }*/
   
   public void runRounds() {
      //String query = "ns";
      //String[] nsArgs = new String[1];
      //nsArgs[0] = nsProperties[0].getArguments();
      new NSResultListener( numOfJobs, nsScriptProperties ).start();
      gridJobDiscovery.submitJobRequest(new String[] { "ns", "java" }, numOfJobs, getFiles() , RUN_ROUNDS_SCRIPT.getName(), NSScriptSender.getArguments( nsScriptProperties ));
      
   }

   public void handleMogridResource(RequestIdentifier reqID, Object resource) {
      
   }

}
