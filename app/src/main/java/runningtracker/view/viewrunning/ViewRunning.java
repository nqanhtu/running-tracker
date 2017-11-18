package runningtracker.view.viewrunning;
import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.GoogleMap;

import org.json.JSONException;
import org.json.JSONObject;

public interface ViewRunning {
    Context getMainActivity();//lay view main
    //JSONObject getValueRunning() throws JSONException;//get value running
    void setupViewRunning(float mDistanceValue, float mPaceValue, float mCalorie); //view load update running
    void startTime();//Ham bat dau dem thoi gian chay
    float getUpdateTime();//Lay gia tri thay doi thoi gian
    void pauseTime();//Tam thoi dung thoi gian
    void stopTime();//Dung thoi gian chay
    GoogleMap getMap();//
}
