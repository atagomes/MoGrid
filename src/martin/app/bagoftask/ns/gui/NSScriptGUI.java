package martin.app.bagoftask.ns.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import martin.app.bagoftask.ns.NSMaster;
import martin.app.bagoftask.ns.NSScriptProperties;

public class NSScriptGUI {
   
   private JTextField simScriptTextField;
   private JTextField nMinTextField;
   private JTextField nMaxTextField;
   private JTextField nStepTextField;
   private JTextField pMinTextField;
   private JTextField pMaxTextField;
   private JTextField pStepTextField;
   private JTextField roundMinTextField;
   private JTextField roundMaxTextField;
   private JButton    simScriptBrowserButton;
   private JButton    sendScriptButton;
   private JButton    cancelButton;
   
   private JFrame frame;
   private NSScriptProperties scriptDispatcher;
   
   public static void main( String[] args ) {
      new NSScriptGUI().showGUI();
   }
   
   public void showGUI() {
      frame = new JFrame( " NetWork Simulator Scripts Dispatcher " );      
      
      simScriptTextField = new JTextField( 10 );
      simScriptTextField.setText( "/home/bfbastos/workspace/MoGrid/NSFiles/grade_sim.tcl" );
      
      simScriptBrowserButton = new JButton( "Browser" );
      simScriptBrowserButton.addActionListener( new SimScriptBrowserListener() );
      
      JPanel simScriptBrowserPanel = new JPanel();
      simScriptBrowserPanel.add( simScriptTextField );
      simScriptBrowserPanel.add( simScriptBrowserButton );
      
      JPanel simPanel  = new JPanel( );
      simPanel.add( simScriptBrowserPanel );
      simPanel.setLayout( new BoxLayout( simPanel, BoxLayout.Y_AXIS ) );
      simPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder( "Sim Script Path" ),
            BorderFactory.createEmptyBorder(5,5,5,5)));
      
      nMinTextField = new JTextField( 4 );
      JLabel nMinLabel = new JLabel( "N Min" );
      nMinLabel.setLabelFor( nMinTextField );
      nMinTextField.setText("3");
            
      nMaxTextField = new JTextField( 4 ); 
      JLabel nMaxLabel = new JLabel( "N Max" );
      nMaxLabel.setLabelFor( nMaxTextField );
      nMaxTextField.setText("3");
      
      nStepTextField = new JTextField( 4 ); 
      JLabel nStepLabel = new JLabel( "N Step" );
      nStepLabel.setLabelFor( nStepTextField );
      nStepTextField.setText("1");
      
      JPanel nMinPanel = new JPanel();
      nMinPanel.add( nMinLabel );
      nMinPanel.add( nMinTextField );
      nMinPanel.setLayout( new BoxLayout( nMinPanel, BoxLayout.Y_AXIS ) );
      
      JPanel nMaxPanel = new JPanel();
      nMaxPanel.add( nMaxLabel );
      nMaxPanel.add( nMaxTextField );
      nMaxPanel.setLayout( new BoxLayout( nMaxPanel, BoxLayout.Y_AXIS ) );
      
      JPanel nStepPanel = new JPanel();
      nStepPanel.add( nStepLabel );
      nStepPanel.add( nStepTextField );
      nStepPanel.setLayout( new BoxLayout( nStepPanel, BoxLayout.Y_AXIS ) );
      
      JPanel nPanel = new JPanel();
      nPanel.add( nMinPanel );
      nPanel.add( nMaxPanel );
      nPanel.add( nStepPanel );   
      nPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder( "N Attributes" ),
            BorderFactory.createEmptyBorder(5,5,5,5)));
      
      pMinTextField  = new JTextField( 4 );
      JLabel pMinLabel = new JLabel( "P Min" );
      pMinLabel.setLabelFor( pMinTextField );
      pMinTextField.setText("2");
      
      pMaxTextField  = new JTextField( 4 );
      JLabel pMaxLabel = new JLabel( "P Max" );
      pMaxLabel.setLabelFor( pMaxTextField );
      pMaxTextField.setText("8");
      
      pStepTextField = new JTextField( 4 );
      JLabel pStepLabel = new JLabel( "P Step" );
      pStepLabel.setLabelFor( pStepTextField );
      pStepTextField.setText("2");
      
      JPanel pMinPanel = new JPanel();
      pMinPanel.add( pMinLabel );
      pMinPanel.add( pMinTextField );
      pMinPanel.setLayout( new BoxLayout( pMinPanel, BoxLayout.Y_AXIS ) );
      
      JPanel pMaxPanel = new JPanel();
      pMaxPanel.add( pMaxLabel );
      pMaxPanel.add( pMaxTextField );
      pMaxPanel.setLayout( new BoxLayout( pMaxPanel, BoxLayout.Y_AXIS ) );
      
      JPanel pStepPanel = new JPanel();
      pStepPanel.add( pStepLabel );
      pStepPanel.add( pStepTextField );
      pStepPanel.setLayout( new BoxLayout( pStepPanel, BoxLayout.Y_AXIS ) );
      
      JPanel pPanel = new JPanel();
      pPanel.add( pMinPanel );
      pPanel.add( pMaxPanel );
      pPanel.add( pStepPanel ); 
      pPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder( "P Attributes" ),
            BorderFactory.createEmptyBorder(5,5,5,5)));
      
      roundMinTextField = new JTextField( 4 );
      JLabel roundMinLabel = new JLabel( "Round Min" );
      roundMinLabel.setLabelFor( roundMinTextField );
      roundMinTextField.setText( "1" );
      
      roundMaxTextField = new JTextField( 4 );
      JLabel roundMaxLabel = new JLabel( "Round Max" );
      roundMaxLabel.setLabelFor( roundMaxTextField );
      roundMaxTextField.setText( "5" );
      
      JPanel roundPanel = new JPanel();
      roundPanel.add( roundMinLabel );
      roundPanel.add( roundMinTextField );
      roundPanel.add( roundMaxLabel );
      roundPanel.add( roundMaxTextField );
      roundPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder( "Rounds" ),
            BorderFactory.createEmptyBorder(5,5,5,5)));
      
      sendScriptButton = new JButton( "Send" );
      sendScriptButton.addActionListener( new SendScriptListener() );
      
      cancelButton = new JButton( "Cancel" );
      cancelButton.addActionListener( new CancelListener() );
      
      JPanel buttonPanel = new JPanel();
      buttonPanel.add( sendScriptButton );
      buttonPanel.add( cancelButton );
      
      JPanel mainPanel = new JPanel();
      mainPanel.add( simPanel );
      mainPanel.add( nPanel );
      mainPanel.add( pPanel );
      mainPanel.add( roundPanel );
      mainPanel.add( buttonPanel );
      mainPanel.setLayout( new BoxLayout( mainPanel, BoxLayout.Y_AXIS ) );
      
      frame.getContentPane().add( BorderLayout.NORTH, mainPanel );
      frame.pack();
      frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      frame.setVisible( true );
      
      
   }
   
   class SimScriptBrowserListener implements ActionListener {
      public void actionPerformed( ActionEvent e ) {
         JFileChooser fileChooser = new JFileChooser( );
         int returnVal = fileChooser.showOpenDialog( fileChooser );
         
         if (returnVal == JFileChooser.APPROVE_OPTION) {
           File file = fileChooser.getSelectedFile();
           simScriptTextField.setText( file.getAbsolutePath() );
         }
      }
   }
   
   class SendScriptListener implements ActionListener {
      public void actionPerformed( ActionEvent e ) {
         scriptDispatcher = new NSScriptProperties();
         
         if( !checkFields() )
            return;
         
         File simScript =  new File ( simScriptTextField.getText() );
         int nMax = Integer.parseInt( nMaxTextField.getText() );
         int nMin = Integer.parseInt( nMinTextField.getText() );
         int nStep = Integer.parseInt( nStepTextField.getText() );
         int pMin = Integer.parseInt( pMinTextField.getText() );
         int pMax = Integer.parseInt( pMaxTextField.getText() );
         int pStep = Integer.parseInt( pStepTextField.getText() );
         int roundMin = Integer.parseInt( roundMinTextField.getText() );
         int roundMax = Integer.parseInt( roundMaxTextField.getText() );
         
         scriptDispatcher.setSimScript( simScript );
         scriptDispatcher.setNMax( nMax );
         scriptDispatcher.setNMin( nMin );
         scriptDispatcher.setNStep( nStep );
         scriptDispatcher.setPMin( pMin );
         scriptDispatcher.setPMax( pMax );;
         scriptDispatcher.setPStep( pStep );
         scriptDispatcher.setRoundMin( roundMin );
         scriptDispatcher.setRoundMax( roundMax );
         
         new NSMaster( scriptDispatcher ).runRounds();
         sendScriptButton.setEnabled( false );
      }

      private boolean checkFields() {
         
         if( simScriptTextField.getText().equals("") ) { 
            showMessage();
            return false;
         }
         else if ( nMaxTextField.getText().equals("") ) {
            showMessage();
            return false;
         }
         else if ( nMinTextField.getText().equals("") ) {
            showMessage();
            return false;
         }
         else if ( nStepTextField.getText().equals("") ) {
            showMessage();
            return false;
         }
         else if ( pMinTextField.getText().equals("") ) {
            showMessage();
            return false;
         }
         else if ( pMaxTextField.getText().equals("") ) { 
            showMessage();
            return false;
         }
         else if ( pStepTextField.getText().equals("") ) {
            showMessage();
            return false;
         }
         else if ( roundMaxTextField.getText().equals("") ) {
            showMessage(); 
            return false;
         }
         return true;
      }
      
      private void showMessage() {
         JOptionPane.showMessageDialog( null, "Fill all fields!" );
      }
   }
   
   class CancelListener implements ActionListener {
      public void actionPerformed( ActionEvent e ) {
         
      }
   }
}
