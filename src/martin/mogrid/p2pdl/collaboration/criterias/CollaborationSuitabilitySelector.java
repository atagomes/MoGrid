
package martin.mogrid.p2pdl.collaboration.criterias;

import java.util.Arrays;

import martin.mogrid.common.context.MonitoredContext;
import martin.mogrid.p2pdl.api.CollaboratorReply;
import martin.mogrid.p2pdl.api.CollaboratorReplyList;

import org.apache.log4j.Logger;

/**
 * @author Luciana Lima & Bruno Bastos
 *
 * Created on 12/04/2006
 * 
 * Ordena uma lista de respostas de colaboradores compararando o contexto dos diferentes 
 * dispositivos (colaboradores)
 */


public class CollaborationSuitabilitySelector {

   //Manutencao do arquivo de log do servico
   private static final Logger logger = Logger.getLogger(CollaborationSuitabilitySelector.class);  
   
   private float maxConnectivity = 0;    // Wireless link connectivity level in dBm   
   private float maxEnergy       = 0;    // Energy available in ...
   private float maxCpu          = 0;    // CPU available in KHz
   private float maxMemory       = 0;    // Memory available in Kb
   
   private CollaboratorReplyList replies = null;
   
   
   public CollaborationSuitabilitySelector(CollaboratorReplyList replies) {
      this.replies = replies;
      defineMaxContextValues();
   }
   
   
   /**
    * Sorts CollaboratorReplyList into ascending "resource" order.
    * Index "0" points to more resourcefull collaborator.  
    **/
   public CollaboratorReplyList sortRepliesByContext() {
      if ( replies == null ) { return null; }
            
      CollaboratorReply[] cRep    = replies.getAll();     
      float[]             indexes = new float[cRep.length];
      
      for ( int i=0; i<cRep.length; i++ ) {
         //logger.debug("*** "+cRep[i].toString());
         MonitoredContext monitoredCtxt = cRep[i].getCollaboratorContext();
         indexes[i]    = 0;
         float current = 0;
         int   p       = 0;  
         //TODO verificar monitoredCtxt...      
         if ( monitoredCtxt.isConnectivityMonitored() ) {
            current += calcConnectivityPercent(monitoredCtxt.getConnectivityValue()) * monitoredCtxt.getConnectivityWeight();
            p += monitoredCtxt.getConnectivityWeight(); 
         }
         if ( monitoredCtxt.isCpuMonitored() ) {
            current += calcCpuPercent(monitoredCtxt.getCpuValue()) * monitoredCtxt.getCpuWeight();
            p += monitoredCtxt.getCpuWeight(); 
         }
         if ( monitoredCtxt.isEnergyMonitored() ) {
            current += calcEnergyPercent(monitoredCtxt.getEnergyValue()) * monitoredCtxt.getEnergyWeight();
            p += monitoredCtxt.getEnergyWeight(); 
         }
         if ( monitoredCtxt.isMemoryMonitored() ) { 
            current += calcMemoryPercent(monitoredCtxt.getMemoryValue()) * monitoredCtxt.getMemoryWeight();
            p += monitoredCtxt.getMemoryWeight(); 
         } 
         //TODO
         if ( p > 0 ) {
            indexes[i] = current / p;
         }
      }
      
      float[] sortedIndexes = indexes;
      Arrays.sort(sortedIndexes);
      
      CollaboratorReply[] sortedCRep = new CollaboratorReply[cRep.length]; 
      for ( int i=0; i<sortedIndexes.length; i++ ) {         
         for ( int j=i; j<indexes.length; j++ ) {
            if ( sortedIndexes[i] == indexes[j] ) {
               sortedCRep[i] = cRep[j];
               indexes[j]    = -1;
               break;
            }
         }
      }

      CollaboratorReplyList sortedReplies = replies;
      sortedReplies.addAll(sortedCRep);
      
      return sortedReplies;      
   }
   
   
   /** 
    * Compare all context information (connectivity, cpu, energy, and memory) at 
    * CollaboratorReplyList and determine the bigger value for each resource.
    **/
   private void defineMaxContextValues() {
      if ( replies == null ) { return; }
      
      CollaboratorReply[] cRep = replies.getAll();

      for ( int i=0; i<cRep.length; i++ ) {
         MonitoredContext monitoredCtxt = cRep[i].getCollaboratorContext();
         
         if ( monitoredCtxt.isConnectivityMonitored() ) {
            maxConnectivity = Math.max(maxConnectivity, monitoredCtxt.getConnectivityValue()); 
         }
         if ( monitoredCtxt.isCpuMonitored() ) {
            maxCpu = Math.max(maxCpu, monitoredCtxt.getCpuValue());
         }
         if ( monitoredCtxt.isEnergyMonitored() ) {
            maxEnergy = Math.max(maxEnergy, monitoredCtxt.getEnergyValue());
         }
         if ( monitoredCtxt.isMemoryMonitored() ) { 
            maxMemory = Math.max(maxMemory, monitoredCtxt.getMemoryValue());
         }    
      }
   }
   
   /** 
    * Calculate the percent value .
    **/
   private float calcPercent(float absolute, float x) {
      return (100 * x) / absolute;      
   }

   /** 
    * Calculate the percent value for connectivity.
    **/
   private float calcConnectivityPercent(float x) {
      return calcPercent(maxConnectivity, x);      
   }

   /** 
    * Calculate the percent value for energy.
    **/
   private float calcEnergyPercent(float x) {
      return calcPercent(maxEnergy, x);      
   }

   /** 
    * Calculate the percent value for cpu.
    **/
   private float calcCpuPercent(float x) {
      return calcPercent(maxCpu, x);      
   }

   /** 
    * Calculate the percent value for memory.
    **/
   private float calcMemoryPercent(float x) {
      return calcPercent(maxMemory, x);      
   }
   
}
