package martin.mogrid.p2pdl.collaboration.scheduler.globus;

import java.util.Enumeration;
import java.util.Timer;
import java.util.Vector;

public class ProxyCollaborationReplyScheduled {
   
   private Vector proxyReply = null;
   private int    numReplies  = 0;
   
   public ProxyCollaborationReplyScheduled(int numMaxReplies) {
      proxyReply = new Vector();
      numReplies = numMaxReplies;
   }

   public void incNumRepliesListened() {
      // numReplies is set to MAX_NUM_REPLIES waited then 'increment' number of replies listened 
      // corresponds to decrease the numReplies (MAX:MAX_NUM_REPLIES -> MIN:0)
      numReplies--;
   }

   //First 'increment' numReplies
   public boolean discardReplyScheduled() {
      return ( numReplies >= 0 ? false : true );
   }
   
   
   public void addElement(Timer replyScheduled) {
      proxyReply.add(replyScheduled);
   }
   
   public Timer getElementAt(int position) {
      return (Timer)proxyReply.elementAt(position);
   }

   public boolean removeElement(Timer request) {
      return proxyReply.remove(request);
   }
   
   public Timer[] elements() {
      Timer[] requests = new Timer[size()];
      int i = 0;
      for ( Enumeration element=proxyReply.elements(); element.hasMoreElements(); i++ ) {
         requests[i] = (Timer)element.nextElement();
      }
      return requests;
   }
   
   public int size() {
      return proxyReply.size();  
   }

   public boolean isEmpty() {
      return proxyReply.isEmpty();  
   }
   
}
