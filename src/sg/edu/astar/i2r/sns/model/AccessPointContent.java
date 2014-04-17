package sg.edu.astar.i2r.sns.model;
	import android.os.Parcelable;
import sg.edu.astar.i2r.sns.utility.Constant;
	
/**
 * An abstract class.
 * This is the superclass of VisibleContent & NearbyContent.
 *
 */
public abstract class AccessPointContent implements Parcelable {
	public int	ratedSpeed;	// 0 - fast, 1 - medium, 2 - slow
	public boolean popular;
	public boolean login;
	public String ssid;
	public String bssid;
	public String address;
	public String place;
	public String floor;
	public String room;

	public AccessPointContent() {
		ratedSpeed = Constant.INVALID;
		popular = false;
		login = false;
		ssid = null;
		bssid = null;
		address = "Current location"; // For testing purpose. Change to null later.
		place = null;
		floor = null;
		room = null;
	}
	
	public boolean hasLogin() {
		return login;
	}

	public void setLogin(boolean login) {
		this.login = login;
	}
	
	public boolean getLogin() {
		return login;
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public String getBssid() {
		return bssid;
	}

	public void setBssid(String bssid) {
		this.bssid = bssid;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public boolean isPopular() {
		return popular;
	}
	
	public void setPopular(boolean popular) {
		this.popular = popular;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public int getRatedSpeed() {
		return ratedSpeed;
	}
	
	public void setRatedSpeed(int ratedSpeed) {
		this.ratedSpeed = ratedSpeed;
	}	
	
	@Override
	public int describeContents() {
		return 0;
	}
}
