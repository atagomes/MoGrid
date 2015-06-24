/*
 * Created on 07/06/2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package martin.mogrid.common.util;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;

public class ProjectHeader {

   //Manutencao do arquivo de log do servico
   private static final Logger logger = Logger.getLogger(ProjectHeader.class);
   
   private static Calendar calendar    = new GregorianCalendar();
   private static int      currentYear = calendar.get(Calendar.YEAR); 
   
   public static void print() {
      logger.info("");
      logger.info("*********************************************************");
      logger.info("*  MoGrid 1.0 - A Mobile Grid Middleware (2005 - "+currentYear+")  *");
      logger.info("*                                                       *");
      logger.info("*  Luciana dos Santos Lima  (lslima@lncc.br)            *");
      logger.info("*  Antonio Tadeu A. Gomes   (atagomes@lncc.br)          *");
      logger.info("*  Artur Ziviani            (ziviani@lncc.br)           *");
      logger.info("*  Markus Endler            (endler@inf.puc-rio.br)     *");
      logger.info("*  Luiz Fernando G. Soares  (lfgs@inf.puc-rio.br)       *");
      logger.info("*                                                       *");
      logger.info("*  LNCC (National Laboratory for Scientific Computing)  *");
      logger.info("*  PUC-Rio (Catholic University of Rio)                 *");
      logger.info("*  Brazil                                               *"); 
      logger.info("*********************************************************");
      logger.info("\n");
   }

}
