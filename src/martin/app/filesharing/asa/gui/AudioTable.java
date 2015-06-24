package martin.app.filesharing.asa.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.io.File;
import java.net.URL;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import martin.app.filesharing.asa.AudioSharing;
import martin.app.filesharing.asa.AudioSharingException;




/**
 * Table to display the name of the sound.
 */
public class AudioTable extends JPanel implements TableView {

   /** Comment for <code>serialVersionUID</code> */
   private static final long serialVersionUID = 2220396174965565277L;
   
   private JTable          audioTable           = null;
   private TableSorter     sorter               = null;
   private AudioTableModel audioTableModel      = null;
   private Vector          sounds               = new Vector();  //File or URL
   private JScrollPane     audioTableScrollPane = null;
   private AudioSharing    application          = null;
   
   //Controles da lista de audio
   private boolean isModified = false;
   
   //private String[]  extensions = {"m3u", "pls", "wsz", "snd", "aifc", "aif", "wav", "au", "mp1", "mp2", "mp3", "ogg", "spx"};

   public AudioTable() {      
      setLayout(new BorderLayout());
     
      audioTableModel = new AudioTableModel();
      
      //Com ordenacao:
      sorter     = new TableSorter(audioTableModel); 
      audioTable = new JTable(sorter);    
      sorter.setTableView(this); 
           
      TableColumn col = audioTable.getColumn(" # ");
      col.setMaxWidth(20);
      audioTable.doLayout();
      audioTable.getTableHeader().setToolTipText("Click to sort");
      audioTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

      audioTableScrollPane = new JScrollPane(audioTable);
      audioTableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);      
      audioTableScrollPane.setPreferredSize(new Dimension(500, 240));
      EmptyBorder emptyBorder = new EmptyBorder(10, 10, 10, 10);
      audioTableScrollPane.setBorder(new CompoundBorder(emptyBorder, new EtchedBorder()));      
      add(audioTableScrollPane);     
   }
   
   public void setApplication(AudioSharing application) {
      this.application = application;
   }
   
   private void updateVerticalScrollValue(int selectedRow) {
      JScrollBar vbar = audioTableScrollPane.getVerticalScrollBar();
      
      if ( audioTable.getSelectedRow() == 0 ) {  
         vbar.setValue(0);
         return;
      }

      Rectangle rect  = audioTable.getCellRect(selectedRow, 0, true);
      int rowPosition = ( selectedRow + 1 ) * ( audioTable.getRowHeight() );
      int posVBar     = vbar.getVisibleAmount() + vbar.getValue();
      if ( rowPosition > posVBar  ) { 
         audioTableScrollPane.getViewport().setViewPosition(rect.getLocation());    
      } 
   }
      
   private void setRowSelection(int row) {
      audioTable.setRowSelectionInterval(row, row);
      updateVerticalScrollValue(row);
   }
   
   public boolean hasNext() {
      int index = audioTable.getSelectedRow();
      if ( index+1 < sounds.size() ) {
         return true;
      }
      return false;
   }
   
   public boolean isEmpty() {
      return ( sounds.size() == 0 );
   }

   private void tableChanged() {   
      sorter.fireTableDataChanged();  
      isModified = true;
   }
   
   private void fireTableRowsDeleted() {
      sorter.setSortingStatus(1, sorter.getSortingStatus(1));
      isModified = true;
   }   
     
   public boolean isModified() {
      return isModified;
   }
   
   public void setIsModified(boolean isModified) {
      this.isModified = isModified;
   }   
   
   public void setSelectedRow() { 
      int row = audioTable.getSelectedRow();      
      if ( row == -1 ) { 
         row = 0;
         setRowSelection(row);
      }
   }

   public boolean isSelectedRow(int row) {
      int[] rowsSel = audioTable.getSelectedRows();
      for ( int i=0; i<rowsSel.length; i++ ) {
         if ( row == rowsSel[i] ) { 
            return true;
         }
      }
      return false;
   }
   
   //START - Metodos da interface TableView
   public int getSelectedRow() { 
      int row = audioTable.getSelectedRow();      
      if ( row == -1 ) { 
         row = 0;
      }
      return row;
   }
   
   public void forceSelectedRow(int row) { 
      if ( row > -1 && row < sounds.size() ) {
         setRowSelection(row);
      }
   }
   
   public void clearSelectedRows() { 
      audioTable.clearSelection();
   }
   
   public JTableHeader getTableHeader() {
      return audioTable.getTableHeader();
   }
   //END - Metodos da interface TableView
   
   public void setNextRow() { 
      int row = audioTable.getSelectedRow() + 1;
      if ( row == sounds.size() ) {
         row = 0;
      } 
      setRowSelection(row);      
   }
   
   public void setPreviousRow() {
      int row = audioTable.getSelectedRow();
      
      if ( row == -1 ) {
         row = 0;
      } else if ( row == 0 ) {
         row = sounds.size()-1;
      } else {
         row -= 1;
      }
      setRowSelection(row);
   }
   
   public void removeSelectedRows() {
      int rows[] = audioTable.getSelectedRows();
      Vector soundsToRemove = new Vector();
      for (int i = 0; i < rows.length; i++) {
         soundsToRemove.add(sounds.get(getTableIndex(rows[i])));
      }
      application.deregisterFilesInMogrid(soundsToRemove);
      
      sounds.removeAll(soundsToRemove);
      soundsToRemove.clear();
      fireTableRowsDeleted();
   }

   public void removeAllRows() {
      application.deregisterFilesInMogrid(sounds);
      
      sounds.clear();
      fireTableRowsDeleted();
   }   
   
   private void addSound(File file) {
      if ( file != null && file.getName().endsWith(".mp3") ) {
         sounds.add(file);
         application.registerFilesInMogrid(file);
         tableChanged();
      }
   }   
   private void loadJuke(String name) throws AudioSharingException {
      try {
          File file = new File(name);
          if (file != null ) {
             if ( file.isDirectory() ) {
                String files[] = file.list();
                for (int i = 0; i < files.length; i++) {
                   File leafFile = new File(file.getAbsolutePath(), files[i]);
                   if (leafFile.isDirectory()) {
                      loadJuke(leafFile.getAbsolutePath());
                   } else {
                      addSound(leafFile);
                   }
                }
             } else if ( file.exists() ) {
                addSound(file);
             }
          }
          
      } catch (SecurityException sex) {
          throw new AudioSharingException(sex);
          
      } catch (Exception ex) {
         throw new AudioSharingException(ex); 
      }
   }
      
   public void addRows(String name) throws AudioSharingException {
      if ( name != null ) {
         name = name.trim();
         //URL
         if ( name.startsWith("http") || name.startsWith("file") ) {
            try {
               URL audioUrl = new URL(name);
               sounds.add(audioUrl);
               application.registerFilesInMogrid(audioUrl);
               tableChanged();
               
            } catch (Exception ex) {
               throw new AudioSharingException(ex);
            }
         //FILE
         } else {
            loadJuke(name);
         }
      }
   }  
   
   //Pega a musica (coluna 1) associada a tabela na posicao indicada
   public Object getTableItem(int index){
      return ( sounds.get(getTableIndex(index)) );
   }   
   
   public int getTableSelectedIndex() {
      return ( getTableIndex(getSelectedRow()) );
   }
   
   private int getTableIndex(int index) {
      return ((Integer)audioTable.getValueAt(index, 0)).intValue();
   }
      
   
   
   class AudioTableModel extends AbstractTableModel {
      /** Comment for <code>serialVersionUID</code> */
      private static final long serialVersionUID = 8047866214858965428L;
      
      private final String[] names = { " # ", " Musics " };
      
      public int getColumnCount() {
         return names.length;
      }

      public int getRowCount() {
         return sounds.size();
      }

      public Object getValueAt(int row, int col) {
         if (col == 0) {
            return new Integer(row);
         } else if (col == 1) {
            try {               
               Object object = sounds.get(row);
               if (object instanceof File) {
                  return ((File) object).getName();
               } else if (object instanceof URL) {
                  return ((URL) object).getFile();
               }            
            } catch (ArrayIndexOutOfBoundsException ex) {
               
            }
         }
         return null;
      }

      public String getColumnName(int col) {
         return names[col];
      }

      public Class getColumnClass(int c) {
         return (getValueAt(0, c)).getClass();
      }

      public boolean isCellEditable(int row, int col) {
         return false;
      }

      public void setValueAt(Object aValue, int row, int col) {
      }   
   }

}
