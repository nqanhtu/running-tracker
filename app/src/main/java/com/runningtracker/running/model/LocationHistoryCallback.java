package com.runningtracker.running.model;

import java.util.List;

import com.runningtracker.data.model.running.LocationObject;

public interface LocationHistoryCallback {

    void dataLocation(List<LocationObject> locationObject);
}
