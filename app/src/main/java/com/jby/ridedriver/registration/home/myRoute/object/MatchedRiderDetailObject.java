package com.jby.ridedriver.registration.home.myRoute.object;

import android.os.Parcel;
import android.os.Parcelable;

public class MatchedRiderDetailObject implements Parcelable {
    private String rideID;

    public MatchedRiderDetailObject(String rideID) {
        this.rideID = rideID;
    }

    public String getRideID() {
        return rideID;
    }

    private MatchedRiderDetailObject(Parcel in) {
        rideID = in.readString();
    }

    public static final Creator<MatchedRiderDetailObject> CREATOR = new Creator<MatchedRiderDetailObject>() {
        @Override
        public MatchedRiderDetailObject createFromParcel(Parcel in) {
            return new MatchedRiderDetailObject(in);
        }

        @Override
        public MatchedRiderDetailObject[] newArray(int size) {
            return new MatchedRiderDetailObject[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(rideID);
    }
}
