package runningtracker.running;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import runningtracker.R;
import runningtracker.common.MyLocation;
import runningtracker.data.model.running.ResultObject;
import runningtracker.model.ObjectCommon;
import runningtracker.model.modelrunning.BodilyCharacteristicObject;
import runningtracker.model.ResAPICommon;
import runningtracker.model.modelrunning.DatabaseLocation;
import runningtracker.data.model.running.LocationObject;
import runningtracker.fitnessstatistic.Calculator;


public class PresenterRunning {
    private static final int REQUEST_FINE_LOCATION = 0;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 3000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private static String TAG = "Error ";
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private ArrayList<LocationObject> startToPresentLocations;
    private Polygon polygon;
    private boolean mStatusTime;
    float rGrossCalorie;
    private LocationManager rLocationManager;
    private LocationListener locationListener;
    private String provider;
    private float rDisaTance;
    private Location mLocation;
    double mLatitude, mLongitude;
    float rCalories, rPace, maxPace;
    private MediaPlayer ring;
    private FirebaseUser currentUser;

    BodilyCharacteristicObject m_Bodily;

    RunningContract runningContract;
    ResAPICommon resAPICommon;
    ObjectCommon objectCommon;
    MyLocation myLocation;

    public PresenterRunning(RunningContract runningContract) {
        this.runningContract = runningContract;
        this.resAPICommon = new ResAPICommon();
        objectCommon = new ObjectCommon();
        myLocation = new MyLocation();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    public PresenterRunning() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    public void saveRunning() throws JSONException {
    }


    public void getData() {
    }


    public float DistanceLocation(Location locationA, Location locationB) {
        float distance;
        distance = locationA.distanceTo(locationB) / 1000;// change to meter to kilometer
        return distance;
    }


    public float RoundAvoid(double value, int places) {
        double scale = Math.pow(10, places);
        return (float) (Math.round(value * scale) / scale);
    }


    //Setup running
    public void initialization() {
        rCalories = 0;
        maxPace = 0;
        rGrossCalorie = 0;
        mLocation = new Location("A");
        mStatusTime = false;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(runningContract.getMainActivity());
        mSettingsClient = LocationServices.getSettingsClient(runningContract.getMainActivity());
        objectCommon.setMaxCalores(2);
        ring = MediaPlayer.create(runningContract.getMainActivity(), R.raw.report_maxcalorie);
    }

    public void createLocationCallback(Boolean onCheckConnect, final String ID, final FirebaseFirestore firestore) {
        if (onCheckConnect) {
            /**
             * Set value callback if have connect internet
             * */
            if (objectCommon.getMaxCalores() > 0) {
                mLocationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        if (objectCommon.getMaxCalores() < rCalories)
                            ring.start();
                        onLocationChanged(locationResult.getLastLocation(), ID, firestore);
                    }
                };
            }
            /**
             * Set value callback if don't connect internet
             * */
            else {
                mLocationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        onLocationChanged(locationResult.getLastLocation(), ID, firestore);
                    }
                };
            }
        } else {
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    Location location = myLocation.getMyLocation(runningContract.getMainActivity());
                    onLocationChangedOffline(location, ID, firestore);
                }
            };
        }
    }

    /**
     * @param : Location
     * @function: Update change information if location change
     */
    public void onLocationChanged(Location location, String ID, FirebaseFirestore firestore) {
        DatabaseLocation mQuery = new DatabaseLocation(runningContract.getMainActivity());
        LocationObject iLocation = new LocationObject();
        iLocation.setLatitudeValue(location.getLatitude());
        iLocation.setLongitudeValue(location.getLongitude());
        moveCamera(location);
        if (mLatitude != 0) {
            rPace = 0;
            mLocation = new Location("B");
            mLocation.setLatitude(mLatitude);
            mLocation.setLongitude(mLongitude);
            float mDisaTance = rDisaTance;
            rDisaTance = rDisaTance + DistanceLocation(mLocation, location);
            if (rDisaTance > 0) {
                rPace = (runningContract.getUpdateTime() / 60000) / rDisaTance;
            }
            //if(m_Bodily != null)
            //rCalories = (float) Calculator.netCalorieBurned(m_Bodily.getWeightInKg(), m_Bodily.getVo2Max(), rDisaTance, 0, false);
            rCalories = (float) Calculator.netCalorieBurned(80, 42, rDisaTance, 0, false);
            if (rPace > maxPace) {
                maxPace = rPace;
            }
            runningContract.setupViewRunning(RoundAvoid(rDisaTance, 2), RoundAvoid(rPace, 2), RoundAvoid(rCalories, 1));
            polylineBetweenTwoPoint(mLocation, location);
        } else {
            LatLng myLocation = null;
            myLocation = new LatLng(location.getLatitude(), location.getLongitude());
            runningContract.getMap().addMarker(new MarkerOptions().position(myLocation).title("My Location"));
        }
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();

        saveLocationData(ID, firestore, iLocation);
    }

    /**
     * @param : Location need move camera
     */
    public void moveCamera(Location location) {
        LatLng latLng;
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
        runningContract.getMap().animateCamera(cameraUpdate);
    }

    /**
     * @param : 2 location need draw
     */
    public void polylineBetweenTwoPoint(Location A, Location B) {
        polygon = runningContract.getMap().addPolygon(new PolygonOptions()
                .add(new LatLng(A.getLatitude(), A.getLongitude()), new LatLng(B.getLatitude(), B.getLongitude()))
                .strokeColor(Color.BLUE)
                .fillColor(Color.BLACK));
    }

    public void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    public void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener((Activity) runningContract.getMainActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
    }


    public void startLocationUpdates() {

        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener((Activity) runningContract.getMainActivity(), new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        if (ActivityCompat.checkSelfPermission((Activity) runningContract.getMainActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission((Activity) runningContract.getMainActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback,
                                Looper.myLooper());
                    }
                })
                .addOnFailureListener((Activity) runningContract.getMainActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(String.valueOf(runningContract.getMainActivity()), "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult()
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult((Activity) runningContract.getMainActivity(), REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(String.valueOf(runningContract.getMainActivity()), "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                        }
                    }
                });
    }


    public boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(runningContract.getMainActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }


    public void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public float getCalories() {
        return rCalories;
    }

    public float getDisaTance() {
        return rDisaTance;
    }

    public float getPace() {
        return rPace;
    }

    public float getMaxPace() {
        return maxPace;
    }

    public void onLocationChangedOffline(Location location, String ID, FirebaseFirestore firestore) {
        if (mLatitude != 0) {
            rPace = 0;
            mLocation = new Location("B");
            mLocation.setLatitude(mLatitude);
            mLocation.setLongitude(mLongitude);
            float mDisaTance = rDisaTance;
            rDisaTance = rDisaTance + DistanceLocation(mLocation, location);
            if (rDisaTance > 0) {
                rPace = (runningContract.getUpdateTime() / 60000) / rDisaTance;
            }
            rCalories = (float) Calculator.netCalorieBurned(80, 42, rDisaTance, 0, false);
            if (rPace > maxPace) {
                maxPace = rPace;
            }
            runningContract.setupViewRunning(RoundAvoid(rDisaTance, 2), RoundAvoid(rPace, 2), RoundAvoid(rCalories, 1));

            LocationObject locationObject = new LocationObject(mLocation.getLatitude(), mLocation.getLongitude());
            saveLocationData(ID, firestore, locationObject);
        }
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();

    }

    /**
     * @param : activity
     * @return: true if connect internet, false if don't connect internet
     */
    public boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param ID
     */
    public void saveHistory(String ID, FirebaseFirestore firestore) {
        Map<String, Object> history = new HashMap<>();
        history.put("id", ID);
        firestore.collection("users").document(currentUser.getUid()).collection("histories").document(ID)
                .set(history)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    /**
     * @param : id histories collection, FirebaseFirestore and location object
     * @function: Save location data firebase
     */
    private void saveLocationData(String ID, FirebaseFirestore firestore, LocationObject location) {
        firestore.collection("users").document(currentUser.getUid()).collection("histories").document(ID)
                .collection("locations").document().set(location)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });


    }


    /**
     * @param : id histories collection, FirebaseFirestore and ResultObject object
     * @function: Save location data firebase
     */
    public void saveHistoryRunningData(String ID, FirebaseFirestore firestore, ResultObject resultObject) {
        firestore.collection("users").document(currentUser.getUid()).collection("histories").document(ID)
                .collection("result").document().set(resultObject)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    /**
     * @function: Get id history nearer
    * */
    public void getIdHistory(String id, final FirebaseFirestore firestore, final IdHistoryCallback idHistoryCallback){

        final List<Map<String, Object>> histories = new ArrayList<>();
        firestore.collection("users")
                .document(currentUser.getUid())
                .collection("histories")
                .orderBy("id", Query.Direction.DESCENDING).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        /**
                         * get information history nearer
                        * */
                        histories.add(document.getData());
                        idHistoryCallback.onSuccess(histories);
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    /**
     * @param:
     * @return:
     */
    public void getDataLocation(String id, final FirebaseFirestore firestore, final LocationHistoryCallback locationCallback) {

        getIdHistory(id, firestore, new IdHistoryCallback() {
            @Override
            public void onSuccess(List<Map<String, Object>> histories) {
                Map<String, Object> lastHistory = histories.get(0);
                String historyID = lastHistory.get("id").toString();

                firestore.collection("users").
                        document(currentUser.getUid())
                        .collection("histories")
                        .document(historyID).collection("locations")
                        .get()

                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    List<LocationObject> locationList = task.getResult().toObjects(LocationObject.class);
                                    locationCallback.dataLocation(locationList);
                                }
                            }
                        });
            }
        });
    }
    /**
     * Get data tracking history of user
    * */
    public void getTrackingHistory(String id, final FirebaseFirestore firestore, final TrackingHistoryCallback trackingHistoryCallback) {

        firestore.collection("users").
                document(currentUser.getUid())
                .collection("histories")
                .document(id).collection("result")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<ResultObject> resultObject = task.getResult().toObjects(ResultObject.class);
                            trackingHistoryCallback.onSuccessTrackingData(resultObject);
                        }
                    }
                });
    }
    /**
     * Get list data location history of user
    * */
    public void getListLocationHistory(String id, final FirebaseFirestore firestore, final LocationHistoryCallback locationCallback) {

        firestore.collection("users").
                document(currentUser.getUid())
                .collection("histories")
                .document(id).collection("locations")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<LocationObject> locationList = task.getResult().toObjects(LocationObject.class);
                            locationCallback.dataLocation(locationList);
                        }
                    }
                });
    }
}

