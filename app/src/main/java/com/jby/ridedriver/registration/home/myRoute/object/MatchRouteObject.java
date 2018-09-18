package com.jby.ridedriver.registration.home.myRoute.object;

import java.util.ArrayList;

public class MatchRouteObject{
    private String routeID;
    private String pickUpAddress;
    private String dropOffAddress;
    private String date;
    private String time;
    private String note;
    private String matchNum;
    private String routeTitle;
    private String matchRiderID;
    private ArrayList<MatchedRiderDetailObject> matchedRiderDetailObjectArrayList = new ArrayList<>();

    public MatchRouteObject(String routeID, String pickUpAddress, String dropOffAddress, String date, String time, String note,
                            String matchNum, String routeTitle, ArrayList<MatchedRiderDetailObject> matchedRiderDetailObjectArrayList) {
        this.routeID = routeID;
        this.pickUpAddress = pickUpAddress;
        this.dropOffAddress = dropOffAddress;
        this.date = date;
        this.time = time;
        this.note = note;
        this.matchNum = matchNum;
        this.routeTitle = routeTitle;
        this.matchedRiderDetailObjectArrayList = matchedRiderDetailObjectArrayList;

    }

    public String getRouteID() {
        return routeID;
    }

    public String getPickUpAddress() {
        return pickUpAddress;
    }

    public String getDropOffAddress() {
        return dropOffAddress;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getNote() {
        return note;
    }

    public String getMatchNum() {
        return matchNum;
    }

    public void setMatchNum(String matchNum){
        this.matchNum = matchNum;
    }

    public String getRouteTitle() {
        return routeTitle;
    }

    public ArrayList<MatchedRiderDetailObject> getArrayList() {
        return matchedRiderDetailObjectArrayList;
    }

}
