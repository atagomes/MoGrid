
package martin.mogrid.service.monitor;

import java.io.Serializable;
import java.sql.Timestamp;

import martin.mogrid.common.util.SystemUtil;

public abstract class DeviceContext implements Serializable {

   private Timestamp  timestamp;          // Device Context creation time   
   private String     deviceIPAddr;       // Device's Local IP address
   private String     deviceMacAddr;      // Device's MAC address
   private String     deviceNetMask;      // Device's Network Mask    
   private boolean    ipChanged;          // If the current Device's IP Address changed 
   private int        advtPeriodicity;    // Periodicity of sending in seconds
   
   //RESOURCES in %
   private float      connectivityP;      // Wireless link connectivity level in %   
   private float      energyP;            // Energy available in %
   private float      cpuP;               // CPU available in %
   private float      memoryP;            // Memory available in %   
   //RESOURCES in absolute value (free) 
   private float      connectivity;       // Wireless link connectivity level in dBm   
   private float      energy;             // Energy available in ...
   private float      cpu;                // CPU available in KHz
   private float      memory;             // Memory available in Kb


   public DeviceContext() {
      timestamp = new Timestamp(System.currentTimeMillis()); 
   }

   /** 
    * @return Device's Local IP address.
    */
   public String getKey() {
      return deviceIPAddr;
   }
   /** 
    * @return Device's Local IP address. 
    */
   public String getIPAddress() {
      return deviceIPAddr;
   }

   /** 
    * @return Device's MAC address. 
    */
   public String getMacAddress() {
      return deviceMacAddr;
   }

   /** 
    * @return Device's Network Mask. 
    */
   public String getNetworkMask() {
      return deviceNetMask;
   }

   //VALORES PERCENTUAIS
   /**
    * @return Wireless link connectivity in %.
    */
   public float getConnectivityLevel() {
      return connectivityP; 
   }
   
   /**
    * @return Energy available in %.
    */
   public float getEnergyLevel() {
      return energyP;
   }
   
   /**
    * @return CPU available in %.
    */
   public float getCpuLevel() {
      return cpuP;
   }
   
   /**
    * @return Memory available in %.
    */
   public float getMemoryLevel() {
      return memoryP; 
   }
         

   //VALORES ABSOLUTOS
   /**
    * @return Wireless link connectivity in %.
    */
   public float getConnectivityValue() {
      return connectivity; 
   }
   
   /**
    * @return Energy available in %.
    */
   public float getEnergyValue() {
      return energy;
   }
   
   /**
    * @return CPU available in %.
    */
   public float getCpuValue() {
      return cpu;
   }
   
   /**
    * @return Memory available in %.
    */
   public float getMemoryValue() {
      return memory; 
   }
         
   
   /**
    * @return   If the current Device's IP Address changed.
    */
   public boolean isIpChanged() {
      return ipChanged;
   }

   /**
    * @return   Periodicity of sending in seconds.
    */
   public int getAdvertisementPeriodicity() {
      return advtPeriodicity;
   }
   
   /** @return How old the information is in seconds. */
   public long getInformationAge() {
      long deltaT = System.currentTimeMillis() - getTimestamp().getTime();      
      return SystemUtil.convertMillisecondsToSeconds(deltaT);
   }

   /** @return When the information was received (milliseconds). */
   public Timestamp getTimestamp() {  
      return timestamp;
   }  

   // TESTE
   /**
    * It infers that a device is online if the time of its periodicity
    * of notification is greater than its last notification.    
    * @return Device's status online or off-line. 
    * */
   public boolean isOnLine() {
      if( getInformationAge() < (getAdvertisementPeriodicity() + 2) ) {
         return true;
      }
      return false;
   }
   
   
   // ATRIBUICAO 
   /** 
    * @param deviceIPAddr - the IP address to be attibuted to device. 
    */
   public void setIPAddress(String deviceIPAddr) {
      this.deviceIPAddr = deviceIPAddr;
   }

