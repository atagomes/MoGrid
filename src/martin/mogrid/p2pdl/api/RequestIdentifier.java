/*
 * Created on 26/09/2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package martin.mogrid.p2pdl.api;

import martin.mogrid.common.util.UniversalIdentifier;

public class RequestIdentifier extends UniversalIdentifier {  
   
   /** Comment for <code>serialVersionUID</code> */
   private static final long serialVersionUID = -7627056549182790035L;

   public RequestIdentifier(String requestIdentifier) {
      super(requestIdentifier);
   }
   
   public String getRequestIdentifier() {
      return getIdentifier();
   }   

   public String getRequestSubject() {      
      return getSubject();
   }
   
}
