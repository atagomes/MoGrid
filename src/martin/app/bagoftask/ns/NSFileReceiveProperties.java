package martin.app.bagoftask.ns;

import java.io.Serializable;

public class NSFileReceiveProperties implements Serializable {
   
   private static final long serialVersionUID = -1857382753233249551L;
   
   private String name;
   private byte[] file;
   
   public NSFileReceiveProperties(String name, byte[] file) {
      this.name = name;
      this.file = file;
   }
   
   public byte[] getFile() {
      return file;
   }
   
   public String getName() {
      return name;
   }
   
   public void setFile(byte[] file) {
      this.file = file;
   }
   
   public void setName(String name) {
      this.name = name;
   }

}
