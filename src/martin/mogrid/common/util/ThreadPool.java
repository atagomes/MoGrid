package martin.mogrid.common.util;

import org.apache.log4j.Logger;

public class ThreadPool {
   
     private static final Logger logger = Logger.getLogger(ThreadPool.class);
   
     private ObjectFIFO         idleWorkers = null;
     private ThreadPoolWorker[] workerList  = null;

     public ThreadPool(int numberOfThreads) {
         // make sure that it's at least one
         numberOfThreads = Math.max(1, numberOfThreads);

         idleWorkers = new ObjectFIFO(numberOfThreads);
         workerList = new ThreadPoolWorker[numberOfThreads];

         for ( int i = 0; i < workerList.length; i++ ) {
             workerList[i] = new ThreadPoolWorker(idleWorkers);
         }
     }

     public void execute(Runnable target) throws InterruptedException {
         // block (forever) until a worker is available
         //logger.debug( "Active Threads: " + idleWorkers.getSize() );
         ThreadPoolWorker worker = (ThreadPoolWorker) idleWorkers.remove();
         worker.process(target);
     }

     public void stopRequestIdleWorkers() {
         try {
             Object[] idle = idleWorkers.removeAll();
             for ( int i = 0; i < idle.length; i++ ) {
                 ( (ThreadPoolWorker) idle[i] ).stopRequest();
             }
         } catch ( InterruptedException x ) {
             Thread.currentThread().interrupt(); // re-assert
         }
     }
 
     public void stopRequestAllWorkers() {
         // Stop the idle one's first
         // productive.
         stopRequestIdleWorkers();

         // give the idle workers a quick chance to die
         try { Thread.sleep(250); } catch ( InterruptedException x ) { }
  
         // Step through the list of ALL workers.
         for ( int i = 0; i < workerList.length; i++ ) {
             if ( workerList[i].isAlive() ) {
                 workerList[i].stopRequest();
             }
         }
     }
 }
