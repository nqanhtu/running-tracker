package runningtracker.presenter.running;
import android.Manifest;
import android.app.Activity;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
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

import runningtracker.model.DataCallback;
import runningtracker.model.modelrunning.BodilyCharacteristicObject;
import runningtracker.model.ResAPICommon;
import runningtracker.model.modelrunning.DatabaseLocation;
import runningtracker.model.modelrunning.DatabaseRunningSession;
import runningtracker.model.modelrunning.LocationObject;
import runningtracker.model.modelrunning.RunningObject;
import runningtracker.presenter.fitnessstatistic.Calculator;
import runningtracker.view.running.ViewRunning;

import static android.content.Context.LOCATION_SERVICE;

public class LogicRunning implements Running {
    //khai bao bien
    private static final int REQUEST_FINE_LOCATION = 0;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 3000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private ArrayList<LocationObject> startToPresentLocations;
    private Polygon polygon;
    private boolean mStatusTime;
    float rGrossCalorie;
    LocationManager rLocationManager;
    LocationListener locationListener;
    private String provider;
    private Location mCurrentLocation;
    private String mLastUpdateTime;
    private float rDisaTance;
    Location mLocation;
    double mLatitude, mLongitude;
    float rCalories, rPace, maxPace;

    BodilyCharacteristicObject m_Bodily;
    //private final DatabaseLocation mQuery = new DatabaseLocation(this);

    //Ket thuc khai bao bien
    ViewRunning viewRunning;
    ResAPICommon resAPICommon;
    DatabaseRunningSession dataRunning;
    public LogicRunning(ViewRunning viewRunning){
        this.viewRunning = viewRunning;
        this.resAPICommon = new ResAPICommon();
    }

    @Override
    public void saveRunning() throws JSONException {
        //resAPICommon.RestPostClient(viewRunning.getMainActivity(), "http://14.169.228.44/runningsession/new", viewRunning.getValueRunning());
    }

    @Override
    public void getData() {
        //resAPICommon.RestGetClient("http://192.168.43.188:8000/runningsession/new", viewRunning.getMainActivity());
    }

    @Override
    public float DistanceLocation(Location locationA, Location locationB) {
        float distance;
        distance = locationA.distanceTo(locationB)/1000;// chang to meter to kilometer
        return distance;
    }

    @Override
    public float RoundAvoid(double value, int places) {
        double scale = Math.pow(10, places);
        return (float) (Math.round(value * scale) / scale);
    }

