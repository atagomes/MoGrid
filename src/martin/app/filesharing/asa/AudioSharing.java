
package martin.app.filesharing.asa;

import java.io.File;
import java.net.URL;
import java.util.Vector;

import javax.swing.JFrame;

import martin.app.filesharing.asa.gui.AudioSharingGUI;
import martin.mogrid.p2pdl.api.MogridApplicationFacade;
import martin.mogrid.p2pdl.api.RequestIdentifier;
import martin.mogrid.tl.asl.filesharing.FileSharingAdaptationSublayer;

import org.apache.log4j.Logger;


/**
 * @author luciana
 *
 * Created on 22/06/2005
 */
public class AudioSharing implements MogridApplicationFacade {
   //Manutencao do arquivo de log do servico
   private static final Logger logger = Logger.getLogger(AudioSharing.class);    

   private FileSharingAdaptationSublayer moGridDiscovery = null;
   private AudioSharingGUI               audioGUI        = null;
   
   public AudioSharing() {
      JFrame.setDefaultLookAndFeelDecorated(true);

      audioGUI        = new AudioSharingGUI(this);      
      moGridDiscovery = new FileSharingAdaptationSublayer(this);
   }

   public void handleMogridResource(RequestIdentifier reqID, Object audioFiles) {
      audioGUI.addAudioFile(audioFiles);
   }
      
   public void findFilesInMogrid(String audioQuery) {
      moGridDiscovery.getFile(audioQuery);
   }
   
   public void registerFilesInMogrid(Object audioFiles) {
      if ( audioFiles instanceof File ) {
         moGridDiscovery.registerFile( ((File) audioFiles).toString() );
      } else if ( audioFiles instanceof URL ) {
         moGridDiscovery.registerFile( ((URL) audioFiles).toString() );
      }
   }
   
   public void deregisterFilesInMogrid(Vector audioFiles) {
      for ( int i=0; i<audioFiles.size(); i++ ) {
         if ( audioFiles.elementAt(i) instanceof File ) {
            moGridDiscovery.deregisterFile( ((File) audioFiles.elementAt(i)).toString() );
         } else if ( audioFiles.elementAt(i) instanceof URL ) {
            moGridDiscovery.deregisterFile( ((URL) audioFiles.elementAt(i)).toString() );
         }
      }
   }
   
   public void finalizeAudioSharing() {
      moGridDiscovery.finalize();
   }
      
   //Execucao da app
   public static void main(String[] args) {
      new AudioSharing();
   }

}
