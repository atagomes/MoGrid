package martin.mogrid.entity.proxy.registry.globus;

import java.io.File;

import martin.mogrid.globus.service.dispatcher.JobElements;
import martin.mogrid.globus.service.dispatcher.JobRequestControler;
import martin.mogrid.globus.service.dispatcher.JobSubmitionException;
import martin.mogrid.service.monitor.globus.GlobusDeviceContext;

import org.apache.log4j.Logger;

public class ProxyScriptDispatcher implements Runnable{
   
   private static Logger logger = Logger.getLogger( ProxyScriptDispatcher.class );
   private JobRequestControler jobController;
   private GlobusDeviceContext deviceContext;
   private ProxyResourceDescriptor resValue;
   private String path;
   private boolean scriptState = false;

   public String getPath( GlobusDeviceContext deviceContext, ProxyResourceDescriptor resValue ) {
      jobController = new JobRequestControler();
      
      String so = deviceContext.getSOName().trim();
      String ip = deviceContext.getIPAddress();
      logger.info( "Sending script to: " + ip );
      
      ScriptType script = new ScriptType( resValue.getLinuxScriptPath(), resValue.getUnixScriptPath() );      
      
      JobElements jobElements = null;
      if( so == null )
         return null;
      if( so.equals( "Linux" ) ) {
         File[] scriptFileLinux = new File[1];
         scriptFileLinux[0] = new File(script.getLinuxScript());
         jobElements = new JobElements( scriptFileLinux[0].getName(), scriptFileLinux, null, ip  );         
      } else {
         File[] scriptFileUnix = new File[1];
         scriptFileUnix[0] = new File( script.getUnixScript() );
         jobElements = new JobElements( scriptFileUnix[0].getName(), scriptFileUnix, null, ip );
      }
      
      //logger.info( jobElements );
      //jobController.setJobElements( jobElements );
      //jobThread = new Thread( jobController );
      //jobThread.start();
      
      try {
         jobController.jobRequest( jobElements );
      } catch (JobSubmitionException e) {
         e.printStackTrace();
      }  
      
      //synchronized( jobThread ) {
        // while( !jobController.getStatus() ) {
          //  try {
           //    jobThread.wait( 300 );
           // } catch (InterruptedException e) {
            //   e.printStackTrace();
            //}
        // }
     // }
      scriptState = true;
      return jobController.getStdoutReturn();      
   }

   public void run() {
      path = getPath ( deviceContext, resValue );    
   }
   
   public String getPath() {
      return path;
   }
   
   public boolean getScriptState() {
      return scriptState;
   }
   
   public void setDeviceContext(GlobusDeviceContext deviceContext) {
      this.deviceContext = deviceContext;
   }

   public void setResValeu(ProxyResourceDescriptor resValue) {
      this.resValue = resValue;
   }
}