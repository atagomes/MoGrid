/*
 * Created on Oct 15, 2004 by Antonio Carlos Theophilo Costa Junior
 *
 */
package moca.service.monitor;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;

/**
 * @author Antonio Carlos Theophilo Costa Junior
 * 
 * Created on Oct 15, 2004
 */
public class ScanConfigurator extends JDialog {


   /** Comment for <code>serialVersionUID</code> */
   private static final long serialVersionUID = 1L;

   private static final String CPU = "CPU";

   private static final String FREE_MEMORY = "FREE_MEMORY";

   private static final String ENERGY_LEVEL = "ENERGY_LEVEL";

   private static final String PERIODICITY = "PERIODICITY";

   private static final String IP_CHANGE = "IP_CHANGE";

   private static final String AP_CHANGE = "AP_CHANGE";

   private static final String IP = "IP";

   private static final String MASK = "MASK";

   private static final String MOBILE_HOST_MAC_ADDR = "MOBILE_HOST_MAC_ADDR";

   private static final String AP_MAC_ADDRESS = "AP_MAC_ADDRESS";

   private static final String AP_SCANS = "AP_SCANS";

   private JPanel jPanel1;

   private JPanel jPanel2;

   private JLabel jlblCpu;

   private JTextField jtxtCpu;

   private JLabel jlblFreeMem;

   private JTextField jtxtFreeMem;

   private JLabel jlblEnergyLevel;

   private JTextField jtxtEnergyLevel;

   private JLabel jlblPeriodicity;

   private JTextField jtxtPeriodicity;

   private JLabel jlblIpChange;

   private JTextField jtxtIpChange;

   private JLabel jlblApChange;

   private JTextField jtxtApChange;

   private JLabel jlblIp;

   private JTextField jtxtIp;

   private JLabel jlblMask;

   private JTextField jtxtMask;

   private JLabel jlblMobile;

   private JTextField jtxtMobile;

   private JLabel jlblApMac;

   private JTextField jtxtApMac;

   private JLabel jlblScans;

   private JComboBox jcmbScans;

   private DefaultComboBoxModel jcmbScansModel;

   private JTable jtblScans;

   private JButton jbtnOk;

   private JButton jbtnCancel;

   private Logger logger = Logger.getLogger(this.getClass());

   private HashMap scanConf = new HashMap();

   private File scanFile;

   ScanConfigurator(Frame owner, String title, boolean modal, File scanFile) {
      super(owner, title, modal);
      this.scanFile = scanFile;
      readScanFile();
      initComponents();
   }

   private void readScanFile() {
      String scanLine = null;
      try {
         BufferedReader buf = new BufferedReader(new FileReader(scanFile));
         scanLine = buf.readLine();
         buf.close();
      } catch (FileNotFoundException fnfe) {
         error("Arquivo " + scanFile.getName() + " nao encontrado.", fnfe);
         return;
      } catch (IOException ioe) {
         error("Erro ao ler arquivo " + scanFile.getName() + ".", ioe);
         return;
      }
      int index = scanLine.indexOf('&');
      StringTokenizer st = new StringTokenizer(scanLine.substring(0, index),
            "#");
      scanConf.put(CPU, st.nextToken());
      scanConf.put(FREE_MEMORY, st.nextToken());
      scanConf.put(ENERGY_LEVEL, st.nextToken());
      scanConf.put(PERIODICITY, st.nextToken());
      scanConf.put(IP_CHANGE, st.nextToken());
      scanConf.put(AP_CHANGE, st.nextToken());
      scanConf.put(IP, st.nextToken());
      scanConf.put(MASK, st.nextToken());
      scanConf.put(MOBILE_HOST_MAC_ADDR, st.nextToken());
      scanConf.put(AP_MAC_ADDRESS, st.nextToken());

      st = new StringTokenizer(scanLine.substring(index + 1), "&");
      List scans = new ArrayList();
      scanConf.put(AP_SCANS, scans);
      while (st.hasMoreTokens()) {
         String[] scan = new String[3];
         StringTokenizer st2 = new StringTokenizer(st.nextToken(), "#");
         for (int i = 0; i < 3; i++) {
            scan[i] = st2.nextToken();
         }
         scans.add(new ScanTableModel(scan));
      }
   }

