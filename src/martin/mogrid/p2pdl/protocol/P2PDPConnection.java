/*
 * Created on 18/08/2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package martin.mogrid.p2pdl.protocol;

public interface P2PDPConnection {
       
   public abstract void open() throws P2PDPConnectionException; //estabelece um canal para comunicacao multicast (endereco e porta definidos no arquivo XML de configuracao do protocolo de comunicacao)
   public abstract void close();

}
