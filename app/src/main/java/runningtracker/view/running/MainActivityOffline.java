package runningtracker.view.running;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;

import org.json.JSONException;

import java.util.Calendar;
import java.util.Date;

import runningtracker.R;
import runningtracker.model.modelrunning.DatabaseLocation;
import runningtracker.presenter.running.LogicRunning;


public class MainActivityOffline extends AppCompatActivity implements ViewRunning{
    private static final String TAG = MainActivityOffline.class.getSimpleName();
    Date startCurrentTime, stopCurrentTime;
    float rGrossCalorie;
    TextView txtTimer;
    long lStartTime, rUpdateTime, lPauseTime, lSystemTime = 0L;
    Handler handler = new Handler();
    boolean isRun;
    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            lSystemTime = SystemClock.uptimeMillis() - lStartTime;
            long lUpdateTime = lPauseTime + lSystemTime;
            rUpdateTime = lUpdateTime;
            long secs = (long) (lUpdateTime / 1000);
            long mins = secs / 60;
            long hour = mins /60;
            secs = secs % 60;
            txtTimer.setText("" + hour + ":" + String.format("%02d", mins) + ":" + String.format("%02d", secs));
            handler.postDelayed(this, 0);
        }
    };
    private LogicRunning logicRunning;
    private final DatabaseLocation mQuery = new DatabaseLocation(this);
    LocationManager locationManager;
    String locationProvider;
    Location lastKnownLocation;
    LocationListener mlocListener, locListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running_offline);
        txtTimer = (TextView) findViewById(R.id.txtTimer);
        logicRunning = new LogicRunning(this);

        logicRunning.initialization();
        logicRunning.createLocationCallbackOffline();
        logicRunning.createLocationRequest();
        logicRunning.buildLocationSettingsRequest();
        //logicRunning.getMyLocation();
        locationManager = (LocationManager)this.getSystemService(LOCATION_SERVICE);
        locationProvider =LocationManager.GPS_PROVIDER;
        locListener = new LocationListener(){
            @Override
            public void onLocationChanged(Location loc) {
                logicRunning.onLocationChangedOffline(loc);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        /*List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (int i =0; i < providers.size(); i++) {
            Location myLocation = locationManager.getLastKnownLocation(providers.get(i));
            if (myLocation == null) {
                continue;
            }
            if (bestLocation == null || myLocation.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = myLocation;
                locationProvider = providers.get(i);
            }
        }*/
        /*if(bestLocation != null){
            logicRunning.onLocationChangedOffline(bestLocation);
        }*/

    }

    @Override
    public Context getMainActivity() {
        return MainActivityOffline.this;
    }

    @Override
    public void setupViewRunning(float mDistanceValue, float mPaceValue, float mCalorie) {
        TextView txtDistance = (TextView) findViewById(R.id.txtDuration);
        TextView txtNetCalorie = (TextView) findViewById(R.id.txtCalorie);
        TextView txtPace = (TextView) findViewById(R.id.txtAvgPace);
        txtDistance.setText(Float.toString(mDistanceValue));
        txtNetCalorie.setText(Float.toString(mCalorie));
        String rMin ="";
        String rSec ="";
        if(mPaceValue > 0.0) {
            Log.d(TAG, "index=" + mPaceValue);
            int rA = (int) (mPaceValue);
            int rB = (int) ((mPaceValue -rA )*100);
            if(rA > 999){
                rSec = String.valueOf(rB);
                String rPace = "999:"+rSec;
                txtPace.setText(rPace);
            }
            else{
                rMin = String.valueOf(rA);
                rSec = String.valueOf(rB);
                String rPace = rMin+":"+rSec;
                txtPace.setText(rPace);
            }
        }
        else{
            txtPace.setText("00:00");
        }
    }

    @Override
    public void startTime() {
        if(isRun)
            return;
        isRun = true;
        lStartTime = SystemClock.uptimeMillis();
        handler.postDelayed(runnable, 0);
    }

    @Override
    public float getUpdateTime() {
        return rUpdateTime;
    }

    @Override
    public void pauseTime() {
        if(!isRun)
            return;
        isRun = false;
        lPauseTime += lSystemTime;
        handler.removeCallbacks(runnable);
    }

    @Override
    public void stopTime() {
        if(!isRun)
            return;
        isRun = false;
        lPauseTime = 0;
        handler.removeCallbacks(runnable);
    }

    @Override
    public GoogleMap getMap() {
        return null;
    }
    public void onClickStartButton(View startButton) {
        // Perform animation
        ImageButton pauseButton = (ImageButton) findViewById(R.id.pauseButton);
        ImageButton stopButton = (ImageButton) findViewById(R.id.stopButton);

        Animation pauseButtonAnimation = AnimationUtils.loadAnimation(MainActivityOffline.this, R.anim.pause_button_separation);
        Animation stopButtonAnimation = AnimationUtils.loadAnimation(MainActivityOffline.this, R.anim.stop_button_separation);
        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
        stopButton.setEnabled(true);
        startButton.setVisibility(View.INVISIBLE);
        pauseButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.VISIBLE);
        pauseButton.startAnimation(pauseButtonAnimation);
        stopButton.startAnimation(stopButtonAnimation);
        startTime();
        locationManager.requestLocationUpdates(locationProvider, 0, 0, locListener);

        //mQuery.deleteAll();
        //logicRunning.startLocationUpdates();
        //refreshMap(mMap);
        // Get start time
        startCurrentTime = Calendar.getInstance().getTime();
    }

    public void onClickPauseButton(View pauseButton) {
        // Perform animation
        ImageButton resumeButton = (ImageButton) findViewById(R.id.resumeButton);
        ImageButton stopButton = (ImageButton) findViewById(R.id.stopButton);
        Animation resumeButtonAnimation = AnimationUtils.loadAnimation(MainActivityOffline.this, R.anim.resume_button_fade_in);
        Animation pauseButtonAnimation = AnimationUtils.loadAnimation(MainActivityOffline.this, R.anim.pause_button_unification);
        Animation stopButtonAnimation = AnimationUtils.loadAnimation(MainActivityOffline.this, R.anim.stop_button_unification);
        resumeButton.setEnabled(true);
        pauseButton.setEnabled(false);
        stopButton.setEnabled(false);
        resumeButton.setVisibility(View.VISIBLE);
        pauseButton.setVisibility(View.INVISIBLE);
        stopButton.setVisibility(View.INVISIBLE);
        resumeButton.startAnimation(resumeButtonAnimation);
        pauseButton.startAnimation(pauseButtonAnimation);
        stopButton.startAnimation(stopButtonAnimation);

        // Remove the listener you previously added
        locationManager.removeUpdates(locListener);

        pauseTime();
        //logicRunning.stopLocationUpdates();
    }

    public void onClickResumeButton(View resumeButton) {
        // Perform animation
        ImageButton pauseButton = (ImageButton) findViewById(R.id.pauseButton);
        ImageButton stopButton = (ImageButton) findViewById(R.id.stopButton);
        Animation pauseButtonAnimation = AnimationUtils.loadAnimation(MainActivityOffline.this, R.anim.pause_button_separation);
        Animation stopButtonAnimation = AnimationUtils.loadAnimation(MainActivityOffline.this, R.anim.stop_button_separation);
        resumeButton.setEnabled(false);
        pauseButton.setEnabled(true);
        stopButton.setEnabled(true);
        resumeButton.setVisibility(View.INVISIBLE);
        pauseButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.VISIBLE);
        pauseButton.startAnimation(pauseButtonAnimation);
        stopButton.startAnimation(stopButtonAnimation);
        locationManager.requestLocationUpdates(locationProvider, 0, 0, locListener);
        startTime();
        //logicRunning.startLocationUpdates();
    }

    public void onClickStopButton(View view) throws JSONException {
        //logicRunning.stopLocationUpdates();
        stopTime();
        locationManager.removeUpdates(locListener);
        stopCurrentTime = Calendar.getInstance().getTime();
        //sendDataToResult();
    }
}
