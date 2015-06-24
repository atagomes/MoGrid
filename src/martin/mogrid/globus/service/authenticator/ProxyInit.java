package martin.mogrid.globus.service.authenticator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import org.apache.log4j.Logger;
import org.globus.common.CoGProperties;
import org.globus.gsi.CertUtil;
import org.globus.gsi.GSIConstants;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.OpenSSLKey;
import org.globus.gsi.TrustedCertificates;
import org.globus.gsi.bc.BouncyCastleCertProcessingFactory;
import org.globus.gsi.bc.BouncyCastleOpenSSLKey;
import org.globus.gsi.proxy.ProxyPathValidator;
import org.globus.gsi.proxy.ProxyPathValidatorException;
import org.globus.gsi.proxy.ProxyPolicyHandler;
import org.globus.gsi.proxy.ext.ProxyCertInfo;
import org.globus.util.Util;


public class ProxyInit implements Runnable {
		
    private static Logger logger = Logger.getLogger( ProxyInit.class );
   
	 private X509Certificate certificate;
	 private int bits  = 512;
	 private int lifetime = 3600 * 12;
    private PrivateKey userKey = null;
    private ProxyCertInfo proxyCertInfo = null;
    private int proxyType;
    private GlobusCredential proxy = null;
    
    public void verify() throws ProxyInitException {	
       TrustedCertificates trustedCerts = TrustedCertificates.getDefaultTrustedCertificates();
	
       if (trustedCerts == null) {
          throw new ProxyInitException("Unable to load CA ceritificates");
       }

       ProxyPathValidator validator = new ProxyPathValidator();

       if (proxyCertInfo != null) {
          String oid = proxyCertInfo.getProxyPolicy().getPolicyLanguage().getId();
          validator.setProxyPolicyHandler(oid, new ProxyPolicyHandler() {
             public void validate(ProxyCertInfo proxyCertInfo,X509Certificate[] certPath,int index)throws ProxyPathValidatorException {
                logger.info("Proxy verify: Ignoring proxy policy");
		    }
		});
	}

	try {
      validator.validate(proxy.getCertificateChain(), trustedCerts.getCertificates());
   } catch (ProxyPathValidatorException e) {
      throw new ProxyInitException( "Proxy path validation failed" );
   }

    }

    public void setBits(int bits) {
	  this.bits = bits;
    }

    public void setLifetime(int lifetime) {
	  this.lifetime = lifetime;
    }

    public void setProxyType(int proxyType) {
	  this.proxyType = proxyType;
    }

    public void setProxyCertInfo(ProxyCertInfo proxyCertInfo) {
	  this.proxyCertInfo = proxyCertInfo;
    }
    
    public void createProxy(String cert,String key,boolean verify, /*boolean globusStyle,*/ String proxyFile,String passPhrase) {
       init(new String [] {cert, key});
       loadCertificate(cert);
       loadKey(key,passPhrase);
       sign();
	
       if (verify) {
          try {
             verify();
             logger.info("Proxy verify OK");
          } catch(Exception e) {
             logger.error("Proxy verify failed: ", e);		
             System.exit(-1);
          }
       }
       OutputStream out = null;
       try {
          out = new FileOutputStream(proxyFile);
          if (!Util.setFilePermissions(proxyFile, 600)) {
             logger.warn("Please check file permissions for your proxy file.");
          }
          proxy.save(out);
       } catch (IOException e) {
          logger.error("Failed to save proxy to a file", e );
          System.exit(-1);
       } finally {
          if (out != null) {
             try { out.close(); } catch(Exception e) {}
          }
       }

    }
    