    @Override
    public void getBodilyCharacter(BodilyCharacteristicObject m_Bodily) throws JSONException {
        final BodilyCharacteristicObject finalM_Bodily = m_Bodily;
        ResAPICommon.RestGetClient("" +
                        "http://14.169.228.44/appuser/get/1", viewRunning.getMainActivity(),
                new DataCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        try {
                            finalM_Bodily.setAge((Integer) result.get("Age"));
                            finalM_Bodily.setGender((String) result.get("Gender"));
                            finalM_Bodily.setWeightInKg((Integer) result.get("WeightInKg"));
                            Toast.makeText(viewRunning.getMainActivity(), "Error:  " + finalM_Bodily.getWeightInKg(), Toast.LENGTH_SHORT).show();
                            finalM_Bodily.setHeightInCm((Integer) result.get("HeightInCm"));
                            finalM_Bodily.setVO2max((Integer) result.get("VO2max"));
                            finalM_Bodily.setRestingMetabolicRate((Integer) result.get("RestingMetabolicRate"));
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(viewRunning.getMainActivity(), "Error" +e, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    @Override
    public RunningObject setRunningObject(int runningSessionID, int userID, String startTimestamp, String finishTimestamp, double distanceInKm, int roadGradient, int runOnTreadmill, int netCalorieBurned, int grossCalorieBurned, int flagStatus) {
        RunningObject runningObject = new RunningObject(runningSessionID, userID, startTimestamp, finishTimestamp, distanceInKm, roadGradient, runOnTreadmill, netCalorieBurned, grossCalorieBurned, flagStatus);
        return runningObject;
    }

    @Override
    public boolean SaveRunningSession(RunningObject runningObject) {
       /* if(dataRunning.addNewRunningSession(runningObject))
            return true;*/
        return false;
    }

    //Ham cai dat running
    @Override
    public void initialization() {
        mLastUpdateTime = "";
        rCalories = 0;
        maxPace = 0;
        rGrossCalorie = 0;
        mLocation = new Location("A");
        mStatusTime = false;
        //logicRunning = new LogicRunning(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient( viewRunning.getMainActivity());
        mSettingsClient = LocationServices.getSettingsClient( viewRunning.getMainActivity());
    }

    @Override
    public void createLocationCallback() {
            mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                onLocationChanged(locationResult.getLastLocation());
                //onLocationChangedOffline(locationResult.getLastLocation());
            }
        };
    }

    @Override
    public void createLocationCallbackOffline() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = getMyLocation();
                onLocationChangedOffline(location);
            }
        };
    }

    @Override
    public void onLocationChanged(Location location) {
        DatabaseLocation mQuery = new DatabaseLocation(viewRunning.getMainActivity());
        LocationObject iLocation = new LocationObject();
        iLocation.setLatitudeValue(location.getLatitude());
        iLocation.setLongitudeValue(location.getLongitude());
        moveCamera(location);
/*        if(mStatusTime == true){
            viewRunning.startTime();
            mStatusTime = false;
        }*/
        if(mLatitude != 0){
            mLocation = new Location("B");
            mLocation.setLatitude(mLatitude);
            mLocation.setLongitude(mLongitude);
            float mDisaTance = rDisaTance;
            rDisaTance = rDisaTance +  DistanceLocation(mLocation,location);
            float rAvg_Distance = rDisaTance - mDisaTance;
            rPace = 0;
            if(rDisaTance > 0) {rPace = (viewRunning.getUpdateTime() / 60000) / rDisaTance;}
            //if(m_Bodily != null)
            //rCalories = (float) Calculator.netCalorieBurned(m_Bodily.getWeightInKg(), m_Bodily.getVO2max(), rDisaTance, 0, false);
            rCalories = (float) Calculator.netCalorieBurned(80, 42, rDisaTance, 0, false);
            if(rPace > maxPace) {maxPace = rPace;}
            viewRunning.setupViewRunning(RoundAvoid(rDisaTance,2), RoundAvoid(rPace,2), RoundAvoid(rCalories, 1));
            polylineBetweenTwoPoint(mLocation, location);
        }
        else {
            LatLng myLocation = null;
            myLocation = new LatLng(location.getLatitude(), location.getLongitude());
            viewRunning.getMap().addMarker(new MarkerOptions().position(myLocation).title("My Location"));
        }
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        //mQuery.addLocation(iLocation);
    }

    @Override
    public void moveCamera(Location location) {
        LatLng latLng;
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
        viewRunning.getMap().animateCamera(cameraUpdate);
    }

    @Override
    public void polylineBetweenTwoPoint(Location A, Location B) {
        polygon = viewRunning.getMap().addPolygon(new PolygonOptions()
                .add(new LatLng(A.getLatitude(), A.getLongitude()), new LatLng(B.getLatitude(), B.getLongitude()))
                .strokeColor(Color.BLUE)
                .fillColor(Color.BLACK));
    }

    @Override
    public Location getMyLocation() {
        rLocationManager = (LocationManager)viewRunning.getMainActivity().getSystemService(LOCATION_SERVICE);
        List<String> providers = rLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location myLocation = rLocationManager.getLastKnownLocation(provider);
            if (myLocation == null) {
                continue;
            }
            if (bestLocation == null || myLocation.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = myLocation;
            }
        }
        return bestLocation;
    }

    @Override
    public void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    @Override
    public void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener((Activity) viewRunning.getMainActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
    }

    @Override
    public void startLocationUpdates() {

        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener((Activity) viewRunning.getMainActivity(), new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback,
                                Looper.myLooper());
                    }
                })
                .addOnFailureListener((Activity) viewRunning.getMainActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(String.valueOf(viewRunning.getMainActivity()), "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult()
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult((Activity) viewRunning.getMainActivity(), REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(String.valueOf(viewRunning.getMainActivity()), "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Toast.makeText(viewRunning.getMainActivity(), errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(viewRunning.getMainActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public float getCalories() {
        return rCalories;
    }

    @Override
    public float getDisaTance() {
        return rDisaTance;
    }

    @Override
    public float getPace() {
        return rPace;
    }

    @Override
    public float getMaxPace() {
        return maxPace;
    }

    @Override
    public void onLocationChangedOffline(Location location) {
/*        DatabaseLocation mQuery = new DatabaseLocation(viewRunning.getMainActivity());
        LocationObject iLocation = new LocationObject();
        iLocation.setLatitudeValue(location.getLatitude());
        iLocation.setLongitudeValue(location.getLongitude());*/
/*        if(mStatusTime == true){
            viewRunning.startTime();
            mStatusTime = false;
        }*/
        if(mLatitude != 0){
            mLocation = new Location("B");
            mLocation.setLatitude(mLatitude);
            mLocation.setLongitude(mLongitude);
            float mDisaTance = rDisaTance;
            rDisaTance = rDisaTance +  DistanceLocation(mLocation,location);
            Log.v(String.valueOf(viewRunning.getMainActivity()), "A=" +mLocation);
            Log.v(String.valueOf(viewRunning.getMainActivity()), "B=" +location);
            float rAvg_Distance = rDisaTance - mDisaTance;
            rPace = 0;
            if(rDisaTance > 0) {
                rPace = (viewRunning.getUpdateTime() / 60000) / rDisaTance;
                Toast.makeText(viewRunning.getMainActivity(), ""+viewRunning.getUpdateTime(), Toast.LENGTH_LONG).show();
            }
            rCalories = (float) Calculator.netCalorieBurned(80, 42, rDisaTance, 0, false);
            if(rPace > maxPace) {maxPace = rPace;}
            Toast.makeText(viewRunning.getMainActivity(), ""+rDisaTance, Toast.LENGTH_LONG).show();
            viewRunning.setupViewRunning(RoundAvoid(rDisaTance,2), RoundAvoid(rPace,2), RoundAvoid(rCalories, 1));
        }
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
    }


}
