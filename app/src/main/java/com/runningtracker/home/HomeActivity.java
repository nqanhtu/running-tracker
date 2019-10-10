package com.runningtracker.home;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.runningtracker.adapter.ViewPagerAdapter;
import com.runningtracker.NavigationHost;
import com.runningtracker.dashboard.DashboardFragment;
import com.runningtracker.notification.NotificationsFragment;
import com.runningtracker.profile.ProfileFragment;
import com.runningtracker.running.RunningActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import runningtracker.R;

import com.runningtracker.login.LoginActivity;

public class HomeActivity extends AppCompatActivity implements DashboardFragment.OnFragmentInteractionListener, NavigationHost {

    private static final String TAG = "HomeActivity";
    @BindView(R.id.navigation)
    BottomNavigationView bottomNavigation;
    FirebaseAuth auth;
    DashboardFragment dashboardFragment;
    ProfileFragment profileFragment;
    NotificationsFragment notificationsFragment;
    @BindView(R.id.main_view_pager)
    ViewPager viewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            finish();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            setupViewPager(viewPager);
            viewPager.setCurrentItem(0);
            mainApp();
        }
    }


    @Override
    public void onStartRunning() {
        Intent nextActivity = new Intent(HomeActivity.this, RunningActivity.class);
        startActivity(nextActivity);
    }

    /**
     * Navigate to the given fragment.
     *
     * @param fragment       Fragment to navigate to.
     * @param addToBackstack Whether or not the current fragment should be added to the backstack.
     */
    @Override
    public void navigateTo(Fragment fragment, boolean addToBackstack) {
        FragmentTransaction transaction =
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, fragment);

        if (addToBackstack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    private void mainApp() {
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_dashboard:
                        viewPager.setCurrentItem(0);
                        return true;
                    case R.id.navigation_profile:
                        viewPager.setCurrentItem(1);
                        return true;
                    case R.id.navigation_notifications:
                        viewPager.setCurrentItem(2);
                        return true;
                }
                return false;
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        dashboardFragment = new DashboardFragment();
        profileFragment = new ProfileFragment();
        notificationsFragment = new NotificationsFragment();
        adapter.addFragment(dashboardFragment, "");
        adapter.addFragment(profileFragment, "");
        adapter.addFragment(notificationsFragment, "");
        viewPager.setAdapter(adapter);
    }
}
