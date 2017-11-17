package runningtracker.view.viewrunning;
import android.content.Context;
import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;

public interface ViewRunning {
    Context getMainActivity();//lay view main
    JSONObject getValueRunning() throws JSONException;//get value running
    Location getMyLocation();//Lay vi tri nguoi dung
    void setupViewRunning(float mDistanceValue, float mPaceValue, float mCalorie); //view load update running
    void moveCamera(Location location);//duy chuyen camera den vi tri nguoi dung
    void createLocationCallback();//khoi tao bien callback
    void stopLocationUpdates();//Dung lay thay doi vi tri
    void startLocationUpdates();//Bat dau lay su thay doi vi tri

}
