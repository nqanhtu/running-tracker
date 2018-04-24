package runningtracker.suggestplace;


import android.location.Location;

import java.util.List;

public interface SuggestCallback {

    void getListLocation(List<Location> locationList);
}
