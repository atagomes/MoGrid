/*
 * Created on 03/11/2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package martin.mogrid.entity.proxy.globus;

import java.net.SocketTimeoutException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import martin.mogrid.common.context.MonitoredContext;
import martin.mogrid.common.network.LocalHost;
import martin.mogrid.common.network.NetworkUtil;
import martin.mogrid.common.resource.ResourceDescriptor;
import martin.mogrid.common.resource.ResourceIdentifier;
import martin.mogrid.common.util.MoGridClassLoader;
import martin.mogrid.common.util.MoGridClassLoaderException;
import martin.mogrid.common.util.SystemUtil;
import martin.mogrid.entity.collaborator.CollaboratorP2PDPMessageHandler;
import martin.mogrid.entity.collaborator.registry.RequestResource;
import martin.mogrid.entity.collaborator.registry.ResourceRegistry;
import martin.mogrid.entity.proxy.registry.globus.ProxyResource;
import martin.mogrid.entity.proxy.registry.globus.ProxyResourceRegistry;
import martin.mogrid.p2pdl.api.DiscoveryCollaboratorFacade;
import martin.mogrid.p2pdl.api.RequestIdentifier;
import martin.mogrid.p2pdl.collaboration.criterias.AdmissionController;
import martin.mogrid.p2pdl.collaboration.criterias.AdmissionControllerException;
import martin.mogrid.p2pdl.collaboration.criterias.CollaboratorReplyTimerFunction;
import martin.mogrid.p2pdl.collaboration.scheduler.globus.ProxyCollaborationReplySendingScheduler;
import martin.mogrid.p2pdl.protocol.P2PDPConnectionException;
import martin.mogrid.p2pdl.protocol.P2PDPConnectionFactory;
import martin.mogrid.p2pdl.protocol.P2PDPCoordinationConnection;
import martin.mogrid.p2pdl.protocol.P2PDPProperties;
import martin.mogrid.p2pdl.protocol.message.P2PDPCollaboratorReplyMessage;
import martin.mogrid.p2pdl.protocol.message.P2PDPInitiatorRequestMessage;
import martin.mogrid.p2pdl.protocol.message.P2PDPMessageInterface;
import martin.mogrid.p2pdl.routing.ReversePath;
import martin.mogrid.p2pdl.routing.ReversePathScheduler;
import martin.mogrid.service.contextlistener.ContextListener;
import martin.mogrid.service.contextlistener.ContextListenerException;
import martin.mogrid.service.monitor.ContextParser;
import martin.mogrid.service.monitor.DeviceContext;
import martin.mogrid.service.monitor.DeviceContextHistory;

import org.apache.log4j.Logger;


/**
 * @author   luciana
 * 
 * Created on 26/07/2005
 */
