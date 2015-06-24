package martin.mogrid.common.network;

////////////////////////////////////////////////////////////////////////////
// Program: copyURL.java
// Author: Anil Hemrajani (anil@patriot.net)
// Purpose: Utility for copying files from the Internet to local disk
// Example: 1. java copyURL http://www.patriot.net/users/anil/resume/resume.gif

//          2. java copyURL http://www.ibm.com/index.html abcd.html
////////////////////////////////////////////////////////////////////////////

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.StringTokenizer;

public class URLFileCopy {

   public static void copy(String path, String str) {
      try {
         URL url = new URL(path);

         System.out.println("Opening connection to " + path + "...");

         URLConnection urlC = url.openConnection();
         // Copy resource to local file, use remote file

         // if no local file name specified
         InputStream is = url.openStream();
         // Print info about resource

         System.out.print("Copying resource (type: " + urlC.getContentType());
         Date date = new Date(urlC.getLastModified());
         System.out.println(", modified on: " + date.toString() + ")...");
         System.out.flush();
         
         FileOutputStream fos = null;
         if ( str != null ) {
            String localFile = null;
            // Get only file name
            StringTokenizer st = new StringTokenizer(url.getFile(), "/");
            while (st.hasMoreTokens()) {
               localFile = st.nextToken();
            }
            fos = new FileOutputStream(localFile);

         } else {
            fos = new FileOutputStream(str);
         }
         
         int oneChar, count = 0;
         while ( (oneChar = is.read()) != -1 ) {
            fos.write(oneChar);
            count++;
         }

         is.close();
         fos.close();
         System.out.println(count + " byte(s) copied");

      } catch (MalformedURLException e) {
         System.err.println(e.toString());
      
      } catch (IOException e) {
         System.err.println(e.toString());
      }

   }

}
