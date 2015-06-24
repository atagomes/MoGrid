package martin.mogrid.p2pdl.collaboration.criterias;

import java.util.Random;

import martin.mogrid.common.network.LocalHost;
import martin.mogrid.common.util.SystemUtil;
import martin.mogrid.p2pdl.protocol.message.P2PDPInitiatorRequestMessage;
import martin.mogrid.service.monitor.DeviceContext;
import martin.mogrid.service.monitor.moca.MoCADeviceContext;



public class NCTUnsCollaborationReplyTimer extends CollaborationReplyTimer {  
   
   private float maxReplyDelay; 
   
   private String[] localAddress = LocalHost.getLocalHostAddress().split("\\.");
   private int nodeID;
   private Random rand;
   
   //Garante que o SEED nao serah igual quando mais de um colaborador eh executado 
   //em uma mesma maquina (EX: simulador NCTUns)
   {
      try {
         nodeID = Integer.parseInt(localAddress[2]) + Integer.parseInt(localAddress[3]);
         rand = new Random(System.currentTimeMillis() * nodeID);
      //TODO otimizar tratamento de possivel excecao 
      } catch (Exception ex) {
         nodeID = Integer.parseInt(localAddress[3]);
         rand = new Random(System.currentTimeMillis() * nodeID);      
      }  
   }
 
   public synchronized void configure(DeviceContext devCtxt, P2PDPInitiatorRequestMessage iReqMessage, float willingness, float transferDelay) {
      super.configure(devCtxt, iReqMessage, willingness, transferDelay);
      maxReplyDelay = iReqMessage.getMaxReplyDelay(); // - twoHS      
   }

   /**
    * @return The time that the collaborator need to wait to send a CollaboratorReply message. 
    * The time is in seconds. 
    **/
   public long getTimeout() {      
      //Returns the next pseudorandom, uniformly distributed float  value between 0.0 and 1.0 
      //from this random number generator's sequence:
      // => 0.0f (inclusive) to 1.0f (exclusive)
      float increment  = Math.max(rand.nextFloat(), 0.1f); // => 0.1f (inclusive) to 1.0f (exclusive)
      float timeToWait = increment*maxReplyDelay;
      timeToWait = SystemUtil.convertSecondsToMilliseconds(timeToWait);
      long timeToWaitInMilli = SystemUtil.convertFloatToLong(timeToWait);
      return timeToWaitInMilli;  //in milliseconds
   }
   
   
   //TESTE
   public static void main(String[] args) {
      NCTUnsCollaborationReplyTimer nova = new NCTUnsCollaborationReplyTimer();
      nova.configure(new MoCADeviceContext(), new P2PDPInitiatorRequestMessage(), 1, 2);
      for (int i=0; i<20; i++)
      System.out.println(nova.getTimeout());
   }
   
}
