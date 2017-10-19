package runningtracker.presenter.presenterrunning;


import android.location.Location;

import org.json.JSONException;

import runningtracker.model.modelrunning.M_BodilyCharacteristicObject;

public interface PreRunning {
     //save data in server
    void saveRunning() throws JSONException;
    //get data to server
    void getData();
    //diastance location
    float  DistanceLocation(Location locationA, Location locationB);
    //
    float RoundAvoid(double value, int places);
    void getBodilyCharacter(M_BodilyCharacteristicObject m_Bodily) throws JSONException;

}
