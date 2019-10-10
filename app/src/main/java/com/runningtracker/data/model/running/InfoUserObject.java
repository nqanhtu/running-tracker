package com.runningtracker.data.model.running;

public class InfoUserObject {

    private String birthday;
    private float heartRate;
    private float height;
    private float weight;

    public InfoUserObject(String birthday, float heartRate, float height, float weight) {
        this.birthday = birthday;
        this.heartRate = heartRate;
        this.height = height;
        this.weight = weight;
    }

    public InfoUserObject() {
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public float getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(float heartRate) {
        this.heartRate = heartRate;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }
}