    public void run() { 
       logger.info( "Starting Proxy Init" );
       int bits         = 512;
       int lifetime     = 3600 * 12;
       boolean verify   = false;
   	 boolean pkcs11   = false;
   	 int proxyType    = -1;
   	
   	 CoGProperties properties = CoGProperties.getDefault();
   
   	 //boolean globusStyle = false;
   	 String proxyFile    = properties.getProxyFile();
   	 String keyFile      = null;
   	 String certFile     = null;
   	
          
   	 if (proxyFile == null) {
   	    error("Proxy file not specified.");
   	 }
       
   	 ProxyInit init = null;
   	 if (pkcs11) {
   	    if (keyFile == null) {
   	       if (certFile == null) {
   	          keyFile = certFile = properties.getDefaultPKCS11Handle();
   	       } else {
   	          keyFile = certFile;
   	       }
   	    } else {
   	        if (certFile == null) {
   	           certFile = keyFile;
   	        }
   	    }
          
   	    try {
   	       Class iClass = Class.forName("org.globus.pkcs11.tools.PKCS11ProxyInit");
   	       init = (ProxyInit)iClass.newInstance();
   	    } catch (ClassNotFoundException e) {
   	       logger.error("Failed to load PKCS11 module.");
   	       System.exit(-1);
   	    } catch (InstantiationException e) {
   	       logger.error("Failed to instantiate PKCS11 module", e );
   	       System.exit(-1);
   	    } catch (IllegalAccessException e) {
   	       logger.error("Failed to initialize PKCS11 module", e );
   	       System.exit(-1);
   	    }
   	 } else {
   	    if (keyFile == null) {
   	       keyFile = properties.getUserKeyFile();
   	    }
   	    if (certFile == null) {
   	       certFile = properties.getUserCertFile();
   	    }
   	    init = new ProxyInit();
   	 }

       CertUtil.init();

       ProxyCertInfo proxyCertInfo = null;
       proxyType = GSIConstants.DELEGATION_FULL;
       init.setBits(bits);
       init.setLifetime(lifetime);
   
       init.setProxyType(proxyType);
       init.setProxyCertInfo(proxyCertInfo);
   	   	  	
       init.createProxy(certFile, keyFile, verify, /*globusStyle,*/ proxyFile, GridAuthentication.getPassPhrase());
    }
    
  
    protected static void error(String error) {       
       logger.error( error );
       System.exit(1);
    }
    
    public void init(String [] args) {
    	   verify(args[1], "User key");
    	   verify(args[0], "User certificate");
    }
       
    private static void verify(String file, String msg) {
       
       if (file == null) 
          error(msg + " not specified.");
       
       File f = new File(file);
    	 if (!f.exists() || f.isDirectory())
    	    error(msg + " not found.");
    }

    public void loadCertificate(String arg) {
       try {
          certificate = CertUtil.loadCertificate(arg);
    	 } catch(IOException e) {
    	    logger.error("Failed to load cert: " + arg );
    	    System.exit(-1);
    	 } catch(GeneralSecurityException e) {
    	    logger.error("Unable to load user certificate", e );
    	    System.exit(-1);
    	 }
    }
      
    public void loadKey(String arg,String passPhrase) {
       try {
          OpenSSLKey key = new BouncyCastleOpenSSLKey(arg);
    	    
    	    if (key.isEncrypted()) {
    	       String pwd = passPhrase;		
    	       key.decrypt(pwd);
    	    }
    	    userKey = key.getPrivateKey();    	    
    	 } catch(IOException e) {
    	    logger.error("Failed to load key: " + arg);
    	    System.exit(-1);
    	 } catch(GeneralSecurityException e) {
    	    logger.error("Wrong pass phrase");
    	    System.exit(-1);
    	 }
    }
    
    public void sign() {
       try {
          BouncyCastleCertProcessingFactory factory =	BouncyCastleCertProcessingFactory.getDefault();
    	    proxy = factory.createCredential(new X509Certificate[] {certificate},userKey,bits,lifetime,proxyType);
    	 }catch(GeneralSecurityException e) {
    	     logger.error("Failed to create a proxy", e );
    	     System.exit(-1);
    	 }
    }
    
}
