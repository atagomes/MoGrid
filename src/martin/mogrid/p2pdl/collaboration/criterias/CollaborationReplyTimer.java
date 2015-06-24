package martin.mogrid.p2pdl.collaboration.criterias;

import java.util.Vector;

import martin.mogrid.common.context.ContextInformation;
import martin.mogrid.common.context.MonitoredContext;
import martin.mogrid.common.util.SystemUtil;
import martin.mogrid.p2pdl.protocol.message.P2PDPInitiatorRequestMessage;
import martin.mogrid.service.monitor.DeviceContext;

import org.apache.log4j.Logger;

public class CollaborationReplyTimer implements CollaboratorReplyTimerFunction {
    //Manutencao do arquivo de log do servico
    private static final Logger logger = Logger.getLogger(CollaborationReplyTimer.class);    //private static final int DEFAULT_S = 100;             //in milliseconds
    private static final float DEFAULT_S = 0.5f; //in miliseconds
    private DeviceContext devContext = null;
    protected ContextInformation ctxtInfo = null;    //Valores de retorno
    protected MonitoredContext monitoredCtxt = null;
    private long timeToWait = 0;       //in milliseconds
    //Valores auxiliares no calculo do atributo timeToWait
    private float w = 0;        //willingness
    private int n = 0;        //number of resources
    private Vector alfa = null;     //FLOAT - level of availability of each resource (0 < a <= 1)
    private Vector weight = null;     //FLOAT -weight of each resource (relative importance)     
    private float dMax = 0;        //maximum reply delay (in seconds)               
    protected float twoHS = 0;        // 2 * distance in hops (Collab-Ini) * transfer delay  

    public synchronized void configure(DeviceContext devCtxt, P2PDPInitiatorRequestMessage iReqMessage, float willingness, float transferDelay) {
        devContext = devCtxt;
        ctxtInfo = iReqMessage.getContextInfo();
        dMax = iReqMessage.getMaxReplyDelay();


        //TODO corrigir DR, tah gerando valores negativos...
        //O valor de twoHS eh alterado em funcao do numero de saltos que a mensagem jah percorreu
        //Ex:         SOURCE (0)        -> host01 -> host02 -> host03   (NUM_HOPS = 2)
        //    (twoHS = 2*currentHop*10)      20        40       (60)
        //RequestDiameter: 0 = single hop; 0 < n < 255 = multihop controlled; 255: multihop to all
        //float S = ( transferDelay <= 0 ) ? DEFAULT_S : transferDelay;
        //twoHS *= iReqMessage.getHopCount() * S; 
        twoHS = 2 * iReqMessage.getHopCount() * DEFAULT_S;
        //logger.info("*** hopCount: "+iReqMessage.getHopCount());
        // 0 < willingness <= 1: 
        if (willingness > 1) {
            w = 1;
        } //else if ( willingness < 0 )  { w = 0.1f;        } // Se chegou ateh aqui com o valor zero, configura para 0.1
        else {
            w = willingness;
        }

        //Determina o contexto do dispositivo monitorado
        setMonitoredContext();
        //Calcula o tempo de espera para o envio da mensagem de colaborracao
        setTimeout();
    }
    //Funcoes auxiliares
    private synchronized void setMonitoredContext() {
        //esses parametros precisam ser reinicializados toda vez que os valores de alfa, weight e n sao modificados em funcao de um novo contexto
        alfa = new Vector();
        weight = new Vector();
        n = 0;

        //a: in % (0 < a <= 100)
        monitoredCtxt = new MonitoredContext();  //absolute resource value
        if (ctxtInfo == null) {
            return;
        }

        //TODO Como avaliar RSSI?      
        if (ctxtInfo.isConnectivityMonitored()) {
            float a = devContext.getConnectivityLevel();  //in % 
            float p = ctxtInfo.getConnectivityWeight();
            setAlfaANDWeight(a, p);
            float absFreeValue = devContext.getConnectivityValue();
            monitoredCtxt.setConnectivityValue(absFreeValue, p);
            //logger.debug("CI: ctxtInfo.isConnectivityMonitored()"+monitoredCtxt.getConnectivityValue());
            n++;
        }
        if (ctxtInfo.isCpuMonitored()) {
            float a = devContext.getCpuLevel();          //in %
            float p = ctxtInfo.getCpuWeight();
            setAlfaANDWeight(a, p);
            float absFreeValue = devContext.getCpuValue();
            monitoredCtxt.setCpuValue(absFreeValue, p);
            //logger.debug("CI: ctxtInfo.isCpuMonitored()"+monitoredCtxt.getCpuValue());
            n++;
        }
        if (ctxtInfo.isEnergyMonitored()) {
            float a = devContext.getEnergyLevel();       //in %
            float p = ctxtInfo.getEnergyWeight();
            setAlfaANDWeight(a, p);
            float absFreeValue = devContext.getEnergyValue();
            monitoredCtxt.setEnergyValue(absFreeValue, p);
            //logger.debug("CI: ctxtInfo.isEnergyMonitored()"+monitoredCtxt.getEnergyValue());
            n++;
        }
        if (ctxtInfo.isMemoryMonitored()) {
            float a = devContext.getMemoryLevel();       //in % 
            float p = ctxtInfo.getMemoryWeight();
            setAlfaANDWeight(a, p);
            float absFreeValue = devContext.getMemoryValue();
            monitoredCtxt.setMemoryValue(absFreeValue, p);
            //logger.debug("CI: ctxtInfo.isMemoryMonitored()"+monitoredCtxt.getMemoryValue());
            n++;
        }
    //logger.debug("MC: "+ monitoredCtxt.toString());
    }

