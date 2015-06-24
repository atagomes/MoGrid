
package martin.mogrid.entity.proxy.registry.globus;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import martin.mogrid.common.network.Payload;
import martin.mogrid.common.resource.ResourceDescriptor;
import martin.mogrid.common.resource.ResourceQuery;

import org.apache.log4j.Logger;

public class ProxyResourceDescriptor extends ResourceDescriptor implements Payload {
   //Manutencao do arquivo de log do servico
   private static final Logger logger = Logger.getLogger(ProxyResourceDescriptor.class);

   
   /** Generated serial version uid  */
   private static final long serialVersionUID = 6025860819962692311L;

   public ProxyResourceDescriptor(double length, double duration, String identifier, String description, String[] keywords, String linuxPath, String unixPath) {
      super(length, duration, identifier, description, keywords, null);
      setLinuxScriptPath( linuxPath );
      setUnixScriptPath( unixPath );
   }
   
   public ProxyResourceDescriptor(String identifier, String description, String[] keywords, String linuxPath, String unixPath) {
      this(0, 0, identifier, description, keywords, linuxPath, unixPath);    
   }
   
   /*public ProxyResourceDescriptor() {
      this( 0, 0, null, null, null, null );
   }*/
   
   
   //private String     identifier;
   //private String     description;
   //private String[]   keywords;
   private String     linuxScriptPath;
   private String     unixScriptPath;
   
   /*public String getDescription() {
      return description;
   }
   
   public String getIdentifier() {
      return identifier;
   }
   
   public String[] getKeywords() {
      return keywords;
   }*/
   
   /*public void setDescription(String description) {
      this.description = description;
   }
   
   public void setIdentifier(String identifier) {
      this.identifier = identifier;
   }
   
   public void setKeywords(String[] keywords) {
      this.keywords = keywords;
   }*/
   
   public void setLinuxScriptPath(String scriptPath) {
      this.linuxScriptPath = scriptPath;
   }
   
   public String getLinuxScriptPath() {
      return linuxScriptPath;
   }

   public String getUnixScriptPath() {
      return unixScriptPath;
   }

   public void setUnixScriptPath(String unixScriptPath) {
      this.unixScriptPath = unixScriptPath;
   }
   
   public boolean compareWith(ResourceQuery query) {
      String match = query.getResourceToMatch().toLowerCase();
      int    op    = query.getWhereClausule();    
      
      logger.info("op: "+op+ " match: "+ match);
      
      switch ( op ) {
         case ResourceQuery.CP_ALL :
            if ( equalsIgnoreCase(getIdentifier(), match) )
               return true;
            if ( equalsIgnoreCase(getDescription(), match) )
               return true;
            /*if ( equalsIgnoreCase(getLinuxScriptPath(), match) )
               return true;
            if ( equalsIgnoreCase(getUnixScriptPath(), match) )
               return true;*/
            if ( equalsIgnoreCase( getKeywords(), match) )
               return true;
            break;
            
         case ResourceQuery.CP_CONTAINS :
            if ( contains(getIdentifier(), match) )
               return true;
            if ( contains(getDescription(), match) )
               return true;
            /*if ( contains(getLinuxScriptPath(), match) )
               return true;
            if ( contains(getUnixScriptPath(), match) )
               return true;*/
            if ( contains(getKeywords(), match) )
               return true;
            break;
            
         case ResourceQuery.CP_ENDS_WITH :
            if ( endsWith(getIdentifier(), match) )
               return true;
            if ( endsWith(getDescription(), match) )
               return true;
            /*if ( endsWith(getLinuxScriptPath(), match) )
               return true;
            if ( endsWith(getUnixScriptPath(), match) )
               return true;*/
            if ( endsWith(getKeywords(), match) )
               return true;
            break;
            
         case ResourceQuery.CP_STARTS_WITH :
            if ( startsWith(getIdentifier(), match) )
               return true;
            if ( startsWith(getDescription(), match) )
               return true;
            /*if ( startsWith(getLinuxScriptPath(), match) )
               return true;
            if ( startsWith(getUnixScriptPath(), match) )
               return true;*/
            if ( startsWith(getKeywords(), match) )
               return true;
            break;
      }
      
      return false;    
   }
   
   private boolean equalsIgnoreCase(String input, String regex) {
      if ( input != null && regex != null ) {
         Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
         Matcher matcher = pattern.matcher(input);
         if ( matcher.matches() ) {
            return true;
         }
      }
      return false;        
   }
   
   private boolean equalsIgnoreCase(String[] input, String regex) {
      if ( input != null && regex != null ) {
         for (int i=0; i<input.length; i++) {
            if ( equalsIgnoreCase( input[i], regex) )
               return true;
         }
      }
      return false;      
   }

   private boolean contains(String input, String regex) { 
      if ( input != null && regex != null ) {
         Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
         Matcher matcher = pattern.matcher(input);
         if ( matcher.find() ) {
            return true;
         }
      }
      return false;      
   }
   
   private boolean contains(String[] input, String regex) {
      if ( input != null && regex != null ) {
         for (int i=0; i<input.length; i++) {
            if ( contains(input[i], regex) )
               return true;
         }
      }
      return false;      
   }
   
   private boolean endsWith(String input, String regex) {
      if ( input != null && regex != null && 
           input.toLowerCase().endsWith(regex.toLowerCase()) ) 
      {
         return true;
      }
      return false;      
   }
   
   private boolean endsWith(String[] input, String regex) {
      if ( input != null && regex != null ) {
         for (int i=0; i<input.length; i++) {
            if ( endsWith( input[i], regex) )
               return true;
         }
      }
      return false;      
   }
   
   private boolean startsWith(String input, String regex) {
      if ( input != null && regex != null && 
           input.toLowerCase().startsWith(regex.toLowerCase()) )
      {
         return true;
      }
      return false;      
   }
   
   private boolean startsWith(String[] input, String regex) {
      if ( input != null && regex != null ) {
         for (int i=0; i<input.length; i++) {
            if ( startsWith( input[i], regex) )
               return true;
         }
      }
      return false;      
   }

   public ResourceDescriptor getResourceDescriptor() {      
      return new ResourceDescriptor( getIdentifier(), getDescription(), getKeywords(), getPath() );      
   }
   
   
   //IMPRESSAO
   public String toString() {
      String taskStr = super.toString();
      taskStr += "\nScript [Linux Path]: ";
      if ( linuxScriptPath != null ) { taskStr += linuxScriptPath; }
      taskStr += "\nScript [Unix Path]: ";
      if ( unixScriptPath != null ) { taskStr += unixScriptPath; }
         
      return taskStr;
   }
   
}