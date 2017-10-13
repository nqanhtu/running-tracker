package runningtracker.View;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.logging.Handler;

/**
 * Created by Minh Tri on 2017-09-28.
 */

public interface ViewRunning {
    //get value running
    JSONObject getValueRunning() throws JSONException;
    Context getMainActivity();
    //view load update running
    void setupViewRunning(float mDistanceValue, float mPaceValue, float mCalorie);

}
