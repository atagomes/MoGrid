/**
 * @author lslima
 * 
 * Created on 10/03/2006
 */
package martin.mogrid.entity.dispatcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.log4j.Logger;

public class TaskExecutor {
    //Manutencao do arquivo de log do servico
    private static final Logger logger = Logger.getLogger(TaskExecutor.class);
    private int jobID;
    private String execString;
    private String execPath;

    public TaskExecutor(int jobID, String execString, String execPath) {
        this.jobID = jobID;
        this.execString = execString;
        this.execPath = execPath;
    }

    public Object exec() {
        //execString: linha de comando a ser executada (Ex: java -jar -Dkey=123 gridtask.jar
        //execPath  : diretorio onde estah o executavel (.jar, .class, etc.)
        String result = null;  //Na ASL eh comparado Object != null
        int exitVal = 0;

        String resultFilePath = execPath + File.separator + "TaskResult" + jobID + ".txt";
        File resultFile = new File(resultFilePath);
        try {
            execString = "nice -n19 " + execString;
            String[] cmd = {"", "", execString};
            String osName = System.getProperty("os.name");

            if (osName.equals("Windows 95")) {
                cmd[0] = "command.com";
                cmd[1] = "/C";
            } else if (osName.startsWith("Windows")) {  //Windows NT, Windows 2000, Windows XP, etc
                cmd[0] = "cmd.exe";
                cmd[1] = "/C";
            } else if (osName.endsWith("Linux")) {
                cmd[0] = "/bin/sh";
                cmd[1] = "-c";
            }

            FileOutputStream fileOutStream = new FileOutputStream(resultFile);
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(cmd, null, new File(execPath));

            // any error message?
            //StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "ERROR", fileOutStream);
            // any output?
            StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "OUTPUT", fileOutStream);
            // kick them off
            //errorGobbler.start();
            outputGobbler.start();

            //any error??? (exitVal != 0)
            exitVal = process.waitFor();
            //if normal exit, read result string 
            if (exitVal == 0) {
                result = outputGobbler.getResultStr();
            }
            return result;

        } catch (Throwable t) {
            logger.error("Error executing remote grid job, exit procesing: " + exitVal + ". " + t.getMessage());
            return result;
        }
    }
}

class StreamGobbler extends Thread {
    //Manutencao do arquivo de log do servico
    private static final Logger logger = Logger.getLogger(StreamGobbler.class);
    private String out = "";
    private InputStream inStream = null;
    private OutputStream outStream = null;
    private String redirectType = null;

    StreamGobbler(InputStream inStream, String type, OutputStream redirect) {
        this.inStream = inStream;
        this.redirectType = type;
        this.outStream = redirect;
    }

    public String getResultStr() {
        return out;
    }

    public void run() {
        try {
            PrintWriter pw = null;
            String line = null;

            if (outStream != null) {
                pw = new PrintWriter(outStream);
            }
            InputStreamReader inStreamReader = new InputStreamReader(inStream);
            BufferedReader bufferedStream = new BufferedReader(inStreamReader);

            while ((line = bufferedStream.readLine()) != null) {
                if (pw != null) {
                    pw.println(line);
                    out += line + "\n";
                }
            }
            if (pw != null) {
                pw.flush();
                pw.close();
            }

        } catch (IOException ioe) {
            logger.error("Error recovering grid job result: " + ioe.getMessage());
        }
    }
}
