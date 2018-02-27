package runningtracker.presenter.suggest_place;

import java.util.List;

import runningtracker.model.suggets_place.Route;



public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}
