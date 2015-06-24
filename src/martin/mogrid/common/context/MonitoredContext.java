
package martin.mogrid.common.context;

/**
 * @author   luciana
 */
public class MonitoredContext extends ContextInformation {
   
   /** Comment for <code>serialVersionUID</code> */
   private static final long serialVersionUID = -5139617954286459851L;
   
   //Resources available in absolute value
   private float connectivity = 0;  // Wireless link connectivity level (in dBm)  
   private float energy       = 0;  // Remaining Energy  (%?)        
   private float cpu          = 0;  // CPU available (in KHz) 
   private float memory       = 0;  // Memory available (in Kb)    
     
   //CONSTRUTORES   
   public MonitoredContext() {
      super();
   }
   
   public MonitoredContext(float connectivity, float energy, float cpu, float memory) {      
      setConnectivityValue(connectivity);    
      setEnergyValue(energy);               
      setCpuValue(cpu);
      setMemoryValue(memory); 
   }   

   public MonitoredContext(float connectivity, float energy, float cpu, float memory, float connectivityWeight, float energyWeight, float cpuWeight, float memoryWeight) {      
      setConnectivityValue(connectivity, connectivityWeight);    
      setEnergyValue(energy, energyWeight);               
      setCpuValue(cpu, cpuWeight);
      setMemoryValue(memory, memoryWeight); 
   }
   
   
   //LEITURA
   public float getConnectivityValue() {
      return connectivity;
   }
   
   public float getEnergyValue() {
      return energy;
   }
   
   public float getCpuValue() {
      return cpu;
   }
   
   public float getMemoryValue() {
      return memory;
   }  
       
   
   //ATRIBUICAO
   public void setConnectivityValue(float wlanConnectivityLevel, float connectivityWeight) {
      connectivity = wlanConnectivityLevel;
      setConnectivityWeight(connectivityWeight);
      
      if ( connectivity > 0 )
         setIsConnectivityMonitored(true);
      else
         setIsConnectivityMonitored(false);
   }
   
   public void setEnergyValue(float freeEnergy, float energyWeight) {
      energy = freeEnergy;
      setEnergyWeight(energyWeight);

      if ( energy > 0 )
         setIsEnergyMonitored(true);
      else
         setIsEnergyMonitored(false);
   }
   
   public void setCpuValue(float freeCpu, float cpuWeight) {
      cpu  = freeCpu;
      setCpuWeight(cpuWeight);

      if ( cpu > 0 )
         setIsCpuMonitored(true);
      else
         setIsCpuMonitored(false);
   }
   
   public void setMemoryValue(float freeMemory, float memoryWeight) {
      memory  = freeMemory;
      setMemoryWeight(memoryWeight);
      
      if ( memory > 0 )
         setIsMemoryMonitored(true);
      else
         setIsMemoryMonitored(false);
   }
   

   public void setConnectivityValue(float wlanConnectivityLevel) {
      setConnectivityValue(wlanConnectivityLevel, 1);
   }   

   public void setEnergyValue(float freeEnergy) {
      setEnergyValue(freeEnergy, 1);
   }

   public void setCpuValue(float freeCpu) {
      setCpuValue(freeCpu, 1);
   }

   public void setMemoryValue(float freeMemory) {
      setMemoryValue(freeMemory, 1);
   }
   
   
   //IMPRESSAO
   public String toString() {
      String contextStr = "\n[Monitored Context]";
      
      if ( isConnectivityMonitored() )
         contextStr += "\nConnectivity (%): " + connectivity;
      if ( isEnergyMonitored() )
         contextStr += "\nBattery (%): " + energy;
      if ( isCpuMonitored() )
         contextStr += "\nCPU (Kb): " + cpu;
      if ( isMemoryMonitored() )
         contextStr += "\nMemory (Kb): " + memory; 
         
      return contextStr;
   }

}
