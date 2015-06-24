package martin.app.filesharing.asa.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;
import martin.app.filesharing.asa.AudioSharing;
import martin.mogrid.common.util.Console;
import martin.mogrid.common.util.MoGridImage;
import martin.mogrid.common.util.MoGridString;

public class AudioSharingGUI extends JFrame implements BasicPlayerListener,
                                                       ActionListener,
                                                       ItemListener     {

   //TODO Adicionar mais informacoes sobre as musicas (recurso do MP3)
   //TODO Redirecionar msgs do MoGrid para a Console
   
   /** Comment for <code>serialVersionUID</code> */
   private static final long serialVersionUID = 1L;
   
   private static final String MENU_TOOLS                 = "Tools";
   private static final String MENU_ITEM_ADD_MUSIC        = "Add Music";
   private static final String MENU_SUBITEM_ADD_FILE      = "File or Directory of Files";
   private static final String MENU_SUBITEM_ADD_URL       = "URL";
   private static final String MENU_ITEM_REMOVE_MUSIC     = "Remove Music";
   private static final String MENU_SUBITEM_REMOVE_SEL    = "Selected";
   private static final String MENU_SUBITEM_REMOVE_ALL    = "All";
   private static final String MENU_ITEM_DISCOVERY        = "Find Musics in MoGrid";
   private static final String MENU_ITEM_MOGRID_MSG       = "Show MoGrid Messages";
   private static final String MENU_ITEM_EXIT             = "Exit";

   private static final String MENU_AUDIO_CONTROLS        = "Audio Controls";
   private static final String MENU_ITEM_CONTROL_PLAY     = "Play";
   private static final String MENU_ITEM_CONTROL_PAUSE    = "Pause";
   private static final String MENU_ITEM_CONTROL_PREVIOUS = "Previous"; 
   private static final String BUTTON_CONTROL_PREVIOUS    = "<<";  
   private static final String MENU_ITEM_CONTROL_NEXT     = "Next";
   private static final String BUTTON_CONTROL_NEXT        = ">>";
   private static final String MENU_ITEM_CONTROL_RESUME   = "Resume";   
   private static final String MENU_ITEM_CONTROL_STOP     = "Stop";
   private static final String MENU_ITEM_CONTROL_LOOP     = "Loop";
   
   private static final String MENU_HELP                  = "Help";
   private static final String MENU_ITEM_ABOUT_ASA        = "About ASA";

   // Estados do player
   private static final int INIT  = 0;
   private static final int PLAY  = 1;
   private static final int PAUSE = 2;
   private static final int STOP  = 3;
   
   // Aplicacao que usa a GUI
   private AudioSharing application = null;

   // Componentes graficos
   private AudioTable  audioList               = new AudioTable();
   private JTextArea   mogridTxArea            = new JTextArea();
   private JScrollPane mogridMessageScrollPane = new JScrollPane(mogridTxArea);

   //Menus
   private JMenu menuItemAddMusic, menuItemRemoveMusic;
   private JMenuItem menuItemDiscoveryMusic, menuItemExit, menuItemPlay, menuItemPause, menuItemNext, menuItemPrevious, menuItemStop;
   private JMenuItem menuSubItemAddFile, menuSubItemAddURL, menuSubItemRemoveSelected, menuSubItemRemoveAll;
   private JCheckBoxMenuItem ckBoxMenuMogridMsg, ckBoxMenuItemLoop;
   private JButton play, pause, stop, next, previous, loop;

   private boolean         inLoop            = false;
   private int             mp3PlayerState    = INIT;
   private int             currentAudioFile  = -1;
   private BasicController basicController   = null;
   private BasicPlayer     basicPlayer       = null;
   private CustomDialog    urlDialog         = null;
   private CustomDialog    mogridDialog      = null;
   private JFrame          asaInfoFrame      = null; 
   
   public AudioSharingGUI(AudioSharing application) {
      this.application = application;
      audioList.setApplication(application);      
      
      JDialog.setDefaultLookAndFeelDecorated(true);   
      try {
         // Metal (java) look and feel:
         UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
      } catch (Exception e) {
      }

      setTitle("Audio Sharing Application (ASA)");
      MoGridImage.setFrameImageIcon(this, "playicon.gif");
      setJMenuBar(createJMenuBar());
      getContentPane().add(createInternalComponents());
      pack();
      
      //String[] javaVersion = System.getProperty("java.version").split("\\.");
      String[] javaVersion = MoGridString.split(System.getProperty("java.version"), "\\.");
      if ( javaVersion != null && Integer.parseInt(javaVersion[1]) >= 4 ) {
         setLocationRelativeTo(getParent()); // since 1.4
      }  

      setVisible(true);
      transferFocus();      

      // configura acao a ser executada qdo a GUI eh fechada
      addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent we) {
            finalizeGUI();
         }
      });

      basicPlayer = new BasicPlayer();
      basicPlayer.addBasicPlayerListener(this);
      setController(basicPlayer); 

      urlDialog    = new CustomDialog(this, "Add Audio File from URL", "Enter an Internet location to open here :", "For example : http://www.server.com:8000", "Open");
      mogridDialog = new CustomDialog(this, "Find Audio File in MoGrid", "Enter your query here :", "For example : Imagine", "Find");
            
      Console.setOutput(mogridTxArea);
   }

   private JMenuBar createJMenuBar() {
      // Create lightweight-disabled menu
      JPopupMenu.setDefaultLightWeightPopupEnabled(false);

      JMenuBar menu = new JMenuBar();
      JMenu menuTools, menuControls;

      // Tools Menu
      menuTools = createMenu(MENU_TOOLS, KeyEvent.VK_T, menu);

      menuItemAddMusic = createMenu(MENU_ITEM_ADD_MUSIC, KeyEvent.VK_A, menuTools);
      menuSubItemAddFile = createMenuItem(MENU_SUBITEM_ADD_FILE, KeyEvent.VK_F, menuItemAddMusic);
      menuSubItemAddURL = createMenuItem(MENU_SUBITEM_ADD_URL, KeyEvent.VK_U, menuItemAddMusic);

      menuItemRemoveMusic = createMenu(MENU_ITEM_REMOVE_MUSIC, KeyEvent.VK_R, menuTools);
      menuSubItemRemoveSelected = createMenuItem(MENU_SUBITEM_REMOVE_SEL, KeyEvent.VK_S, menuItemRemoveMusic);
      menuSubItemRemoveAll = createMenuItem(MENU_SUBITEM_REMOVE_ALL, KeyEvent.VK_A, menuItemRemoveMusic);

      menuTools.addSeparator();
      menuItemDiscoveryMusic = createMenuItem(MENU_ITEM_DISCOVERY, KeyEvent.VK_F, menuTools);
      ckBoxMenuMogridMsg = createCheckBoxMenuItem(MENU_ITEM_MOGRID_MSG, KeyEvent.VK_M, menuTools);      

      menuTools.addSeparator();
      menuItemExit = createMenuItem(MENU_ITEM_EXIT, KeyEvent.VK_X, menuTools);

      // Audio Controls Menu
      menuControls = createMenu(MENU_AUDIO_CONTROLS, KeyEvent.VK_A, menu);
      menuItemPlay = createMenuItem(MENU_ITEM_CONTROL_PLAY, KeyEvent.VK_P, menuControls);
      menuItemPause = createMenuItem(MENU_ITEM_CONTROL_PAUSE, KeyEvent.VK_A, menuControls);
      menuItemStop = createMenuItem(MENU_ITEM_CONTROL_STOP, KeyEvent.VK_S, menuControls);      
      menuItemPrevious = createMenuItem(MENU_ITEM_CONTROL_PREVIOUS, KeyEvent.VK_R, menuControls);
      menuItemNext = createMenuItem(MENU_ITEM_CONTROL_NEXT, KeyEvent.VK_N, menuControls);
      
      menuControls.addSeparator();
      ckBoxMenuItemLoop = createCheckBoxMenuItem(MENU_ITEM_CONTROL_LOOP, KeyEvent.VK_L, menuControls);

      // Help Menu
      JMenu help = createMenu(MENU_HELP, KeyEvent.VK_H, menu);
      createMenuItem(MENU_ITEM_ABOUT_ASA, KeyEvent.VK_A, help);
      
      return menu;
   }

   private JPanel createInternalComponents() {
      JPanel guiPanel = new JPanel(new GridBagLayout());
      GridBagConstraints gridBagCons = new GridBagConstraints();

      gridBagCons.fill = GridBagConstraints.BOTH; // redimensiona o panel na
                                                  // horizontal e vertical
      gridBagCons.weightx = 1.0; // panel assume todo o espaco horizontal extra
      gridBagCons.weighty = 1.0; // panel assume todo o espaco vertical extra
      gridBagCons.gridx = 0;

      // Audio Table List
      gridBagCons.gridy = 0;
      guiPanel.add(audioList, gridBagCons);

      // Controles de Audio
      JPanel buttonsPanel = new JPanel();
      play     = createButton(MENU_ITEM_CONTROL_PLAY, buttonsPanel);
      pause    = createButton(MENU_ITEM_CONTROL_PAUSE, buttonsPanel);
      stop     = createButton(MENU_ITEM_CONTROL_STOP, buttonsPanel);
      previous = createButton(BUTTON_CONTROL_PREVIOUS, buttonsPanel);
      next     = createButton(BUTTON_CONTROL_NEXT, buttonsPanel);
      loop     = createButton(MENU_ITEM_CONTROL_LOOP, buttonsPanel);
      buttonsPanel.setPreferredSize(new Dimension(500, 60));
      buttonsPanel.setBorder(createBorder("Audio Controls", 0, 0, 5, 0, null));
      gridBagCons.gridy = 1;
      guiPanel.add(buttonsPanel, gridBagCons);

      // Mensagens MoGrid
      mogridTxArea.setFont(new Font("Arial", Font.PLAIN, 12));
      mogridTxArea.setLineWrap(true);
      mogridTxArea.setWrapStyleWord(true);
      mogridTxArea.setEditable(false);
      mogridMessageScrollPane.setVisible(false);
      mogridMessageScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
      mogridMessageScrollPane.setPreferredSize(new Dimension(500, 100));
      mogridMessageScrollPane.setBorder(createBorder("MoGrid Messages", 5, 5, 5, 5, mogridMessageScrollPane.getBorder()));
      gridBagCons.gridy = 2;
      guiPanel.add(mogridMessageScrollPane, gridBagCons);

      return guiPanel;
   }
   
   private void discoveryFiles() {
      mogridDialog.setVisible(true);
      String query = mogridDialog.getTextValue();
      if ( query!=null && !query.trim().equals("") ) {
         application.findFilesInMogrid(query);
      }
   }
   
  
   public void addAudioFile(Object audioObject) {
      if ( audioObject != null ) {
         String audioFilePath = "";
         String audioFileName = ""; 
         try {
            if ( audioObject instanceof File ) {
               audioFilePath = ((File) audioObject).getCanonicalPath();
               audioFileName = ((File) audioObject).getName();
            } else if ( audioObject instanceof URL ) {
               audioFilePath = ((URL) audioObject).getPath();
               audioFileName = ((URL) audioObject).getFile();
            }     
            audioList.addRows(audioFilePath);
            Console.println("Adding music: " + audioFileName + " (" + audioFilePath + ")\n");
          } catch (Exception e) {
            Console.println("Erro adding music: " + e.getMessage() + "\n");
          }
      }
   }   
   
   private void addAudioUrl() {
      urlDialog.setVisible(true);
      String urlFile = urlDialog.getTextValue();      
      if ( urlFile!=null && !urlFile.trim().equals("") ) {
         try {
            URL audioUrl = new URL(urlFile);
            addAudioFile(audioUrl);
         } catch (MalformedURLException e) {
             Console.println("Erro adding music " + urlFile + "\n[ERROR] "+ e.getMessage() + "\n");
         }
      }
   }
   
   private void addAudioFiles() {   
      FileChooserFilter filter = new FileChooserFilter();
      filter.addExtension("mp3");
      filter.setDescription("MP3 Files");
      
      JFileChooser fc = new JFileChooser();
      fc.setDialogTitle( "Add Audio Files or Directory" );      
      fc.setApproveButtonText("Add Files");
      fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
      fc.setMultiSelectionEnabled(true);
      fc.setFileFilter(filter);
      try {
         fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
      } catch (SecurityException ex) { } 
      
      int returnVal = fc.showOpenDialog(this);
      if( returnVal == JFileChooser.APPROVE_OPTION ) {
         File[] path = fc.getSelectedFiles(); 
         for ( int i=0; i<path.length; i++ ) {
            addAudioFile(path[i]);
         }
      }
   }   
   
   // Metodo da interface ActionListener
   public void actionPerformed(ActionEvent event) {
      String strCmd = event.getActionCommand();

      if (strCmd == null) {
         Object objSource = event.getSource();
         if (objSource instanceof JMenuItem || objSource instanceof JButton) {
            strCmd = ((AbstractButton) objSource).getActionCommand();
         }
      }

      if (strCmd == null) {
         return;

      // Menu Tools
      } else if (strCmd.equals(MENU_SUBITEM_ADD_FILE)) {
        Runnable longTask = new Runnable() {
            public void run() { 
               addAudioFiles();
            }
         };
         Thread t = new Thread(longTask);
         t.setPriority(Thread.NORM_PRIORITY);
         t.start();       

      } else if (strCmd.equals(MENU_SUBITEM_ADD_URL)) {
         addAudioUrl();
            
      } else if (strCmd.equals(MENU_SUBITEM_REMOVE_SEL)) {
         if ( audioList.isSelectedRow(currentAudioFile) ) {
            controlStop();
         }
         audioList.removeSelectedRows();
         
      } else if (strCmd.equals(MENU_SUBITEM_REMOVE_ALL)) {
         audioList.removeAllRows();
         controlStop();

      } else if (strCmd.equals(MENU_ITEM_DISCOVERY)) {
         discoveryFiles();

      } else if (strCmd.equals(MENU_ITEM_EXIT)) {
         finalizeGUI();

         
      // Menu Audio Controls and Buttons
      } else if (strCmd.equals(MENU_ITEM_CONTROL_PLAY)) { 
         controlPlay(); 

      } else if (strCmd.equals(MENU_ITEM_CONTROL_PAUSE) || strCmd.equals(MENU_ITEM_CONTROL_RESUME)) {
         controlPause();

      } else if (strCmd.equals(MENU_ITEM_CONTROL_NEXT) || strCmd.equals(BUTTON_CONTROL_NEXT) ) {
         controlNext();

      } else if (strCmd.equals(MENU_ITEM_CONTROL_PREVIOUS) || strCmd.equals(BUTTON_CONTROL_PREVIOUS) ) {
         controlPrevious();

      } else if (strCmd.equals(MENU_ITEM_CONTROL_STOP)) {
         controlStop();

      } else if (strCmd.equals(MENU_ITEM_CONTROL_LOOP)) { 
         ckBoxMenuItemLoop.setSelected(!inLoop);
      
      //Menu help
      } else if (strCmd.equals(MENU_ITEM_ABOUT_ASA)) {
         showASAInfo();
      }

   }

   public void itemStateChanged(ItemEvent event) {
      Object objSource = event.getSource();
      boolean state = false;
      String objText = null;

      if (objSource instanceof JCheckBoxMenuItem) {
         objText = ((JCheckBoxMenuItem) objSource).getText();
         state   = ((JCheckBoxMenuItem) objSource).getState();
      }

      if (objText == null) {
         return;

      // Audio Control Pause
      } else if (objText.equals(MENU_ITEM_CONTROL_LOOP)) {
         inLoop = state;
         if (inLoop) {
            loop.setBorder( BorderFactory.createLoweredBevelBorder() ); 
         } else {        
            loop.setBorder( BorderFactory.createRaisedBevelBorder() );   
         }        
         
      } else if (objText.equals(MENU_ITEM_MOGRID_MSG)) { 
         mogridMessageScrollPane.setVisible(state);
         pack();
      }

   }
      
   private JButton createButton(String title, JPanel panel) {
      JButton newButton = new JButton(title);
      newButton.addActionListener(this);
      newButton.setBorder(new BevelBorder(BevelBorder.RAISED));
      panel.add(newButton);

      return newButton;
   }

   private JMenu createMenu(String title, int mnemonic, JMenuBar menu) {
      JMenu menuItem = new JMenu(title);
      menuItem.setMnemonic(mnemonic);
      menu.add(menuItem);

      return menuItem;
   }

   private JMenu createMenu(String title, int mnemonic, JMenu menu) {
      JMenu menuItem = new JMenu(title);
      menuItem.setMnemonic(mnemonic);
      menu.add(menuItem);

      return menuItem;
   }

   private JMenuItem createMenuItem(String title, int mnemonic, JMenu menu) {
      JMenuItem menuItem = new JMenuItem(title);
      menuItem.addActionListener(this);
      menuItem.setMnemonic(mnemonic);
      menu.add(menuItem);

      return menuItem;
   }

   private JCheckBoxMenuItem createCheckBoxMenuItem(String title, int mnemonic, JMenu menu) {
      JCheckBoxMenuItem ckBoxMenuItem = new JCheckBoxMenuItem(title);
      ckBoxMenuItem.addItemListener(this);
      ckBoxMenuItem.setMnemonic(mnemonic);
      menu.add(ckBoxMenuItem);

      return ckBoxMenuItem;
   }

   private Border createBorder(String title, int top, int left, int bottom, int right, Border border) {
      Border newBorder = BorderFactory.createCompoundBorder(
         BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(title),
            BorderFactory.createEmptyBorder(top, left, bottom, right)
         ),
         border
      );
      
      return newBorder;
   }
   
   public void showASAInfo() {
      if ( asaInfoFrame != null ) {         
         asaInfoFrame.setVisible(true);
         asaInfoFrame.setState(JFrame.NORMAL);
         asaInfoFrame.setLocationRelativeTo(this);
         return;
      }

      asaInfoFrame = new JFrame();
      asaInfoFrame.setTitle("About ASA");
      asaInfoFrame.setLocationRelativeTo(this);
      asaInfoFrame.setResizable(false);
      asaInfoFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
      MoGridImage.setFrameImageIcon(asaInfoFrame, "playicon.gif");
      JPanel vdInfo = new JPanel(new BorderLayout());
      JTextArea info = new JTextArea(); info.setEditable(false);
      info.setAutoscrolls(false);
      info.setFont(new Font("Arial", Font.PLAIN , 11));      
      info.setText("\n  ASA\n  Versão 1.0.0 \n\n  Audio Sharing Application (ASA) \n\n  Copyright (C) 2005 LNCC - PUC-Rio.   \n");
      
      vdInfo.add(info);
      asaInfoFrame.getContentPane().add(vdInfo);
     
      asaInfoFrame.setVisible(true);
      asaInfoFrame.pack();       
   }
   
   private void setMP3PlayerState(int state) {
      mp3PlayerState = state;
   }   
   
   private File getCurrentAudioFile() {
      currentAudioFile = audioList.getSelectedRow();
      String filePath  = audioList.getTableItem(currentAudioFile).toString();
      File audioFile   = new File(filePath);
      return audioFile;
   }
   
   private void controlPlay() {
      if ( ! audioList.isEmpty() ) {
         try {
            if ( mp3PlayerState == PAUSE ) {
               basicController.resume();
               pause.setText( MENU_ITEM_CONTROL_PAUSE );  
                  
            } else {
               if ( mp3PlayerState == PLAY ) {
                  basicController.stop();  
                  setMP3PlayerState( STOP );
               }
               
               audioList.setSelectedRow(); 
               File fileToPlay = getCurrentAudioFile();
               if ( fileToPlay != null && fileToPlay.exists() ) {
                  basicController.open(fileToPlay);               
                  basicController.play();
               } else {
                  Console.println("Error trying to play the song " + fileToPlay + ". Removing invalid audio file." );
                  audioList.removeSelectedRows();
               }
            }
            setMP3PlayerState( PLAY );

         } catch (BasicPlayerException e) {
            Console.println("Error playing the song...\n" + e.getMessage());
            e.printStackTrace();
         }
      } 
   }      
   
   private void controlPause() {
      if ( mp3PlayerState == PLAY ) {
         try {
            basicController.pause();
            setMP3PlayerState( PAUSE );
            pause.setText( MENU_ITEM_CONTROL_RESUME );
         } catch (BasicPlayerException e) {
            Console.println("Error pausing the song...\n" + e.getMessage());
         }
         
      } else if ( mp3PlayerState == PAUSE ) {
         try {
            basicController.resume();
            setMP3PlayerState( PLAY );
            pause.setText( MENU_ITEM_CONTROL_PAUSE );
         } catch (BasicPlayerException e) {
            Console.println("Error resuming the song...\n" + e.getMessage());
         }
      }              
   }
   
   private void controlNext() {
      if ( ! audioList.isEmpty() ) {
         audioList.setNextRow();  
         if ( mp3PlayerState == PLAY ) {     
            controlPlay();
         }
      }
   }

   private void controlPrevious() {
      if ( ! audioList.isEmpty() ) {
         audioList.setPreviousRow();
         if ( mp3PlayerState == PLAY ) {     
            controlPlay();
         }
      }
   }
   
   private void controlStop() {
      if ( mp3PlayerState == PLAY || mp3PlayerState == PAUSE ) {
         try {
            basicController.stop();
            setMP3PlayerState( STOP );
            
         } catch (BasicPlayerException e) {
            Console.println("Error stoping the song...\n" + e.getMessage());
         }
      }
   }     
      
   private void finalizeGUI() {
      controlStop();
      if ( asaInfoFrame!=null ) {
         asaInfoFrame.dispose();
      }
      inLoop = false;
      
      if ( application!=null ) {
         application.finalizeAudioSharing();
      }
      
      dispose();
   }
   
   public void opened(Object arg0, Map arg1) {
      //Console.println("opened: " + arg0.toString()+ " "+ arg1.toString());      
   }

   public void progress(int arg0, long arg1, byte[] arg2, Map arg3) {
      //Console.println("progress: " + arg0+ " "+ arg1+" "+ arg2.toString()+ " "+ arg3.toString()); 
   }

   public void stateUpdated(BasicPlayerEvent playerEvent) {
      if ( playerEvent.getCode() == BasicPlayerEvent.EOM && (inLoop || audioList.hasNext()) ) {         
         if ( currentAudioFile == audioList.getTableSelectedIndex() ) {
            audioList.setNextRow();
         }
         controlPlay();
      } 
   }

   public void setController(BasicController controller) {
      basicController = controller;      
   }
 
}
