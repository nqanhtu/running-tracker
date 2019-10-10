package com.runningtracker.model.modelrunning;


/**
 * Created by Anh Tu on 1/30/2018.
 */
public class SuggestLocation {
    private Long id;

    private double latitudeValue;

    private double longitudeValue;

    public SuggestLocation(Long id, double latitudeValue, double longitudeValue) {
        this.id = id;
        this.latitudeValue = latitudeValue;
        this.longitudeValue = longitudeValue;
    }

    public SuggestLocation() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getLatitudeValue() {
        return this.latitudeValue;
    }

    public void setLatitudeValue(double latitudeValue) {
        this.latitudeValue = latitudeValue;
    }

    public double getLongitudeValue() {
        return this.longitudeValue;
    }

    public void setLongitudeValue(double longitudeValue) {
        this.longitudeValue = longitudeValue;
    }
}
