package com.runningtracker.model.modelrunning;


public class DetailRunningObject {
    private double latitudeValue;
    private double longitudeValue;
    private int firstLocation;
    private int idLocation;

    public DetailRunningObject() {
    }

    public DetailRunningObject(double latitudeValue, double longitudeValue, int idLocation, int firstLocation) {
        this.latitudeValue = latitudeValue;
        this.longitudeValue = longitudeValue;
        this.idLocation = idLocation;
        this.firstLocation = firstLocation;
    }

    public int getFirstLocation() {
        return firstLocation;
    }

    public void setFirstLocation(int firstLocation) {
        this.firstLocation = firstLocation;
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

    public int getIdLocation() {
        return idLocation;
    }

    public void setIdLocation(int idLocation) {
        this.idLocation = idLocation;
    }
}
