package runningtracker.presenter.suggest_place;


import android.location.Location;

import java.util.List;

public interface SuggestCallback {

    void getListLocation(List<Location> locationList);
}
