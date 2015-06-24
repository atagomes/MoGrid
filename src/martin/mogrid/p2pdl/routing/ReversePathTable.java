package martin.mogrid.p2pdl.routing;

import java.util.Enumeration;
import java.util.Hashtable;

import martin.mogrid.p2pdl.api.RequestIdentifier;


public class ReversePathTable {
   //KEY:         RequestIdentifier (Request Universal Identifier)
   //VALUE:       ReversePathEntry  (next hop IP Address and max number of replies)
   
   private Hashtable reversePathTable; 

   //TODO Como controlar o tamanho da tabela de rotas?
   public ReversePathTable() {
      reversePathTable = new Hashtable();
   }
      

   //TODO Como tratar diversas rotas para um mesmo caminho? 
   public boolean put(RequestIdentifier key, ReversePath value) {
      if ( key != null && value != null ) {
         reversePathTable.put(key, value);
         return true;
      }
      return false;
   }
   
   public ReversePath get(RequestIdentifier key) {
      ReversePath nextHop = null;
      
      if ( key != null ) 
         nextHop = (ReversePath) reversePathTable.get(key);
      
      return nextHop;
   }
   
   // TODO Como manter a tabela sempre atualizada removendo as rotas invalidas?
   public ReversePath remove(RequestIdentifier key) {
      ReversePath nextHop = null;
      if ( key != null )
         nextHop = (ReversePath) reversePathTable.remove(key);
      
      return nextHop;
   }

   public boolean containsKey(RequestIdentifier key) {
      return reversePathTable.containsKey(key);
   }

   public boolean containsValue(ReversePath value) {
      return reversePathTable.contains(value);
   }
   
   public Enumeration keys() {
      return reversePathTable.keys();
   } 

   public Enumeration elements() {
      return reversePathTable.elements();
   } 
   
 
   public int size() {
      return reversePathTable.size();
   }  
}
