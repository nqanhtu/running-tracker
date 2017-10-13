package runningtracker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
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
import android.widget.TextView;
import android.widget.Toast;


import runningtracker.model.modelrunning.M_BodilyCharacteristicObject;
import runningtracker.model.modelrunning.M_DatabaseLocation;
import runningtracker.model.modelrunning.M_LocationObject;
import runningtracker.presenter.presenterrunning.PreLogicRunning;
import runningtracker.presenter.fitnessstatistic.Calculator;
import runningtracker.view.ViewRunning;

import runningtracker.model.modelrunning.M_DatabaseLocation;
import runningtracker.model.modelrunning.M_LocationObject;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity  implements ViewRunning, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks{
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_FINE_LOCATION = 0;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;

    /**
     * Constant used in the location settings dialog.
     */
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 3000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    private final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    private final static String KEY_LOCATION = "location";
    private final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";
    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;
    /**
     * Provides access to the Location Settings API.
     */
    private SettingsClient mSettingsClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    private LocationRequest mLocationRequest;

    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    private LocationSettingsRequest mLocationSettingsRequest;

    /**
     * Callback for Location events.
     */
    private LocationCallback mLocationCallback;
   // public M_DatabaseLocation mQuery;
    private ArrayList<M_LocationObject> startToPresentLocations;
    private Polyline polyline;


    /**
     * Represents a geographical location.
     */
    private Location mCurrentLocation;
    LocationManager rLocationManager;



 /*   private Button mStartUpdatesButton;
    private Button mStopUpdatesButton;*/ //van tri
    //private M_LocationObject locationObject;

    //private LocationObject locationObject;

    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    private Boolean mRequestingLocationUpdates;
    /**
     * Time when the location was updated represented as a String.
     */
    private String mLastUpdateTime;
    /*
     * setup running global variable
     */
    private float rDisaTance;
    Location rlocation;
    double mLatitude, mLongitude;

    /*
     * Real timer running
     */
    TextView txtTimer = (TextView) findViewById(R.id.textDurationValue);
    long lStartTime, lPauseTime, lSystemTime = 0L;
    Handler handler = new Handler();
    boolean isRun;
    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            lSystemTime = SystemClock.uptimeMillis() - lStartTime;
            long lUpdateTime = lPauseTime + lSystemTime;
            long secs = (long) (lUpdateTime / 1000);
            long mins = secs / 60;
            long hour = mins /60;
            secs = secs % 60;
            txtTimer.setText("" + hour + ":" + String.format("%02d", mins) + ":" + String.format("%02d", secs));
            handler.postDelayed(this, 0);
        }
    };

    M_BodilyCharacteristicObject m_Bodily;

    private PreLogicRunning preLogicRunning;
    private final M_DatabaseLocation mQuery = new M_DatabaseLocation(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeUI();

        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";

        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        rlocation = new Location("A");
        // Kick off the process of building the LocationCallback, LocationRequest, and
        // LocationSettingsRequest objects.
        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();

        preLogicRunning = new PreLogicRunning(this);

        m_Bodily = new M_BodilyCharacteristicObject();
        try {
            preLogicRunning.getBodilyCharacter(m_Bodily);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        final M_DatabaseLocation mQuery = new M_DatabaseLocation(this);
//        mStartUpdatesButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                mQuery.deleteAll();
//                //setupViewRunning();
//               // preLogicRunning.getData();
//                refreshMap(mMap);
//                startUpdatesButtonHandler(v);
//            }
//        });
//        mStopUpdatesButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                stopUpdatesButtonHandler(v);
//                rStop();
//                startPolyline();
//                try {
//                    preLogicRunning.saveRunning();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        startButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                // Perform animation
//                Animation separateButton = AnimationUtils.loadAnimation(MainActivity.this, R.anim.pause_button_separation);
//                v.startAnimation(separateButton);
//
//                mQuery.deleteAll();
//                preLogicRunning.getData();
//                refreshMap(mMap);
//                startUpdatesButtonHandler(v);
//            }
//        });
//        stopButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                stopUpdatesButtonHandler(v);
//                startPolyline();
//                //preLogicRunning.saveRunning();
//            }
//        });

    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */

    private Location getMyLocation() {
/*        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (myLocation == null) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            String provider = lm.getBestProvider(criteria, true);
            myLocation = lm.getLastKnownLocation(provider);
        }
        return myLocation;*/
        rLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = rLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = rLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }
    //move camera to my location
    private void moveCamera(Location location){
        LatLng latLng;
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
        mMap.animateCamera(cameraUpdate);
    }
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Creates a callback for receiving location events.
     */
    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                onLocationChanged(locationResult.getLastLocation());
            }
        };
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(KEY_REQUESTING_LOCATION_UPDATES)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        KEY_REQUESTING_LOCATION_UPDATES);
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(KEY_LOCATION)) {
                // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(KEY_LAST_UPDATED_TIME_STRING)) {
                mLastUpdateTime = savedInstanceState.getString(KEY_LAST_UPDATED_TIME_STRING);
            }
            updateUI();
        }
    }
    /**
     * Uses a {@link com.google.android.gms.location.LocationSettingsRequest.Builder} to build
     * a {@link com.google.android.gms.location.LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        mRequestingLocationUpdates = false;
                        updateUI();
                        break;
                }
                break;
        }
    }
    private void updateUI() {
        setButtonsEnabledState();
        //updateLocationUI();
    }
    /**
     * Disables both buttons when functionality is disabled due to insuffucient location settings.
     * Otherwise ensures that only one button is enabled at any time. The Start Updates button is
     * enabled if the user is not requesting location updates. The Stop Updates button is enabled
     * if the user is requesting location updates.
     */
    private void setButtonsEnabledState() {
//        if (mRequestingLocationUpdates) {
//            startButton.setEnabled(false);
//            startButton.setVisibility(Button.INVISIBLE);
//            stopButton.setEnabled(true);
//            stopButton.setVisibility(Button.VISIBLE);
//        } else {
//            startButton.setEnabled(true);
//            startButton.setVisibility(Button.VISIBLE);
//            stopButton.setEnabled(false);
//            stopButton.setVisibility(Button.INVISIBLE);
//        }
    }
    /**
     * Handles the Start Updates button and requests start of location updates. Does nothing if
     * updates have already been requested.
     */
    public void startUpdatesButtonHandler(View view) {
        if (!mRequestingLocationUpdates) {
            mRequestingLocationUpdates = true;
            setButtonsEnabledState();
            startLocationUpdates();
        }
    }
    /**
     * Handles the Stop Updates button, and requests removal of location updates.
     */
    public void stopUpdatesButtonHandler(View view) {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        stopLocationUpdates();
    }
    /**
     * Removes location updates from the FusedLocationApi.
     */
    private void stopLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            Log.d(TAG, "stopLocationUpdates: updates never requested, no-op.");
            return;
        }

        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mRequestingLocationUpdates = false;
                        setButtonsEnabledState();
                    }
                });
    }
    private void startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback,
                                Looper.myLooper());

                        updateUI();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                                mRequestingLocationUpdates = false;
                        }

                        updateUI();
                    }
                });
    }
    public void onLocationChanged(Location location) {
        // New location has now been determined
        // You can now create a LatLng Object for use with maps
        M_DatabaseLocation mQuery = new M_DatabaseLocation(this);
        M_LocationObject iLocation = new M_LocationObject();
        iLocation.setLatitudeValue(location.getLatitude());
        iLocation.setLongitudeValue(location.getLongitude());
        moveCamera(location);
        rStart();
        if(mLatitude != 0){
            rlocation = new Location("A");
            rlocation.setLatitude(mLatitude);
            rlocation.setLongitude(mLongitude);
            float mDisaTance = rDisaTance;
            rDisaTance = rDisaTance +  preLogicRunning.DistanceLocation(rlocation,location);
            float rPace = ((rDisaTance - mDisaTance) / ((float)UPDATE_INTERVAL_IN_MILLISECONDS / 3600000));
            float rCalories = 0;
            if(m_Bodily != null)
                rCalories = (float) Calculator.netCalorieBurned(m_Bodily.getWeightInKg(), m_Bodily.getVO2max(), rDisaTance, 0, false);
            setupViewRunning(preLogicRunning.RoundAvoid(rDisaTance,2), preLogicRunning.RoundAvoid(rPace,1), preLogicRunning.RoundAvoid(rCalories, 1));
            polylineBetweenTwoPoint(rlocation, location);
        }
        else {
            LatLng myLocation = null;
            myLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(myLocation).title("My Location"));
        }
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        mQuery.addLocation(iLocation);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(checkPermissions()) {
            mMap = googleMap;
            mMap.setMyLocationEnabled(true);
           mMap.getUiSettings().setMyLocationButtonEnabled(false);
            Location L = getMyLocation();
            if(L != null) {
                moveCamera(L);
            }
        }
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }


    //get all points
    private ArrayList<LatLng> getPoints(ArrayList<M_LocationObject> mLocations){
        ArrayList<LatLng> points = new ArrayList<LatLng>();
        for(M_LocationObject mLocation : mLocations){
            points.add(new LatLng(mLocation.getLatitudeValue(), mLocation.getLongitudeValue()));
        }
        return points;
    }
    //start polyline
    private void startPolyline(){
        M_DatabaseLocation mQuery = new M_DatabaseLocation(this);
        startToPresentLocations = mQuery.getAllLocation();
        LatLng myLocation = null;
        ArrayList<LatLng> points; // list of latlng
        try {
           points = getPoints(startToPresentLocations);
            for (int z = 0; z < points.size() - 1; z++) {  //add Arraylist points on Polyline
              LatLng src = points.get(z);
              LatLng dest = points.get(z + 1);
                    myLocation = new LatLng(src.latitude, src.longitude);
              polyline = mMap.addPolyline(new PolylineOptions()
                      .add(new LatLng(src.latitude, src.longitude),
                              new LatLng(dest.latitude, dest.longitude)).color(Color.BLUE).width(10));
          }
            mMap.addMarker(new MarkerOptions().position(myLocation).title("My Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 14));

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    private void polylineBetweenTwoPoint(Location A, Location B){
        polyline = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(A.getLatitude(), A.getLongitude()),
                        new LatLng(B.getLatitude(), B.getLongitude())).color(Color.BLUE).width(10).geodesic(true));
    }
    private void refreshMap(GoogleMap mapInstance){
        mapInstance.clear();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }




    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onStart() {
        // mGoogleApiClient.connect();


        super.onStart();


    }

    @Override
    protected void onResume() {
        super.onResume();
        // Within {@code onPause()}, we remove location updates. Here, we resume receiving
        // location updates if the user has requested them.
        if (mRequestingLocationUpdates && checkPermissions()) {
            startLocationUpdates();
        }
        updateUI();
    }
    @Override
    protected void onPause() {
        super.onPause();
        // Remove location updates to save battery.
        stopLocationUpdates();

    }


    @Override
    protected void onStop() {

        super.onStop();

    }

    @Override
    public JSONObject getValueRunning(){
        JSONObject jsonObject = new JSONObject();
        try{
            //jsonObject.put("RunningSessionID",1);
            jsonObject.put("Userid", 1);
            jsonObject.put("Runontreadmill", 0);
            jsonObject.put("Roadgradient", 23);
            jsonObject.put("Grosscalorieburned", 100);
            jsonObject.put("Netcalorieburned", 100);
            jsonObject.put("Distanceinkm", 50);
            jsonObject.put("Starttimestamp", "2017-2-1");
            jsonObject.put("Finishtimestamp", "2017-1-1");

            return jsonObject;
        }catch (JSONException e){
            throw new RuntimeException(e);
        }

    }

    @Override
    public Context getMainActivity() {
        return MainActivity.this;
    }


    public void rStart(){
        if(isRun)
            return;
        isRun = true;
        lStartTime = SystemClock.uptimeMillis();
        handler.postDelayed(runnable, 0);
    }
     public void rStop(){
         if(!isRun)
             return;
         isRun = false;
         lPauseTime = 0;
         handler.removeCallbacks(runnable);
     }

    @Override
    public void setupViewRunning(float mDistanceValue, float mPaceValue, float mCalorie) {

        TextView txtDisaTance = (TextView) findViewById(R.id.textDistanceValue);
        TextView txtNetCalorie = (TextView) findViewById(R.id.textCalorieValue);
        TextView txtPace = (TextView) findViewById(R.id.textPaceValue);
/*        txtDisaTance.setText(Float.toString(mDistanceValue));
        txtNetCalorie.setText(Float.toString(mCalorie));
        txtPace.setText(Float.toString(mPaceValue));*/
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
        actionBar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorSecondary));
        setSupportActionBar(actionBar);

        // Map
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void onClickStartButton(View startButton) {
        // Perform animation
//        ViewGroup parentLayout = (ViewGroup) findViewById(R.id.belowSectionLayout);
        ImageButton pauseButton = (ImageButton) findViewById(R.id.pauseButton);
        ImageButton stopButton = (ImageButton) findViewById(R.id.stopButton);

        /*Animation pauseButtonAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.pause_button_separation);
        Animation stopButtonAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.stop_button_separation);*/
        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
        stopButton.setEnabled(true);
//        TransitionManager.beginDelayedTransition(parentLayout);
        startButton.setVisibility(View.INVISIBLE);
        pauseButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.VISIBLE);
//        pauseButton.startAnimation(pauseButtonAnimation);
//        stopButton.startAnimation(stopButtonAnimation);

         mQuery.deleteAll();
         preLogicRunning.getData();
//         setupViewRunning();
//         preLogicRunning.getData();
         refreshMap(mMap);
         startUpdatesButtonHandler(startButton);
    }

    public void onClickPauseButton(View pauseButton) {
        // Perform animation
        ImageButton resumeButton = (ImageButton) findViewById(R.id.resumeButton);
        ImageButton stopButton = (ImageButton) findViewById(R.id.stopButton);
        Animation resumeButtonAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.resume_button_fade_in);
        Animation pauseButtonAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.pause_button_unification);
        Animation stopButtonAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.stop_button_unification);
        resumeButton.setEnabled(true);
        pauseButton.setEnabled(false);
        stopButton.setEnabled(false);
        resumeButton.setVisibility(View.VISIBLE);
        pauseButton.setVisibility(View.INVISIBLE);
        stopButton.setVisibility(View.INVISIBLE);
        resumeButton.startAnimation(resumeButtonAnimation);
        pauseButton.startAnimation(pauseButtonAnimation);
        stopButton.startAnimation(stopButtonAnimation);
    }

    public void onClickResumeButton(View resumeButton) {
        // Perform animation
        ImageButton pauseButton = (ImageButton) findViewById(R.id.pauseButton);
        ImageButton stopButton = (ImageButton) findViewById(R.id.stopButton);
        Animation pauseButtonAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.pause_button_separation);
        Animation stopButtonAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.stop_button_separation);
        resumeButton.setEnabled(false);
        pauseButton.setEnabled(true);
        stopButton.setEnabled(true);
        resumeButton.setVisibility(View.INVISIBLE);
        pauseButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.VISIBLE);
        pauseButton.startAnimation(pauseButtonAnimation);
        stopButton.startAnimation(stopButtonAnimation);
    }

    public void onClickStopButton(View view) {
        stopUpdatesButtonHandler(view);
        rStop();
//        startPolyline();
        try {
            preLogicRunning.saveRunning();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    
}
