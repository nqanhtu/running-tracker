package com.runningtracker.data.model.running;

public class LocationTObject {

    private double latitudeValue;
    private double longitudeValue;
    private double timeUpdate;

    public LocationTObject() {
    }

    public LocationTObject(double latitudeValue, double longitudeValue, double timeUpdate) {
        this.latitudeValue = latitudeValue;
        this.longitudeValue = longitudeValue;
        this.timeUpdate = timeUpdate;
    }

    public double getLatitudeValue() {
        return latitudeValue;
    }

    public void setLatitudeValue(double latitudeValue) {
        this.latitudeValue = latitudeValue;
    }

    public double getLongitudeValue() {
        return longitudeValue;
    }

    public void setLongitudeValue(double longitudeValue) {
        this.longitudeValue = longitudeValue;
    }

    public double getTimeUpdate() {
        return timeUpdate;
    }

    public void setTimeUpdate(double timeUpdate) {
        this.timeUpdate = timeUpdate;
    }
}
