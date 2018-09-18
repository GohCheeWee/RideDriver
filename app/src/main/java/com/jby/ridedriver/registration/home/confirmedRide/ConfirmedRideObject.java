package com.jby.ridedriver.registration.home.confirmedRide;

public class ConfirmedRideObject {
    private String title, pick_up, drop_off, note, confirm_rider, estimate_earn, date, time, id, status;

    public ConfirmedRideObject(String title, String pick_up, String drop_off, String note, String confirm_rider, String estimate_earn, String date,
                               String time, String id, String status) {
        this.title = title;
        this.pick_up = pick_up;
        this.drop_off = drop_off;
        this.note = note;
        this.confirm_rider = confirm_rider;
        this.estimate_earn = estimate_earn;
        this.date = date;
        this.time = time;
        this.id = id;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public String getPick_up() {
        return pick_up;
    }

    public String getDrop_off() {
        return drop_off;
    }

    public String getNote() {
        return note;
    }

    public String getConfirm_rider() {
        return confirm_rider;
    }

    public String getEstimate_earn() {
        return estimate_earn;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }
}
