/**
 * @author lslima
 * 
 * Created on 12/04/2006
 */

package martin.mogrid.common.logging;

import java.net.URL;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.xml.DOMConfigurator;

public class MoGridLog4jConfigurator {

   private static final String  PROPERTIES_EXT = ".properties";
   private static final String  XML_EXT        = ".xml";
   private static       boolean INITIALIZED    = false;
  
   public static void configure() {
      if ( ! INITIALIZED ) {
         INITIALIZED = true;
         
         // Configura o log4j de acordo com os parametros do arquivo de
         // configuracao indicado em log4j.configuration
         String log4jFile = System.getProperty("log4j.configuration");
         LogLog.debug("Log4j configuration file: " + log4jFile);

         if ( log4jFile != null ) {
            if ( log4jFile.toLowerCase().endsWith(PROPERTIES_EXT) && 
                 log4jFile.trim().length() > PROPERTIES_EXT.length() ) 
            {
               // Usa os parametros do arquivo de configuracao .properties
               URL log4jURL = MoGridLog4jConfigurator.class.getResource(log4jFile);
               initUsingPropertiesFile(log4jURL);
               
            } else if ( log4jFile.toLowerCase().endsWith(XML_EXT) && 
                        log4jFile.trim().length() > XML_EXT.length() ) 
            {
               // Usa os parametros do arquivo de configuracao .xml
               URL log4jURL = MoGridLog4jConfigurator.class.getResource(log4jFile);
               initUsingXMLPropertiesFile(log4jURL);
               
            } else {
               initUsingBasicConfigurator();
            }
            
         } else {
            initUsingBasicConfigurator();
         }
      }      
   }

   private static void initUsingBasicConfigurator() {
      LogLog.warn("Problemas com o arquivo de configuração do Log4j, utilizando a configuração default: BasicConfigurator.");
      
      // Para evitar erro de inicializacao, utiliza a configuracao
      // default do Log4j
      BasicConfigurator.configure();
   }

   private static void initUsingPropertiesFile(URL log4jURL) {
      // Configura o log4j de acordo com os parametros do arquivo de
      // configuracao que eh um .properties
      PropertyConfigurator.configure(log4jURL);
   }

   private static void initUsingXMLPropertiesFile(URL log4jURL) {
      // Configura o log4j de acordo com os parametros do arquivo xml de
      // configuracao que é um .xml
      DOMConfigurator.configure(log4jURL);
   }
   
}
