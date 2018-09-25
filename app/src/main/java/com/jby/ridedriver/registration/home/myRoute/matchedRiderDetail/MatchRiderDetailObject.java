package com.jby.ridedriver.registration.home.myRoute.matchedRiderDetail;

public class MatchRiderDetailObject {
    private String name, profile_picture, gender, pickup, drop_off, note, fare, payment_method, userId, routeID, status;

    public MatchRiderDetailObject(String name, String profile_picture, String gender, String pickup, String drop_off, String note, String fare, String payment_method, String userId, String routeID, String status) {
        this.name = name;
        this.profile_picture = profile_picture;
        this.gender = gender;
        this.pickup = pickup;
        this.drop_off = drop_off;
        this.note = note;
        this.fare = fare;
        this.payment_method = payment_method;
        this.userId = userId;
        this.routeID = routeID;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public String getGender() {
        return gender;
    }

    public String getPickup() {
        return pickup;
    }

    public String getDrop_off() {
        return drop_off;
    }

    public String getNote() {
        return note;
    }

    public String getFare() {
        return fare;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public String getUserId() {
        return userId;
    }

    public String getRouteID() {
        return routeID;
    }

    public String getStatus() {
        return status;
    }
}
