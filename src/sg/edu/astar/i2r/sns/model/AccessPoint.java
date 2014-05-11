package sg.edu.astar.i2r.sns.model;

public class AccessPoint { 
	private String access_point ="";
	private String bssid ="";
	private String network_name ="";
	private boolean login_required = false;
	
	//for visible accesspoint
	private String ssid = "";
	private String capabilities = "";
	int frequency = 0;
	int level = 0;
			
	
	public String getSsid() {
		return ssid;
	}
	public void setSsid(String ssid) {
		this.ssid = ssid;
	}
	public String getCapabilities() {
		return capabilities;
	}
	public void setCapabilities(String capabilities) {
		this.capabilities = capabilities;
	}
	public int getFrequency() {
		return frequency;
	}
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public String getAccess_point() {
		return access_point;
	}
	public void setAccess_point(String access_point) {
		this.access_point = access_point;
	}
	public String getBssid() {
		return bssid;
	}
	public void setBssid(String bssid) {
		this.bssid = bssid;
	}
	public String getNetwork_name() {
		return network_name;
	}
	public void setNetwork_name(String network_name) {
		this.network_name = network_name;
	}
	public boolean getLogin_required() {
		return login_required;
	}
	public void setLogin_required(boolean login_required) {
		this.login_required = login_required;
	}
}
