package com.runningtracker.common;


import android.location.Location;

public class DistanceTwoPoint {

    public DistanceTwoPoint() {
    }

    /**
     * @param : two point location
     * @return : distance between two point
    * */
    public float DistanceLocation(Location locationA, Location locationB) {
        float distance;
        distance = locationA.distanceTo(locationB) / 1000;// change to meter to kilometer
        return distance;
    }
}
