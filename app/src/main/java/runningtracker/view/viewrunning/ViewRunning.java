package runningtracker.view.viewrunning;
import android.content.Context;
import org.json.JSONException;
import org.json.JSONObject;

public interface ViewRunning {
    Context getMainActivity();
    //get value running
    JSONObject getValueRunning() throws JSONException;
    //view load update running
    void setupViewRunning(float mDistanceValue, float mPaceValue, float mCalorie);
}
