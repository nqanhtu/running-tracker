package runningtracker.running;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import runningtracker.R;
import runningtracker.common.GenerateID;
import runningtracker.common.InitializationFirebase;
import runningtracker.common.MyLocation;
import runningtracker.data.model.running.IdHistory;
import runningtracker.data.model.running.LocationObject;
import runningtracker.data.model.running.ResultObject;
import runningtracker.model.modelrunning.BodilyCharacteristicObject;
import runningtracker.model.modelrunning.DatabaseLocation;
import runningtracker.fitnessstatistic.Calculator;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;



public class RunningActivity extends AppCompatActivity implements RunningContract, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks {
    private static final String TAG = RunningActivity.class.getSimpleName();
    private GoogleMap mMap, mMapShareLocation;
    Date startCurrentTime, stopCurrentTime;
    float rGrossCalorie;
    TextView txtTimer;
    long lStartTime, rUpdateTime, lPauseTime, lSystemTime = 0L;
    Handler handler = new Handler();
    boolean isRun;
    private String timeRunning;
    private ImageView statusConnect;

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            lSystemTime = SystemClock.uptimeMillis() - lStartTime;
            long lUpdateTime = lPauseTime + lSystemTime;
            rUpdateTime = lUpdateTime;
            long secs = (long) (lUpdateTime / 1000);
            long mins = secs / 60;
            long hour = mins / 60;
            secs = secs % 60;

