package martin.mogrid.entity;

import martin.mogrid.entity.collaborator.Collaborator;
import martin.mogrid.entity.collaborator.CollaboratorException;
import martin.mogrid.entity.initiatorcoordinator.InitiatorCoordinator;
import martin.mogrid.entity.initiatorcoordinator.InitiatorCoordinatorException;

import org.apache.log4j.Logger;

public class MoGridAdHocEntity {
   
   //Manutencao do arquivo de log do servico
   private static final Logger logger = Logger.getLogger(MoGridAdHocEntity.class);
  
   private static InitiatorCoordinator initiator    = null;
   private static Collaborator         collaborator = null;
 
   public MoGridAdHocEntity() {
      try {
         initiator    = InitiatorCoordinator.getInstance();
         collaborator = Collaborator.getInstance();
         
      } catch (InitiatorCoordinatorException e) {
         logger.error("\nIt was not possible to start Initiator-Coordinator.\n[ERROR] " + e.getMessage());
                  
      } catch (CollaboratorException e) {
         logger.error("\nIt was not possible to start Collaborator.\n[ERROR] " + e.getMessage());         
      }
   }
   
   public static InitiatorCoordinator getInitiator() {
      return initiator;
   }
   public static Collaborator getCollaborator() {
      return collaborator;
   }
   
}
