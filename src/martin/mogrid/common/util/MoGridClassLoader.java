/*
 * Created on 08/06/2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package martin.mogrid.common.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.log4j.Logger;

/* Search and load a .class in MoGrid jar file ($MOGRID_HOME/bin/mogrid.jar) and load it */
public class MoGridClassLoader {
   
   //Manutencao do arquivo de log do servico
   private static final Logger logger = Logger.getLogger(MoGridClassLoader.class);
   
   private static JarFile jarfile;
   
   private static final String MOGRID_JAR = System.getProperty("mogrid.home") + File.separator + "bin" + File.separator + "mogrid.jar";
   private static final String MOGRID_BIN = System.getProperty("mogrid.home") + File.separator + "bin" + File.separator;
      
  
   public static Class load(String classFileName) throws MoGridClassLoaderException {
      String jarfilename = "";            
      if ( new File(MOGRID_JAR).exists() ) {
         jarfilename = MOGRID_JAR;
      } else if ( new File(MOGRID_BIN).exists() ) {
         jarfilename = MOGRID_BIN;
      } else {
         logger.warn(classFileName + ".class file was not located in repository.");
         throw new MoGridClassLoaderException(classFileName + ".class file was not located in repository.");
      }
      
      Class goalClass = null;
      try {
         String className = classFileName.replace('.', '/');
         className += ".class";
         
         final JarFile ajar = new JarFile(jarfilename);        
         jarfile = ajar;
         java.util.Enumeration classEnum = ajar.entries();
         for (; classEnum.hasMoreElements();) {
            JarEntry loadedentry = (JarEntry) classEnum.nextElement();
            //System.out.println(loadedentry.getName()+"  "+className);
            if (isClassFile(loadedentry.getName()) && loadedentry.getName().equals(className) ) {
               goalClass = loadClass(loadedentry);
               break;
            }
         }
      } catch (Exception e) {
         logger.warn(classFileName + " class was not located in repository: " + e.getMessage(), e);
         // We could not find the class, so indicate the problem with an exception
         throw new MoGridClassLoaderException(classFileName + " class was not located in repository: " + e.getMessage());
      }
      
      return goalClass;
   }

   private static Class loadClass(final JarEntry jarentry) throws ClassNotFoundException {
      try {
         Class loadedclass = new ClassLoader() {
            public Class findClass(String name) throws ClassNotFoundException {
               try {
                  InputStream is = jarfile.getInputStream(jarentry);
                  BufferedInputStream bis = new BufferedInputStream(is);
                  int jarSize = (int)jarentry.getSize();
                  byte[] data = new byte[jarSize];
                  bis.read(data, 0, jarSize);                  
                  name = parseClassName(name);                  
                  return defineClass(name, data, 0, data.length);  // catch (ClassFormatError err) {
                  
               } catch (Exception ex) {
                  // We could not find the class, so indicate the problem with an exception
                  throw new ClassNotFoundException(name);
               }
            }
         }.loadClass(jarentry.getName());
         return loadedclass;
         
      } catch (Exception e) {
         // We could not find the class, so indicate the problem with an exception
         throw new ClassNotFoundException();
      }
   }

   private static boolean isClassFile(String jarentryname) {
      return jarentryname.endsWith(".class");
   }

   private static String parseClassName(String jarentryname) {
      int    index     = jarentryname.indexOf("class");
      String classname = jarentryname.substring(0, index - 1);
      String path      = classname.replace('/', '.');
      
      return path;
   }
   
   
   public static void main(String args[]) throws Exception {
      String progClass = args[0];
      Class loadedClass = MoGridClassLoader.load(progClass);
      
      Object ob = loadedClass;
      if (ob == null) {
         logger.info("Class "+progClass+" not loaded...");
      } else {
         logger.info("Class "+progClass+" loaded...");         
      }
   }   
   
}