            timeRunning = "" + hour + ":" + String.format("%02d", mins) + ":" + String.format("%02d", secs);
            txtTimer.setText(timeRunning);
            handler.postDelayed(this, 0);
        }
    };

    BodilyCharacteristicObject m_Bodily;
    private PresenterRunning presenterRunning;
    private MyLocation myLocation;
    private final DatabaseLocation mQuery = new DatabaseLocation(this);
    private Boolean checkConnect;

    LocationManager locationManager;
    LocationListener locListener;
    String locationProvider;

    //private String id;
    private IdHistory idHistory;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);
        presenterRunning = new PresenterRunning(this);
        myLocation = new MyLocation();

        initializeUI();

        /**
         *  online onCreate
        * */
        presenterRunning.initialization();
        txtTimer = (TextView) findViewById(R.id.textValueDuration);
        statusConnect = (ImageView) findViewById(R.id.iconStatus);

        checkConnect = presenterRunning.isConnected(this);

        InitializationFirebase initializationFirebase = new InitializationFirebase();
        firestore = initializationFirebase.createFirebase();

        GenerateID generateID = new GenerateID();
        idHistory = new IdHistory();
        idHistory.id = generateID.generateTimeID();

        /**
         * Save file day help select and chosen data
        * */
        presenterRunning.saveHistory( idHistory.id, firestore);

        presenterRunning.createLocationCallback(checkConnect,  idHistory.id , firestore);
        presenterRunning.createLocationRequest();
        presenterRunning.buildLocationSettingsRequest();

        /**
         * offline onCreate
        * */
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationProvider = LocationManager.GPS_PROVIDER;
        locListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                presenterRunning.onLocationChangedOffline(location,  idHistory.id , firestore);
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

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (presenterRunning.checkPermissions()) {
            mMap = googleMap;
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            Location L = myLocation.getMyLocation(this);
            if(L != null) {
                presenterRunning.moveCamera(L);
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public Context getMainActivity() {
        return RunningActivity.this;
    }

    @Override
    public void startTime(){
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
    public void stopTime(){
        if(!isRun)
            return;
        isRun = false;
        lPauseTime = 0;
        handler.removeCallbacks(runnable);
    }

    @Override
    public GoogleMap getMap() {
        return mMap;
    }

    @Override
    public void pauseTime(){
        if(!isRun)
            return;
        isRun = false;
        lPauseTime += lSystemTime;
        handler.removeCallbacks(runnable);
    }

    @Override
    public void setupViewRunning(float mDistanceValue, float mPaceValue, float mCalorie) {
        TextView txtDistance = (TextView) findViewById(R.id.textValueDistance);
        TextView txtNetCalorie = (TextView) findViewById(R.id.textValueCalorie);
        TextView txtPace = (TextView) findViewById(R.id.textValuePace);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate navigation menu from the resources by using the menu inflater.
        getMenuInflater().inflate(R.menu.navigation_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.setting:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initializeUI() {
        // Toolbar
        Toolbar actionBar = (Toolbar) findViewById(R.id.actionbar);
        actionBar.setTitle(R.string.RunningTitle);
        actionBar.setTitleTextColor(ContextCompat.getColor(this, R.color.textColorPrimary));
        setSupportActionBar(actionBar);
        /**
         * Create map tracking
        * */
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /**
         * Create map view location share of friends
        * */
        MapFragment mapFragmentShare = (MapFragment) getFragmentManager().findFragmentById(R.id.mapShareLocation);
        //mapFragmentShare.getView().setVisibility(View.INVISIBLE);
        mapFragmentShare.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                if (presenterRunning.checkPermissions()) {
                    mMapShareLocation = googleMap;
                    mMapShareLocation.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    if (ActivityCompat.checkSelfPermission(getMainActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getMainActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mMapShareLocation.setMyLocationEnabled(true);
                    mMapShareLocation.getUiSettings().setMyLocationButtonEnabled(false);

                    /**
                     * Create list location friends demo
                     * */
                    ArrayList<LocationObject> list = new ArrayList<>();
                    list.add(new LocationObject(10.736096, 106.674790));
                    list.add(new LocationObject(10.738214, 106.677853));
                    list.add(new LocationObject(10.734489, 106.678814));
                    list.add(new LocationObject(10.738797, 106.681894));

                    /**
                     * Move camera first friend
                     * */
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(list.get(0).getLatitudeValue(), list.get(0).getLongitudeValue()), 15);
                    mMapShareLocation.animateCamera(cameraUpdate);

                    /**
                     * Set marker friend on map
                     * */
                    for(int i = 0; i< list.size(); i++){
                        mMapShareLocation.addMarker(new MarkerOptions().position(new LatLng(list.get(i).getLatitudeValue(), list.get(i).getLongitudeValue()))
                                .title("Friend " + (i + 1)));
                    }

                    /**
                     * Get touch event on map fragment
                    * */
                    mMapShareLocation.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                        @Override
                        public void onMapLongClick(LatLng latLng) {
                            Intent nextActivity = new Intent(RunningActivity.this, ViewFriendsActivity.class);
                            startActivity(nextActivity);
                        }
                    });
                }
            }
        });
    }

    //send data to ResultActivity
    private void sendDataToResult() throws JSONException {
        //calculator grossCalorieBurned
        rGrossCalorie = 0;
        if(m_Bodily != null) {
            rGrossCalorie = (float) Calculator.grossCalorieBurned(presenterRunning.getCalories(), m_Bodily.getRestingMetabolicRate(), rUpdateTime /3600000);
            rGrossCalorie = presenterRunning.RoundAvoid(rGrossCalorie, 2);
        }
        Intent nextActivity = new Intent(RunningActivity.this, ResultActivity.class);
        nextActivity.putExtra("duration", rUpdateTime);
        nextActivity.putExtra("distance", presenterRunning.RoundAvoid(presenterRunning.getDisaTance(),2));
        nextActivity.putExtra("avgPace", presenterRunning.RoundAvoid(presenterRunning.getPace(),2));
        nextActivity.putExtra("maxPace", presenterRunning.RoundAvoid(presenterRunning.getMaxPace(),2));
        nextActivity.putExtra("netCalorie", presenterRunning.RoundAvoid(presenterRunning.getCalories(),1));
        nextActivity.putExtra("grossCalorie", presenterRunning.RoundAvoid(rGrossCalorie,1));

        saveRunning();
        startActivity(nextActivity);
    }

    public void saveRunning(){
        ResultObject resultObject;
        resultObject = new ResultObject(timeRunning, presenterRunning.getDisaTance(), presenterRunning.getPace(), presenterRunning.getMaxPace(),
                presenterRunning.getCalories(), rGrossCalorie, Long.toString(System.currentTimeMillis()));
        /**
         * push data to firebase
        * */
        presenterRunning.saveHistoryRunningData( idHistory.id, firestore, resultObject);
    }

    public void onClickStartButton(View startButton) {
        // Perform animation
        ImageButton pauseButton = (ImageButton) findViewById(R.id.pauseButton);
        ImageButton stopButton = (ImageButton) findViewById(R.id.stopButton);

        Animation pauseButtonAnimation = AnimationUtils.loadAnimation(RunningActivity.this, R.anim.pause_button_separation);
        Animation stopButtonAnimation = AnimationUtils.loadAnimation(RunningActivity.this, R.anim.stop_button_separation);
        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
        stopButton.setEnabled(true);
        startButton.setVisibility(View.INVISIBLE);
        pauseButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.VISIBLE);
        pauseButton.startAnimation(pauseButtonAnimation);
        stopButton.startAnimation(stopButtonAnimation);

        startTime();
        if(checkConnect) {
            statusConnect.setImageResource(R.drawable.ic_online);
            presenterRunning.startLocationUpdates();
        }
        else{
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            statusConnect.setImageResource(R.drawable.ic_offline);
            locationManager.requestLocationUpdates(locationProvider, 0, 0, locListener);
        }
        //refreshMap(mMap);
        // Get start time
        startCurrentTime = Calendar.getInstance().getTime();
    }

    public void onClickPauseButton(View pauseButton) {
        // Perform animation
        ImageButton resumeButton = (ImageButton) findViewById(R.id.resumeButton);
        ImageButton stopButton = (ImageButton) findViewById(R.id.stopButton);
        Animation resumeButtonAnimation = AnimationUtils.loadAnimation(RunningActivity.this, R.anim.resume_button_fade_in);
        Animation pauseButtonAnimation = AnimationUtils.loadAnimation(RunningActivity.this, R.anim.pause_button_unification);
        Animation stopButtonAnimation = AnimationUtils.loadAnimation(RunningActivity.this, R.anim.stop_button_unification);
        resumeButton.setEnabled(true);
        pauseButton.setEnabled(false);
        stopButton.setEnabled(false);
        resumeButton.setVisibility(View.VISIBLE);
        pauseButton.setVisibility(View.INVISIBLE);
        stopButton.setVisibility(View.INVISIBLE);
        resumeButton.startAnimation(resumeButtonAnimation);
        pauseButton.startAnimation(pauseButtonAnimation);
        stopButton.startAnimation(stopButtonAnimation);

        pauseTime();
        if(checkConnect) {
            presenterRunning.stopLocationUpdates();
        }else{
            locationManager.removeUpdates(locListener);
        }
    }

    public void onClickResumeButton(View resumeButton) {
        // Perform animation
        ImageButton pauseButton = (ImageButton) findViewById(R.id.pauseButton);
        ImageButton stopButton = (ImageButton) findViewById(R.id.stopButton);
        Animation pauseButtonAnimation = AnimationUtils.loadAnimation(RunningActivity.this, R.anim.pause_button_separation);
        Animation stopButtonAnimation = AnimationUtils.loadAnimation(RunningActivity.this, R.anim.stop_button_separation);
        resumeButton.setEnabled(false);
        pauseButton.setEnabled(true);
        stopButton.setEnabled(true);
        resumeButton.setVisibility(View.INVISIBLE);
        pauseButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.VISIBLE);
        pauseButton.startAnimation(pauseButtonAnimation);
        stopButton.startAnimation(stopButtonAnimation);

        startTime();
        if(checkConnect) {
            presenterRunning.startLocationUpdates();
        }else{
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(locationProvider, 0, 0, locListener);
        }
    }

    public void onClickStopButton(View view) throws JSONException {
        if(checkConnect) {
            presenterRunning.stopLocationUpdates();
        }else{
            locationManager.removeUpdates(locListener);
        }
        stopTime();
        stopCurrentTime = Calendar.getInstance().getTime();
        sendDataToResult();
    }
}