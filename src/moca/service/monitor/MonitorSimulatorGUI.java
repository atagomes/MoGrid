/*
 * Created on Oct 3, 2004 by Antonio Carlos Theophilo Costa Junior
 *
 */
package moca.service.monitor;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import martin.mogrid.common.util.SystemUtil;

import org.apache.log4j.Logger;

/**
 * @author luciana
 */
public class MonitorSimulatorGUI extends JFrame {

   /** Comment for <code>serialVersionUID</code> */
   private static final long serialVersionUID = 1L;

   public static final String SCAN_INTERVAL_PROP = "monitor.scanInterval";

   public static final String CIS_IP_PROP = "cis.server.host";

   public static final String CIS_PORT_PROP = "cis.monitor.port";

   private String configurationFile;

   private Logger logger = Logger.getLogger(this.getClass());

   private JPanel jPanel1;

   private JPanel jPanel2;

   private JLabel jlblServer;

   private JTextField jtxtServer;

   private JLabel jlblPort;

   private JTextField jtxtPort;

   private JLabel jlblFiles;

   private JComboBox jcmbFiles;

   private DefaultComboBoxModel jcmbFilesModel;

   private JButton jbtnConf;

   private JButton jbtnAdd;

   private JButton jbtnStart;

   private JButton jbtnStop;

   private JButton jbtnExit;

   private Map fileTable;

   private Properties conf;

   private SendingThread sendingThread;

   /**
    * Construtor da classe. Inicializa os elementos gráficos da aplicação.
    */
   public MonitorSimulatorGUI(String configurationFile) {
      this.configurationFile = configurationFile;
      fileTable = new HashMap();
      try {
         logger.debug("Carregando arquivo de propriedades em "
               + System.getProperty("user.dir") + "/" + configurationFile
               + "...");
         conf = new Properties();
         conf.load(new FileInputStream(configurationFile));
         logger.debug("Propriedades carregadas.");
      } catch (IOException ioe) {
         logger.error("Erro ao carregar propriedades. Usando padroes.", ioe);
      }
      initComponents();
   }

