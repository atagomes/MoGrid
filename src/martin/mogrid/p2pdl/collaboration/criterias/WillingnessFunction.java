/*
 * Created on 25/01/2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package martin.mogrid.p2pdl.collaboration.criterias;

import martin.mogrid.service.monitor.DeviceContext;

import org.apache.log4j.Logger;

public class WillingnessFunction {
   
   //Manutencao do arquivo de log do servico
   private static final Logger logger = Logger.getLogger(WillingnessFunction.class);  
   
   private static final float DEFAULT_W = 1f;   
   private static float willingness = DEFAULT_W;
   
   private static boolean[] resources; 
          
   //Relational Operators
   private static final String EQUAL                 = "=";
   private static final String NOT_EQUAL             = "!=";
   private static final String GREATER_THAN          = ">";
   private static final String LESS_THAN             = "<";
   private static final String GREATER_THAN_OR_EQUAL = ">=";
   private static final String LESS_THAN_OR_EQUAL    = "<=";   
   //Logical operators 
   private static final String LOGICAL_AND = "AND";
   private static final String LOGICAL_OR  = "OR";
      

   //TODO: MUDAR isso! Devo calcular a willingness em função do contexto do dispositivo e não definir apenas se posso ou não 
   //      colaborar (estou retornando sempre 0 ou 1 e não valores fracionados nesse intervalo)
   public synchronized static float getValue(DeviceContext devContext, float w) {
      try {
         WillingnessProfileProperties.load();
          
         int idx = 0;
         resources = new boolean[4];
         if ( willingness > 0 ) {              
            //Compare
            float value1 = devContext.getConnectivityLevel();  
            float value2 = WillingnessProfileProperties.getConnectivityLevel();
            String operator = WillingnessProfileProperties.getConnectivityOperator();
            resources[idx++] = compareRelational(value1, value2, operator); 
            
            value1 = devContext.getCpuLevel();  
            value2 = WillingnessProfileProperties.getCPULevel();
            operator = WillingnessProfileProperties.getCPUOperator();
            resources[idx++] = compareRelational(value1, value2, operator); 

            value1 = devContext.getEnergyLevel();  
            value2 = WillingnessProfileProperties.getEnergyLevel();
            operator = WillingnessProfileProperties.getEnergyOperator();
            resources[idx++] = compareRelational(value1, value2, operator); 
            
            value1 = devContext.getMemoryLevel();  
            value2 = WillingnessProfileProperties.getMemoryLevel();
            operator = WillingnessProfileProperties.getMemoryOperator();
            resources[idx++] = compareRelational(value1, value2, operator);    
            
            boolean canCollaborate = compareLogical(resources, WillingnessProfileProperties.getLogicalOperator());
            
            if ( canCollaborate ) {
               logger.info("Current willingness value: 1");
               return 1; 
            } else { 
               logger.info("Current willingness value: 0");
               return 0; 
            }
            
         } 
         
      } catch (Exception e) {         
         if      ( w < 0 )  { willingness = 0; } 
         else if ( w > 1 )  { willingness = 1; } 
         else               { willingness = w; }    
         logger.info("Current willingness value: "+ willingness);     
      }
      
      return willingness;
   }
   
   private static boolean compareRelational(float value1, float value2, String operator) {
      logger.info("value1: "+ value1+ " value2: "+value2);
      if ( operator.equalsIgnoreCase(EQUAL) )
         return value1 == value2;
      if ( operator.equalsIgnoreCase(NOT_EQUAL) )
         return value1 != value2;
      if ( operator.equalsIgnoreCase(GREATER_THAN) )
         return value1 > value2;
      if ( operator.equalsIgnoreCase(LESS_THAN) )
         return value1 < value2;
      if ( operator.equalsIgnoreCase(GREATER_THAN_OR_EQUAL) )
         return value1 >= value2;
      if ( operator.equalsIgnoreCase(LESS_THAN_OR_EQUAL) )
         return value1 <= value2;
      
      return false;
   }
   
   private static boolean compareLogical(boolean[] exps, String operator) {
      boolean teste = false;
      if ( operator.equalsIgnoreCase(LOGICAL_AND) ) {
         teste = true;
         for ( int i=0; i<exps.length; i++ ) {
            teste = teste && exps[i];
         }
         
      } else if ( operator.equalsIgnoreCase(LOGICAL_OR) ) {
         for ( int i=0; i<exps.length; i++ ) {
            teste = teste || exps[i];
         }
      }
      return teste;
   }

}
