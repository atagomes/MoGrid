/*
 * Created on 16/08/2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package martin.mogrid.common.util;

public class MoGridString {

   //Parameters:
   //   string - the string is splited around matches of the given regular expression (regex)
   //   regex  - the delimiting regular expression 
   //Returns:
   //    The array of strings computed by splitting the "string" around matches of the given regular expression
   public static String[] split(String string, String regex) {
      String[] result = new String[0];
      
      if ( string!=null && !string.trim().equals("") && regex!=null ) {
         result = string.split(regex);
      }
      return result;
   }
   
   public static String replaceAll(String string, String regex, String replacement) {
      String result = null;
      
      if ( string!=null && !string.trim().equals("") && regex!=null && replacement!=null ) {
         result = string.replaceAll(regex, replacement);
      }
      return result;
   }
}
