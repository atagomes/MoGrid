package martin.mogrid.globus.service.mds;

import java.util.StringTokenizer;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

public class OperationalSystemInformation {

   public String getSOName(Attributes attrs) {
    
      String str;
      Attribute at;
      at = attrs.get( MdsConstants.OPERATIONAL_SYSTEM_NAME );
      try{  
         str = at.toString();
      }catch(Exception e){
         return null;
      }
      StringTokenizer st = new StringTokenizer(str,":");
      st.nextToken();
      String soName = st.nextToken().trim();
       
      return soName;
   }
}
