package martin.mogrid.globus.service.mds;

import java.util.StringTokenizer;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

public class CpuInformation {

   public float getFreeCpuPercent(Attributes attrs) {
      StringTokenizer st;
      Attribute at;
      String str;
      int cpuPercent = getCpuTotalFree( attrs );
        
      at = attrs.get( MdsConstants.CPU_TOTAL_COUNT );
      try{	
         str = at.toString();
      }catch(Exception e){
         return 0;
      }
      st = new StringTokenizer(str,":");
      st.nextToken();
      int cpuCount = Integer.parseInt(st.nextToken().trim());
	    
      return ( cpuPercent / cpuCount );
   }

   public float getFreeCpuHz(Attributes attrs){
      StringTokenizer st;
      String str;
    	Attribute at;
      int cpuPercent = getCpuTotalFree( attrs );
      
      at = attrs.get( MdsConstants.CPU_SPEED_MHZ );
      
    	try{	
    	   str = at.toString();
    	}catch(Exception e){
    	   return 0;
    	}
    	st = new StringTokenizer(str,":");
    	st.nextToken();
    	int hz = Integer.parseInt(st.nextToken().trim());
    	return (float)cpuPercent*hz/100;    	 	
    }
    
    private int getCpuTotalFree( Attributes attrs ) {
       StringTokenizer st;
       String str;
       Attribute at;
       at = attrs.get( MdsConstants.CPU_TOTAL_FREE );
       try{  
          str = at.toString();
       }catch(Exception e){
          return 0;
       }
       st = new StringTokenizer(str,":");
       st.nextToken();
       int cpuPercent = Integer.parseInt(st.nextToken().trim());
       
       return cpuPercent;
    }
}

