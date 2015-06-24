package martin.mogrid.service.monitor.moca;

import martin.mogrid.service.monitor.DeviceContext;

public class MoCADeviceContext extends DeviceContext {
   // Context monitored by moca.service.monitor:
   // CPU Usage#Free Memory#Energy Level#Periodicity#IPChange#APChange#IP#MASK#MobileHost's MacAddr#Current AP's MacAddr&AP1&AP2&AP3&APn
   // => Where APn is equal to "APn's MacAddr#APn's Signal Strenght#APn's SSID"
   // For instance: 2#264696#83#4#0#0#10.10.10.11#255.255.255.0#00:02:2D:A5:06:8C#00:02:2D:A5:06:12&00:02:2D:A5:06:7D#-44#LAC&00:02:2D:A5:06:12#-71#LAC
   //CONSTRUTROR: inicializa os atributos referentes ao contexto do dispositivo
  
   /** Comment for <code>serialVersionUID</code> */
   private static final long serialVersionUID = -1209194020060001276L;
 
}
