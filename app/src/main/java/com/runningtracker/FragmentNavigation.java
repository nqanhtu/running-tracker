package com.runningtracker;

import androidx.fragment.app.Fragment;

public interface FragmentNavigation {

    void navigateTo(Fragment fragment, boolean addToBackStack);

}
