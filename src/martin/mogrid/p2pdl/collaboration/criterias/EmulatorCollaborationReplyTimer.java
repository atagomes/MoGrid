package martin.mogrid.p2pdl.collaboration.criterias;

import martin.mogrid.common.context.MonitoredContext;



public class EmulatorCollaborationReplyTimer extends CollaborationReplyTimer {  //implements CollaboratorReplyTimerFunction {   

   private int n = 0;  //number of resources
   
   //devContext eh nulo, os valores relativos aos recursos da maquina sao obtidos atraves
   //das funcoes de emulacao 

   //Funcoes auxiliares
   private synchronized void setMonitoredContext() {
      //a: in % (0 < a <= 100)
      monitoredCtxt = new MonitoredContext();  //absolute resource value
      
      //TODO Como avaliar RSSI?
      if ( ctxtInfo.isConnectivityMonitored() ) {
         float a = emulatedConnectivityLevel();  //in % 
         float p = ctxtInfo.getConnectivityWeight(); 
         setAlfaANDWeight(a, p);
         float absFreeValue = emulatedConnectivityValue();
         monitoredCtxt.setConnectivityValue(absFreeValue, p);
         n++; 
      }
      if ( ctxtInfo.isCpuMonitored() ) {
         float a = emulatedCpuLevel();          //in %
         float p = ctxtInfo.getCpuWeight();     
         setAlfaANDWeight(a, p);
         float absFreeValue = emulatedCpuValue();    
         monitoredCtxt.setCpuValue(absFreeValue, p);
         n++;
      }
      if ( ctxtInfo.isEnergyMonitored() ) {
         float a = emulatedEnergyLevel();       //in %
         float p = ctxtInfo.getEnergyWeight();  
         setAlfaANDWeight(a, p);
         float absFreeValue = emulatedEnergyValue(); 
         monitoredCtxt.setEnergyValue(absFreeValue, p);
         n++;
      }
      if ( ctxtInfo.isMemoryMonitored() ) {
         float a  = emulatedMemoryLevel();       //in % 
         float p  = ctxtInfo.getMemoryWeight(); 
         setAlfaANDWeight(a, p);
         float absFreeValue = emulatedMemoryValue();
         monitoredCtxt.setMemoryValue(absFreeValue, p); 
         n++;
      }  
   }
   
   
   //Metodos que emulam a obtencao de valores das informacoes de CONTEXTO
   private float emulatedConnectivityLevel() {
      return 0;
   }

   private float emulatedConnectivityValue() {
      return 0;
   }
   
   private float emulatedCpuLevel() {
      return 0;
   }
   
   private float emulatedCpuValue() {
      return 0;
   }
   
   private float emulatedEnergyLevel() {
      return 0;
   }
   
   private float emulatedEnergyValue() {
      return 0;
   }
   
   private float emulatedMemoryLevel() {
      return 0;
   }
   
   private float emulatedMemoryValue() {
      return 0;
   }
   
}
