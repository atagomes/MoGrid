package martin.app.bagoftask.proxy.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import martin.mogrid.common.resource.ResourceIdentifier;
import martin.mogrid.entity.dispatcher.globus.ProxyCollaboratorDispatcherFactory;
import martin.mogrid.entity.proxy.registry.globus.ProxyResource;
import martin.mogrid.entity.proxy.registry.globus.ProxyResourceDescriptor;
import martin.mogrid.entity.proxy.registry.globus.ProxyResourceRegistry;
import martin.mogrid.entity.proxy.registry.globus.ProxyScriptDispatcher;
import martin.mogrid.p2pdl.api.MogridApplicationFacade;
import martin.mogrid.p2pdl.api.RequestIdentifier;
import martin.mogrid.service.contextlistener.ContextListener;
import martin.mogrid.service.contextlistener.ContextListenerException;
import martin.mogrid.service.monitor.DeviceContextHistory;
import martin.mogrid.service.monitor.globus.GlobusDeviceContext;
import martin.mogrid.tl.asl.proxy.ProxyAdaptationSublayer;

public class ProxyResourceDescriptorGui implements MogridApplicationFacade {
   
   
   private JFrame frame;
   private JTextField identifierTextField;
   private JTextField descriptionTextField;
   private JTextField linuxScriptTextField;
   private JTextField unixScriptTextField;
   private JTextField keywordTextField;
   private DefaultListModel listModel;
   private JList list;
   private JButton listAddButton;
   private JButton listRemoveButton;
   private JButton linuxScriptLocationButton;
   private JButton unixScriptLocationButton;
   private JButton confirmButton;
   private JButton cancelButton;
   private ProxyAdaptationSublayer gridJobDiscovery;
   private ProxyResourceRegistry proxyRegistry;
   private ContextListener contextListener;
   
   public static void main( String[] args ) {
      new ProxyResourceDescriptorGui().showGui();
   }
   
   private void showMessage() {
      JOptionPane.showMessageDialog( null, "Fill all field" );
   }
   
