package runningtracker.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import runningtracker.R;
import runningtracker.dashboard.DashboardFragment;
import runningtracker.notification.NotificationsFragment;
import runningtracker.profile.ProfileFragment;
import runningtracker.running.RunningActivity;

public class HomeActivity extends AppCompatActivity implements DashboardFragment.OnFragmentInteractionListener {

    @BindView(R.id.navigation)
    BottomNavigationView bottomNavigation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);


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

    }


    @Override
    public void onStartRunning() {
        Intent nextActivity = new Intent(HomeActivity.this, RunningActivity.class);
        startActivity(nextActivity);
    }
}
