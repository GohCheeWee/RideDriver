package com.jby.ridedriver.registration.home.confirmedRide.startRoute.object;

import android.os.Parcel;
import android.os.Parcelable;

public class StartRouteRiderObject implements Parcelable {
    private String pickUpLocation, dropOffLocation, phone, dropOffAddress, pickUpAddress, status, matchRideID;
    private String rider_name, profile_pic, fare, payment_method, note;
    private double pickUpDistance, dropOffDistance;

    public StartRouteRiderObject(String pickUpLocation, String dropOffLocation, String phone, String dropOffAddress, String pickUpAddress,
                                 String status, String matchRideID, String rider_name, String profile_pic, String fare,
                                 String payment_method, String note, double pickUpDistance, double dropOffDistance) {

        this.pickUpLocation = pickUpLocation;
        this.dropOffLocation = dropOffLocation;
        this.phone = phone;
        this.dropOffAddress = dropOffAddress;
        this.pickUpAddress = pickUpAddress;
        this.status = status;
        this.matchRideID = matchRideID;
        this.rider_name = rider_name;
        this.profile_pic = profile_pic;
        this.fare = fare;
        this.payment_method = payment_method;
        this.note = note;
        this.pickUpDistance = pickUpDistance;
        this.dropOffDistance = dropOffDistance;
    }

    private StartRouteRiderObject(Parcel in) {
        pickUpLocation = in.readString();
        dropOffLocation = in.readString();
        phone = in.readString();
        dropOffAddress = in.readString();
        pickUpAddress = in.readString();
        status = in.readString();
        matchRideID = in.readString();
        rider_name = in.readString();
        profile_pic = in.readString();
        fare = in.readString();
        payment_method = in.readString();
        note = in.readString();
        pickUpDistance = in.readDouble();
        dropOffDistance = in.readDouble();
    }

    public static final Creator<StartRouteRiderObject> CREATOR = new Creator<StartRouteRiderObject>() {
        @Override
        public StartRouteRiderObject createFromParcel(Parcel in) {
            return new StartRouteRiderObject(in);
        }

        @Override
        public StartRouteRiderObject[] newArray(int size) {
            return new StartRouteRiderObject[size];
        }
    };

    public String getPickUpLocation() {
        return pickUpLocation;
    }

    public String getDropOffLocation() {
        return dropOffLocation;
    }

    public String getPhone() {
        return phone;
    }

    public String getDropOffAddress() {
        return dropOffAddress;
    }

    public String getPickUpAddress() {
        return pickUpAddress;
    }

    public String getStatus() {
        return status;
    }

    public String getMatchRideID() {
        return matchRideID;
    }

    public String getRider_name() {
        return rider_name;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public String getFare() {
        return fare;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public String getNote() {
        return note;
    }

    public double getPickUpDistance() {
        return pickUpDistance;
    }

    public double getDropOffDistance() {
        return dropOffDistance;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(matchRideID);
        parcel.writeString(rider_name);
        parcel.writeString(profile_pic);
        parcel.writeString(pickUpAddress);
        parcel.writeString(dropOffAddress);
        parcel.writeDouble(pickUpDistance);
        parcel.writeDouble(dropOffDistance);
        parcel.writeString(status);
    }
}
