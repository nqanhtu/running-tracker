package runningtracker.presenter.presenterrunning;
import android.location.Location;
import org.json.JSONException;
import runningtracker.model.modelrunning.BodilyCharacteristicObject;
import runningtracker.model.modelrunning.RunningObject;

public interface Running {
    void saveRunning() throws JSONException;//save data in server
    void getData();//get data to server
    float  DistanceLocation(Location locationA, Location locationB);//diastance location
    float RoundAvoid(double value, int places);
    void getBodilyCharacter(BodilyCharacteristicObject m_Bodily) throws JSONException;
    RunningObject setRunningObject(int runningSessionID, int userID, String startTimestamp, String finishTimestamp, double distanceInKm, int roadGradient, int runOnTreadmill, int netCalorieBurned, int grossCalorieBurned, int flagStatus);
    //save database running session
    boolean SaveRunningSession(RunningObject runningObject);
}
