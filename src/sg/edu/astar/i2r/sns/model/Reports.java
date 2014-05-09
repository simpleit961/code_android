package sg.edu.astar.i2r.sns.model;

public class Reports {
	private Places mPlace;
	private AccessPoint mAccessPoint;
	
	private String reported_at = "";
	private String reported_at_datetime = "";
	private String rating = "";
	private String deviceID = "";
	
	public Places getmPlace() {
		return mPlace;
	}
	public void setmPlace(Places mPlace) {
		this.mPlace = mPlace;
	}
	public AccessPoint getmAccessPoint() {
		return mAccessPoint;
	}
	public void setmAccessPoint(AccessPoint mAccessPoint) {
		this.mAccessPoint = mAccessPoint;
	}
	public String getReported_at() {
		return reported_at;
	}
	public void setReported_at(String reported_at) {
		this.reported_at = reported_at;
	}
	public String getReported_at_datetime() {
		return reported_at_datetime;
	}
	public void setReported_at_datetime(String reported_at_datetime) {
		this.reported_at_datetime = reported_at_datetime;
	}
	public String getRating() {
		return rating;
	}
	public void setRating(String rating) {
		this.rating = rating;
	}
	public String getDeviceID() {
		return deviceID;
	}
	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}
}
