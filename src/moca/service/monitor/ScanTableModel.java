/*
 * Created on Oct 16, 2004 by Antonio Carlos Theophilo Costa Junior
 *
 */
package moca.service.monitor;

import javax.swing.table.AbstractTableModel;

/**
 * @author Antonio Carlos Theophilo Costa Junior
 * 
 * Created on Oct 16, 2004
 */
public class ScanTableModel extends AbstractTableModel {

   /** Comment for <code>serialVersionUID</code> */
   private static final long serialVersionUID = 1L;
   
   private String[][] data;

   ScanTableModel(String[] scanData) {
      data = new String[3][2];
      data[0][0] = "AP (MAC)";
      data[0][1] = scanData[0];
      data[1][0] = "Sinal";
      data[1][1] = scanData[1];
      data[2][0] = "SSID";
      data[2][1] = scanData[2];
   }

   public int getRowCount() {
      return 3;
   }

   public int getColumnCount() {
      return 2;
   }

   public Object getValueAt(int row, int column) {
      return data[row][column];
   }

   public boolean isCellEditable(int rowIndex, int columnIndex) {
      if (columnIndex == 1) {
         return true;
      }
      return false;
   }

   public void setValueAt(Object value, int row, int column) {
      data[row][column] = (String) value;
   }

   public Class getColumnClass(int columnIndex) {
      return String.class;
   }
}
