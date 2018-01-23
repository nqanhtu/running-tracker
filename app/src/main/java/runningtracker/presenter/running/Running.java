package runningtracker.Presenter.running;
import android.location.Location;
import org.json.JSONException;
import runningtracker.Model.modelrunning.BodilyCharacteristicObject;
import runningtracker.Model.modelrunning.RunningObject;

public interface Running {
    void saveRunning() throws JSONException;
    void getData();
    float  DistanceLocation(Location locationA, Location locationB);
    float RoundAvoid(double value, int places);
    void getBodilyCharacter(BodilyCharacteristicObject m_Bodily) throws JSONException;
    RunningObject setRunningObject(int runningSessionID, int userID, String startTimestamp, String finishTimestamp, double distanceInKm, int roadGradient, int runOnTreadmill, int netCalorieBurned, int grossCalorieBurned, int flagStatus);
    boolean SaveRunningSession(RunningObject runningObject);
    void initialization();
    void createLocationCallback();
    void createLocationCallbackOffline();
    void onLocationChanged(Location location);
    void moveCamera(Location location);
    void polylineBetweenTwoPoint(Location A, Location B);
    Location getMyLocation();
    void buildLocationSettingsRequest();
    void stopLocationUpdates();
    void startLocationUpdates();
    boolean checkPermissions();
    void createLocationRequest();
    float getCalories();
    float getDisaTance();
    float getPace();
    float getMaxPace();
    void onLocationChangedOffline(Location location);
}