    protected synchronized void setAlfaANDWeight(float a, float p) {
        alfa.addElement(new Float(a / 100));
        weight.addElement(new Float(p));
    }

    //DR algorithm (Delayed Replies)
    private synchronized void setTimeout() {
        float sum = 0;      // somatorio do (recurso * peso)/somatorio dos pesos
        float sumP = 0;      // somatorio dos pesos associados aos recursos
        //Qto maior for o valor de S menos tempo o no irah esperar para enviar a mensagem de resposta
        //para evitar que sua mensagem seja descartada por timeout
        //float tauMax = SystemUtil.convertSecondsToMilliseconds(dMax) - twoHS;      
        //float tauMax = SystemUtil.convertSecondsToMilliseconds(dMax) / twoHS;
        float dMaxMili = SystemUtil.convertSecondsToMilliseconds(dMax);

        for (int j = 0; j < weight.size(); j++) {
            float p = ((Float) weight.elementAt(j)).floatValue();
            sumP += p;
        }

        logger.info("DMax: " + dMax + " TwoHS: " + twoHS);
        logger.info("Height: " + sumP + " n: " + n);
        for (int i = 0; i < n; i++) { // N = P.size() = alfa.size()
            float a = ((Float) alfa.elementAt(i)).floatValue();
            float p = ((Float) weight.elementAt(i)).floatValue();
            logger.info(n + " > [" + i + "] ALFA: " + a + "  - HEIGHT: " + p);
            if (a < 0) {
                a = 0;
            } else if (a > 1) {
                a = 1;
            }
            sum += ((a * p) / sumP);
        }

        //timeToWait = SystemUtil.convertFloatToLong( ((1 - (w * sum)) * tauMax) );
        //logger.info("*** timeToWait = (1 - ("+w +" * "+sum+")) * "+tauMax+") = "+timeToWait);     
        timeToWait = SystemUtil.convertFloatToLong(((1.0F - (w * sum)) * (dMaxMili - twoHS)));
        timeToWait = timeToWait < 0 ? 0 : timeToWait;
        logger.info("*** timeToWait = (1 - (" + w + " * " + sum + ")) * (" + dMaxMili + " - " + twoHS + ") = " + timeToWait);
    }
    //RETORNO   
    public MonitoredContext getMonitoredContext() {
        return monitoredCtxt;
    }

    /**
     * @return The time that the collaborator need to wait to send a CollaboratorReply message. 
     * The time is in seconds. 
     **/
    public long getTimeout() {
        return timeToWait;  //in seconds
    }
}