public class ProxyCollaborator implements Runnable,
                                          DiscoveryCollaboratorFacade, 
                                          CollaboratorP2PDPMessageHandler {
      
   //Manutencao do arquivo de log do servico
   private static final Logger logger = Logger.getLogger(ProxyCollaborator.class);  
  
   //Garante uma instancia unica da classe
   private static ProxyCollaborator collaboratorListenerInstance = null;

   //Listener das informacoes de contexto do COLLABORATOR
   private ContextListener contextListener = null; 

   //Lista de recursos locais registrados
   private ResourceRegistry resourceRegistry = null;
   //Lista de associacao entre recursos locais e requisicoes remotas
   private RequestResource  requestResource  = null;

   //Lista de requisicoes de colaboracao remotas (INITIATORs) sendo tratadas pelo COLLABORATOR
   //-> CRep enviadas aguardando por reconhecimento via REbroadcast
   private ProxyCollaborationReplySendingScheduler replySchedulerList = null;  
   //Lista de CRep enviadas aguardando por reconhecimento via REbroadcast
   //private CollaborationReplyAcknowledmentScheduler cRepWaitingForAckList = null;
   
   //Tabela de roteamento indicando o proximo salto para cada INITIATOR
   private ReversePathScheduler reversePathTableScheduler = null; 
   
   //Funcao para calcular o timeout de envio das respostas do COLLABORATOR
   private CollaboratorReplyTimerFunction replyTimer = null;
   
   //Controlador de admissao utilizado para definir a disponibilidade do recurso solicitado
   private AdmissionController admissionController = null;
   //Boa vontade do COLLABORATOR em participar (0, 1]
   private float collaborationLevel = 1;
   // Valor associado ao retardo de transferencia em cada transmissao (em milisegundos)
   private float transferDelay = 10;  
   
   //Objetos associados a execucao do listener
   private Thread collaboratorThread             = null;
   private int    collaboratorThreadScanInterval = 1000;
   
   //Canal de coordenacao do P2PDP (COLLABORATOR <--> COORDINATOR)
   private P2PDPCoordinationConnection coordinationChannel = null;
 
   
   /**
    * Creates a new instance of a Collaborator Listener. 
    *      
    * @param coordinatorPort port in that the Collaborator Listener will receive the
    * broadcast requests and responses for tasks of coordinator
    * @param scanIntervalInMilli interval in that the Context Listener will listener the 
    * monitor notifications (in milliseconds)    
    */
   private ProxyCollaborator() throws ProxyCollaboratorException {   
      resourceRegistry          = new ResourceRegistry();
      reversePathTableScheduler = new ReversePathScheduler();
      requestResource           = new RequestResource();
      
      //Carrega do arquivo de configuracao do protocolo de descoberta a classe responsavel pelo
      //calculo do timer de envio das respostas (mensagens CRep)
      loadCollaboratorReplyTimerFunctionClass();
      
      try {
         coordinationChannel = P2PDPConnectionFactory.create();
         replySchedulerList  = new ProxyCollaborationReplySendingScheduler(coordinationChannel);
         //cRepWaitingForAckList = new CollaborationReplyAcknowledmentScheduler(coordinationChannel);
         start();
         
      } catch (P2PDPConnectionException e) {
         logger.warn("Proxy Collaborator -> Coordination channel creation error: " + e.getMessage(), e);
         throw new ProxyCollaboratorException("It was not possible to create a Collaborator´s coordination channel. Proxy Collaborator initialization was interrupted.", e);
      
      } catch (ContextListenerException cle) {
         logger.warn("Proxy Collaborator -> Context Listener initialization error: " + cle.getMessage(), cle); 
         throw new ProxyCollaboratorException("It was not possible to start Context Listener. Proxy Collaborator initialization was interrupted.", cle);
      }       
   }
   
   private void loadCollaboratorReplyTimerFunctionClass() throws ProxyCollaboratorException {
      P2PDPProperties.load();
      String timerFunctionClass = P2PDPProperties.getTimerFunctionClass();
      try {
         CollaboratorReplyTimerFunction collabRepTimerFunction = (CollaboratorReplyTimerFunction)MoGridClassLoader.load(timerFunctionClass).newInstance();         
         setCollaboratorReplyTimerFunction(collabRepTimerFunction);
         logger.info("Proxy Collaborator is using " + timerFunctionClass);
        
      } catch (NullPointerException e) {
         throw new ProxyCollaboratorException("Proxy Collaborator Reply TimerFunction class ["+timerFunctionClass+"] was not located: " + e.getMessage(), e);
         
      } catch (InstantiationException e) {
         throw new ProxyCollaboratorException("Proxy Collaborator Reply TimerFunction class ["+timerFunctionClass+"] was not located: " + e.getMessage(), e);
         
      } catch (IllegalAccessException e) {
         throw new ProxyCollaboratorException("Proxy Collaborator Reply TimerFunction class ["+timerFunctionClass+"] was not located: " + e.getMessage(), e);
         
      } catch (MoGridClassLoaderException e) {
         throw new ProxyCollaboratorException("Proxy Collaborator Reply TimerFunction class ["+timerFunctionClass+"] was not loaded: " + e.getMessage(), e);        
      }       
   }
   
   /**
    * Get handle to the singleton
    * @return the singleton instance of this class.
    */
   public static synchronized ProxyCollaborator getInstance() throws ProxyCollaboratorException {
      if ( collaboratorListenerInstance == null ) {        
         collaboratorListenerInstance = new ProxyCollaborator();        
      } 
      return collaboratorListenerInstance;
   }
    
   private void start() throws P2PDPConnectionException, ContextListenerException {
      logger.info("Starting Collaborator...");
      
      contextListener = ContextListener.getInstance();
      if( coordinationChannel != null ) {
         collaboratorThreadScanInterval = coordinationChannel.getScanInterval();
      }
      
      if( collaboratorThread == null ) { 
         collaboratorThread = new Thread(this, "CollaboratorListener");
         collaboratorThread.start();
      }
      
      logger.info("Proxy Collaborator running.\n");             
   }
    
   public synchronized void stop() {  
      logger.info("Stopping Proxy Collaborator...");

      if ( contextListener!=null )
         contextListener.stop(); 
      
      collaboratorThread.interrupt();      
      try {
         collaboratorThread.join(collaboratorThreadScanInterval);      
      } catch (InterruptedException e) { 
         logger.warn("Stopping Proxy Collaborator Thread: " + e.getMessage(), e); 
      } finally { 
         collaboratorThread = null;
         if( coordinationChannel != null ) {
            coordinationChannel.close(); 
            coordinationChannel = null;
         }
         logger.info("Proxy Collaborator sttoped.");  
      }
   }   

   public void run() { 
      Thread myThread = Thread.currentThread();
      P2PDPMessageInterface msgReceived = null;
      
      while( collaboratorThread == myThread ) {          
         SystemUtil.sleep(collaboratorThreadScanInterval);  //Wait in milliseconds

         if ( coordinationChannel != null ) {
            try {
                  msgReceived = coordinationChannel.receive();               
                  handlerCoordinationMessage(msgReceived);
               
            } catch (SocketTimeoutException stex) {
               //logger.warn("SocketTimeoutException: " + stex.getMessage(), stex);
               //It doesn´t anything, exception was thow because a value >0 (0=infinite) was atributed a socket.SO_TIMEOUT
               //to evit that the receive method blocks until a datagram was received.            
             
            } catch (P2PDPConnectionException ex) {     
               logger.warn("The Proxy Collaborator was interrupted or it occurred some error receiving datagram from coordinator: " + ex.getMessage(), ex);
               //Util.abnormalExit();
            }
         }
      }
   }  

   /**
    * Parse protocol messages: 
    *
    * @param protMessage - a coordination protocol message
    */
   private void handlerCoordinationMessage(P2PDPMessageInterface protMessage) {      
      if ( protMessage == null ) {
         logger.warn("Error at the Coordination Protocol Message handler: the message is null.");
         return;
      }
         
      String msgIP = protMessage.getDeviceIPAddress();
      //Soh trato as requisicoes(IReq)/respostas(CRep) que NAO foram originadas localmente
      //if ( ! LocalHost.isLocalHostAddress(msgIP) ) {
         int msgType = protMessage.getMessageType();
         switch ( msgType ) { 
            case P2PDPMessageInterface.CP_MSG_IREQ:
               handlerInitiatorRequestMessage(protMessage);
               break;
               
            case P2PDPMessageInterface.CP_MSG_CREP:       
               //TESTE LOCAL
               if ( ! LocalHost.isLocalHostAddress(msgIP) ) 
                  handlerCollaboratorReplyMessage(protMessage);
               break;                    
            
            default:
               break;
         }  
      //}
   }   

   //INICIO - Metodos da interface CollaboratorP2PDPMessageHandler   
   public void handlerInitiatorRequestMessage(P2PDPMessageInterface protMessage) {
      String              localHostAddress = LocalHost.getLocalHostAddress();
      P2PDPInitiatorRequestMessage message = (P2PDPInitiatorRequestMessage)protMessage;
      RequestIdentifier            reqID   = message.getRequestIdentifier();
      
      //(1) Se a mensagem IReq nao for duplicada ela eh tratada pelo COLABORADOR 
      if ( ! isInitiatorRequestMessageDuplicate(reqID) ) { 
         String initiatorIPAddr = message.getDeviceIPAddress();         
         logger.trace("\n< Proxy Collaborator [" + localHostAddress + "] received from [" +initiatorIPAddr+"->"+ message.getHopID()+" : "+message.getHopCount()+"] " + message.getMessageTypeStr() + " message {ReqID: " + reqID+"}");         

         //(2) Increase the hop count: it starts with 0 and goes to REQUEST_DIAMETER (RD)
         //Example: SOURCE (RD=2) -> 0 (HC=1) -> 1 (HC=2) -> 2 (HC=3) [~-> 3]
         message.incHopCount();
         
         //(3) Armazena na tabela de rotas o caminho reverso para a requisição (INICIADOR)         
         String returnPath        = message.getHopID();
         int    currenthopCount   = message.getHopCount();
         int    reqDiameter       = message.getRequestDiameter();
         int    numMaxReplies     = message.getNumMaxReplies(); 
         long   maxReplyDelay     = message.getMaxReplyDelay();  
         ReversePath revPathEntry = new ReversePath(returnPath, numMaxReplies, currenthopCount, reqDiameter);        
         reversePathTableScheduler.addEntry(reqID, revPathEntry, currenthopCount, maxReplyDelay, transferDelay);
         
         //(4) Verifica a "boa vontade" (willingness) do COLABORADOR em atender a requisicoes (==0 -> nao QUER colaborar)
         if ( collaborationLevel != 0 ) {
            replyInitiatorRequestMessage(message);
         } else {
            logger.info ("Proxy Collaborator [" + localHostAddress + "] does NOT WANT collaborate (request id : "+ reqID+")\n");
            logger.debug("Proxy Collaborator [" + localHostAddress + "] does NOT WANT Collaborate (request id : "+ reqID+").");
         }
         
         //(7) Encaminha a requisicao para a vizinhanca utilizando broadcast local 
         forwardInitiatorRequestMessage(message);
          
      } else {
         logger.trace("< Proxy Collaborator [" + localHostAddress + "] received from [" + message.getDeviceIPAddress() + "] [(ph)"+ message.getHopID() +" : "+message.getHopCount()+"] a duplicate " + message.getMessageTypeStr() + " message and Proxy Collaborator discarded it {ReqID: " + reqID+"}");                  
      }
   }      

   public synchronized void replyInitiatorRequestMessage(P2PDPInitiatorRequestMessage iReqMessage) {
      RequestIdentifier reqID = iReqMessage.getRequestIdentifier();
      String initiatorIPAddr  = iReqMessage.getDeviceIPAddress();
      String localHostAddress = LocalHost.getLocalHostAddress();
      
      ResourceIdentifier localResourceID = null;      
      admissionController.setRequest(iReqMessage.getResourceQuery());
      try {
         localResourceID = admissionController.admit();
      } catch (AdmissionControllerException e) {
         logger.warn("Proxy Admission Controller refused request: "+e.getMessage(), e);
      }
      if ( localResourceID != null ) {
         DeviceContextHistory gridContext = contextListener.getDeviceContextHistory();
         Enumeration          IPAddress   = gridContext.keys();
         ipAddressLoop : while( IPAddress.hasMoreElements() ) {
           //Obtem contexto atual do dispositivo
           String        IP         = IPAddress.nextElement().toString(); 
           DeviceContext devContext = contextListener.getDeviceContext(IP);
           if ( ! NetworkUtil.ipAddressIsValid(IP) || devContext == null ) {
              //logger.debug( "Collaborator Thread DeviceContext null ou IP invalido" );
              continue;
           }
           //logger.debug( "Collaborator Thread Passou pelo continue" );
           ResourceDescriptor    resDescription = null;

           ProxyResourceRegistry proxyRegistry  = ProxyResourceRegistry.getInstance();
           ProxyResource proxyResource = null;
           if( localResourceID.isMultiple() ) {
              resDescription = new ResourceDescriptor();
              List resourceIDList = localResourceID.getResourceIdentifierList();
              Iterator iterator  = resourceIDList.iterator();
              //List storeResDescritor = new ArrayList();;
              while( iterator.hasNext() ) {
                 proxyResource = proxyRegistry.get( (ResourceIdentifier)iterator.next() );
                 //logger.info( "*****Resource: " + proxyResource.get( IP ) );
                 if( proxyResource.get( IP ) == null ) {
                    continue ipAddressLoop;
                 } else {
                    resDescription.addResourceDescriptor( proxyResource.get( IP ) );
                 }
              }
              /*for( int i = 0; i < storeResDescritor.size(); i++ ) {
                 logger.info( "i: " + i + " size: " + storeResDescritor.size() );
                 logger.info( "******ResDescritor added: " + ((ResourceDescriptor)storeResDescritor.get(i)).getIdentifier());
                 resDescription.addResourceDescriptor( (ResourceDescriptor)storeResDescritor.get(i) );
              }*/
           } else {            
              proxyResource  = proxyRegistry.get( localResourceID );
              
              resDescription = proxyResource.get( IP );
              if ( resDescription == null ) {
                 //logger.debug( "Collaborator Thread ResourceDescriptor = null" );
                 continue;
              }
           }
           //logger.debug( "Collaborator Thread Criou o ResourceDescriptor" );
                  
           String            nextHopIPAddr   = iReqMessage.getHopID();
           int               numMaxReplies   = iReqMessage.getNumMaxReplies();           
           //Calcula o tempo que deve esperar em funcao da sua "suitability" antes de enviar a resposta de cada colaborador
           replyTimer.configure(devContext, iReqMessage, collaborationLevel, transferDelay);
           long             timeToWait    = replyTimer.getTimeout();
           MonitoredContext monitoredCtxt = replyTimer.getMonitoredContext();

           logger.debug("Collaborator ["+IP+"] [(px)"+localHostAddress+"] CAN collaborate. Collaborator can reply in " + timeToWait + " milliseconds (MaxReplyDelay = " + SystemUtil.convertSecondsToMilliseconds(iReqMessage.getMaxReplyDelay()) +" milliseconds)");
           logger.info("Proxy Collaborator [" + localHostAddress + "] CAN collaborate: " + localResourceID.getResourceIdentifier());
           logger.info("Proxy Collaborator can reply in " + timeToWait + " milliseconds (MaxReplyDelay = " +  SystemUtil.convertSecondsToMilliseconds(iReqMessage.getMaxReplyDelay()) + " milliseconds)\n");
       
           //Monta a mensagem de colaboracao
           P2PDPCollaboratorReplyMessage cRepMessage = new P2PDPCollaboratorReplyMessage(reqID, monitoredCtxt, localResourceID, resDescription, initiatorIPAddr, nextHopIPAddr);    
           cRepMessage.setDeviceIPAddress(devContext.getIPAddress());
           cRepMessage.setDeviceMACAddress(devContext.getMacAddress());
           //Escalona o envio da mensagem
           sendCollaboratorReplyMessage(cRepMessage, timeToWait, numMaxReplies);
           requestResource.put(reqID, localResourceID);
         }
      } else {
         logger.info ("Proxy Collaborator [" + localHostAddress + "] CANNOT collaborate (request id : "+ reqID+")\n");
         logger.debug("Proxy Collaborator [" + localHostAddress + "] CANNOT Collaborate (request id : "+ reqID+").");
      }
   }   
   
   //TODO: TESTAR!!! 
   //Verifico se a mensagem de requisicao (IReq) jah foi tratada pelo COLABORADOR
   private boolean isInitiatorRequestMessageDuplicate(RequestIdentifier reqID) {
      return reversePathTableScheduler.containsEntry(reqID);
   }

   //TODO Melhorar o calculo do tempo de envio do ack...
   /*private void waitForAcknonledment(P2PDPCollaboratorReplyMessage cRepMessage, int currenthopCount, long timeToWait, long maxReplyDelay) {
      //Altera o gateway para o default (broadcast) soh se a mensagem nao receber ACK
      P2PDPCollaboratorReplyMessage replyMessage = cRepMessage;
      replyMessage.setDefaultGatewayIPAddress();
      long timeToWaitForAck = Math.round( timeToWait + ((maxReplyDelay - timeToWait)*1f/currenthopCount) );
      timeToWaitForAck = timeToWaitForAck > maxReplyDelay ? maxReplyDelay : timeToWaitForAck;
      logger.info("Proxy Collaborator wait for ACK in " + Util.convertMillisecondsToSeconds(timeToWaitForAck) + " seconds (Timer to Send = "+ timeToWait+" seconds; MaxReplyDelay = " + maxReplyDelay + " seconds)");      
      cRepWaitingForAckList.schedule(replyMessage, timeToWaitForAck);
   }*/
   
   private synchronized void forwardInitiatorRequestMessage(P2PDPInitiatorRequestMessage message) {
      //OBS:  The hop count is increased in handlerInitiatorRequestMessage() 
      
      //(1) Verify if can forward message: HOP_COUNT <= REQUEST_DIAMETER
      if ( message.canForwardMessage() ) {
         try {
            //(2) Change the identification of the node sending out the message
            message.setHopID(LocalHost.getLocalHostAddress());
            //(3) Send message in local broadcast (coordination channel)
            if( coordinationChannel != null ) {
               coordinationChannel.send(message);
            }
            logger.trace(">> Proxy Collaborator ["+ LocalHost.getLocalHostAddress()+"] forwarded a " + message.getMessageTypeStr() + " message {ReqID: " + message.getRequestIdentifier()+"} (current hop: " + message.getHopCount() + "/"+message.getRequestDiameter()+")");            
           
         } catch (P2PDPConnectionException e) {
            logger.error("!>> Proxy Collaborator ["+ LocalHost.getLocalHostAddress()+"] could not forward a "+message.getMessageTypeStr() + " message {ReqID: " + message.getRequestIdentifier()+"}: " + e.getMessage(), e);                       
         } 
      }
   }

   //TODO melhorar a lógica... principalmente o teste para encaminhamento
   public synchronized void handlerCollaboratorReplyMessage(P2PDPMessageInterface protMessage) {
      P2PDPCollaboratorReplyMessage message = (P2PDPCollaboratorReplyMessage) protMessage;
      RequestIdentifier reqID = message.getRequestIdentifier();
      
      //(*) Verify if the CRep message is in LOOP (duplicate)
      if ( isCollabReplyMessageDuplicate( message ) ) { 
         //(1) Se a mensagem CRep recebida corresponde a alguma enviada anteriormente pelo 
         //    COLABORADOR, eh caracterizado um reconhecimento positivo da CRep (ACK)          
         //    Cancela o envio da mensagem em BROADCAST (nao direcionado) para TODOS os vizinhos
         /*if ( cRepWaitingForAckList.containsCRepScheduled(reqID) ) {
            cRepWaitingForAckList.cancelSchedule(reqID);
         }*/
         logger.debug("!>> Proxy Collaborator ["+ LocalHost.getLocalHostAddress()+"] discard a duplicate "+message.getMessageTypeStr() + " message from ["+message.getDeviceIPAddress()+"] [(ph)"+message.getHopID()+"] {ReqID: "+ reqID+"}");   

      } else { // CRep message is NOT in LOOP
         ReversePath reversePath = null;
         logger.trace("< Proxy Collaborator [" + LocalHost.getLocalHostAddress() + "] received from [" + message.getDeviceIPAddress() + "] a " + message.getMessageTypeStr()+ " message {ReqID: "+ reqID+"} - ResID: " + message.getResourceIdentifier());                      
         //(2) Verify if exist references to Request ID in ReversePathTable
         if ( reversePathTableScheduler.containsEntry(reqID)) {
            reversePath = reversePathTableScheduler.getEntry(reqID);
         }
         //(3) Entrega a mensagem CRep para o modulo de encaminhamento que vai fazer as verificacoes
         //    necessarias para encaminhar a mensagem (broadcast normal ou broadcast direcionado)
         forwardCollaboratorReplyMessage(message, reversePath);
         
         //(4) Se o COLABORADOR estah atendendo a mesma requisicao verifica se ela pode ser suprimida ou nao
         if ( replySchedulerList!=null && replySchedulerList.containsCRepScheduled(reqID)) {             
            replySchedulerList.incNumCRepOverheard(reqID);
            if ( replySchedulerList.canDiscardCRepScheduled(reqID) ) {
               replySchedulerList.cancelSchedule(reqID);
               if ( requestResource!=null && requestResource.containsKey(reqID)) {
                  //recurso jah pode ter sido removido, portanto, verificar resID antes de acessa-lo (caso seja necessario)
                  logger.trace("!> Proxy Collaborator [" + LocalHost.getLocalHostAddress() + "] discard reply to {ReqID: " + reqID+"}");
               }
            }       
         }
      }   
   }      

   //TODO testar!!!
   private boolean isCollabReplyMessageDuplicate(P2PDPCollaboratorReplyMessage message) {      
      return ( LocalHost.isLocalHostAddress(message.getHopID()) );     
   }
   
   //TODO testar!!!
   //     Falta testar de cara o numero de saltos...
   // Faz o encaminhamento da mensagem CRep para os nos intermediarios, verificando se deve ser
   // utilizado broadcast normal ou broadcast direcionado         
   private synchronized void forwardCollaboratorReplyMessage(P2PDPCollaboratorReplyMessage message, ReversePath reversePath) {
      String gatewayIPAddr = message.getGatewayIPAddress();       
      String collabAddress = LocalHost.getLocalHostAddress();
      //(2) Forward message (coordination channel) ONLY if COLLABORATOR is the message´s gateway OR if the message was send to DEFAULT_GATEWAY 
      if ( LocalHost.isLocalHostAddress(gatewayIPAddr) || message.sendToDefaultGateway()) {
         //(2.1) Atualize the value of the last sender nodeID to COLLABORATOR identification
         message.setHopID(collabAddress);
         //(2.2) Increase the message hop count
         message.incHopCount();
         //Exception generated when the message was send: coordinationChannel.send(message)                      
         try {
            //(2.3) If Collaborator is in the reply´s ReturnPath, verify if the number of replies (CRep messages) for this Request (reqID) already was reach
            if ( reversePath!=null ) {
               //(2.3.1) If the sender is in the same hop that me then I DISCART the reply (CRep)
               if ( reversePath.canDiscardReply() ) {
                  logger.trace("!>> Proxy Collaborator [" + collabAddress + "] discard a CollaboratorReply message from ["+message.getDeviceIPAddress()+"], the node is in the same HOP that me - {ReqID: " + message.getRequestIdentifier()+"}");
               
               //(2.3.2) The node overhears replies and atualize its replyCount in PendingList            
               } else {         
                  reversePath.decReplyCount();
                  if ( reversePath.canForwardReply() && coordinationChannel != null ) {
                     //(2.3.2)-> Send message in directed ("direcionado") broadcast to next hop
                     reversePath.addCollab(message.getDeviceIPAddress()); // Adiciono o collab para quem envio na pending list para controlar as duplicadas                                       
                     message.setGatewayIPAddress(reversePath.getReturnPath());                  
                     coordinationChannel.send(message);
                     logger.trace(">> Proxy Collaborator ["+ collabAddress+"] forwarded to ["+reversePath.getReturnPath()+"] a " + message.getMessageTypeStr() + " message {ReqID: " + message.getRequestIdentifier()+"}");                              
                  }    
               }
            } else { //else of (2.3): 
               if ( coordinationChannel != null ) {
                  //(2.3.2) Send message in local broadcast (for ALL neighbors)
                  message.setDefaultGatewayIPAddress();
                  coordinationChannel.send(message);
                  logger.trace(">> Proxy Collaborator [" + collabAddress + "] forwarded to ALL ["+message.getGatewayIPAddress()+"] a " + message.getMessageTypeStr() + " message {ReqID: " + message.getRequestIdentifier()+"}");
               }
            } 
         } catch (P2PDPConnectionException e) {
            logger.error("!>> Proxy Collaborator ["+ collabAddress +"] could not forward a "+message.getMessageTypeStr() + " message {ReqID: "+ message.getRequestIdentifier() + "}: " + e.getMessage(), e);                       
         }
      } else { 
         //else of (2): Discard message ...
         logger.trace("!>> Proxy Collaborator ["+ collabAddress+"] discard a " + message.getMessageTypeStr() + " message from ["+message.getDeviceIPAddress()+"] [(ph)"+message.getHopID()+"], Proxy Collaborator is not the default gateway ["+gatewayIPAddr+"] to request {ReqID: "+ message.getRequestIdentifier()+"}");                        
      }
   }
   
   
   public void sendCollaboratorReplyMessage(P2PDPCollaboratorReplyMessage cRepMessage, long timeToWait, int numMaxReplies) {
      // Escalon envio da mensagem de resposta
      replySchedulerList.schedule(cRepMessage, timeToWait, numMaxReplies);    
      logger.trace("> Proxy Collaborator [" + cRepMessage.getProxyIPAddress() +"] schedule to sent in name of Collaborator ["+ cRepMessage.getDeviceIPAddress()+"] in " + SystemUtil.convertMillisecondsToSeconds(timeToWait) + " seconds a " + cRepMessage.getMessageTypeStr() + " message {ReqID: "+ cRepMessage.getRequestIdentifier() + "} - ResID: " + cRepMessage.getResourceIdentifier());          
   }   
   //FIM - Metodos da interface CollaboratorP2PDPMessageHandler
   

   //INICIO - Metodos da interface DiscoveryCollaboratorFacade
   public ResourceIdentifier register(ResourceDescriptor resourceDescriptor) {
      if ( resourceDescriptor == null ) {
         logger.warn("The registration was not executed, the resource descriptor is null.");
         return null;         
      }

      ResourceIdentifier resID = new ResourceIdentifier(resourceDescriptor.getIdentifier());
      resourceRegistry.put(resID, resourceDescriptor);
      
      logger.debug("Proxy Collaborator [" + LocalHost.getLocalHostAddress() + "] registered resource "+ resID.getResourceIdentifier()+".");
      logger.debug(resourceDescriptor.toString());
      
      return resID;
   }

   public void deregister(ResourceIdentifier resID) {  
      resourceRegistry.remove(resID);

      logger.debug("Proxy Collaborator [" + LocalHost.getLocalHostAddress()  + "] deregistered resource "+ resID.getResourceIdentifier()+".");
      
      if ( requestResource.containsValue(resID) ) {
         RequestIdentifier[] reqID = requestResource.getKeysFromValue(resID);
         for ( int i=0; i<reqID.length; i++ ) {
            replySchedulerList.cancelSchedule(reqID[i]);  
            //TODO TRAS - talvez deva avisar ao coordenador e/ou iniciador 
            //            que o recurso nao estah mais disponivel
         }
      }
   }  
   
   public void deregisterAll() {  
      try {
         for ( Enumeration resKey=resourceRegistry.keys(); resKey.hasMoreElements(); ) {
            ResourceIdentifier resID = (ResourceIdentifier)resKey;
            deregister(resID);  
            
            logger.debug("Proxy Collaborator [" + LocalHost.getLocalHostAddress()  + "] deregistered all resources.");            
         }
      } catch (Exception ex) {
         logger.error("The resources deregistration was not executed.");
      }
   }   
   
   public void setAdmissionController(AdmissionController admController) {
      admController.registerResources(resourceRegistry);
      admissionController = admController;
   }   

   public void setCollaborationLevel(float w) {      
      if      ( w > 1 )  { w = 1.0f; } 
      else if ( w < 0 )  { w = 0.0f; }
      //else if ( w <= 0 ) { w = 0.1f; }
      
      this.collaborationLevel = w;   // willingness
   }
   
   public void setTransferDelay(float s) { 
      if ( s <= 0 ) { s = 2; }      
      transferDelay = s;
   }
   
   public void setCollaboratorReplyTimerFunction(CollaboratorReplyTimerFunction collabRepTimerFunction) {
      replyTimer = collabRepTimerFunction;
   } 
   
   public void setContextParser(ContextParser contextParser) {
      contextListener.setContextParser(contextParser);
   }

   public float getCollaborationLevel() {
      return collaborationLevel;
   }

   public float getTransferDelay() {
      return transferDelay;
   }
   //FIM - Metodos da interface DiscoveryCollaboratorFacade

   public int getMaxForwardRequestDelay() {
      // TODO Auto-generated method stub
      return 0;
   }

   public int getMinForwardRequestDelay() {
      // TODO Auto-generated method stub
      return 0;
   }

   public void setForwardRequestDelay(int minForwardDelay, int maxForwardDelay) {
      // TODO Auto-generated method stub
      
   }

}

