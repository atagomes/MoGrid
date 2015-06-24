package martin.mogrid.globus.service.mds;

public abstract class MdsConstants {

   public final static String CPU_TOTAL_FREE;
   public final static String CPU_TOTAL_COUNT;
   public final static String CPU_SPEED_MHZ;
   public final static String MEMORY_TOTAL_FREE_MB;
   public final static String MEMORY_TOTAL_SIZE_MB;
   public final static String OPERATIONAL_SYSTEM_NAME;
   
   static {
      CPU_TOTAL_FREE          = "mds-cpu-total-free-1minx100";
      CPU_TOTAL_COUNT         = "mds-cpu-total-count";
      CPU_SPEED_MHZ           = "mds-cpu-speedmhz";
      MEMORY_TOTAL_FREE_MB    = "mds-memory-ram-total-freemb";
      MEMORY_TOTAL_SIZE_MB    = "mds-memory-ram-total-sizemb";
      OPERATIONAL_SYSTEM_NAME = "mds-os-name";
   }
   
}
