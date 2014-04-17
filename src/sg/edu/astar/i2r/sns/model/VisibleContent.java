package sg.edu.astar.i2r.sns.model;

import sg.edu.astar.i2r.sns.utility.Constant;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * A class representing a visible access point.
 * A visible access point is one where the user can connect to it on their phone.
 */
public class VisibleContent extends AccessPointContent {
	private int signalLevel;

	public VisibleContent() {
		super();
		signalLevel = Constant.INVALID;
	}
	
	public int getSignalLevel() {
		return signalLevel;
	}

	public void setSignalLevel(int signalLevel) {
		this.signalLevel = signalLevel;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(signalLevel);
		dest.writeInt(ratedSpeed);
		dest.writeByte((byte) (popular ? 1 : 0));
		dest.writeByte((byte) (login ? 1 : 0));
		dest.writeString(ssid);
		dest.writeString(bssid);
		dest.writeString(address);
		dest.writeString(place);
		dest.writeString(floor);
		dest.writeString(room);
	}
	
	// this is used to regenerate the object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<VisibleContent> CREATOR = new Parcelable.Creator<VisibleContent>() {
        public VisibleContent createFromParcel(Parcel in) {
            return new VisibleContent(in);
        }

        public VisibleContent[] newArray(int size) {
            return new VisibleContent[size];
        }
    };

    // Constructor that takes a Parcel and gives an object populated with it's values
    public VisibleContent(Parcel in) {
    	signalLevel = in.readInt();
    	ratedSpeed = in.readInt();
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
