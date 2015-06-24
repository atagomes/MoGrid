package martin.app.bagoftask.ns;

import java.io.File;


public class NSScriptProperties {
   
   private String simScript;
   private int    nMin;
   private int    nMax;
   private int    nStep;
   private int    pMin;
   private int    pMax;
   private int    pStep;
   private int    roundMin;
   private int    roundMax;
   private File simScriptPath;
   
   /*private NSScriptProperties(String simScript, int nMin, int nMax, int nStep, int pMin, int pMax, int pStep, int roundMin, int roundMax, File simScriptPath) {
      this.simScript = simScript;
      this.nMin = nMin;
      this.nMax = nMax;
      this.nStep = nStep;
      this.pMin = pMin;
      this.pMax = pMax;
      this.pStep = pStep;
      this.roundMin = roundMin;
      this.roundMax = roundMax;
      this.simScriptPath = simScriptPath;
   }*/
   
   public NSScriptProperties() {
      
   }
   
   public int getNumOfJobs() {
	   return NSScriptSender.getNumOfJobs( this );
   }

   public int getNMax() {
      return nMax;
   }
   
   public int getNMin() {
      return nMin;
   }
   
   public int getPMax() {
      return pMax;
   }
   
   public int getPMin() {
      return pMin;
   }
   public int getPStep() {
      return pStep;
   }
   
   public int getRoundMin() {
      return roundMin;
   }
   
   public int getRoundMax() {
	   return roundMax;
   }
      
   public String getSimScript() {
      return simScript;
   }
   
   public File getSimScriptPath() {
      return simScriptPath;
   }
   
   public void setNMax(int max) {
      nMax = max;
   }
   
   public void setNMin(int min) {
      nMin = min;
   }
   
   public void setPMax(int max) {
      pMax = max;
   }
   public void setPMin(int min) {
      pMin = min;
   }
   
   public void setPStep(int step) {
      pStep = step;
   }
   
   public void setRoundMin(int roundMin ) {
      this.roundMin = roundMin;
   }
   
   public void setRoundMax(int roundMax ) {
	   this.roundMax = roundMax;
   }
   
   public void setSimScript(File simScript) {
      simScriptPath = simScript;
      this.simScript = simScript.getName();
   }

   public int getNStep() {
      return nStep;
   }

  
   public void setNStep(int step) {
      nStep = step;
   }
  
   /*public static NSScriptProperties generateScriptSingleRoundProperties(NSScriptProperties nsScriptProperties, int i) {
       return new NSScriptProperties( nsScriptProperties.getSimScript(),
                                      nsScriptProperties.getNMin(),
                                      nsScriptProperties.getNMax(),
                                      nsScriptProperties.getNStep(), 
                                      nsScriptProperties.getPMin(),
                                      nsScriptProperties.getPMax(),
                                      nsScriptProperties.getPStep(),
                                      i,
                                      nsScriptProperties.getSimScriptPath());      
   }
    
   public String getArguments() {   
      return " " + simScript + " " +
                    nMin      + " " +
                    nMax      + " " +
                    nStep     + " " +
                    pMin      + " " +
                    pMax      + " " +
                    pStep     + " " +
                    rounds  + " "; 
   }*/
     
}
