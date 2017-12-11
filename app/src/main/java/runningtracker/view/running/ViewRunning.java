package runningtracker.view.running;
import android.content.Context;

import com.google.android.gms.maps.GoogleMap;

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
