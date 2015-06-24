/*
 * Created on 02/08/2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package martin.mogrid.common.util;

import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class MoGridImage {

   private static final String MOGRID_IMAGE = "/resource/image/"; // o separador corresponde ao de uma URL - por isso "/"

   public static ImageIcon create(String filename) {
      ImageIcon icon = null;
      URL imageURL = MoGridImage.class.getResource(MOGRID_IMAGE + filename);
      if (imageURL != null) {
         icon = new ImageIcon(imageURL);
      } 
      return icon;
   }

   // configura o logo do CSV como icone do frame passado como parametro
   public static void setFrameImageIcon(JFrame frame, String filename) {
      if (frame != null) {
         ImageIcon csvIcon = MoGridImage.create(filename);
         if (csvIcon != null) {
            frame.setIconImage(csvIcon.getImage());
         }
      }
   }

}