   /**
    * Método responsável pela inicialização dos componentes gráficos.
    */
   private void initComponents() {
      setTitle("Scan Configurator");
      // setMaximizedBounds(new java.awt.Rectangle(0, 0, 800, 640));
      // setResizable(false);
      // setBounds(0, 0, 800, 400);

      jPanel1 = new javax.swing.JPanel();
      GridBagLayout gridBag = new GridBagLayout();
      jPanel1.setLayout(gridBag);
      getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

      GridBagConstraints gc = new GridBagConstraints();
      gc.anchor = GridBagConstraints.EAST;
      gc.insets = new Insets(2, 2, 2, 2);

      int currentX = 0;

      jlblCpu = new javax.swing.JLabel();
      jlblCpu.setText("CPU:");
      gc.gridx = 0;
      gc.gridy = currentX;
      gc.weightx = 1.0;
      gc.fill = GridBagConstraints.NONE;
      jPanel1.add(jlblCpu, gc);

      jtxtCpu = new JTextField((String) scanConf.get(CPU));
      gc.gridx = 1;
      gc.gridy = currentX;
      gc.weightx = 4.0;
      gc.fill = GridBagConstraints.HORIZONTAL;
      jPanel1.add(jtxtCpu, gc);

      jlblFreeMem = new javax.swing.JLabel();
      jlblFreeMem.setText("Memória Livre:");
      gc.gridx = 0;
      gc.gridy = ++currentX;
      gc.weightx = 1.0;
      gc.fill = GridBagConstraints.NONE;
      jPanel1.add(jlblFreeMem, gc);

      jtxtFreeMem = new JTextField((String) scanConf.get(FREE_MEMORY));
      gc.gridx = 1;
      gc.gridy = currentX;
      gc.weightx = 4.0;
      gc.fill = GridBagConstraints.HORIZONTAL;
      jPanel1.add(jtxtFreeMem, gc);

      jlblEnergyLevel = new javax.swing.JLabel();
      jlblEnergyLevel.setText("Nível de Energia:");
      gc.gridx = 0;
      gc.gridy = ++currentX;
      gc.weightx = 1.0;
      gc.fill = GridBagConstraints.NONE;
      jPanel1.add(jlblEnergyLevel, gc);

      jtxtEnergyLevel = new JTextField((String) scanConf.get(ENERGY_LEVEL));
      gc.gridx = 1;
      gc.gridy = currentX;
      gc.weightx = 4.0;
      gc.fill = GridBagConstraints.HORIZONTAL;
      jPanel1.add(jtxtEnergyLevel, gc);

      jlblPeriodicity = new javax.swing.JLabel();
      jlblPeriodicity.setText("Periodicidade:");
      gc.gridx = 0;
      gc.gridy = ++currentX;
      gc.weightx = 1.0;
      gc.fill = GridBagConstraints.NONE;
      jPanel1.add(jlblPeriodicity, gc);

      jtxtPeriodicity = new JTextField((String) scanConf.get(PERIODICITY));
      gc.gridx = 1;
      gc.gridy = currentX;
      gc.weightx = 4.0;
      gc.fill = GridBagConstraints.HORIZONTAL;
      jPanel1.add(jtxtPeriodicity, gc);

      jlblIpChange = new javax.swing.JLabel();
      jlblIpChange.setText("Mudança de IP:");
      gc.gridx = 0;
      gc.gridy = ++currentX;
      gc.weightx = 1.0;
      gc.fill = GridBagConstraints.NONE;
      jPanel1.add(jlblIpChange, gc);

      jtxtIpChange = new JTextField((String) scanConf.get(IP_CHANGE));
      gc.gridx = 1;
      gc.gridy = currentX;
      gc.weightx = 4.0;
      gc.fill = GridBagConstraints.HORIZONTAL;
      jPanel1.add(jtxtIpChange, gc);

      jlblApChange = new javax.swing.JLabel();
      jlblApChange.setText("Mudança de AP:");
      gc.gridx = 0;
      gc.gridy = ++currentX;
      gc.weightx = 1.0;
      gc.fill = GridBagConstraints.NONE;
      jPanel1.add(jlblApChange, gc);

      jtxtApChange = new JTextField((String) scanConf.get(AP_CHANGE));
      gc.gridx = 1;
      gc.gridy = currentX;
      gc.weightx = 4.0;
      gc.fill = GridBagConstraints.HORIZONTAL;
      jPanel1.add(jtxtApChange, gc);

      jlblIp = new javax.swing.JLabel();
      jlblIp.setText("Endereço IP:");
      gc.gridx = 0;
      gc.gridy = ++currentX;
      gc.weightx = 1.0;
      gc.fill = GridBagConstraints.NONE;
      jPanel1.add(jlblIp, gc);

      jtxtIp = new JTextField((String) scanConf.get(IP));
      gc.gridx = 1;
      gc.gridy = currentX;
      gc.weightx = 4.0;
      gc.fill = GridBagConstraints.HORIZONTAL;
      jPanel1.add(jtxtIp, gc);

      jlblMask = new javax.swing.JLabel();
      jlblMask.setText("Máscara de Rede:");
      gc.gridx = 0;
      gc.gridy = ++currentX;
      gc.weightx = 1.0;
      gc.fill = GridBagConstraints.NONE;
      jPanel1.add(jlblMask, gc);

      jtxtMask = new JTextField((String) scanConf.get(MASK));
      gc.gridx = 1;
      gc.gridy = currentX;
      gc.weightx = 4.0;
      gc.fill = GridBagConstraints.HORIZONTAL;
      jPanel1.add(jtxtMask, gc);

      jlblMobile = new javax.swing.JLabel();
      jlblMobile.setText("Endereço MAC:");
      gc.gridx = 0;
      gc.gridy = ++currentX;
      gc.weightx = 1.0;
      gc.fill = GridBagConstraints.NONE;
      jPanel1.add(jlblMobile, gc);

      jtxtMobile = new JTextField((String) scanConf.get(MOBILE_HOST_MAC_ADDR));
      gc.gridx = 1;
      gc.gridy = currentX;
      gc.weightx = 4.0;
      gc.fill = GridBagConstraints.HORIZONTAL;
      jPanel1.add(jtxtMobile, gc);

      jlblApMac = new javax.swing.JLabel();
      jlblApMac.setText("AP Atual (MAC):");
      gc.gridx = 0;
      gc.gridy = ++currentX;
      gc.weightx = 1.0;
      gc.fill = GridBagConstraints.NONE;
      jPanel1.add(jlblApMac, gc);

      jtxtApMac = new JTextField((String) scanConf.get(AP_MAC_ADDRESS));
      gc.gridx = 1;
      gc.gridy = currentX;
      gc.weightx = 4.0;
      gc.fill = GridBagConstraints.HORIZONTAL;
      jPanel1.add(jtxtApMac, gc);

      jlblScans = new javax.swing.JLabel();
      jlblScans.setText("Scans :");
      gc.gridx = 0;
      gc.gridy = ++currentX;
      gc.weightx = 1.0;
      gc.fill = GridBagConstraints.NONE;
      jPanel1.add(jlblScans, gc);

      jcmbScans = new javax.swing.JComboBox();
      jcmbScansModel = new DefaultComboBoxModel();
      List scansList = ((List) scanConf.get(AP_SCANS));
      for (int i = 1; i < scansList.size() + 1; i++) {
         jcmbScansModel.addElement(new Integer(i));
      }
      jcmbScans.setModel(jcmbScansModel);
      jcmbScans.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            jPanel1.remove(jtblScans);
            buildJtblScans();
            jPanel1.revalidate();
         }
      });
      gc.gridx = 0;
      gc.gridy = ++currentX;
      gc.weightx = 1.0;
      gc.fill = GridBagConstraints.NONE;
      jPanel1.add(jcmbScans, gc);

      buildJtblScans();

      jPanel2 = new javax.swing.JPanel();
      FlowLayout flow = new FlowLayout();
      flow.setAlignment(FlowLayout.RIGHT);
      jPanel2.setLayout(flow);
      getContentPane().add(jPanel2, java.awt.BorderLayout.SOUTH);

      jbtnOk = new javax.swing.JButton();
      jbtnOk.setText("OK");
      jbtnOk.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            jbtnOkActionPerformed();
         }
      });
      jPanel2.add(jbtnOk);

      jbtnCancel = new javax.swing.JButton();
      jbtnCancel.setText("Cancel");
      jbtnCancel.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            dispose();
         }
      });
      jPanel2.add(jbtnCancel);

      pack();
      setVisible(true);
   }

   private void jbtnOkActionPerformed() {
      char fieldSeparator = '#';
      char scanSeparator = '&';
      StringBuffer buf = new StringBuffer(1024);
      buf.append(jtxtCpu.getText()).append(fieldSeparator);
      buf.append(jtxtFreeMem.getText()).append(fieldSeparator);
      buf.append(jtxtEnergyLevel.getText()).append(fieldSeparator);
      buf.append(jtxtPeriodicity.getText()).append(fieldSeparator);
      buf.append(jtxtIpChange.getText()).append(fieldSeparator);
      buf.append(jtxtApChange.getText()).append(fieldSeparator);
      buf.append(jtxtIp.getText()).append(fieldSeparator);
      buf.append(jtxtMask.getText()).append(fieldSeparator);
      buf.append(jtxtMobile.getText()).append(fieldSeparator);
      buf.append(jtxtApMac.getText());
      Iterator itScans = ((List) scanConf.get(AP_SCANS)).iterator();
      while (itScans.hasNext()) {
         TableModel tm = (TableModel) itScans.next();
         buf.append(scanSeparator);
         buf.append(tm.getValueAt(0, 1)).append(fieldSeparator);
         buf.append(tm.getValueAt(1, 1)).append(fieldSeparator);
         buf.append(tm.getValueAt(2, 1));
      }
      try {
         PrintWriter out = new PrintWriter(new FileWriter(scanFile));
         out.println(buf.toString());
         out.close();
      } catch (FileNotFoundException fnfe) {
         error("Arquivo " + scanFile.getName() + " nao encontrado.", fnfe);
      } catch (IOException ioe) {
         error("Erro ao escrever no arquivo " + scanFile.getName() + ".", ioe);
      }
      dispose();
   }

   private void buildJtblScans() {
      List scansList = (List) scanConf.get(AP_SCANS);
      int index = jcmbScans.getSelectedIndex();
      if (index == -1) {
         index = 0;
      }
      jtblScans = new JTable((TableModel) scansList.get(index));
      jtblScans.getColumnModel().getColumn(0).setCellRenderer(
            new MyTableCellRenderer());
      jtblScans.setPreferredSize(new Dimension(250, 45));
      GridBagConstraints gc = new GridBagConstraints();
      gc.gridx = 1;
      gc.gridy = 10;
      gc.weightx = 4.0;
      gc.gridheight = GridBagConstraints.REMAINDER;
      gc.fill = GridBagConstraints.HORIZONTAL;
      jPanel1.add(jtblScans, gc);
   }

   private class MyTableCellRenderer extends DefaultTableCellRenderer {
      /** Comment for <code>serialVersionUID</code>*/
      private static final long serialVersionUID = 1L;

      public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {
         super.getTableCellRendererComponent(table, value, isSelected,
               hasFocus, row, column);
         if (column == 0) {
            Font font = table.getFont();
            setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
            super.setBackground(jPanel1.getBackground());
         }
         return this;
      }
   }

   private void error(String msg, Throwable obj) {
      logger.error(msg, obj);
      JOptionPane.showConfirmDialog(this, msg, "Erro",
            JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
   }
}