   public boolean checkEmpty() {
      if( identifierTextField.getText().trim().equals("") ) {
         showMessage();
         return false;
      }
      if( descriptionTextField.getText().trim().equals("") ) {
         showMessage();
         return false;
      }
      if( linuxScriptTextField.getText().trim().equals("") ) {
         showMessage();
         return false;
      }
      if( listModel.isEmpty() ) {
         showMessage();
         return false;
      }
      return true;
   }
   
   
   public void showGui() {
      
      frame = new JFrame( "Proxy Resource Descriptor" );
      gridJobDiscovery = new ProxyAdaptationSublayer( this );
      gridJobDiscovery.registerTaskDispatcherFactory(new ProxyCollaboratorDispatcherFactory());
      proxyRegistry = ProxyResourceRegistry.getInstance();
      
      try {
         contextListener = ContextListener.getInstance();
      } catch (ContextListenerException e) {
         e.printStackTrace();
      }
      
      confirmButton = new JButton( "Confirm" );
      cancelButton = new JButton( "Cancel" );
      
      JPanel controlPanel = new JPanel();
      controlPanel.add( confirmButton );
      controlPanel.add( cancelButton );
      
      confirmButton.addActionListener( new ConfirmListener() );
      cancelButton.addActionListener( new CancelListener() );
      
      identifierTextField = new JTextField( 10 );
      //identifierTextField.setText( "java" );
      identifierTextField.setText( "ns" );
      
      descriptionTextField = new JTextField( 10 );
      //descriptionTextField .setText( "compile" );
      descriptionTextField .setText( "network simulator" );
      
      JLabel identifierLabel = new JLabel( "Identifier:" );
      JLabel descriptionLabel = new JLabel( "Description:" );
      
      identifierLabel.setLabelFor( identifierTextField );
      descriptionLabel.setLabelFor( descriptionTextField );
      
      JPanel textFieldPanel = new JPanel( );
      textFieldPanel.add( identifierLabel );
      textFieldPanel.add( identifierTextField );
      textFieldPanel.add( descriptionLabel );
      textFieldPanel.add( descriptionTextField );
      textFieldPanel.setLayout( new BoxLayout( textFieldPanel, BoxLayout.Y_AXIS ) );
      
      keywordTextField = new JTextField( 10 );
      JLabel keyWordLabel = new JLabel( "Key Word:" );
      keyWordLabel.setLabelFor( keywordTextField );
      
      listModel = new DefaultListModel();
      //listModel.addElement( "jre" );
      //listModel.addElement( "jdk" );
      listModel.addElement( "ns" );
      
      list = new JList( listModel );
      
      list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      list.setSelectedIndex(0);
      list.addListSelectionListener( new EnableButtonListener() );
      list.setVisibleRowCount(5);
      JScrollPane listScrollPane = new JScrollPane(list);
      
      listAddButton = new JButton( "Add" );
      listRemoveButton = new JButton( "Remove" );
      
      listAddButton.addActionListener( new ListAddListener() );
      listRemoveButton.addActionListener( new ListRemoveListener() );
      listRemoveButton.setEnabled( false );
      
      JPanel buttonPanel = new JPanel();
      buttonPanel.add( listAddButton );
      buttonPanel.add( listRemoveButton );
      
      JPanel keyWordPanel = new JPanel( );
      keyWordPanel.add( keyWordLabel );
      keyWordPanel.add( keywordTextField );
      keyWordPanel.add( listScrollPane );
      keyWordPanel.add( buttonPanel );
      keyWordPanel.setLayout( new BoxLayout( keyWordPanel, BoxLayout.Y_AXIS ) );
      
      linuxScriptTextField = new JTextField( 10 );
      //linuxScriptTextField.setText( "/home/bfbastos/workspace/MoGrid.Multihop/ProxyScripts/javapath.sh" );
      linuxScriptTextField.setText( "/home/bfbastos/workspace/MoGrid/ProxyScripts/LinuxVersion/nspath.sh" );
      
      unixScriptTextField = new JTextField( 10 );
      //unixScriptTextField.setText( "/home/bfbastos/workspace/MoGrid.Multihop/ProxyScripts/javapath.sh" );
      unixScriptTextField.setText( "/home/bfbastos/workspace/MoGrid/ProxyScripts/LinuxVersion/nspath.sh" );
      
      //JLabel scriptPathLabel = new JLabel( "Script Location:" );
      JLabel linuxLabel = new JLabel( "Linux:" );
      JLabel unixLabel = new JLabel( "Unix:" );
      linuxLabel.setLabelFor( linuxScriptTextField );
      unixLabel.setLabelFor( unixScriptTextField );
      
      linuxScriptLocationButton = new JButton( "Browser" );
      linuxScriptLocationButton.addActionListener( new LinuxScriptLocationListener() );
      
      unixScriptLocationButton = new JButton( "Browser" );
      unixScriptLocationButton.addActionListener( new UnixScriptLocationListener() );
      
      JPanel linuxScriptPanel = new JPanel();
      linuxScriptPanel.add( linuxLabel );
      linuxScriptPanel.add( linuxScriptTextField );
      linuxScriptPanel.add( linuxScriptLocationButton );

      JPanel unixScriptPanel = new JPanel();
      unixScriptPanel.add( unixLabel );
      unixScriptPanel.add( unixScriptTextField );
      unixScriptPanel.add( unixScriptLocationButton );

      JPanel scriptPanel = new JPanel();
      //scriptPanel.add( scriptPathLabel );
      scriptPanel.add( linuxScriptPanel );
      scriptPanel.add( unixScriptPanel );
      scriptPanel.setLayout( new BoxLayout( scriptPanel, BoxLayout.Y_AXIS ) );
      scriptPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder( "Script Location" ),
            BorderFactory.createEmptyBorder(5,5,5,5)));
      
      JPanel mainPanel = new JPanel();
      mainPanel.add( textFieldPanel );
      mainPanel.add( keyWordPanel );
      mainPanel.add( scriptPanel );
      mainPanel.add( controlPanel );
      mainPanel.setLayout( new BoxLayout( mainPanel, BoxLayout.Y_AXIS ) );
      
      frame.getContentPane().add( BorderLayout.NORTH, mainPanel );
      frame.pack();
      frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      frame.setVisible( true );
   } 
   
   class ListAddListener implements ActionListener {

      public void actionPerformed(ActionEvent e) {
         if( !keywordTextField.getText().equals( "" ) ) {
            listModel.addElement( keywordTextField.getText() );
            keywordTextField.setText(""); 
            keywordTextField.requestFocus();
         }
      }
      
   }
   
   class ListRemoveListener implements ActionListener {

      public void actionPerformed(ActionEvent e) {
         int index = list.getSelectedIndex();
         
         if ( index < 0 )
            return;
         
         listModel.remove(index);

         int size = listModel.getSize();

         if (size == 0) {
            listRemoveButton.setEnabled(false);

         } else { 
             if (index == listModel.getSize()) {
                 index--;
             }

         list.setSelectedIndex(index);
         list.ensureIndexIsVisible(index);
         }
      }      
   }
   
   class EnableButtonListener implements ListSelectionListener {

      public void valueChanged(ListSelectionEvent e) {
         if (e.getValueIsAdjusting() == false) {

            if (list.getSelectedIndex() == -1) {
                listRemoveButton.setEnabled(false);

            } else {
               listRemoveButton.setEnabled(true);
            }
        }
      }
      
   }
   
   class LinuxScriptLocationListener implements ActionListener {

      public void actionPerformed(ActionEvent e) {
         JFileChooser fileChooser = new JFileChooser( );
         int returnVal = fileChooser.showOpenDialog( fileChooser );
         if (returnVal == JFileChooser.APPROVE_OPTION) {
           File file = fileChooser.getSelectedFile();
           linuxScriptTextField.setText( file.getAbsolutePath() );
         }         
      }      
   }
   
   class UnixScriptLocationListener implements ActionListener {

      public void actionPerformed(ActionEvent e) {
         JFileChooser fileChooser = new JFileChooser( );
         int returnVal = fileChooser.showOpenDialog( fileChooser );
         if (returnVal == JFileChooser.APPROVE_OPTION) {
           File file = fileChooser.getSelectedFile();
           unixScriptTextField.setText( file.getAbsolutePath() );
         }
      }   
   }
   
   class ConfirmListener implements ActionListener {  

      public void actionPerformed(ActionEvent e) {
         if( !checkEmpty() )
            return;
        
         final String[] keyWords = new String[ listModel.getSize() ];
         for( int i = 0; i< keyWords.length; i++ ) {
            keyWords[i] = (String) listModel.elementAt( i );
         }         
         
         /*final ProxyResourceDescriptor resourceDescriptor = new ProxyResourceDescriptor( identifierTextField.getText(),
                                                                                          descriptionTextField.getText(), 
                                                                                          keyWords,
                                                                                          linuxScriptTextField.getText(),
                                                                                          unixScriptTextField.getText() ) ;*/
         
         ResourceIdentifier resID = gridJobDiscovery.register( identifierTextField.getText(),
                                                               descriptionTextField.getText(),
                                                               keyWords,
                                                               linuxScriptTextField.getText(),
                                                               unixScriptTextField.getText() );
         final ProxyResource proxyResource = new ProxyResource();
         
         DeviceContextHistory gridContext = contextListener.getDeviceContextHistory();
         
         Enumeration          IPAddress   = gridContext.keys();
         while( IPAddress.hasMoreElements() ) {
        	 final String IP = IPAddress.nextElement().toString();
        	 new Thread( new Runnable() {
        		 public void run() {
                    ProxyResourceDescriptor resourceDescriptor = new ProxyResourceDescriptor( identifierTextField.getText(),
                    descriptionTextField.getText(), 
                    keyWords,
                    linuxScriptTextField.getText(),
                    unixScriptTextField.getText() );
                    
        			     GlobusDeviceContext devContext;
                    synchronized( contextListener ) {
                       devContext = (GlobusDeviceContext)contextListener.getDeviceContext(IP);
                    }
        	           //String path = new ProxyScriptDispatcher().getPath( devContext, resourceDescriptor );
        	           
        	           ProxyScriptDispatcher dispatcher = new ProxyScriptDispatcher();
        	           
        	           String path = dispatcher.getPath( devContext, resourceDescriptor );
        	           
        	           if( path != null ) {
        	              synchronized( proxyResource ) {
                          resourceDescriptor.setPath( path );
        	            	  proxyResource.put( IP, resourceDescriptor );
        	              }
        	           }
        		 }
        	 }).start();
        	 
           /*String IP = IPAddress.nextElement().toString(); 
           GlobusDeviceContext devContext = (GlobusDeviceContext)contextListener.getDeviceContext(IP);
           //String path = new ProxyScriptDispatcher().getPath( devContext, resourceDescriptor );
           
           ProxyScriptDispatcher dispatcher = new ProxyScriptDispatcher();
           
           String path = dispatcher.getPath( devContext, resourceDescriptor );
           
           if( path != null ) {
              resourceDescriptor.setPath( path );
              proxyResource.put( IP, resourceDescriptor );
           }*/
         }
         proxyRegistry.put( resID, proxyResource );
      }    
   }
   
   class CancelListener implements ActionListener {
      
      public void actionPerformed( ActionEvent e ) {
         identifierTextField.setText("java");
         descriptionTextField.setText("compile");
         linuxScriptTextField.setText("/home/bfbastos/workspace/MoGrid/ProxyScripts/LinuxVersion/javapath.sh");
         keywordTextField.setText("");
         listModel.removeAllElements();
         listModel.add(0,"jre");
      }
   }

   public void handleMogridResource(RequestIdentifier reqID, Object resource) {
      // TODO Auto-generated method stub
      
   }
}