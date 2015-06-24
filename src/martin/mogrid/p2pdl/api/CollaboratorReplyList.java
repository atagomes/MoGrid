package martin.mogrid.p2pdl.api;

import java.util.Vector;

import martin.mogrid.common.network.Payload;
import martin.mogrid.common.util.SystemUtil;



public class CollaboratorReplyList implements Payload {
   
   /** Comment for <code>serialVersionUID</code> */
   private static final long serialVersionUID = -5273366917070867011L;
   
   private Vector replyList; 

   public CollaboratorReplyList() {
      replyList = new Vector();
   }
      
   public boolean addElement(CollaboratorReply value) {
      if ( value != null ) {
         replyList.addElement(value);
         return true;
      }
      return false;
   }
   
   public boolean addAll(CollaboratorReply[] value) {      
      if ( value != null && value.length > 0 ) {
         replyList.clear();
         for ( int i=0; i<value.length; i++ ) {
            replyList.addElement(value[i]);
         } 
         return true;
      }
      return false;
   }
   
   public CollaboratorReply get(int key) {
      if ( isEmpty() ) { return null; }
      
      CollaboratorReply reply = null;
      reply = (CollaboratorReply) replyList.get(key);
      
      return reply;
   }
   
   public CollaboratorReply[] getAll() {
      if ( isEmpty() ) { return null; }
      
      Object[] values = replyList.toArray();      
      int      size   = values.length;
      CollaboratorReply[] reply = new CollaboratorReply[size];
      for ( int i=0; i<size; i++ ) { 
         reply[i] = (CollaboratorReply)values[i];
      }
      
      return reply;
   }
   
   public CollaboratorReply remove(int key) {
      if ( isEmpty() ) { return null; }
      
      CollaboratorReply reply = null;
      reply = (CollaboratorReply) replyList.remove(key);
      
      return reply;
   }

   public CollaboratorReply[] removeAll() {
      if ( isEmpty() ) { return null; }
      
      CollaboratorReply[] reply = getAll();      
      replyList.clear(); 
      
      return reply;
   }
   
   public boolean containsKey(CollaboratorReply value) {
      return replyList.contains(value);
   }
   
   public boolean containsValue(String collabAddr) {
      if ( SystemUtil.strIsNotNull(collabAddr) ) {
         //Se jah armazenei uma resposta de um collab, não posso armazenar outra
         for ( int i=0; i<replyList.size(); i++ ) {
            CollaboratorReply repValue = (CollaboratorReply)replyList.get(i);
            if ( repValue.getCollaboratorIPAddr().equalsIgnoreCase(collabAddr) )
               return true;
         }
      }
      return false;
   }
   
   public int size() {
      return replyList.size();
   }  

   public boolean isEmpty() {
      return replyList.isEmpty();
   }

   public void clear() {
      replyList.clear();
   }
  
   
   public String toString() {
      String listStr = "\nCollaborator Reply List:";
      for ( int key=0; key<size(); key++ ) {
         CollaboratorReply cr = get(key);
         listStr += cr.toString();
      }
      return listStr;
   } 
}
