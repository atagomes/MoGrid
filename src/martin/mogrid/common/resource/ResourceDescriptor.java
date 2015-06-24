
package martin.mogrid.common.resource;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import martin.mogrid.common.network.Payload;
import martin.mogrid.common.util.MoGridString;


/**
 * @author   luciana
 */
public class ResourceDescriptor implements Payload {
   
   /** Comment for <code>serialVersionUID</code> */
   private static final long serialVersionUID = -4859935683056185819L;
   
   //Atributos que compoem a tarefa
   private double     length;             //tamanho
   private double     duration;           //duracao
   
   private String     identifier;         //identificador
   private String     description;        //descricao
   private String[]   keywords;           //palavras-chave
   private String     path;               //localizacao (caminho completo)
   private String     pathName;           //localizacao (nome do arquivo - executavel, audio, video, etc)
   
   //Variavel adicionada para dar suporte a requisicoes copm multiplos recursos
   private boolean isMultiple        = false;
   private List    resDescriptorList = null;
  
   //CONSTRUTOR   
   public ResourceDescriptor(double length, double duration, String identifier, String description, String[] keywords, String path) {
      this.length        = length;
      this.duration      = duration;
      this.identifier    = identifier;
      this.description   = description;
      this.keywords      = keywords;
      setPath(path);
   }
   
   public ResourceDescriptor(String identifier, String description, String[] keywords, String path) {
      this(0, 0, identifier, description, keywords, path);    
   }
   
   public ResourceDescriptor() {
      isMultiple = true;
      resDescriptorList = new ArrayList();
   }
   
   //Metodos adicionados para permitir multiplas requisicoes
   public boolean isMultiple() {
      return isMultiple;
   }
   
   public boolean addResourceDescriptor( ResourceDescriptor resDescriptor ) {
      if( isMultiple() ) {
         resDescriptorList.add( resDescriptor );
         return true;
      }
      return false;
   }
   
   public ResourceDescriptor getResourceDescriptor( String identifier ) {
      if( resDescriptorList != null) {
         Iterator i = resDescriptorList.iterator();
         while( i.hasNext() ) {
            ResourceDescriptor resDescriptor = (ResourceDescriptor)i.next();
            if( identifier.equals( resDescriptor.getIdentifier() ) ) {
               return resDescriptor;
            }
         }
      }
      return null;
   }
   
   
   //LEITURA
   /**
    * @return   Returns the length.
    */
   public double getLength() {
      return length;
   }
   
   /**
    * @return   Returns the duration.
    */
   public double getDuration() {
      return duration;
   }
   
   /**
    * @return   Returns the identification.
    */
   public String getIdentifier() {
      return identifier;
   }
   
   /**
    * @return   Returns the description.
    */
   public String getDescription() {
      return description;
   }
   
   /**
    * @return   Returns the description.
    */
   public String[] getKeywords() {
      return keywords;
   }
      
   /**
    * @return   Returns the path.
    */
   public String getPath() {
      return path;
   }
    
   //ATRIBUICAO
   /**
    * @param length   The length to set.
    */
   public void setLength(double length) {
      this.length = length;
   }
   
   /**
    * @param duration   The duration to set.
    */
   public void setDuration(double duration) {
      this.duration = duration;
   }

   /**
    * @param description   The description to set.
    */
   public void setDescription(String description) {
      this.description = description;
   }

   /**
    * @param description   The description to set.
    */
   public void setKeywords(String[] keywords) {
      this.keywords = keywords;
   }
   /**
    * @param identification   The identification to set.
    */
   public void setPath(String subject) {
      //Tratamento para evitar problemas com File.separator() em diferentes plataformas:
      //Ex: Requisicao gerada no Windows e executada no Linux 
      if ( subject != null ) {     
         //If you want to match a backslash ("\"), the regular expression you would use is \\\\.
         this.path = MoGridString.replaceAll(subject, "\\\\", "/");
         setPathName(); 
      }
   }     
   
