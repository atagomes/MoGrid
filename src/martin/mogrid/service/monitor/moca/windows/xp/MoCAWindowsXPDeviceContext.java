
/**
 * @author Luciana Lima
 * copyright MoCA Project (hhttp://www.lac.inf.puc-rio.br/moca)
 *
 * Created on 08/06/2006
 */

package martin.mogrid.service.monitor.moca.windows.xp;

import martin.mogrid.service.monitor.DeviceContext;

public class MoCAWindowsXPDeviceContext extends DeviceContext {
  
   /** Comment for <code>serialVersionUID</code> */
   private static final long serialVersionUID = 6307016816065603240L;
   
   // Context monitored by moca.service.monitor:
   // CPU Usage#Free Memory#Energy Level#Periodicity#IPChange#APChange#IP#MASK#MobileHost's MacAddr#Current AP's MacAddr&AP1&AP2&AP3&APn
   // => Where APn is equal to "APn's MacAddr#APn's Signal Strenght#APn's SSID"
   // For instance: 2#264696#83#4#0#0#10.10.10.11#255.255.255.0#00:02:2D:A5:06:8C#00:02:2D:A5:06:12&00:02:2D:A5:06:7D#-44#LAC&00:02:2D:A5:06:12#-71#LAC
   //CONSTRUTROR: inicializa os atributos referentes ao contexto do dispositivo
   
   private boolean apChanged;    // If the current Device's AP IP Address changed 
   private String  apMacAddr;    // AP큦 MAC address
    
   
   /**
    * @return   If the current AP's IP Address changed.
    */
   public boolean isApChanged() {
      return apChanged;
   }

   /** 
    * @return AP큦 MAC address. 
    */
   public String getAPMacAddress() {
      return apMacAddr;
   }
   
   
   /**
    * @param apChanged   - true if the current AP큦 IP Address changed or false if not.
    */
   public void setApChanged(boolean apChanged) {
      this.apChanged = apChanged;
   }


   /** 
    * @param apMacAddr - the MAC address to be attibuted to AP. 
    */
   public void setAPMacAddress(String apMacAddr) {
      this.apMacAddr = apMacAddr;
   }
   
   
   //Imprime informacoes relativas ao contexto do dispositivo
   /**
    * @return the current object value.
    */
   public String toString() {
      String deviceContextStr = this.toString();
      deviceContextStr += "\n->[DEVICE큦 AP] Mac Address: " + apMacAddr;
      if ( isApChanged() ) {
         deviceContextStr += "\n->AP Changed";
      }
      return deviceContextStr;
   }

}
