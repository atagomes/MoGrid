
package martin.mogrid.p2pdl.collaboration.criterias;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;



/**
 * @author lslima
 *
 * Created on 14/06/2005
 */
public class WillingnessProfileProperties {
      
   //Manutencao do arquivo de log do servico
   private static final Logger logger = Logger.getLogger(WillingnessProfileProperties.class);  
   
   private static String[] resources = { "connectivity", "cpu", "energy", "memory" };      
   private static float[]  resLevels = new float[resources.length]; 
   
   private static final String DEFAULT_RELATIONAL_OPERATOR = ">"; 
   private static String[] relationalOps = new String[resources.length];  
   {
      for ( int i=0; i<resources.length; i++ ) {        
         relationalOps[i] = DEFAULT_RELATIONAL_OPERATOR;
      }   
   }

   private static final String DEFAULT_LOGICAL_OPERATOR = "AND";
   private static String logicalOp = DEFAULT_LOGICAL_OPERATOR;
   
   private static final int CONNECTIVITY = 0;
   private static final int CPU          = 1;
   private static final int ENERGY       = 2;
   private static final int MEMORY       = 3;
   
   
   //Arquivo de propriedades do gerente de contexto
   private static final String propsFile        = System.getProperty("mogrid.home") + File.separator + "conf" + File.separator + "WillingnessProfile.properties";
        
   
   
   //Carrega propriedades do gerente de contexto com base no arquivo de propriedades   
   public static void load() throws Exception {      
      try {
         Properties cmProperties = new Properties();
         FileInputStream programFileIn = new FileInputStream(propsFile);
         cmProperties.load( programFileIn );
         programFileIn.close();
         
         String property = null; 
         float  value = 0;
         for ( int i=0; i<resources.length; i++ ) {
            //Percentual values to compare
            property = cmProperties.getProperty(resources[i]+"Level");
            if ( property != null ) { 
               value = Float.parseFloat(property.trim());
               if ( value < 0   ) { value = 0;   }
               if ( value > 100 ) { value = 100; }
            }
            resLevels[i] = value;
            //Relational operators 
            property = cmProperties.getProperty(resources[i]+"Operator");
            if ( property != null ) { 
               relationalOps[i] = property.trim();
            } else {
               relationalOps[i] = DEFAULT_RELATIONAL_OPERATOR;
            }
            //Compositions
            property = cmProperties.getProperty("logical");
            if ( property != null ) { 
               logicalOp = property.trim();
            } else {
               logicalOp = DEFAULT_LOGICAL_OPERATOR;
            }
         }
      
      } catch (FileNotFoundException fnfe) {
         logger.warn("Willingness Profile properties file not found: " + propsFile + ". Using willingness default value.") ;
         throw new Exception("Willingness Profile properties file not found: " + propsFile, fnfe) ;
         
      } catch (IOException ioe) {
         logger.warn("Error while acessing Willingness Profile properties file: " + propsFile + ". Using willingness default value. ");
         throw new Exception("Error while acessing Willingness Profile properties file: " + propsFile, ioe) ;
         
      }
   }    
   
   
   //Resources levels
   public static float getConnectivityLevel() {
      return resLevels[CONNECTIVITY];
   } 

   public static float getCPULevel() {
      return resLevels[CPU];
   } 
   
   public static float getEnergyLevel() {
      return resLevels[ENERGY];
   } 
   
   public static float getMemoryLevel() {
      return resLevels[MEMORY];
   } 

   
   //Relational Operators
   public static String getConnectivityOperator() {
      return relationalOps[CONNECTIVITY];
   } 

   public static String getCPUOperator() {
      return relationalOps[CPU];
   } 
   
   public static String getEnergyOperator() {
      return relationalOps[ENERGY];
   } 
   
   public static String getMemoryOperator() {
      return relationalOps[MEMORY];
   } 
   
   
   //Logical Operator
   public static String getLogicalOperator() {
      return logicalOp;
   } 
   
}
