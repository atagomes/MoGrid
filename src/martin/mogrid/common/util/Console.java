/**
 * @file CSVConsole.java
 * (C) COPYRIGHT 2003-2005, TecGraf PUC-Rio.
 * @author Cesar Pozzer, Luciana Lima.
 * @date May 10, 2004
 */

package martin.mogrid.common.util;

import javax.swing.JTextArea;


public class Console {    
    private static JTextArea displayPane = null; 
    
    private Console(JTextArea displayPane) { 
       Console.displayPane = displayPane;     
    }
    
    public static void setOutput(JTextArea textArea) {
       new Console(textArea);
    }
    
    public static void println(String txt) {
       displayPane.append(txt + "\n");
    }
   
    public static void print(String txt) {
       displayPane.append(txt);
    } 
        
}

    