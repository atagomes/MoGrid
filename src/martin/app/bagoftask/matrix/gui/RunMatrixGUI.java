/**
 * @author lslima
 * 
 * Created on 20/03/2006
 */

package martin.app.bagoftask.matrix.gui;


import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import martin.app.bagoftask.matrix.RunMatrix;
import martin.mogrid.common.util.MoGridImage;
import martin.mogrid.common.util.MoGridString;


public class RunMatrixGUI implements ActionListener {

   //TODO 1) criar panels de dialogo para passagem de parametro (linhas, colunas e rand)
   //        para criacao das matrizes A e B com a opcao show
   //     2) criar o botao validate, multiply e show result
   //     3) corrigir registro de recursos
   
   /** Comment for <code>serialVersionUID</code> */
   private static final long serialVersionUID = -640562349618162433L;

   private static final String NEWLINE = "\n";
   
   private JFrame       mainFrame;
   private JTextArea    output;
   private JScrollPane  scrollPane;
   private MatrixDialog createDialog;
   private MatrixDialog registerDialog;

   private static final String REGISTER     = "Register Compiler";
   private static final String CREATE       = "Create Matrices A and B";
   private static final String REQUEST_EXEC = "Request MoGrid Execution";
   private static final String MATRIX_A     = "Show Matrix A";
   private static final String MATRIX_B     = "Show Matrix B";
   private static final String MATRIX_AB    = "Show Matrix AB";
   private static final String EXIT         = "Exit";
    
    private JMenuItem menuItemA, menuItemB, menuItemAB;
    
    // Aplicacao que usa a GUI
    private RunMatrix application = null;
    
    public JMenuBar createMenuBar() {
        JMenuBar menuBar;
        JMenu menu;
        JMenuItem menuItem;

        //Create the menu bar.
        menuBar = new JMenuBar();

        //Build the first menu.
        menu = new JMenu("Options");
        menu.setMnemonic(KeyEvent.VK_O);
        menuBar.add(menu);

        
        //Compiler
        menuItem = new JMenuItem(REGISTER, KeyEvent.VK_R);
        menuItem.addActionListener(this);
        menu.add(menuItem);
        //Create
        menuItem = new JMenuItem(CREATE, KeyEvent.VK_C);
        menuItem.addActionListener(this);
        menu.add(menuItem);
        //Multiply
        menuItem = new JMenuItem(REQUEST_EXEC, KeyEvent.VK_R);
        menuItem.addActionListener(this);
        menu.add(menuItem);
        //Matrix A
        menuItemA = new JMenuItem(MATRIX_A, KeyEvent.VK_A);
        menuItemA.addActionListener(this);
        menuItemA.setEnabled(false);
        menu.add(menuItemA);
        //Matrix B
        menuItemB = new JMenuItem(MATRIX_B, KeyEvent.VK_B);
        menuItemB.addActionListener(this);
        menuItemB.setEnabled(false);
        menu.add(menuItemB);
        //Matrix AB
        menuItemAB = new JMenuItem(MATRIX_AB);
        menuItemAB.addActionListener(this);
        menuItemAB.setEnabled(false);
        menu.add(menuItemAB);
        
        //Exit
        menuItem = new JMenuItem(EXIT, KeyEvent.VK_X);
        menuItem.addActionListener(this);
        menu.add(menuItem);


        return menuBar;
    }

    public Container createContentPane() {
        //Create the content-pane-to-be.
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setOpaque(true);

        //Create a scrolled text area.
        output = new JTextArea(5, 30);
        output.setEditable(false);
        scrollPane = new JScrollPane(output);

        //Add the text area to the content pane.
        contentPane.add(scrollPane, BorderLayout.CENTER);

        return contentPane;
    }

