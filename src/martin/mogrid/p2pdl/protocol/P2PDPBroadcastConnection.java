package martin.mogrid.p2pdl.protocol;

/**
 * @author luciana
 *
 * Created on 12/08/2005
 */
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import martin.mogrid.common.network.LocalHost;
import martin.mogrid.common.network.NetworkUtil;
import martin.mogrid.common.network.UDPDatagramPacket;
import martin.mogrid.common.network.UDPDatagramPacketException;
import martin.mogrid.common.network.UDPDatagramSocket;
import martin.mogrid.p2pdl.protocol.message.P2PDPMessage;
import martin.mogrid.p2pdl.protocol.message.P2PDPMessageInterface;

import org.apache.log4j.Logger;


/*
 * Channel for messge exchange between COORDINATORS and COLLABORATORS (broadcast)
 * UDP broadcasts sends are always enabled on a DatagramSocket. In order to receive 
 * broadcast packets a DatagramSocket should be bound to the wildcard address. In some 
 * implementations, broadcast packets may also be received when a DatagramSocket is 
 * bound to a more specific address.
 */
public class P2PDPBroadcastConnection extends P2PDPCoordinationConnection {

    //Manutencao do arquivo de log para debug
    private static final Logger logger = Logger.getLogger(P2PDPBroadcastConnection.class);
    //Objetos associados a conexao 
    private DatagramSocket broadSenderSocket = null;
    private DatagramSocket broadReceiverSocket = null;
    private InetSocketAddress broadSendToAddress = null;
    private InetSocketAddress broadReceiveAddress = null;
    //Intervalo no qual o canal serah consultado em milisegundos 
    private int scanInterval = 1000;
    private static final int defaultPort = 4445;

    protected void configure(String address, int port) throws P2PDPConnectionException {
        if (NetworkUtil.ipAddressIsNotValid(address) || NetworkUtil.portIsNotValid(port)) {
            throw new P2PDPConnectionException("IP address and/or port are not valid. Port should be a value between 1024 and 65535.");
        }

        //On IP networks, the IP address 255.255.255.255 (in binary, all 1s) is the general 
        //broadcast address. You can't use this address to broadcast a message to every user 
        //on the Internet because routers block it, so all you end up doing is broadcasting it 
        //to all hosts on your own network. 
        broadSendToAddress = new InetSocketAddress(address, port);
        broadReceiveAddress = new InetSocketAddress(port);
    }

    public synchronized void open() throws P2PDPConnectionException {
        try {
            //SENDER CHANNEL
            broadSenderSocket = new DatagramSocket(null);
            broadSenderSocket.setSendBufferSize(UDPDatagramSocket.UDP_SENDER_BUFFER_SIZE);
            //RECEIVER CHANNEL          
         /*
             * For UDP sockets it may be necessary to bind more than one socket to the same socket address. 
             * The SO_REUSEADDR socket option allows multiple sockets to be bound to the same socket address 
             * if the SO_REUSEADDR socket option is enabled prior to binding the socket using bind(SocketAddress).
             * 
             * Don�t set the propertie setSoTimeout()!!!
             * setSoTimeout(): you will receive duplicated messages (? - bug)
             */
            broadReceiverSocket = new DatagramSocket(null);
            broadReceiverSocket.setReuseAddress(true);
            broadReceiverSocket.setBroadcast(true);  // force SO_BROADCAST to TRUE for some java implementations         

            broadReceiverSocket.setReceiveBufferSize(UDPDatagramSocket.UDP_RECEIVER_BUFFER_SIZE);
            //broadReceiverSocket.setSoTimeout(UDPDatagramSocket.UDP_SOCKET_TIMEOUT); //not blocking receive         
            broadReceiverSocket.bind(broadReceiveAddress);

            logger.info("P2PDP Broadcast Connection [" + broadSendToAddress.toString() + "]");

        } catch (IOException ioe) {
            logger.warn("# P2PDPBroadcastConnection: " + ioe.getMessage(), ioe);

            throw new P2PDPConnectionException(ioe);
        }
    }

    protected void setScanInterval(int scanInterval) {
        this.scanInterval = scanInterval;
    }

    public int getScanInterval() {
        return scanInterval;
    }

