package runningtracker.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import runningtracker.Adapter.ViewPagerAdapter;
import runningtracker.NavigationHost;
import runningtracker.R;
import runningtracker.dashboard.DashboardFragment;
import runningtracker.login.LoginFragment;
import runningtracker.notification.NotificationsFragment;
import runningtracker.profile.ProfileFragment;
import runningtracker.running.RunningActivity;

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
        setupViewPager(viewPager);
        startMainApp();


        if (savedInstanceState == null) {
            if (auth.getCurrentUser() == null) {
                enableBottomNav(false);
                navigateTo(new LoginFragment(), false);
            } else {
                enableBottomNav(true);
                viewPager.setCurrentItem(0);
                startMainApp();
            }
        }
    }

    @Override
    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
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

    @Override
    public void enableBottomNav(boolean flag) {
        if (flag) {
            bottomNavigation.setVisibility(View.VISIBLE);
        } else {
            bottomNavigation.setVisibility(View.GONE);
        }
    }

    @Override
    public void startMainApp() {
        enableBottomNav(true);
        mainApp();
    }

    public void setSelectedItem() {
        bottomNavigation.setSelectedItemId(R.id.navigation_profile);
    }

    private void mainApp() {
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_dashboard:

                        viewPager.setCurrentItem(0);
//                        DashboardFragment dashboardFragment = new DashboardFragment();
//                        getSupportFragmentManager().beginTransaction().replace(R.id.container, dashboardFragment).commit();
                        return true;
                    case R.id.navigation_profile:
                        viewPager.setCurrentItem(1);
//                        ProfileFragment profileFragment = new ProfileFragment();
//                        getSupportFragmentManager().beginTransaction().replace(R.id.container, profileFragment).commit();
                        return true;
                    case R.id.navigation_notifications:
                        viewPager.setCurrentItem(2);
//                        NotificationsFragment notificationsFragment = new NotificationsFragment();
//                        getSupportFragmentManager().beginTransaction().replace(R.id.container, notificationsFragment).commit();
                        return true;
                }
                return false;
            }
        });
        //bottomNavigation.getChildAt(2);
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
