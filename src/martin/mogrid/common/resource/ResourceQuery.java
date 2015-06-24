package martin.mogrid.common.resource;

import martin.mogrid.common.network.Payload;
import martin.mogrid.p2pdl.api.RequestIdentifier;


public class ResourceQuery implements Payload {
   
   /** Comment for <code>serialVersionUID</code> */
   private static final long serialVersionUID = -4157431483984827244L;
   
   //QueryMatch - clausulas WHERE considerados na descoberta de recursos
   public static final int       CP_ALL              = 0; 
   public static final int       CP_STARTS_WITH      = 1;
   public static final int       CP_ENDS_WITH        = 2;
   public static final int       CP_CONTAINS         = 3;
   
   //Variávels adicionadas para usar como condição em requisições com
   //multiplos recursos
   
   public static final int       AND                = 0;
   public static final int       OR                 = 1;

   public static final String    CP_ALL_STR          = "all resources"; 
   public static final String    CP_STARTS_WITH_STR  = "resources starts with ";
   public static final String    CP_ENDS_WITH_STR    = "resources ends with ";
   public static final String    CP_CONTAINS_STR     = "resources contains ";
   
   public static final String[]  CP_WHERE_CLAUSULE  =  { 
      CP_ALL_STR, CP_STARTS_WITH_STR, CP_ENDS_WITH_STR, CP_CONTAINS_STR 
   }; 
   
   private String            resourceToMatch      = null;
   private int               whereClausule        = CP_ALL;
   private RequestIdentifier reqID                = null;
   //Variáveis adicionadas para requisições com multiplos recursos
   private String[]          resourceToMatchArray      = null;
   private int               conditionClausule        = AND;      
   private boolean          isMultipleResourceRequest = false;
   
   
   public ResourceQuery(String resourceToMatch, int whereClausule) {
      this.resourceToMatch = resourceToMatch;
      this.whereClausule   = whereClausule;
      reqID = new RequestIdentifier(resourceToMatch);
   }
   
   //Construtor adicionado para requisições que precisem de
   //multiplos recursos
   public ResourceQuery(String[] resourceToMatchArray, int whereClausule, int conditionClausule ) {
      isMultipleResourceRequest = true;
      this.resourceToMatchArray = resourceToMatchArray;
      this.whereClausule   = whereClausule;
      this.conditionClausule = conditionClausule;
    
      resourceToMatch = "";
      for( int i = 0; i < resourceToMatchArray.length; i++ ) {
         resourceToMatch += resourceToMatchArray[i];
      }
      
      reqID = new RequestIdentifier(resourceToMatch);
   }
   
   
   public RequestIdentifier regenerateRequestIdentifier() {
      reqID = new RequestIdentifier(resourceToMatch);
      return reqID;
   }
   
   //LEITURA
   public String getResourceToMatch() {
      return resourceToMatch;
   }
   
   public int getWhereClausule() {
      return whereClausule;
   }
   
   public RequestIdentifier getRequestIdentifier() {
      return reqID;
   }
   //getters adicionados para receber a condição em multiplas requisições
   public int getConditionClausule() {
      return conditionClausule;
   }
   
   public String[] getResourceToMatchArray() {
      return resourceToMatchArray;
   }
   
   public boolean isMultipleResourceRequest() {
      return isMultipleResourceRequest;
   }
   
   //IMPRIME
   public String toString() {
      String resQueryStr = "\nResource Query: " + CP_WHERE_CLAUSULE[whereClausule] + "\"" + resourceToMatch + "\"";
      
      return resQueryStr;
   }
}
