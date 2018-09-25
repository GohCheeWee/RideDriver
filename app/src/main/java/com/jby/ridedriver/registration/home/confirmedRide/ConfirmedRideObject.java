package com.jby.ridedriver.registration.home.confirmedRide;

public class ConfirmedRideObject {
    private String title, pick_up, drop_off, note, confirm_rider, estimate_earn, date, time, status, driver_ride_id, number_people;

    public ConfirmedRideObject(String title, String pick_up, String drop_off, String note, String confirm_rider, String number_people,
                               String estimate_earn, String date, String time, String driver_ride_id, String status) {
        this.title = title;
        this.pick_up = pick_up;
        this.drop_off = drop_off;
        this.note = note;
        this.confirm_rider = confirm_rider;
        this.number_people = number_people;
        this.estimate_earn = estimate_earn;
        this.date = date;
        this.time = time;
        this.driver_ride_id = driver_ride_id;
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

    public String getStatus() {
        return status;
    }

    public String getDriver_ride_id() {
        return driver_ride_id;
    }

    public String getNumber_people() {
        return number_people;
    }

}
