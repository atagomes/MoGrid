package martin.mogrid.entity.proxy.registry.globus;

import javax.swing.JFrame;
import javax.swing.JTextArea;

public class ProxyResourceDescriptorCmd {
   
   public static void main(String[] args) {
      String description = System.getProperty( "description" );
      String identifier = System.getProperty( "identifier" );
      String linuxScriptPath = System.getProperty( "linuxscriptpath" );
      String unixScriptPath = System.getProperty( "unixscriptpath" );
      String[] keywords = System.getProperty( "keywords" ).split(",");      
      
      /*ProxyResourceDescriptor resourceDescriptor = new ProxyResourceDescriptor();
      resourceDescriptor.setDescription( description );
      resourceDescriptor.setIdentifier( identifier );
      resourceDescriptor.setLinuxScriptPath( linuxScriptPath );
      resourceDescriptor.setUnixScriptPath( unixScriptPath );
      resourceDescriptor.setKeywords( keywords );*/
      ProxyResourceDescriptor resourceDescriptor = new ProxyResourceDescriptor( identifier, description, keywords, linuxScriptPath, unixScriptPath );
      
      new ProxyResourceDescriptorCmd().showResource( resourceDescriptor );
      
   }
   
   private void showResource( ProxyResourceDescriptor resource ) {
      JFrame frame = new JFrame();
      JTextArea textArea = new JTextArea( 10, 10 );
      textArea.append( "Identifier: " + resource.getIdentifier() +"\n" );
      textArea.append( "Description: " + resource.getDescription() +"\n" );
      textArea.append( "Linux Script Path: " + resource.getLinuxScriptPath() +"\n" );
      textArea.append( "Unix Script Path: " + resource.getUnixScriptPath() +"\n" );
      textArea.append( "Key Words: " );
      
      String[] keyWords = resource.getKeywords();
      for( int i = 0; i < keyWords.length; i++ ) {
         textArea.append( keyWords[i] + "\n            " );
      }
      
      frame.getContentPane().add( textArea );
      frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
      frame.pack();
      frame.setVisible( true );
   }

}
