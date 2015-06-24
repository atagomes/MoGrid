package martin.mogrid.service.monitor.globus;

import martin.mogrid.service.monitor.DeviceContext;

public class GlobusDeviceContext extends DeviceContext {
  
   /** Comment for <code>serialVersionUID</code> */
   private static final long serialVersionUID = -2089427492944908542L;
   private String soName;

   public String getSOName() {
      return soName;
   }

   public void setSOName(String soName) {
      this.soName = soName;
   }

   public String getNetworkMask() {
      return null;
   }
   
   //VALORES PERCENTUAIS
   public float getConnectivityLevel() {
      return 100;
   }

   public float getEnergyLevel() {
      return 100;
   }  

   //VALORES ABSOLUTOS
   public float getConnectivityValue() {
      return 100;
   }

   public float getEnergyValue() {
      return 100;
   }  

   public boolean isOnLine() {
      return true;
   }   
}
