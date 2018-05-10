package runningtracker.running.model;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;

public interface RunningContract {
    Context getMainActivity();
    void setupViewRunning(float mDistanceValue, float mPaceValue, float mCalorie);
    void startTime();
    float getUpdateTime();
    void pauseTime();
    void stopTime();
    GoogleMap getMap();
    GoogleMap getMapViewFull();
    GoogleMap getMapShare();
}
