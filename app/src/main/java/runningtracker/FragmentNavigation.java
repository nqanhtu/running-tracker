package runningtracker;

import android.support.v4.app.Fragment;

public interface FragmentNavigation {

    void navigateTo(Fragment fragment, boolean addToBackStack);

}
