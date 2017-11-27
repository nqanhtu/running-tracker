package runningtracker.view.main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import runningtracker.R;
import runningtracker.presenter.main.LogicMain;
import runningtracker.view.running.MainActivity;
import runningtracker.view.running.MainActivityOffline;


public class MainActivityHome extends AppCompatActivity implements ViewMain, DashboardFragment.OnFragmentInteractionListener,  NotificationFragment.OnFragmentInteractionListener{
    LogicMain main;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_profile:
                    ProfileFragment profileFragment = new ProfileFragment();
                    transaction.replace(R.id.content,profileFragment,"Fragment name").commit();
                    return true;
                case R.id.navigation_dashboard:
                    DashboardFragment dashboardFragment = new DashboardFragment();
                    transaction.replace(R.id.content,dashboardFragment,"Fragment name").commit();
                    return true;
                case R.id.navigation_notifications:
                    NotificationFragment notificationFragment = new NotificationFragment();
                    transaction.replace(R.id.content,notificationFragment,"Fragment name").commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        DashboardFragment dashboardFragment = new DashboardFragment();
        transaction.replace(R.id.content,dashboardFragment,"Fragment name").commit();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        main = new LogicMain(this);
        main.createLocationRequest();
        main.buildLocationSettingsRequest();
        main.initialization();

/*        setContentView(R.layout.fragment_dashboard);
        Button startRunning = (Button) findViewById(R.id.bnRunning);
        startRunning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                main.navigationActivity();
            }
        });*/
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onStartRunning() {
        main.onNavigationActivity();
    }

    @Override
    public Context getMainActivity() {
        return MainActivityHome.this;
    }

    @Override
    public void navigationRunning() {
        Intent nextActivity = new Intent(MainActivityHome.this, MainActivity.class);
        startActivity(nextActivity);
    }

    @Override
    public void navigationRunningOffline() {
        Intent nextActivity = new Intent(MainActivityHome.this, MainActivityOffline.class);
        startActivity(nextActivity);
    }

}
