package martin.app.bagoftask.ns;

import java.util.ArrayList;
import java.util.List;

public class NSScriptSender {
	
	public static String[] getArguments( NSScriptProperties NSProperties ) {
		int n1 = NSProperties.getNMin();
		
		List arguments = new ArrayList();
		
		while( n1 <= NSProperties.getNMax() ) {
			int n2 = n1;
			int n2Max = n1 + NSProperties.getNStep();
			
			while ( n2 <= n2Max ) {
				int n = n1 * n2;
			    //int nSum= n1 + n2;
			    //int nMedium= nSum / 2; // Conferir comando no SH
			    int p = NSProperties.getPMin();
			    
			    while( p <= NSProperties.getPMax() ) {			    	
			    	double pFloat = p / 10.0; // Conferir comando no SH
			    	int round = NSProperties.getRoundMin(); 
			    	
			    	while( round <= NSProperties.getRoundMax() ) { 
			    		/* Passar para o novo script NS as seguintes variáveis
			    		 * n1, n2, round, pFloat
			    		 *  */
			    		arguments.add( new String( n  + " " +
			    				                   n1 + " " +
			    				                   n2 + " " +
			    				                   round + " " +
			    				                   pFloat + " " +
			    				                   NSProperties.getSimScript() + " "
			    				                   ));			    		
			    		round++;
			    		
			    	}
			    	p += NSProperties.getPStep();			    	
			    }
			    n2 += NSProperties.getNStep();
			}
			n1 += NSProperties.getNStep();
		}
		return getStringArray( arguments );
	}

	private static String[] getStringArray( List arguments ) {
		String[] str = new String[ arguments.size() ];
		
		for( int i = 0; i < str.length; i++ ) {
			str[i] = (String) arguments.get(i);
		}
		return str;
	}

	public static int getNumOfJobs(NSScriptProperties NSProperties) {
		int numOfJobs = 0;
		int n1 = NSProperties.getNMin();
		while( n1 <= NSProperties.getNMax() ) {
			int n2 = n1;
			int n2Max = n1 + NSProperties.getNStep();
			while ( n2 <= n2Max ) {
			    int p = NSProperties.getPMin();
			    while( p <= NSProperties.getPMax() ) {			    	
			    	int round = NSProperties.getRoundMin();
			    	while( round <= NSProperties.getRoundMax() ) { 
			    		round++;
			    		numOfJobs++; // Conta o numero de jobs
			    	}
			    	p += NSProperties.getPStep();			    	
			    }
			    n2 += NSProperties.getNStep();
			}
			n1 += NSProperties.getNStep();
		}
		return numOfJobs;
	}
}
