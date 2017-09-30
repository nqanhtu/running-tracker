package runningtracker.View;

import android.content.Context;

import java.util.HashMap;

/**
 * Created by Minh Tri on 2017-09-28.
 */

public interface ViewRunning {
    //get value running
    HashMap<String, String> getValueRunning();
    Context getMainActivity();

}
