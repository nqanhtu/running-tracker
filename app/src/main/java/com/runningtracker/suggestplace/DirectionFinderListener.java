package com.runningtracker.suggestplace;

import java.util.List;

import com.runningtracker.model.suggets_place.Route;

public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}
