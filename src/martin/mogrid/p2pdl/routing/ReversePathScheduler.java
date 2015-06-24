/*
 * Created on 15/09/2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package martin.mogrid.p2pdl.routing;

import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import martin.mogrid.common.util.SystemUtil;
import martin.mogrid.p2pdl.api.RequestIdentifier;

import org.apache.log4j.Logger;

public class ReversePathScheduler {
   
   // Manutencao do arquivo de log para debug
   private static final Logger logger = Logger.getLogger(ReversePathScheduler.class);
   
   //private static final int DEFAULT_S = 100;  // in milliseconds
   private static final int DEFAULT_S = 2;
    
   private ReversePathTable reversePathTable = null;
   private Hashtable        timers           = null;
      
   
   public ReversePathScheduler() {
      reversePathTable = new ReversePathTable() ;
      timers           = new Hashtable();
   }
 
   public void addEntry(RequestIdentifier key, ReversePath value, int currentHopCount, long maxReplyDelay, float transferDelay) {   
      //(1) Add reverse path (value) to request identifier (key) in the table
      reversePathTable.put(key, value);
      
      Timer entryTimer = new Timer();
      //The TTL set in Timer Task need to be a little bigger then the real MAX_REPLY_DELAY
      //=> For this purpose is used the CURRENT_HOP_COUNT to increase the TTL, the increase value is in range [0,1]
      //-> CURRENT_HOP_COUNT (> 0): incValue = 1/CURRENT_HOP_COUNT
      //-> TTL = MAX_REPLY_DELAY/lambda  (lambda >=1 -> it allows tunning newTTL with regard to the transfer delay of P2PDP messages)
      //=> newTTL = TTL + TTL*incValue = TTL*(1 + incValue)
      //=> Examples:
      //-> If CURRENT_HOP_COUNT is 1 we have: TTL*(1 + 1)    = 2    TTL 
      //-> If CURRENT_HOP_COUNT is 2 we have: TTL*(1 + 0.50) = 1.50 TTL
      //-> If CURRENT_HOP_COUNT is 3 we have: TTL*(1 + 0.33) = 1.33 TTL
      //transferDelay = ( transferDelay < 1 ) ? DEFAULT_S : transferDelay; 
      //float incTTL = 0f;
      //if ( currentHopCount > 0 ) { incTTL = 1f/currentHopCount; } 
      //long cleanUpTimer = Math.round((maxReplyDelay/transferDelay) * (1 + incTTL));
      //cleanUpTimer = SystemUtil.convertSecondsToMilliseconds(cleanUpTimer) ;
      //logger.info("*** cleanUP = ("+maxReplyDelay+"/"+transferDelay+") * (1 + "+incTTL+") = "+ cleanUpTimer+" in milliseconds");
      
      //SbV algorithm (Supression by Vicinity): tauMax = 2*Dmax - 2*hopCount*transferDelay
      //transferDelay = ( transferDelay < 1 ) ? DEFAULT_S : transferDelay; 
      //float tauMax = 2 * SystemUtil.convertSecondsToMilliseconds(maxReplyDelay) - ( 2 * currentHopCount * transferDelay );      
      float tauMax = 2 * SystemUtil.convertSecondsToMilliseconds(maxReplyDelay) - ( 2 * currentHopCount * 100 );
      //Evita delay negativo
      if ( tauMax < SystemUtil.convertSecondsToMilliseconds(maxReplyDelay) )
         tauMax = SystemUtil.convertSecondsToMilliseconds(maxReplyDelay);
      long cleanUpTimer = SystemUtil.convertFloatToLong(tauMax);
      
      //(2) Set a new timer task for remove the added entry in the timer specified (entryTTL)      
      entryTimer.schedule(new RemoveEntry(key), cleanUpTimer); //milliseconds
      timers.put(key, entryTimer);
      
      logger.debug("(+) Reverse Path entry to IReq [" + key + " -> " + value.getReturnPath() + "] scheduled to be removed in " + cleanUpTimer + " milliseconds");         
   }
   
   public boolean containsEntry(RequestIdentifier key) {
      return reversePathTable.containsKey(key);
   }

   public ReversePath getEntry(RequestIdentifier key) {
      return reversePathTable.get(key);
   }
   
   public int size() {
      return reversePathTable.size();
   }
   
   class RemoveEntry extends TimerTask {
      private RequestIdentifier entryKey;
      
      public RemoveEntry(RequestIdentifier entryKey) {
         this.entryKey = entryKey;
      }
      
      public void run() {
         //(1) Remove the entry of Reverse Path Table
         ReversePath entry = reversePathTable.remove(entryKey); 
         logger.debug("(-) Reverse Path entry to IReq [" + entryKey + " -> " + entry.getReturnPath() + "] was removed"); 
         
         //(2) Terminate the timer thread created for the Task
         if ( timers.contains(entryKey) ) {
            Timer timerToRemove = (Timer)timers.remove(entryKey);
            if ( timerToRemove != null ) { 
               timerToRemove.cancel(); 
               timerToRemove = null;
            }
         }
      }
   }

}
