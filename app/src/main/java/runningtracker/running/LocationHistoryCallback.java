package runningtracker.running;

import java.util.List;

import runningtracker.data.model.running.LocationObject;

public interface LocationHistoryCallback {

    void dataLocation(List<LocationObject> locationObject);
}
