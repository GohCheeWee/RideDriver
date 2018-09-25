package com.jby.ridedriver.registration.home.confirmedRide.startRoute.dialog;

public class RouteScheduleDialogObject {
    private String userID, address, userProfilePicture, distance, status, username;

    public RouteScheduleDialogObject(String userID, String address, String userProfilePicture, String distance, String status, String username) {
        this.userID = userID;
        this.address = address;
        this.userProfilePicture = userProfilePicture;
        this.distance = distance;
        this.status = status;
        this.username = username;
    }

    public String getUserID() {
        return userID;
    }

    public String getAddress() {
        return address;
    }

    public String getUserProfilePicture() {
        return userProfilePicture;
    }

    public String getDistance() {
        return distance;
    }

    public String getStatus() {
        return status;
    }

    public String getUsername() {
        return username;
    }
}
