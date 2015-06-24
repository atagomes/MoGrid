
package martin.mogrid.service.monitor.moca;

import java.util.Random;

import martin.mogrid.common.util.MoGridString;
import martin.mogrid.common.util.SystemUtil;
import martin.mogrid.service.monitor.ContextParser;
import martin.mogrid.service.monitor.ContextParserException;
import martin.mogrid.service.monitor.DeviceContext;




/**
 * @author luciana
 * copyright Vagner Sacramento
 *
 * Created on 15/06/2005
 */
public class MoCAContextParser implements ContextParser {    

   long oldSeed     = 0;
   long currentSeed = 0;
   
   /**
    * Parse data received from the moca.service.monitor:
    * CPU Usage#Free Memory#Energy Level#Periodicity#IPChange#APChange#IP#MASK#MobileHost's MacAddr#Current AP's MacAddr&AP1&AP2&AP3&APn
    * => Where APn is equal to "APn's MacAddr#APn's Signal Strenght#APn's SSID"
    * 
    * For instance: 2#264696#83#4#0#0#10.10.10.11#255.255.255.0#00:02:2D:A5:06:8C#00:02:2D:A5:06:12&00:02:2D:A5:06:7D#-44#LAC&00:02:2D:A5:06:12#-71#LAC
    *
    * MoGrid Adaptation:
    * CPU Usage [0], Free Memory[1], Energy Level[2] and APn's Signal Strenght[5] in %
    * 
    * @param monitorData - a data packet from monitor
    * @return a register with the device큦 context information
    */  
   
   public DeviceContext parseMonitorData(Object data) throws ContextParserException {   
      if ( data == null ) {
         throw new ContextParserException("Data received from Monitor is null.");
      }
      if ( !(data instanceof String) ) {
         throw new ContextParserException("Data received from Monitor in an invalid format.");
      }
   
      String monitorData = (String) data;
      if ( SystemUtil.strIsNotNull(monitorData) ) {
         MoCADeviceContext deviceContext = new MoCADeviceContext();  // Store the device큦 context information
         
         //Auxiliar data structures
         //ArrayList apcList      = new ArrayList();          // Array to store the list of access points and your context                 
         //String[] registers     = monitorData.split("&");   // 0: context information tokens; 1..n: ap큦 tokens
         //String[] contextTokens = registers[0].split("#");  // Context information tokens
         String[] registers     = MoGridString.split(monitorData, "&");   // 0: context information tokens; 1..n: ap큦 tokens
         String[] contextTokens = MoGridString.split(registers[0], "#");  // Context information tokens
        
         if ( contextTokens != null && contextTokens.length == 10 ) {
            try {          
               /*
               // Parsing the access points:
               for (int i = 1; i < registers.length; i++) {
                  //String[] apcTokens = registers[i].split("#");
                  String[] apcTokens = MoGridString.split(registers[i], "#");
                  if ( apcTokens.length == 3 ) {
                     AccessPointContext apc = new AccessPointContext(apcTokens[0], Integer.parseInt(apcTokens[1]), apcTokens[2]);
                     apcList.add(apc);
                  } else if ( apcTokens.length == 2 ) { // Considero os params como 0: MAC e 1: RSSI
                     AccessPointContext apc = new AccessPointContext(apcTokens[0], Integer.parseInt(apcTokens[1]), apcTokens[0]);
                     apcList.add(apc);
                  } else {
                     //logger.warn("Error at the PARSE to extrat AP List received from the Monitor: " + registers[i]);
                  }
               }
               */

               
               //Mogrid Adaptation: problemas com Memory e Connectivity
               //o primeiro eh disponibilizado em Kb e o segundo em um
               //valor maluco que nao sabemos ainda como tratar
               
               deviceContext.setTimestamp();
                              
               deviceContext.setCpuValue(getNextIntRandom(5000, 88000));
               deviceContext.setCpuLevel(getNextIntRandom(15, 95));
               deviceContext.setMemoryValue(Float.parseFloat(contextTokens[1]));
               deviceContext.setMemoryLevel(getNextIntRandom(9, 90));
               deviceContext.setEnergyValue(Float.parseFloat(contextTokens[2]));
               deviceContext.setEnergyLevel(getNextIntRandom(10, 100));
               
               deviceContext.setAdvertisementPeriodicity(Integer.parseInt(contextTokens[3]));
      
               int ipChange = Integer.parseInt(contextTokens[4]);
               deviceContext.setIpChanged( (ipChange == 0) ? false : true );
               
               //MoGrid adaptation 
               deviceContext.setConnectivityValue(Integer.parseInt(contextTokens[5])); 
               deviceContext.setConnectivityLevel(getNextIntRandom(8, 80));
               
               //int apChange = Integer.parseInt(contextTokens[5]);
               //deviceContext.setApChanged( (apChange == 0) ? false : true );
               
               deviceContext.setIPAddress(contextTokens[6]);
               deviceContext.setNetworkMask(contextTokens[7]);
               
               if ( SystemUtil.strIsNotNull(contextTokens[8]) )
                  deviceContext.setMacAddress(contextTokens[8].toUpperCase());
               
               /*
               deviceContext.setCurrentAPMacAddress(contextTokens[9]);
               if ( Util.strIsNotNull(contextTokens[9]) && apcList != null ) {
                  Iterator iterator = apcList.iterator();
                  AccessPointContext apc = null;
                  while( iterator.hasNext() ) {
                     apc = (AccessPointContext)iterator.next();
                     if ( apc.getMacAddress().equalsIgnoreCase(contextTokens[9]) ) { 
                        deviceContext.setConnectivityLevel(apc.getSignalStrenght());
                        break;
                     }
                  }
               }
               deviceContext.setApList(apcList);
               */
               
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
 
   
   private int getNextIntRandom(int minValue, int maxValue) {
      while ( currentSeed == oldSeed ) {
         currentSeed = System.currentTimeMillis();   
      } 
      oldSeed = currentSeed;
      Random rand = new Random(currentSeed);    
      // Random integers that range from from 0 to maxValue -> we need [1, maxValue]
      int randValue = rand.nextInt(maxValue-minValue) + minValue;
      return randValue;   
   }
   
}
