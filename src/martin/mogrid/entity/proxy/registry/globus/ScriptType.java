package martin.mogrid.entity.proxy.registry.globus;

public class ScriptType {
   
   private String linuxScript;
   private String unixScript;
   
   public ScriptType(String linuxScript, String unixScript) {
      this.linuxScript = linuxScript;
      this.unixScript = unixScript;
   }
   
   public String getLinuxScript() {
      return linuxScript;
   }
   
   public String getUnixScript() {
      return unixScript;
   }
   
   public void setLinuxScript(String linuxScript) {
      this.linuxScript = linuxScript;
   }

   public void setUnixScript(String unixScript) {
      this.unixScript = unixScript;
   }
}
