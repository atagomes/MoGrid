package martin.mogrid.p2pdl.protocol.message;

import martin.mogrid.common.network.LocalHost;
import martin.mogrid.p2pdl.api.RequestIdentifier;

/**
 * @author lslima
 *
 * Created on 13/07/2005
 */
public abstract class P2PDPMessage implements P2PDPMessageInterface {

    private int msgType;
    private String devIPAddress;
    private String devMACAddress;
    private int hopCount;       // default: source = 0

    private String hopID;          // used to identify the node sending out the message > ID: MAC address, IP address, etc

    private RequestIdentifier requestIdentifier;

    //CONSTRUTOR
    public P2PDPMessage(int msgType) {
        this.msgType = msgType;
        this.devIPAddress = LocalHost.getLocalHostAddress();
        this.devMACAddress = LocalHost.getLocalMacAddress();
        this.hopID = LocalHost.getLocalHostAddress();
        this.hopCount = 0;
    }

    public P2PDPMessage(int msgType, RequestIdentifier requestIdentifier) {
        this(msgType);
        this.requestIdentifier = requestIdentifier;
    }

    //LEITURA
    public int getMessageType() {
        return msgType;
    }

    public String getMessageTypeStr() {
        return CP_MSG_STR[msgType];
    }

    public String getDeviceIPAddress() {
        return devIPAddress;
    }

    public String getDeviceMACAddress() {
        return devMACAddress;
    }

    public RequestIdentifier getRequestIdentifier() {
        return requestIdentifier;
    }

    public String getHopID() {
        return hopID;
    }

    public int getHopCount() {
        return hopCount;
    }

    public boolean isNewMessage() {
        if (hopCount == 0) {
            return true;
        }
        return false;
    }

    //ATRIBUICAO
    public void setMessageType(int msgType) {
        this.msgType = msgType;
    }

    public void setDeviceIPAddress(String devIPAddress) {
        this.devIPAddress = devIPAddress;
    }

    public void setDeviceMACAddress(String devMACAddress) {
        this.devMACAddress = devMACAddress;
    }

    public void setRequestIdentifier(RequestIdentifier requestIdentifier) {
        this.requestIdentifier = requestIdentifier;
    }

    public void setHopID(String hopID) {
        this.hopID = hopID;
    }

    public void incHopCount() {
        hopCount++;
    }

    //IMPRESSAO
    public String toString() {
        String messageStr = "\n[" + CP_MSG_STR[msgType] + "]" +
                "\nDevice IP Address: " + devIPAddress +
                "\nDevice MAC Address: " + devMACAddress +
                "\nRequest Identifier: " + requestIdentifier.getRequestIdentifier() +
                "\nHop ID: " + hopID +
                "\nHop Count: " + hopCount;

        return messageStr;
    }
}