    public void actionPerformed(ActionEvent e) {        
        JMenuItem source  = (JMenuItem)(e.getSource());
        String    command =  source.getText();        
        
        if ( command.equals(REGISTER) ) {       
           registerDialog.setVisible(true);
           String params = registerDialog.getParameters(); 
           if ( params!=null && !params.trim().equals("") ) {
              //String[] compiler = params.split(" ");
              String[] compiler = MoGridString.split(params, " ");
              if ( compiler.length == 2 ) {
                 application.registerCompilers(compiler[0], compiler[1]);
              }
           }
           
        } else if ( command.equals(CREATE) ) {        
           createDialog.setVisible(true);
           String params = createDialog.getParameters();           
           if ( params!=null && !params.trim().equals("") ) {
              //String[] index = params.split(",");
              String[] index = MoGridString.split(params, ",");
              if ( index.length == 4 ) {
                 try {
                    int rA = Integer.parseInt(index[0].trim());
                    int cA = Integer.parseInt(index[1].trim());
                    int rB = Integer.parseInt(index[2].trim());
                    int cB = Integer.parseInt(index[3].trim());
                    
                    application.createMatrices(rA, cA, rB, cB);
                    
                    menuItemA.setEnabled(true); 
                    menuItemB.setEnabled(true);
                    showMatrixA();
                    showMatrixB();
                    
                 } catch (NumberFormatException ex) {                   
                 }
              }
           }
        
        } else if ( command.equals(REQUEST_EXEC) ) {
           synchronized (application) {
              application.sendJobRequest(); 
           }
           menuItemAB.setEnabled(true); 

        } else if ( command.equals(MATRIX_A) ) {
           showMatrixA();

        } else if ( command.equals(MATRIX_B) ) {
           showMatrixB();
           
        } else if ( command.equals(MATRIX_AB) ) {
           showMatrixAB();
           
        } else if ( command.equals(EXIT) ) {
           finalizeGUI();
        }        
    }
    
    public void println(String txt) {
       output.append(txt);
       output.setCaretPosition(output.getDocument().getLength());       
    }
    private void showMatrixA() {
       String matrix = application.readMatrixA(); 
       println("Matrix A:" + NEWLINE + matrix + NEWLINE);
    }
    private void showMatrixB() {
       String matrix = application.readMatrixB();   
       println("Matrix B:" + NEWLINE + matrix + NEWLINE);  
    }
    public void showMatrixAB() {
       String matrix = application.readMatrixAB();   
       println("Matrix AB:" + NEWLINE + matrix + NEWLINE);    
    }

    // Returns just the class name -- no package info.
    protected String getClassName(Object o) {
        String classString = o.getClass().getName();
        int dotIndex = classString.lastIndexOf(".");
        return classString.substring(dotIndex+1);
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private void createAndShowGUI() {
        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);   
        try {
           // Metal (java) look and feel:
           UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (Exception e) {
        }
        
        //Create and set up the window.
        mainFrame = new JFrame("Multiply Matrices");
        
        //configura acao a ser executada qdo a GUI eh fechada
        mainFrame.addWindowListener(new WindowAdapter() {
           public void windowClosing(WindowEvent we) {
              finalizeGUI();
           }
        });
        ImageIcon icon = MoGridImage.create("matrix-logo.gif");      
        mainFrame.setIconImage(icon.getImage());

        //Create and set up the content pane.
        //RunMatrixGUI demo = new RunMatrixGUI();
        mainFrame.setJMenuBar(createMenuBar());
        mainFrame.setContentPane(createContentPane());

        //Display the window.
        mainFrame.setSize(450, 260);

        //String[] javaVersion = System.getProperty("java.version").split("\\.");
        String[] javaVersion = MoGridString.split(System.getProperty("java.version"), "\\.");
        if ( javaVersion != null && Integer.parseInt(javaVersion[1]) >= 4 ) {
           mainFrame.setLocationRelativeTo(mainFrame.getParent()); // since 1.4
        }  

        mainFrame.setVisible(true);
        mainFrame.transferFocus();
        
        //CREATE
        String[] createTitles = {"Matrices Creation Dialog", "Preencha com o número de linhas e colunas", "para criação das matrizes A e B:", "4, 3, 3, 2", "Exemplo: 4, 3, 3, 2", "Create"};
        createDialog = new MatrixDialog(mainFrame, createTitles);
        //REGISTER
        String[] registerTitles = {"Compiler Register Dialog", "Preencha com o exe do compilador e o seu diretório", "O caminho deve apontar para o diretório onde está o id", "java C:\\lng\\java\\jre\\bin", "Exemplo: java C:\\lng\\java\\jre\\bin", "Register"};
        registerDialog = new MatrixDialog(mainFrame, registerTitles);
    }

    
    private void finalizeGUI() {
       if ( application!=null ) {
          application.finalize();
       }
       mainFrame.dispose();
    }
    
    public RunMatrixGUI(RunMatrix application) {
       this.application = application;
              
       //Schedule a job for the event-dispatching thread:
       //creating and showing this application's GUI.
       javax.swing.SwingUtilities.invokeLater(new Runnable() {
          public void run() {
             createAndShowGUI();
         }
       });
    }
}


