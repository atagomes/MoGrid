
/**
 * @author Luciana Lima
 * copyright MoCA Project (hhttp://www.lac.inf.puc-rio.br/moca)
 *
 * Created on 08/06/2006
 */

package martin.mogrid.service.monitor.moca.windows.xp;

import java.util.StringTokenizer;

import martin.mogrid.common.util.SystemUtil;
import martin.mogrid.service.monitor.ContextParser;
import martin.mogrid.service.monitor.ContextParserException;
import martin.mogrid.service.monitor.DeviceContext;

public class MoCAWindowsXPContextParser implements ContextParser {      
   
   //Where PERFECT_RSSI is roughly the maximum signal you see when holding a  
   //laptop up to an access point and WORST_RSSI is roughly the signal level 
   //at which disassociation occurs. 
   private static final int PERFECT_RSSI  = -20; //in dBm
   private static final int WORST_RSSI    = -85; //in dBm
   
   /**
    * Parse data received from the moca.service.monitor:
    * CPU Usage#Free Memory#Energy Level#Periodicity#IPChange#APChange#IP#MASK#MobileHost's MacAddr#Current AP's MacAddr&AP1&AP2&AP3&APn
    * => Where APn is equal to "APn's MacAddr#APn's Signal Strenght#APn's SSID"
    * 
    * For instance: 2#264696#83#4#0#0#10.10.10.11#255.255.255.0#00:02:2D:A5:06:8C#00:02:2D:A5:06:12&00:02:2D:A5:06:7D#-44#LAC&00:02:2D:A5:06:12#-71#LAC
    *
    * @param monitorData - a data packet from monitor
    * @return a register with the device´s context information
    */    
   public DeviceContext parseMonitorData(Object data) throws ContextParserException {
      if ( data == null ) {
         throw new ContextParserException("Data received from Monitor is null.");
      }
      if ( !(data instanceof String) ) {
         throw new ContextParserException("Data received from Monitor in an invalid format.");
      }
      
      String monitorData = data.toString().trim();
      
      if ( SystemUtil.strIsNotNull(monitorData) ) {
         MoCAWindowsXPDeviceContext deviceContext = new MoCAWindowsXPDeviceContext();  // Store the device´s context information
         deviceContext.setTimestamp();
         
         int index = monitorData.indexOf('&');
         StringTokenizer st = new StringTokenizer(monitorData.substring(0, index), "#");

         if ( st != null ) {
            try {                
               deviceContext.setCpuLevel( 100 - Integer.parseInt(st.nextToken()) );
               deviceContext.setMemoryLevel( 70 ); //The MoCA XP Monitor doens´t informs the free percentual
               deviceContext.setMemoryValue( Float.parseFloat(st.nextToken()) );              
               deviceContext.setEnergyLevel( Float.parseFloat(st.nextToken()) );
               deviceContext.setAdvertisementPeriodicity( Integer.parseInt(st.nextToken()) );
               int ipChanged = Integer.parseInt(st.nextToken());
               deviceContext.setIpChanged( (ipChanged == 0) ? false : true );
               int apChanged = Integer.parseInt(st.nextToken());
               deviceContext.setApChanged( (apChanged == 0) ? false : true );
               deviceContext.setIPAddress( st.nextToken() );
               deviceContext.setNetworkMask( st.nextToken() );
               deviceContext.setMacAddress( st.nextToken() );
               deviceContext.setAPMacAddress( st.nextToken() ); 
                              
               st = new StringTokenizer(monitorData.substring(index + 1), "&");
               while ( st.hasMoreTokens() ) {
                  StringTokenizer apTokens = new StringTokenizer(st.nextToken(), "#");
                  if ( apTokens.nextToken().equalsIgnoreCase(deviceContext.getAPMacAddress()) ){
                     float signal = Float.parseFloat(apTokens.nextToken());
                     int   signalLevel = 100;
                     if      ( signal >= PERFECT_RSSI ) { signalLevel = 100; }
                     else if ( signal <= WORST_RSSI )   { signalLevel = 1;   }
                     else {  
                        signalLevel = calcQuadraticPercentValue(signal);
                        //System.out.println("\n*SIGNAL: "+ signal+" Q: "+calcQuadraticPercentValue(signal)+" L: "+calcLinearPercentValue(signal));
                     } 
                     deviceContext.setConnectivityLevel( signalLevel );
                     deviceContext.setConnectivityValue( signal );
                     break;
                  }
               }    
               
            } catch (NumberFormatException nfe) {
               throw new ContextParserException(nfe);
            }
         } else {
            throw new ContextParserException("Data received from Monitor is not in a valid format.");
         }
         return deviceContext;
         
      } else {        
         throw new ContextParserException("Data received from Monitor is null.");         
      }
   }  
   
   
   
   // Quadratic Model Reference: http://www.ces.clemson.edu/linux/nm-ipw2200.shtml
   private int calcQuadraticPercentValue(float signal) {
      float RSSI           = PERFECT_RSSI - signal;
      int   RSSI_RANGE     = PERFECT_RSSI - WORST_RSSI;
      int   QUADRATIC_TERM = RSSI_RANGE * RSSI_RANGE;
      float signalQuality  = ((100 * QUADRATIC_TERM) - (RSSI * ((15 * RSSI_RANGE) + (62 * RSSI)))) / QUADRATIC_TERM;

      return Math.round(signalQuality);
   }
   
   // Linear Model Reference: http://www.ces.clemson.edu/linux/dbm-rssi.shtml
   private int calcLinearPercentValue(float signal) { 
      float signalQuality = 100 - (80 * (PERFECT_RSSI - signal)) / (PERFECT_RSSI - WORST_RSSI);
      return Math.round( signalQuality );         
   }
   
}
