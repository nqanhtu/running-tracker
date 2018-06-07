package runningtracker.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import runningtracker.NavigationHost;
import runningtracker.R;
import runningtracker.dashboard.DashboardFragment;
import runningtracker.login.LoginContract;
import runningtracker.login.LoginFragment;
import runningtracker.notification.NotificationsFragment;
import runningtracker.profile.ProfileFragment;
import runningtracker.running.RunningActivity;

public class HomeActivity extends AppCompatActivity implements DashboardFragment.OnFragmentInteractionListener, NavigationHost, LoginContract {

    @BindView(R.id.navigation)
    BottomNavigationView bottomNavigation;
    FirebaseAuth auth;
    @BindView(R.id.sub_container)
    public ConstraintLayout subContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        auth = FirebaseAuth.getInstance();
        if (savedInstanceState == null) {
            if (auth.getCurrentUser() == null) {
                subContainer.setVisibility(View.GONE);

                LoginFragment loginFragment = new LoginFragment();
                loginFragment.setInterface(this);

                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.container, loginFragment)
                        .commit();

            } else {
                mainApp();
            }
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
    public void loginSuccessed() {
        subContainer.setVisibility(View.VISIBLE);
        mainApp();
    }

    private void mainApp() {
        // Default fragment
        DashboardFragment dashboardFragment = new DashboardFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, dashboardFragment).commit();
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_dashboard:
                        DashboardFragment dashboardFragment = new DashboardFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, dashboardFragment).commit();
                        return true;
                    case R.id.navigation_profile:
                        ProfileFragment profileFragment = new ProfileFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, profileFragment).commit();
                        return true;
                    case R.id.navigation_notifications:
                        NotificationsFragment notificationsFragment = new NotificationsFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, notificationsFragment).commit();
                        return true;
                }
                return false;

            }
        });
        bottomNavigation.getChildAt(2);
    }
}