   private void setPathName() {
      if ( path != null ) {
         File fileSubject = new File(path);
         if ( fileSubject.isFile() ) {
            //If you want to match a backslash ("."), the regular expression you would use is \\.
            String[] file = MoGridString.split(fileSubject.getName(), "\\.");
            pathName = file[0];
            return;
         }
         pathName = path;
      }
   }
   
   public void resetPath() {
      path     = null;
      pathName = null; 
   }     
   
   
   public boolean compareWith(ResourceQuery query) {
      // Os testes abaixo verificam se a query procura por recusros multiplos
      // e chama recursivamente o metodo para poder atender a todos os recursos
      if ( query.isMultipleResourceRequest() ) {
         String match[] = query.getResourceToMatchArray();
         
         for ( int i = 0; i < match.length; i++ ) {
            if( compareWith( new ResourceQuery( match[i], query.getWhereClausule() ) ) ) {
               if ( query.getConditionClausule() == ResourceQuery.AND ) {
                  continue;
               }
               if ( query.getConditionClausule() == ResourceQuery.OR ) {
                  return true;
               }
            } else {
               if ( query.getConditionClausule() == ResourceQuery.OR ) {
                  continue;
               }
               if ( query.getConditionClausule() == ResourceQuery.AND ) {
                  return false;
               }
            }
         }
         if( query.getConditionClausule() == ResourceQuery.AND ) {
            return true;
         } else {
            return false;
         }               
      }
      
      String match = query.getResourceToMatch().toLowerCase();
      int    op    = query.getWhereClausule();            
      
      switch ( op ) {
         case ResourceQuery.CP_ALL :
            if ( equalsIgnoreCase(identifier, match) )
               return true;
            if ( equalsIgnoreCase(description, match) )
               return true;
            if ( equalsIgnoreCase(pathName, match) )
               return true;            
            if ( equalsIgnoreCase( keywords, match) )
               return true;
            break;
            
         case ResourceQuery.CP_CONTAINS :
            if ( contains(identifier, match) )
               return true;
            if ( contains(description, match) )
               return true;
            if ( contains(pathName, match) )
               return true;
            if ( contains(keywords, match) )
               return true;
            break;
            
         case ResourceQuery.CP_ENDS_WITH :
            if ( endsWith(identifier, match) )
               return true;
            if ( endsWith(description, match) )
               return true;
            if ( endsWith(pathName, match) )
               return true;
            if ( endsWith(keywords, match) )
               return true;
            break;
            
         case ResourceQuery.CP_STARTS_WITH :
            if ( startsWith(identifier, match) )
               return true;
            if ( startsWith(description, match) )
               return true;
            if ( startsWith(pathName, match) )
               return true;
            if ( startsWith(keywords, match) )
               return true;
            break;
      }
      
      return false;    
   }
   
   private boolean equalsIgnoreCase(String input, String regex) {   
      if ( input != null && regex != null ) {
         return input.regionMatches( true, 0, regex, 0, regex.length() );        
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
      //Attempts to find the first subsequence of the input sequence that matches the pattern (regex).      
      if ( input != null && regex != null ) {
         input = input.toLowerCase();
         regex = regex.toLowerCase();
         if ( input.indexOf(regex) != -1 ) {
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
   
   
   //IMPRESSAO
   public String toString() {
      String taskStr = "\n[Resource Description]";
      taskStr += "\nIdentifier: ";
      if ( identifier != null ) { taskStr += identifier; }
      taskStr += "\nPath: ";
      if ( path != null ) { taskStr += path; }
      taskStr += "\nDescription: ";
      if ( description != null ) { taskStr += description; }
      taskStr += "\nKeywords: ";
      if ( keywords != null ) { 
         for (int i=0; i<keywords.length; i++) {
            taskStr += keywords[i]; 
            if ( i < keywords.length-1 ) { taskStr += ", "; }
         }
      }
      taskStr +="\nLength: " + length + " Duration: " + duration; 
         
      return taskStr;
   }
   
}
