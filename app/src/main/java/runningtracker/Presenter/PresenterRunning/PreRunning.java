package runningtracker.Presenter.PresenterRunning;


import android.content.Context;
import android.location.Location;

import org.json.JSONException;

public interface PreRunning {
     //save data in server
    void saveRunnig() throws JSONException;
    //get data to server
    void getData();
    //diastance location
    float  DistanceLocation(Location locationA, Location locationB);


}
