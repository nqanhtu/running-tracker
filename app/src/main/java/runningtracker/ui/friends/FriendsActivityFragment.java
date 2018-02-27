package runningtracker.ui.friends;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import runningtracker.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class FriendsActivityFragment extends Fragment {

    public FriendsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }
}
