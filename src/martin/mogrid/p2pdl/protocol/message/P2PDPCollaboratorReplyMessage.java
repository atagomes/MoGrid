package martin.mogrid.p2pdl.protocol.message;

import martin.mogrid.common.context.MonitoredContext;
import martin.mogrid.common.network.LocalHost;
import martin.mogrid.common.resource.ResourceDescriptor;
import martin.mogrid.common.resource.ResourceIdentifier;
import martin.mogrid.common.util.SystemUtil;
import martin.mogrid.p2pdl.api.RequestIdentifier;
import martin.mogrid.p2pdl.protocol.P2PDPProperties;

/**
 * @author   luciana
 */
public class P2PDPCollaboratorReplyMessage extends P2PDPMessage {

    /** Comment for <code>serialVersionUID</code> */
    private static final long serialVersionUID = -3108641701881978507L;

    //Default gateway to send P2PDP messages (normal broadcast): EX:  = "255.255.255.255"   
    private final String DEFAULT_GATEWAY;
    private MonitoredContext monitoredCtxt = null;
    private ResourceIdentifier resIdentifier = null;
    private ResourceDescriptor resDescription = null;
    private String initiatorIPAddress = null;
    private String gatewayIPAddress = null;  // used to direct the broadcast response (forward CRep)
    // Enderecos relativos ao CollaboratorProxy, que pode ser usado em uma grade fixa ou 
    // em uma rede movel infra-estruturada
    private String proxyIPAddress = LocalHost.getLocalHostAddress();

    public P2PDPCollaboratorReplyMessage() {
        super(CP_MSG_CREP);
        //init();
        this.DEFAULT_GATEWAY = getDefaultGatewayValue();
    }
    
    /*private void init() {
        P2PDPProperties.load();
        String[] addr = P2PDPProperties.getNetworkInterfaceAddresses();
        System.out.println("Colaborator Message: " + addr.length);
        for (int i = 0; i < addr.length; i++) {
            System.out.println("Colaborator Message: " + addr[i]);
            addresses += addr[i];
        }
    }
    
    private String addresses;

    public String getAddresses() {
        return addresses;
    }*/

    public P2PDPCollaboratorReplyMessage(RequestIdentifier requestIdentifier, MonitoredContext monitoredCtxt, ResourceIdentifier resIdentifier, ResourceDescriptor resDescription, String initiatorIPAddress, String nextHopAddress) {
        super(CP_MSG_CREP, requestIdentifier);
        //init();
        this.monitoredCtxt = monitoredCtxt;
        this.resIdentifier = resIdentifier;
        this.resDescription = resDescription;
        this.initiatorIPAddress = initiatorIPAddress;
        this.gatewayIPAddress = nextHopAddress;
        this.DEFAULT_GATEWAY = getDefaultGatewayValue();
    }

    public P2PDPCollaboratorReplyMessage(RequestIdentifier requestIdentifier, MonitoredContext monitoredCtxt, ResourceIdentifier resIdentifier, ResourceDescriptor resDescription, String proxyIPAddress, String initiatorIPAddress, String nextHopAddress) {
        this(requestIdentifier, monitoredCtxt, resIdentifier, resDescription, initiatorIPAddress, nextHopAddress);
        this.proxyIPAddress = proxyIPAddress;
    }

    private String getDefaultGatewayValue() {
        P2PDPProperties.load();
        return P2PDPProperties.getCoordinationAddress();
    }

    //LEITURA
    /**
     * @return   Returns the monitoredCtxt.
     * @uml.property   name="monitoredCtxt"
     */
    public MonitoredContext getMonitoredContext() {
        return monitoredCtxt;
    }

    public ResourceIdentifier getResourceIdentifier() {
        return resIdentifier;
    }

    public ResourceDescriptor getResourceDescriptor() {
        return resDescription;
    }

    public String getGatewayIPAddress() {
        return gatewayIPAddress;
    }

    public String getInitiatorIPAddress() {
        return initiatorIPAddress;
    }

    public String getProxyIPAddress() {
        return proxyIPAddress;
    }

    //ATRIBUICAO     
    /**
     * @param monitoredCtxt   The monitoredCtxt to set.
     * @uml.property   name="monitoredCtxt"
     */
    public void setMonitoredContext(MonitoredContext monitoredCtxt) {
        this.monitoredCtxt = monitoredCtxt;
    }

    public void setGatewayIPAddress(String gatewayIPAddress) {
        this.gatewayIPAddress = gatewayIPAddress;
    }

    public void setDefaultGatewayIPAddress() {
        this.gatewayIPAddress = DEFAULT_GATEWAY;
    }

    public void setProxyIPAddress(String proxyIPAddress) {
        this.proxyIPAddress = proxyIPAddress;
    }

    //TESTE
    public boolean sendToDefaultGateway() {
        if (SystemUtil.strIsNotNull(gatewayIPAddress) && gatewayIPAddress.equalsIgnoreCase(DEFAULT_GATEWAY)) {
            return true;
        }
        return false;
    }

    //IMPRIME
    public String toString() {
        String messageStr = super.toString() +
                "\nMonitored Context: " + monitoredCtxt.toString() +
                "\nResource Descriptor: " + resDescription.toString() +
                "\nGateway IP Address: " + gatewayIPAddress +
                "\nProxy IP Address: " + proxyIPAddress;

        return messageStr;
    }
}