   /**
    * Método responsável pela inicialização dos componentes gráficos.
    */
   private void initComponents() {
      setTitle("Wireless Monitor Simulator");
      // setMaximizedBounds(new java.awt.Rectangle(0, 0, 800, 640));
      // setResizable(false);
      // setBounds(0, 0, 800, 400);
      addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent evt) {
            SystemUtil.normalExit();
         }
      });

      jPanel1 = new javax.swing.JPanel();
      GridBagLayout gridBag = new GridBagLayout();
      jPanel1.setLayout(gridBag);
      getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

      GridBagConstraints gc = new GridBagConstraints();
      gc.anchor = GridBagConstraints.EAST;
      gc.insets = new Insets(2, 2, 2, 2);

      int currentX = 0;

      jlblServer = new javax.swing.JLabel();
      jlblServer.setText("Endereço CIS:");
      gc.gridx = 0;
      gc.gridy = currentX;
      gc.weightx = 1.0;
      gc.fill = GridBagConstraints.NONE;
      jPanel1.add(jlblServer, gc);

      jtxtServer = new JTextField(conf.getProperty(CIS_IP_PROP));
      gc.gridx = 1;
      gc.gridy = currentX;
      gc.weightx = 4.0;
      gc.fill = GridBagConstraints.HORIZONTAL;
      jPanel1.add(jtxtServer, gc);

      jlblPort = new javax.swing.JLabel();
      jlblPort.setText("Porta CIS:");
      gc.gridx = 0;
      gc.gridy = ++currentX;
      gc.weightx = 1.0;
      gc.fill = GridBagConstraints.NONE;
      jPanel1.add(jlblPort, gc);

      jtxtPort = new JTextField(conf.getProperty(CIS_PORT_PROP));
      gc.gridx = 1;
      gc.gridy = currentX;
      gc.weightx = 4.0;
      gc.fill = GridBagConstraints.HORIZONTAL;
      jPanel1.add(jtxtPort, gc);

      jlblFiles = new javax.swing.JLabel();
      jlblFiles.setText("Arquivos :");
      gc.gridx = 0;
      gc.gridy = ++currentX;
      gc.weightx = 1.0;
      gc.fill = GridBagConstraints.NONE;
      jPanel1.add(jlblFiles, gc);

      jcmbFiles = new javax.swing.JComboBox();
      jcmbFilesModel = new DefaultComboBoxModel();
      jcmbFiles.setModel(jcmbFilesModel);
      gc.gridx = 1;
      gc.gridy = currentX;
      gc.weightx = 4.0;
      gc.fill = GridBagConstraints.HORIZONTAL;
      jPanel1.add(jcmbFiles, gc);

      jbtnConf = new javax.swing.JButton();
      jbtnConf.setText("Configurar");
      jbtnConf.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            configureScan();
         }
      });
      gc.gridx = 2;
      gc.gridy = currentX;
      gc.weightx = 1.0;
      gc.fill = GridBagConstraints.HORIZONTAL;
      gc.anchor = GridBagConstraints.EAST;
      jPanel1.add(jbtnConf, gc);

      jbtnAdd = new javax.swing.JButton();
      jbtnAdd.setText("Adicionar");
      jbtnAdd.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            jbtnAddActionPerformed();
         }
      });
      gc.gridx = 2;
      gc.gridy = ++currentX;
      gc.weightx = 1.0;
      gc.fill = GridBagConstraints.HORIZONTAL;
      gc.anchor = GridBagConstraints.EAST;
      jPanel1.add(jbtnAdd, gc);

      jPanel2 = new javax.swing.JPanel();
      FlowLayout flow = new FlowLayout();
      flow.setAlignment(FlowLayout.RIGHT);
      jPanel2.setLayout(flow);
      getContentPane().add(jPanel2, java.awt.BorderLayout.SOUTH);

      jbtnStart = new javax.swing.JButton();
      jbtnStart.setText("Iniciar");
      jbtnStart.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            jbtnStartActionPerformed();
         }
      });
      jPanel2.add(jbtnStart);

      jbtnStop = new javax.swing.JButton();
      jbtnStop.setText("Parar");
      jbtnStop.setEnabled(false);
      jbtnStop.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            jbtnStopActionPerformed();
         }
      });
      jPanel2.add(jbtnStop);

      jbtnExit = new javax.swing.JButton();
      jbtnExit.setText("Sair");
      jbtnExit.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            jbtnExitActionPerformed();
         }
      });
      jPanel2.add(jbtnExit);

      pack();
   }

   private void jbtnAddActionPerformed() {
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setMultiSelectionEnabled(true);

      if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
         File[] files = fileChooser.getSelectedFiles();
         for (int i = 0; i < files.length; i++) {
            fileTable.put(files[i].getName(), files[i]);
            logger.debug(files[i].getName());
            jcmbFilesModel.addElement(files[i].getName());
         }
      }
   }

   private void jbtnStartActionPerformed() {
      jbtnStart.setEnabled(false);

      String errorMsg = null;
      if ((errorMsg = parseInput()) != null) {
         JOptionPane.showConfirmDialog(this, errorMsg, "Erro",
               JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
         jbtnStart.setEnabled(true);
         return;
      }

      try {
         BufferedReader buf = new BufferedReader(new FileReader(
               (File) fileTable.get(jcmbFiles.getSelectedItem())));
         List scans = new ArrayList();
         while (buf.ready()) {
            scans.add(buf.readLine());
         }

         conf.setProperty(SCAN_INTERVAL_PROP, getPeriodicity((String) scans
               .get(0)));
         conf.setProperty(CIS_IP_PROP, jtxtServer.getText());
         conf.setProperty(CIS_PORT_PROP, jtxtPort.getText());

         sendingThread = new SendingThread(conf, scans);
         sendingThread.start();
         buf.close();
      } catch (FileNotFoundException fnfe) {
         logger.error("Arquivo " + jcmbFiles.getSelectedItem()
               + " nao encontrado.", fnfe);
         jbtnStart.setEnabled(true);
         return;
      } catch (IOException ioe) {
         logger.error("Erro ao ler arquivo " + jcmbFiles.getSelectedItem()
               + ".", ioe);
         jbtnStart.setEnabled(true);
         return;
      }

      jbtnStop.setEnabled(true);
   }

   private void jbtnStopActionPerformed() {
      jbtnStop.setEnabled(false);
      sendingThread.setSending(false);
      jbtnStart.setEnabled(true);
   }

   private void jbtnExitActionPerformed() {
      try {
         conf.setProperty(CIS_IP_PROP, jtxtServer.getText());
         conf.setProperty(CIS_PORT_PROP, jtxtPort.getText());
         conf.store(new FileOutputStream(configurationFile), null);
      } catch (IOException ioe) {
         logger.error(ioe);
      }
      SystemUtil.normalExit();
   }

   private String parseInput() {
      String temp = jtxtServer.getText();
      if (temp == null || temp.equals("")) {
         return "Insira o endereço do servidor CIS.";
      }

      temp = jtxtPort.getText();
      if (temp == null || temp.equals("")) {
         return "Insira a porta do servidor CIS";
      }
      try {
         Integer.parseInt(temp);
      } catch (NumberFormatException nfe) {
         return "Insira um valor válido para a porta do servidor CIS";
      }

      if (jcmbFiles.getSelectedItem() == null) {
         return "Escolha um arquivo de scan.";
      }
      return null;
   }

   private static String getPeriodicity(String scan) {
      StringTokenizer st = new StringTokenizer(scan, "#");
      st.nextToken();
      st.nextToken();
      st.nextToken();
      return st.nextToken();
   }

   private void configureScan() {
      Object item = jcmbFiles.getSelectedItem();
      if (item == null) {
         error("Escolha um arquivo.", null);
         return;
      }
      new ScanConfigurator(this, "Configuração de Scan", true, (File) fileTable.get(item));
   }

   private void error(String msg, Throwable obj) {
      logger.error(msg, obj);
      JOptionPane.showConfirmDialog(this, msg, "Erro",
            JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
   }

   /**
    * Método principal da aplicação.
    * 
    * @param args
    *           Argumentos passados pela linha de comando.
    * 
    * @todo remover a linha de código que tenta carregar o arquivo de properties
    *       <code>target/classes/moca/service/monitor/MonitorSimulatorGUI.properties</code>
    *       assim como removê-lo fisicamente do diretório do pacote (está
    *       duplicado no diretório do pacote e no conf)
    */
   public static void main(String args[]) {
      if (args.length > 0) {
         new MonitorSimulatorGUI(args[0]).setVisible(true);
      } else {
         new MonitorSimulatorGUI("conf.MonitorSimulatorGUI.properties").setVisible(true);
      }
   }
}
