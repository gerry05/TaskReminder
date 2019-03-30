package com.admiral.taskreminder.model;

public class Tasks {
    int id;
    String notes;
    double lat,lon;
    long alarm;
    long end_date;

    public Tasks(int id,String notes,double lat,double lon,long alarm,long end_date) {
        this.notes = notes;
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.alarm = alarm;
        this.end_date = end_date;
    }

    public long getEnd_date() {
        return end_date;
    }

    public void setEnd_date(long end_date) {
        this.end_date = end_date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public long getAlarm() {
        return alarm;
    }

    public void setAlarm(long alarm) {
        this.alarm = alarm;
    }
}
