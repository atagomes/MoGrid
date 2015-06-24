package martin.mogrid.common.resource;

import java.util.ArrayList;
import java.util.List;

import martin.mogrid.common.util.UniversalIdentifier;

public class ResourceIdentifier extends UniversalIdentifier {  
   
   /** Comment for <code>serialVersionUID</code> */
   private static final long serialVersionUID = 2195402874588168601L;
   
   // Variavel adicionada para testar de a requisicao eh de multiplos recursos   
   private boolean isMultiple = false;
   private List    resourceIdentifierList = null;


   public ResourceIdentifier() {
      super("");
         isMultiple = true;
         resourceIdentifierList = new ArrayList();
   }
   
   public ResourceIdentifier(String resourceSubject) {
      super(resourceSubject); 
   }
   
   public String getResourceIdentifier() {
      return getIdentifier();
   }   

   public String getResourceSubject() {      
      return getSubject();
   }

   public boolean isMultiple() {
      return isMultiple;
   }
   
   public boolean addResourceIdentifier( ResourceIdentifier resID ) {
      if( isMultiple() ) {
         resourceIdentifierList.add( resID );
         return true;
      }     
      return false;
   }
   
   public List getResourceIdentifierList() {
      return resourceIdentifierList;
   }
}
