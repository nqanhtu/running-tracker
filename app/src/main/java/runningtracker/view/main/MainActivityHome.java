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
import runningtracker.Presenter.main.LogicMain;
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
        main.supPortWeather();
/*      setContentView(R.layout.fragment_dashboard);
        Button startRunning = (Button) findViewById(R.id.bnRunning);
        startRunning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                main.navigationActivity();
            }
        });*/

        //insert data demo
      /*  DatabaseRunningLocation databaseRunningLocation = new DatabaseRunningLocation(this);
        RunningLocationObject runningLocationObject = new RunningLocationObject();
        DetailRunningObject detailRunningObject = new DetailRunningObject();

        *//*databaseRunningLocation.deleteAllDetail();
        databaseRunningLocation.deleteAll();*//*

        for(int i = 1; i <= 3; i++){
            runningLocationObject.setName("abc");
            runningLocationObject.setType(i);
            databaseRunningLocation.addNewRunningLocation(runningLocationObject);
        }
        for(int i = 1; i <= 9; i++){
            if(i<=3){
                if(i==1) {
                    detailRunningObject.setLatitudeValue(10.765339);
                    detailRunningObject.setLongitudeValue(106.662921);
                    detailRunningObject.setFirstLocation(1);
                    detailRunningObject.setIdLocation(1);
                    databaseRunningLocation.addLocation(detailRunningObject);
                }
                else{
                    detailRunningObject.setLatitudeValue(10.766492);
                    detailRunningObject.setLongitudeValue(106.664975);
                    detailRunningObject.setFirstLocation(0);
                    detailRunningObject.setIdLocation(1);
                    databaseRunningLocation.addLocation(detailRunningObject);
                }
            }
            else if(i> 3 && i<=6){
                if(i==4) {
                    detailRunningObject.setLatitudeValue(10.775192);
                    detailRunningObject.setLongitudeValue(106.652864);
                    detailRunningObject.setFirstLocation(1);
                    detailRunningObject.setIdLocation(2);
                    databaseRunningLocation.addLocation(detailRunningObject);
                }
                else{
                    detailRunningObject.setLatitudeValue(10.775010);
                    detailRunningObject.setLongitudeValue(106.652853);
                    detailRunningObject.setFirstLocation(0);
                    detailRunningObject.setIdLocation(2);
                    databaseRunningLocation.addLocation(detailRunningObject);
                }
            }
            else{
                if(i==4) {
                    detailRunningObject.setLatitudeValue(10.785968);
                    detailRunningObject.setLongitudeValue(106.665097);
                    detailRunningObject.setFirstLocation(1);
                    detailRunningObject.setIdLocation(3);
                    databaseRunningLocation.addLocation(detailRunningObject);
                }
                else{
                    detailRunningObject.setLatitudeValue(10.785702);
                    detailRunningObject.setLongitudeValue(106.664926);
                    detailRunningObject.setFirstLocation(0);
                    detailRunningObject.setIdLocation(3);
                    databaseRunningLocation.addLocation(detailRunningObject);
                }
            }
        }
*/
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
