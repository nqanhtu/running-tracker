package runningtracker.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import runningtracker.NavigationHost;
import runningtracker.R;
import runningtracker.dashboard.DashboardFragment;
import runningtracker.login.LoginFragment;
import runningtracker.notification.NotificationsFragment;
import runningtracker.profile.ProfileFragment;
import runningtracker.running.RunningActivity;

public class HomeActivity extends AppCompatActivity implements DashboardFragment.OnFragmentInteractionListener, NavigationHost {

    @BindView(R.id.navigation)
    BottomNavigationView bottomNavigation;
    FirebaseAuth auth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        auth = FirebaseAuth.getInstance();
        startMainApp();

        if (savedInstanceState == null) {
            if (auth.getCurrentUser() == null) {
                enableBottomNav(false);
                navigateTo(new LoginFragment(), false);
            } else {
                enableBottomNav(true);
                navigateTo(new DashboardFragment(), false);
            }
        }
    }

    @Override
    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
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
                        DashboardFragment dashboardFragment = new DashboardFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, dashboardFragment).commit();
                        return true;
                    case R.id.navigation_profile:
                        ProfileFragment profileFragment = new ProfileFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, profileFragment).commit();
                        return true;
                    case R.id.navigation_notifications:
                        NotificationsFragment notificationsFragment = new NotificationsFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, notificationsFragment).commit();
                        return true;
                }
                return false;
            }
        });
        //bottomNavigation.getChildAt(2);
    }


}