   /** 
    * @param deviceMacAddr - the MAC address to be attibuted to device. 
    */
   public void setMacAddress(String deviceMacAddr) {
      this.deviceMacAddr = deviceMacAddr;
   }

   /** 
    * @param deviceNetMask - the Network Mask to be attibuted to device. 
    */
   public void setNetworkMask(String deviceNetMask) {
      this.deviceNetMask = deviceNetMask;
   }
   
   /** Set the time in that the information was received (milliseconds). */
   public void setTimestamp() {  
      timestamp = new Timestamp(System.currentTimeMillis());
   }

   
   //Valores PERCENTUAIS
   /**
    * @param connectivityLevel - Wireless link connectivity in %.
    */
   public void setConnectivityLevel(float connectivityLevel) {
      this.connectivityP = connectivityLevel; 
   }
   
   /**
    * @param energyLevel - Energy available in %.
    */
   public void setEnergyLevel(float energyLevel) {
      this.energyP = energyLevel;
   }
   
   /**
    * @param cpuLevel - CPU available in %.
    */
   public void setCpuLevel(float cpuLevel) {
      this.cpuP = cpuLevel;
   }
   
   /**
    * @param memoryLevel - Memory available in %.
    */
   public void setMemoryLevel(float memoryLevel) {
      this.memoryP = memoryLevel; 
   } 

   //Valores ABSOLUTOS
   /**
    * @param connectivityLevel - Wireless link connectivity in %.
    */
   public void setConnectivityValue(float connectivityValue) {
      this.connectivity = connectivityValue; 
   }
   
   /**
    * @param energyLevel - Energy available in %.
    */
   public void setEnergyValue(float energyValue) {
      this.energy = energyValue;
   }
   
   /**
    * @param cpuLevel - CPU available in %.
    */
   public void setCpuValue(float cpuValue) {
      this.cpu = cpuValue;
   }
   
   /**
    * @param memoryLevel - Memory available in %.
    */
   public void setMemoryValue(float memoryValue) {
      this.memory = memoryValue; 
   } 
   
   
   /**
    * @param ipChanged   - true if the current device's IP Address changed or false if not.
    */
   public void setIpChanged(boolean ipChanged) {
      this.ipChanged = ipChanged;
   }
     
   /**
    *  @param advertisementPeriodicity   - the periodicity of the device´s information is send in seconds.
    */
   public void setAdvertisementPeriodicity(int advertisementPeriodicity) {
      this.advtPeriodicity = advertisementPeriodicity;
   }
   
   

   //Imprime informacoes relativas ao contexto do dispositivo
   /**
    * @return the current object value.
    */
   public String toString() {
      String status = "offline";
      if ( isOnLine() ) {
         status = "online";
      }
      String deviceContextStr;          
      deviceContextStr = "[DEVICE] Mac Address: " + getMacAddress() +
                         " IP Address: "          + getIPAddress() +  
                         " Network Mask: "        + getNetworkMask() +
                         " Status: "              + status + 
                         " Connectivity (dBm): "  + getConnectivityValue() + " ("+getConnectivityLevel()+"%)" +  
                         " Free CPU (KHz): "      + getCpuValue()          + " ("+getCpuLevel()+"%)" + 
                         " Free Memory (Kb): "    + getMemoryValue()       + " ("+getMemoryLevel()+"%)" + 
                         " Battery Power (): "    + getEnergyValue()       + " ("+getEnergyLevel()+"%)";              
      
      if ( isIpChanged() ) {
         deviceContextStr += "\n-> IP Changed";
      }
      
      /* 
      deviceContextStr += "\nInformation received at: "            + getTimestamp().toString() + 
                          " Information age (seconds): "           + getInformationAge() + 
                          " Advertisement Periodicity (seconds): " + getAdvertisementPeriodicity();
     */
      return deviceContextStr;
   }
}
