package martin.app.bagoftask.ns;

import java.io.IOException;

import org.apache.log4j.Logger;

public abstract class NSRoundsProcessor {
   
   private static final String NS_PROCESS_ROUNDS_PATH;
   
   private static final Logger logger = Logger.getLogger(NSRoundsProcessor.class);
   
   static {
      NS_PROCESS_ROUNDS_PATH = "NSFiles/process_rounds.sh";
   }
   
   public static void processRounds( NSScriptProperties NSProperties ) {
      logger.info( "*********STARTING PROCESS ROUNDS*********" );      
      String[] arguments = NSScriptSender.getArguments( NSProperties );
      
      for( int i = 0; i < arguments.length; i++ ) {
         Runtime runTime = Runtime.getRuntime();
         try {
            runTime.exec( NS_PROCESS_ROUNDS_PATH + " " + arguments[i] );
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }   
}
