package runningtracker.running.model;

import java.util.List;

import runningtracker.data.model.running.ResultObject;

public interface TrackingHistoryCallback {

    void onSuccessTrackingData(List<ResultObject> resultObject);
}
