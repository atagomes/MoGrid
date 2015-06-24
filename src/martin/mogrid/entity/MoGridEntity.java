package martin.mogrid.entity;

import martin.mogrid.entity.collaborator.Collaborator;
import martin.mogrid.entity.collaborator.CollaboratorException;
import martin.mogrid.entity.coordinator.Coordinator;
import martin.mogrid.entity.coordinator.CoordinatorException;
import martin.mogrid.entity.initiator.Initiator;
import martin.mogrid.entity.initiator.InitiatorException;

import org.apache.log4j.Logger;

public class MoGridEntity {
   
   //Manutencao do arquivo de log do servico
   private static final Logger logger = Logger.getLogger(MoGridEntity.class);
  
   private static Initiator    initiator    = null;
   private static Coordinator  coordinator  = null;
   private static Collaborator collaborator = null;
 
   public MoGridEntity() {
      try {
         initiator    = Initiator.getInstance();
         coordinator  = Coordinator.getInstance();
         collaborator = Collaborator.getInstance();
         
      } catch (InitiatorException e) {
         logger.error("\nIt was not possible to start Initiator.\n[ERROR] " + e.getMessage());
         
      } catch (CoordinatorException e) {
         logger.error("\nIt was not possible to start Coordinator.\n[ERROR] " + e.getMessage());
         
      } catch (CollaboratorException e) {
         logger.error("\nIt was not possible to start Collaborator.\n[ERROR] " + e.getMessage());         
      }
   }
   
   public static Initiator getInitiator() {
      return initiator;
   }
 
   public static Coordinator getCoordinator() {
      return coordinator;
   }
 
   public static Collaborator getCollaborator() {
      return collaborator;
   }
   
}
