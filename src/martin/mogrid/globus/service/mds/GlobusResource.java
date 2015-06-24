package martin.mogrid.globus.service.mds;

public class GlobusResource {
	
	private String hostName;
   private String soName;
	private float freeCpuPercent;
	private float freeMemoryPercent;
	private float freeCpuHz;
	private float freeMemoryKb;
	private float percent;
	
	public GlobusResource( String hostName, float freeCpuPercent, float freeMemoryPercent, float freeCpuHz, float freeMemoryKb, String soName ) {
      this.soName = soName;
		this.hostName = hostName;
		this.freeCpuPercent = freeCpuPercent;
		this.freeMemoryPercent = freeMemoryPercent;
		this.freeCpuHz = freeCpuHz;
		this.freeMemoryKb = freeMemoryKb;
	}

	public GlobusResource(){
		
	}
	
	public void setFreeCpuPercent(float freeCpu){
		this.freeCpuPercent = freeCpu;
	}
	
	public void setFreeMemoryPercent(float freeMemory){
		this.freeMemoryPercent = freeMemory;
	}
	
	public void setFreeCpuHz(float totalCpu){
		this.freeCpuHz = totalCpu;
	}
	
	public void setFreeMemoryKb(float totalMemory){
		this.freeMemoryKb = totalMemory;
	}
	
	public float getFreeCpuPercent(){
		return freeCpuPercent;
	}
	
	public float getFreeMemoryPercent(){
		return freeMemoryPercent;
	}
	
	public float getFreeCpuHz(){
		return freeCpuHz;
	}
	
	public float getFreeMemoryKb(){
		return freeMemoryKb;
	}
	
	public void calculatePercent() {
		percent = ( freeMemoryPercent + freeCpuPercent ) / 2;
	}
	
	public float getPercent() {
		calculatePercent();
		return percent;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

   public String getSoName() {
      return soName;
   }

   public void setSoName(String soName) {
      this.soName = soName;
   }

}
