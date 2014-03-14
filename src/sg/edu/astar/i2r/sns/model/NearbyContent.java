package sg.edu.astar.i2r.sns.model;

import sg.edu.astar.i2r.sns.utility.Constant;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class represents the object for a nearby access point.
 * A near by access point is one which is near the user but they can not detect it on their phone.
 */
public class NearbyContent extends AccessPointContent {
	private double latitude;
	private double longitude;
	private int distance;
	
	public NearbyContent() {
		super();
		latitude = Constant.INVALID;
		longitude = Constant.INVALID;
		distance = Constant.INVALID;
	}

	public double getLatitude() {
		return latitude;
	}


	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}


	public double getLongitude() {
		return longitude;
	}


	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}
	
	// These following methods are needed to pass this object between the wifi list fragment and the detail activity
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(latitude);
		dest.writeDouble(longitude);
		dest.writeInt(ratedSpeed);
		dest.writeInt(distance);
		dest.writeByte((byte) (popular ? 1 : 0));
		dest.writeByte((byte) (login ? 1 : 0));
		dest.writeString(ssid);
		dest.writeString(bssid);
		dest.writeString(address);
		dest.writeString(place);
		dest.writeString(floor);
		dest.writeString(room);
	}
	
	// this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<NearbyContent> CREATOR = new Parcelable.Creator<NearbyContent>() {
        public NearbyContent createFromParcel(Parcel in) {
            return new NearbyContent(in);
        }

        public NearbyContent[] newArray(int size) {
            return new NearbyContent[size];
        }
    };

    // Constructor that takes a Parcel and gives you an object populated with it's values
    public NearbyContent(Parcel in) {
    	latitude = in.readDouble();
    	longitude = in.readDouble();
    	ratedSpeed = in.readInt();
    	distance = in.readInt();
		popular = in.readByte() != 0;
		login = in.readByte() != 0;
		ssid = in.readString();
		bssid = in.readString();
		address =in.readString();
		place = in.readString();
		floor = in.readString();
		room = in.readString();
    }
}
