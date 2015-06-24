package martin.mogrid.common.network;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import martin.mogrid.common.util.SystemUtil;

import org.apache.log4j.Logger;
import org.safehaus.uuid.EthernetAddress;
import org.safehaus.uuid.NativeInterfaces;

import com.ccg.net.ethernet.BadAddressException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import martin.mogrid.p2pdl.protocol.message.P2PDPMessageInterface;

/**
 * @author luciana
 *
 * Created on 22/06/2005
 */
public class LocalHost {
    //Manutencao do arquivo de log do servico
    private static final Logger logger = Logger.getLogger(LocalHost.class);
    private static String localMacAddress;
    private static String localIpAddress;
    private static String[] networkInterfacesIPAddress;
    

    static {
        searchForNetworkInterfaces();
        initializeLocalMacAddress();
        initializeLocalHostAddress();
    }

    private static void searchForNetworkInterfaces() {
        String osName = System.getProperty("os.name");

        if (osName.equalsIgnoreCase("Linux")) {
            searchFornetworkInterfacesInLinux();
        }
    }

    private static void searchFornetworkInterfacesInLinux() {
        try {
            Process p = Runtime.getRuntime().exec("/sbin/ifconfig -a");

            InputStream in = p.getInputStream();
            String line;
            String output = "";
            List interfaces = new ArrayList();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            while ((line = br.readLine()) != null) {
                if (line.equals("")) {
                    if (output.contains("HWaddr") && output.contains("inet addr:")) {
                        interfaces.add(output);
                    }
                    output = "";
                    continue;
                }
                output += line + "\n";
            }
            if (!interfaces.isEmpty()) {
                networkInterfacesIPAddress = new String[interfaces.size()];
                for (int i = 0; i < interfaces.size(); i++) {
                    String s = (String) interfaces.get(i);
                    String aux = s.split("inet addr:")[1];
                    networkInterfacesIPAddress[i] = aux.substring(0, aux.indexOf(" "));
                //logger.trace("Devive IP Address(" + i + "): " + networkInterfacesIPAddress[i]);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static boolean hasMultipleInterfaces() {
        if (networkInterfacesIPAddress != null && networkInterfacesIPAddress.length > 1) {
            return true;
        }
        return false;
    }

    //Retorna as interfaces em ordem aleatoria para evitar privilegiar
    //colaboradores especificos
    public static String[] getNetworkInterfacesIPAddress() {
        return randChooseInterfaces(networkInterfacesIPAddress);
    }

    private static String[] randChooseInterfaces(String[] interfaces) {
        int size = interfaces.length;
        int[] indexes = new int[size];
        for (int i = 0; i < size; i++) {
            indexes[i] = -1;
        }
        int i = 0;
        while (size != i) {
            int r = (int) (Math.random() * size);
            if (!contains(indexes, r)) {
                indexes[i] = r;
                i++;
            }
        }
        String[] randInterfaces = new String[size];
        for (int j = 0; j < size; j++) {
            randInterfaces[j] = interfaces[indexes[j]];
        }
        return randInterfaces;
    }

    private static boolean contains(int[] indexes, int value) {
        for (int i = 0; i < indexes.length; i++) {
            if (indexes[i] == value) {
                return true;
            }
        }
        return false;
    }

    public static final String getLocalMacAddress() {
        return localMacAddress;
    }

    public static final String initializeLocalMacAddress() {
        if (SystemUtil.strIsNull(localMacAddress)) {
            //Tenta recuperar o Endereco MAC das propriedades Java, ideal para as simulacoes no NCTUns
            localMacAddress = getMACAddressProperty();
            if (SystemUtil.strIsNull(localMacAddress)) {
                //Se nao obtem sucesso, recupera o endereco MAC atraves da biblioteca nativa
                try {
                    NativeInterfaces.setUseStdLibDir(true);
                    EthernetAddress primaryEthInterface = NativeInterfaces.getPrimaryInterface();
                    localMacAddress = primaryEthInterface.toString();
                    logger.debug("The local interface MAC-address is: " + localMacAddress);

                } catch (Throwable t) { //The Throwable class is the superclass of all errors and exceptions in the Java language. 

                    logger.warn("Capture MAC Address failed: " + t.getMessage());
                }
            }
        }
        return localMacAddress;
    }

    public static boolean isMacAddress(String mac) {
        if (SystemUtil.strIsNotNull(mac)) {
            try {
                com.ccg.net.ethernet.EthernetAddress.fromString(mac);
                return true;
            } catch (BadAddressException baex) {
                logger.warn("Bad Ethernet Address: " + baex.getMessage());
            }
        }
        return false;
    }

    public static boolean isLocalMacAddress(String mac) {
        if (SystemUtil.strIsNotNull(mac) && mac.equalsIgnoreCase(LocalHost.localMacAddress)) {
            return true;
        }
        return false;
    }

    private static String getRealPath(String path) {
        String realPath = path;

        try {
            File csvPath = new File(path);
            realPath = csvPath.getCanonicalPath();
        } catch (IOException e) {
            logger.warn("It was not possible resolve path: " + path + ": " + e.getMessage());
        }
        return realPath;
    }

    //Retorna uma string (IP) correspondente ao endereco local 
    public static final String getLocalHostAddress() {
        return localIpAddress;
    }

    //Retorna uma string (IP) correspondente ao endereco local 
    public static final String initializeLocalHostAddress() {
        if (SystemUtil.strIsNull(localIpAddress)) {
            if (networkInterfacesIPAddress != null) {
                localIpAddress = networkInterfacesIPAddress[0];
            } else {
                //Tenta recuperar o Endereco IP das propriedades Java, ideal para as simulacoes no NCTUns
                localIpAddress = getIPAddressProperty();
                if (SystemUtil.strIsNull(localIpAddress)) {
                    //Se nao obtem sucesso, recupera o endereco IP via Java
                    try {
                        localIpAddress = InetAddress.getLocalHost().getHostAddress();
                        return localIpAddress;
                    } catch (UnknownHostException uhe) {
                        logger.warn("It was not possible resolve the local IP Address: " + uhe.getMessage());
                        return "0.0.0.0";
                    }
                }
            }
        }
        return localIpAddress;
    }

    public static boolean isLocalHostAddress(String ip) {
        if (hasMultipleInterfaces()) {
            for (int i = 0; i < networkInterfacesIPAddress.length; i++) {
                if (SystemUtil.strIsNotNull(ip) && ip.equalsIgnoreCase(networkInterfacesIPAddress[i])) {
                    return true;
                }
            }
            return false;
        } else if (SystemUtil.strIsNotNull(ip) && ip.equalsIgnoreCase(LocalHost.getLocalHostAddress())) {
            return true;
        }
        return false;
    }
    //Retorna um endereco de rede (IP) correspondente ao endereco local 
    public static final InetAddress getLocalHost() {
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException uhe) {
            logger.warn("It was not possible resolve the local IP Address: " + uhe.getMessage());
            return null;
        }
    }
    //TODO Falta testar...
    public static boolean isLocalHost(InetAddress ip) {
        if (ip != null && ip == LocalHost.getLocalHost()) {
            return true;
        }
        return false;
    }

    public static boolean soIsLinux() {
        String osName = System.getProperty("os.name");
        if (osName.endsWith("Linux")) {
            return true;
        }
        return false;
    }

    public static boolean soIsWindows() {
        String osName = System.getProperty("os.name");
        if (osName.startsWith("Windows")) //Windows NT, Windows 2000, Windows XP, etc
        {
            return true;
        }
        return false;
    }

    public static boolean soIsWindowsXP() {
        String osName = System.getProperty("os.name");
        if (osName.startsWith("Windows XP")) //Windows NT, Windows 2000, Windows XP, etc
        {
            return true;
        }
        return false;
    }

    private static String getIPAddressProperty() {
        String ipAddrProp = System.getProperty("IP");
        if (SystemUtil.strIsNotNull(ipAddrProp)) {
            try {
                InetAddress.getByName(ipAddrProp);
            } catch (UnknownHostException e) {
                return null;
            }
        }
        return ipAddrProp;
    }

    private static String getMACAddressProperty() {
        String macAddrProp = System.getProperty("MAC");
        System.out.println();
        if (SystemUtil.strIsNotNull(macAddrProp)) {
            try {
                //Informa pacote completo para evitar conflito de namespace com org.safehaus.uuid.EthernetAddress
                com.ccg.net.ethernet.EthernetAddress.fromString(macAddrProp);
            } catch (BadAddressException baex) {
                return null;
            }
        }
        return macAddrProp;
    }
}
