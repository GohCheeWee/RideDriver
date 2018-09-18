package com.jby.ridedriver.registration.shareObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class AddressObject implements Parcelable {
    private String fullAddress;
    private String streetName;
    private double latitude;
    private double longitude;



    public AddressObject(String fullAddress, String streetName, double latitude, double longitude) {
        this.fullAddress = fullAddress;
        this.streetName = streetName;
        this.latitude = latitude;
        this.longitude = longitude;

    }

    public String getFullAddress() {
        return fullAddress;
    }

    public String getStreetName() {
        return streetName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int arg1) {
        // TODO Auto-generated method stub
        dest.writeString(fullAddress);
        dest.writeString(streetName);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }

    private AddressObject(Parcel in) {
        fullAddress = in.readString();
        streetName = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();

    }

    public static final Creator<AddressObject> CREATOR = new Creator<AddressObject>() {
        public AddressObject createFromParcel(Parcel in) {
            return new AddressObject(in);
        }

        public AddressObject[] newArray(int size) {
            return new AddressObject[size];
        }
    };


}