    public void send(P2PDPMessageInterface msg) throws P2PDPConnectionException {
        if (broadSenderSocket == null) {
            throw new P2PDPConnectionException("P2PDP Broadcast Channel does not exist.");
        }
        P2PDPMessage message = (P2PDPMessage) msg;
        DatagramPacket packet;
        try {
            packet = UDPDatagramPacket.createSendPacket(broadSendToAddress);
            //Caso a maquina tenha mais do que uma interface, a mensagem sera enviada
            //para todas as interfaces uma de cada vez.
            if (LocalHost.hasMultipleInterfaces()) {
                try {
                    String[] networkInterfacesIPAddress = LocalHost.getNetworkInterfacesIPAddress();
                    for (int i = 0; i < networkInterfacesIPAddress.length; i++) {
                        if (broadSenderSocket != null) {
                            broadSenderSocket.close();
                            broadSenderSocket = null;
                        }

                        broadSenderSocket = new DatagramSocket(null);
                        broadSenderSocket.setSendBufferSize(UDPDatagramSocket.UDP_SENDER_BUFFER_SIZE);
                        String outAddress = networkInterfacesIPAddress[i];
                        broadSenderSocket.bind(new InetSocketAddress(outAddress, defaultPort));

                        if (message.isNewMessage()) {
                            message.setDeviceIPAddress(outAddress);
                            //logger.trace("P2PDPMessage created, setting device IP Address: " + outAddress);
                        } else {
                            message.setHopID(outAddress);
                            //logger.trace("The HopID is: " + outAddress + " and the HopCount is: " + message.getHopCount());
                        }
                        packet.setData(UDPDatagramPacket.convertObjectToByteArray(message));
                        broadSenderSocket.send(packet);
                    //logger.trace("> P2PDP Broadcast channel [send] " + msg.getMessageTypeStr() + "<" + msg.getRequestIdentifier() + "> [" + msg.getDeviceIPAddress() + "]");
                    }
                } catch (SocketException ex) {
                    ex.printStackTrace();
                }
            } else {
                if (broadSenderSocket != null) {
                    broadSenderSocket.close();
                    broadSenderSocket = null;
                }

                broadSenderSocket = new DatagramSocket(null);
                broadSenderSocket.setSendBufferSize(UDPDatagramSocket.UDP_SENDER_BUFFER_SIZE);
                String outAddress = LocalHost.getLocalHostAddress();
                broadSenderSocket.bind(new InetSocketAddress(outAddress, defaultPort));
                
                //logger.trace("P2PDPMessage created, setting device IP Address: " + outAddress);
                packet.setData(UDPDatagramPacket.convertObjectToByteArray(message));
                broadSenderSocket.send(packet);
            //logger.trace("> P2PDP Broadcast channel [send] " + msg.getMessageTypeStr() + "<" + msg.getRequestIdentifier() + "> [" + msg.getDeviceIPAddress() + "]");
            }

        } catch (UDPDatagramPacketException UDPDPex) {
            throw new P2PDPConnectionException(UDPDPex);

        } catch (IOException ioex) {
            throw new P2PDPConnectionException(ioex);
        }
    }

    public P2PDPMessageInterface receive() throws P2PDPConnectionException, SocketTimeoutException {
        DatagramPacket packet = UDPDatagramPacket.createReceivePacket();
        P2PDPMessageInterface message = null;

        try {
            if (broadReceiverSocket != null) {
                broadReceiverSocket.receive(packet);

                Object receivedData = UDPDatagramPacket.convertByteArrayToObject(packet.getData());
                if (receivedData instanceof P2PDPMessageInterface) {
                    message = (P2PDPMessageInterface) receivedData;
                //logger.trace("< P2PDP Broadcast channel [receive] " + message.getMessageTypeStr() + "<" + message.getRequestIdentifier() + "> from [" + message.getDeviceIPAddress() + "]");
                }
            }
            return message;

        } catch (SocketTimeoutException stex) {
            throw new SocketTimeoutException();

        } catch (IOException ioex) {
            throw new P2PDPConnectionException(ioex);

        } catch (NullPointerException npex) {
            throw new P2PDPConnectionException(npex);

        } catch (UDPDatagramPacketException UDPDPex) {
            throw new P2PDPConnectionException(UDPDPex);
        }
    }

    public void close() {
        if (broadSenderSocket != null) {
            broadSenderSocket.close();
            broadSenderSocket = null;

            broadSendToAddress = null;
        }

        if (broadReceiverSocket != null) {
            broadReceiverSocket.close();
            broadReceiverSocket = null;
        }

        logger.info("P2PDP Broadcast Connection closed.");
    }
}
