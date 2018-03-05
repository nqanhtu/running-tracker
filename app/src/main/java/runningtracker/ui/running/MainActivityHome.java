package runningtracker.ui.running;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import runningtracker.R;
import runningtracker.presenter.main.LogicMain;
import runningtracker.view.running.MainActivity;
import runningtracker.view.running.MainActivityOffline;


public class MainActivityHome extends AppCompatActivity {
    LogicMain main;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        main = new LogicMain();
        main.createLocationRequest();
        main.buildLocationSettingsRequest();
        main.initialization();
       // main.supPortWeather();

    }


   /* @Override
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
    }*/

}
