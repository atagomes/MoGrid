package martin.mogrid.p2pdl.routing.optimized;

import java.util.ArrayList;
import java.util.HashMap;

import martin.mogrid.p2pdl.api.RequestIdentifier;


public class ReversePathTable {
   
   //KEY:         RequestIdentifier (Request Universal Identifier)
   //VALUE:       ReversePathEntry  (next hop Address, max number of replies, and number of hops to initiator)   
   private HashMap reversePathTable; 

   //TODO Como controlar o tamanho da tabela de rotas?
   public ReversePathTable() {
      reversePathTable = new HashMap();
   }
      
   public boolean put(RequestIdentifier key, ArrayList value) {
      if ( key != null && value != null ) {
         reversePathTable.put(key, value);
         return true;
      }
      return false;
   }   

   //Como tratar diversas rotas para um mesmo caminho?
   // - Escolher a com o menor numero de saltos
   public ReversePath[] get(RequestIdentifier key) {
      ReversePath[] reqIDnextHops = null;
      synchronized (reversePathTable) {
         reqIDnextHops = (ReversePath[]) reversePathTable.get(key);
      }
      return reqIDnextHops;
   } 
   
   // TODO Como manter a tabela sempre atualizada removendo as rotas invalidas?
   public ReversePath[] remove(RequestIdentifier key) {
      ReversePath[] nextHop = null;
      if ( key != null ) {
         nextHop = (ReversePath[]) reversePathTable.remove(key);
      }
      return nextHop;
   }

   public boolean containsKey(RequestIdentifier key) {
      return reversePathTable.containsKey(key);
   }
   
   public RequestIdentifier[] keys() {
      RequestIdentifier[] tableKeys = null;
      synchronized (reversePathTable) {
         tableKeys = (RequestIdentifier[]) reversePathTable.keySet().toArray();
      }
      return tableKeys;
   } 
   
   public int size() {
      return reversePathTable.size();
   } 

   public boolean isEmpty() {
      return reversePathTable.isEmpty();
   } 
   
}
