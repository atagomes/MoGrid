/*
 * Created on 27/04/2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package martin.mogrid.tl.asl.bagoftask;

import martin.mogrid.common.resource.ResourceQuery;
import martin.mogrid.p2pdl.api.RequestProfile;

public class BoTPendingRequest {

   private ResourceQuery  resourceQuery = null;
   private RequestProfile reqProfile    = null;
   
   public BoTPendingRequest(ResourceQuery resourceQuery, RequestProfile reqProfile) {
      this.resourceQuery = resourceQuery;
      this.reqProfile    = reqProfile;
   }
   
   public ResourceQuery getResourceQuery(){
      return resourceQuery;
   }
   
   public RequestProfile getRequestProfile() {
      return reqProfile;
   }

}
