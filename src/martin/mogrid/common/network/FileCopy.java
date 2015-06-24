package martin.mogrid.common.network;

// This example is from the book _Java in a Nutshell_ by David Flanagan.
// Written by David Flanagan.  Copyright (c) 1996 O'Reilly & Associates.
// You may study, use, modify, and distribute this example for any purpose.
// This example is provided WITHOUT WARRANTY either expressed or implied.

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;

public class FileCopy {
   
   //Manutencao do arquivo de log do servico
   private static final Logger logger = Logger.getLogger(FileCopy.class);    
   
   public static File copy(File sourceFile, File destinationFile, boolean overwrite) throws IOException {     
      FileInputStream  source      = null;
      FileOutputStream destination = null;
      byte[] buffer;
      int bytes_read;

      try {
         // First make sure the specified source file
         // exists, is a file, and is readable.
         if (!sourceFile.exists() || !sourceFile.isFile()) {
            throw new FileCopyException("FileCopy: no such source file: " +
                                        sourceFile.getName());
         }
         if (!sourceFile.canRead()) {
            throw new FileCopyException("FileCopy: source file " +
                                        "is unreadable: " + sourceFile.getName());
         }
            
         // If the destination exists, make sure it is a writeable file
         // and ask before overwriting it. If the destination doesn't
         // exist, make sure the directory exists and is writeable.
         if (destinationFile.exists()) {
            
            if (destinationFile.isFile()) {
               if ( !destinationFile.canWrite() ) {
                  throw new FileCopyException("FileCopy: destination " +
                                              "file is unwriteable: " + 
                                              destinationFile.getName());
               }
               if ( !overwrite ) {
                  logger.warn("File " + destinationFile.getName() + " already exists.");                  
                  throw new FileCopyException("FileCopy: copy cancelled.");
               }
            } else {
               throw new FileCopyException("FileCopy: destination " +
                                           "is not a file: " + destinationFile.getName());
            } 
            
         } else {
            File parentdir = parent(destinationFile);
            if ( !parentdir.exists() ) {
               throw new FileCopyException("FileCopy: destination " +
                                           "directory doesn't exist: " + 
                                           destinationFile.getName());
            }
            if ( !parentdir.canWrite() ) {
               throw new FileCopyException("FileCopy: destination " +
                                           "directory is unwriteable: " + 
                                           destinationFile.getName());
            }
         }

         // If we've gotten this far, then everything is okay; we can
         // copy the file.
         source      = new FileInputStream(sourceFile);
         destination = new FileOutputStream(destinationFile);
         buffer      = new byte[1024];
         while (true) {
            bytes_read = source.read(buffer);
            if (bytes_read == -1) {
               break;
            }
            destination.write(buffer, 0, bytes_read);
         }
         
      // No matter what happens, always close any streams we've opened.
      } finally {
         if ( source != null ) {
            try {
               source.close();
            } catch (IOException e) { ; }
         }
         if (destination != null) {
            try {
               destination.close();
            } catch (IOException e) { ; }
         }            
      }
      return destinationFile;
   }

   // File.getParent() can return null when the file is specified without
   // a directory or is in the root directory.
   // This method handles those cases.
   private static File parent(File file) {
      String dirname = file.getParent();
      if (dirname == null) {
         if (file.isAbsolute())
            return new File(File.separator);
         else
            return new File(System.getProperty("user.dir"));
      }
      return new File(dirname);
   }

}

class FileCopyException extends IOException {
  
   /** Comment for <code>serialVersionUID</code> */
   private static final long serialVersionUID = 1L;

   /**
    * Constructs an ioException with the specified detail message
    * @param message message that detail the exception
    */
    public FileCopyException(String message) {
       super(message);
    }
   
}
