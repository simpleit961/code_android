package sg.edu.astar.i2r.sns.model;

public class AccessPoint { 
	private String access_point ="";
	private String bssid ="";
	private String network_name ="";
	private String login_required ="";
	
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
	public String getLogin_required() {
		return login_required;
	}
	public void setLogin_required(String login_required) {
		this.login_required = login_required;
	}
}
