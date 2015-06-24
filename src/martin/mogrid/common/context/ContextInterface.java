package martin.mogrid.common.context;

import java.io.Serializable;

public interface ContextInterface extends Serializable {
   
   //Informacao de contexto: recursos monitorados 
   public static final int DEVICE_CPU      = 1;           
   public static final int DEVICE_MEMORY   = 2;         
   public static final int DEVICE_ENERGY   = 4;
   public static final int DEVICE_AP_RSSI  = 8;            
   //public static final int HOST_IP       = 16;         
   //public static final int HOST_MASK     = 32;
   //public static final int HOST_MAC      = 64;
   //public static final int AP_MAC        = 128;
   //public static final int AP_SSID       = 256;  
   
}
