package martin.app.filesharing.asa.gui;

import javax.swing.table.JTableHeader;

public interface TableView {

   public abstract JTableHeader getTableHeader();
   public abstract int          getSelectedRow();
   public abstract void         forceSelectedRow(int row);
   public abstract void         clearSelectedRows();
   
}
