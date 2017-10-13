package runningtracker.Presenter.PresenterRunning;


import android.content.Context;
import android.location.Location;
import android.os.Handler;

import org.json.JSONException;
import org.json.JSONObject;

import runningtracker.Model.ModelRunning.M_BodilyCharacteristicObject;


public interface PreRunning {
     //save data in server
    void saveRunnig() throws JSONException;
    //get data to server
    void getData();
    //diastance location
    float  DistanceLocation(Location locationA, Location locationB);
    //
    float RoundAvoid(double value, int places);
    void getBodilyCharacter(M_BodilyCharacteristicObject m_Bodily) throws JSONException;


}
