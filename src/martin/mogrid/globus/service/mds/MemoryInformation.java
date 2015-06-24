package martin.mogrid.globus.service.mds;


import java.util.StringTokenizer;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

public class MemoryInformation {

	public float getFreeMemoryMb(Attributes attrs){
      StringTokenizer st;
		String totalFree;
		Attribute at;
		at = attrs.get( MdsConstants.MEMORY_TOTAL_FREE_MB );
		try{
		   totalFree = at.toString();
	   }catch(Exception e){
	      return 0;
	   }
	   st = new StringTokenizer(totalFree,":");
	   st.nextToken();
	    
	   float absFreeMem = Float.parseFloat(st.nextToken().trim());
		return absFreeMem;
	}

	public float getFreeMemoryPercent(Attributes attrs){
      StringTokenizer st;
      String str;
		Attribute at;
		at = attrs.get( MdsConstants.MEMORY_TOTAL_SIZE_MB );
		try{
		   str = at.toString();
		}catch(Exception e){
		   return 0;
		}
		st = new StringTokenizer(str,":");
		st.nextToken();
		float total = Float.parseFloat(st.nextToken().trim());
	    
		float free = getFreeMemoryMb( attrs );
       
		float totalFree = (float)free*100/total;
		return totalFree;
	}
}

