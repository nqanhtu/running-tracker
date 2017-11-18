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
    boolean SaveRunningSession(RunningObject runningObject);//save database running session
    void initialization();// set up gia tri bien truyen vao
    void createLocationCallback();//ham gan gia tri cho bien callback
    void onLocationChanged(Location location);//ham lay thay doi vi tri
    void moveCamera(Location location);//Ham di chuyen camera den vi tri hien tai
    void polylineBetweenTwoPoint(Location A, Location B);//Ham ve duong di giua 2 diem
    Location getMyLocation();//Lay vị tr tot nhat hien tai
    void buildLocationSettingsRequest();
    void stopLocationUpdates();//Dung viec lay thay doi vi tri
    void startLocationUpdates();//Bat lau lay viec thay doi cap nhat vi tri
    boolean checkPermissions();
    void createLocationRequest();
    float getCalories();//Lay gia tri cua calorie
    float getDisaTance();//Lay gia tri cua DisaTance
    float getPace();//Lay gia tri cua Pace
    float getMaxPace();//Lay gia tri cua MaxPace

}
