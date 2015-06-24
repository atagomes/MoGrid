
package martin.mogrid.common.context;


/**
 * @author   luciana
 */
public class ContextInformation implements ContextInterface {   
     
   /** Comment for <code>serialVersionUID</code> */
   private static final long serialVersionUID = 1858600993403412965L;
   
   private boolean isConnectivityMonitored = false;  // If wireless link connectivity level is monitored   
   private boolean isEnergyMonitored       = false;  // If remaining Energy is monitored
   private boolean isCpuMonitored          = false;  // If CPU available (remaining) is monitored
   private boolean isMemoryMonitored       = false;  // If Memory available is monitored   

   private float  connectivityWeight       = 1;      // Wireless link connectivity level weight   
   private float  energyWeight             = 1;      // Remaining Energy weight
   private float  cpuWeight                = 1;      // CPU weight
   private float  memoryWeight             = 1;      // Memory weight
   
   //CONSTRUTORES
   public ContextInformation() { }
   
   public ContextInformation(boolean isConnectivityMonitored, boolean isEnergyMonitored, boolean isCpuMonitored, boolean isMemoryMonitored) {
      this.isConnectivityMonitored = isConnectivityMonitored;
      this.isEnergyMonitored       = isEnergyMonitored;
      this.isCpuMonitored          = isCpuMonitored;
      this.isMemoryMonitored       = isMemoryMonitored;
   }
   
   public ContextInformation(boolean isConnectivityMonitored, boolean isEnergyMonitored, boolean isCpuMonitored, boolean isMemoryMonitored, float connectivityWeight, float energyWeight, float cpuWeight, float memoryWeight) {
      this(isConnectivityMonitored, isEnergyMonitored, isCpuMonitored, isMemoryMonitored);
      
      this.connectivityWeight = connectivityWeight;     
      this.energyWeight       = energyWeight;      
      this.cpuWeight          = cpuWeight;      
      this.memoryWeight       = memoryWeight;
   }
      
   //LEITURA
   public boolean isConnectivityMonitored() {
      return isConnectivityMonitored;
   }
   
   public boolean isEnergyMonitored() {
      return isEnergyMonitored;
   }
   
   public boolean isCpuMonitored() {
      return isCpuMonitored;
   }
   
   public boolean isMemoryMonitored() {
      return isMemoryMonitored;
   }
   
   public float getConnectivityWeight() {
      return connectivityWeight;
   }
   
   public float getEnergyWeight() {
      return energyWeight;
   }
   
   public float getCpuWeight() {
      return cpuWeight;
   }
   
   public float getMemoryWeight() {
      return memoryWeight;
   }  
   
   
   //ATRIBUICAO
   public void setIsConnectivityMonitored(boolean isConnectivity) {
      isConnectivityMonitored = isConnectivity;
   }
   
   public void setIsEnergyMonitored(boolean isEnergy) {
      isEnergyMonitored = isEnergy;
   }
   
   public void setIsCpuMonitored(boolean isCpu) {
      isCpuMonitored = isCpu;
   }
   
   public void setIsMemoryMonitored(boolean isMemory) {
      isMemoryMonitored = isMemory;
   }   
      
   public void setConnectivityWeight(float weight) {
      connectivityWeight = weight;
   }
   
   public void setEnergyWeight(float weight) {
      energyWeight = weight;
   }

   public void setCpuWeight(float weight) {
      cpuWeight = weight;
   }
   
   public void setMemoryWeight(float weight) {
      memoryWeight = weight;
   }

   public void setConnectivity(boolean isConnectivity, float weight) {
      isConnectivityMonitored = isConnectivity;
      connectivityWeight      = weight;
   }
   
   public void setEnergy(boolean isEnergy, float weight) {
      isEnergyMonitored = isEnergy;
      energyWeight      = weight;
   }

   public void setCpu(boolean isCpu, float weight) {
      isCpuMonitored = isCpu;
      cpuWeight      = weight;
   }
   
   public void setMemory(boolean isMemory, float weight) {
      isMemoryMonitored = isMemory;
      memoryWeight      = weight;
   }
   
   
   //IMPRESSAO
   public String toString() {
      String contextStr = "\nContext Information:";
      
      if ( isConnectivityMonitored() )
         contextStr += "\n   Connectivity - weight: " + connectivityWeight;
      if ( isEnergyMonitored() )
         contextStr += "\n   Battery - weight: " + energyWeight;
      if ( isCpuMonitored() )
         contextStr += "\n   CPU - weight: " + cpuWeight;
      if ( isMemoryMonitored() )
         contextStr += "\n   Memory - weight: " + memoryWeight;
         
      return contextStr;
   }

}
