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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import runningtracker.R;
import runningtracker.common.GenerateID;
import runningtracker.common.InitializationFirebase;
import runningtracker.common.MyLocation;
import runningtracker.data.model.running.InfoUserObject;
import runningtracker.data.model.running.LocationTObject;
import runningtracker.data.model.running.ResultObject;
import runningtracker.data.model.setting.ShareLocationObject;
import runningtracker.model.ObjectCommon;
import runningtracker.model.ResAPICommon;
import runningtracker.data.model.running.LocationObject;
import runningtracker.fitnessstatistic.Calculator;
import runningtracker.running.model.CheckShareCallback;
import runningtracker.running.model.IdFriendsCallback;
import runningtracker.running.model.IdHistoryCallback;
import runningtracker.running.model.InforUserCallback;
import runningtracker.running.model.ListSuggestCallback;
import runningtracker.running.model.LocationHistoryCallback;
import runningtracker.running.model.RunningContract;
import runningtracker.running.model.TrackingHistoryCallback;

import static runningtracker.running.RunningActivity.setupCalories;


public class PresenterRunning {
    private static final int REQUEST_FINE_LOCATION = 0;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 6000;
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
    private MediaPlayer ring, ringPlan;
    private FirebaseUser currentUser;
    private int countNotification = 0;
    private static ArrayList<LatLng> listPoint = new ArrayList<>();
    private GenerateID generateID = new GenerateID();
    private int temp;

    /**
     * Create method list marker
     */
    public ArrayList<Marker> listMarker;


    private InfoUserObject infoUser;
    private FirebaseFirestore firestore;

    RunningContract runningContract;
    ResAPICommon resAPICommon;
    ObjectCommon objectCommon;
    MyLocation myLocation;
    /**
     * ViewFullFriendsActivity method
     */
    private ViewFullFriendsActivity viewFullFriends;

    /**
     * Create constructor PresenterRunning
     *
     * @param runningContract
     */
    public PresenterRunning(RunningContract runningContract) {
        this.runningContract = runningContract;
        this.resAPICommon = new ResAPICommon();
        objectCommon = new ObjectCommon();
        myLocation = new MyLocation();
        viewFullFriends = new ViewFullFriendsActivity();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        /**Create firebase*/
        InitializationFirebase initializationFirebase = new InitializationFirebase();
        firestore = initializationFirebase.createFirebase();

        /**Get info user*/
        infoUser = new InfoUserObject();
        getInforUser(firestore, new InforUserCallback() {
            @Override
            public void successInfor(InfoUserObject infoUserObjectnfo) {
                infoUser = infoUserObjectnfo;
            }
        });
    }

    public PresenterRunning() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        //Create firebase
        InitializationFirebase initializationFirebase = new InitializationFirebase();
        firestore = initializationFirebase.createFirebase();
        //create view full map activity
        viewFullFriends = new ViewFullFriendsActivity();
        //Get info user
        infoUser = new InfoUserObject();
        getInforUser(firestore, new InforUserCallback() {
            @Override
            public void successInfor(InfoUserObject infoUserObjectnfo) {
                infoUser = infoUserObjectnfo;
            }
        });
    }

    public ArrayList<Marker> getListMarker() {
        return listMarker;
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
        ringPlan = MediaPlayer.create(runningContract.getMainActivity(), R.raw.report_plan);
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
                        if(setupCalories > 0) {
                            if (rCalories / setupCalories >= 0.8 && countNotification < 1) {
                                ringPlan.start();
                                countNotification += 1;
                            }
                            if (setupCalories < rCalories && countNotification < 3) {
                                ring.start();
                                countNotification += 1;
                            }
                        }
                        onLocationChanged(locationResult.getLastLocation(), ID, firestore);
                    }
                };
            } else {
                mLocationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        onLocationChanged(locationResult.getLastLocation(), ID, firestore);
                    }
                };
            }
        }

        /**
         * Set value callback if don't connect internet
         * */
        else {
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
     * Update change information if location change
     *
     * @param location
     * @param ID
     * @param firestore
     */
    public void onLocationChanged(Location location, String ID, final FirebaseFirestore firestore) {

        final Map<String, Object> mapShareLocation = new HashMap<>();
        mapShareLocation.put("latitudeValue", location.getLatitude());
        mapShareLocation.put("longitudeValue", location.getLongitude());
        listPoint.add(new LatLng(location.getLatitude(), location.getLongitude()));
        /**
         * Check share location value before update my location value or no
         * */
        firestore.collection("users")
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.getData() != null) {
                            Map<String, Object> userMap = documentSnapshot.getData();

                            if (userMap.containsKey("sharelocation")) {
                                if ((boolean) userMap.get("sharelocation")) {
                                    firestore.collection("users")
                                            .document(currentUser.getUid())
                                            .update("updatelocation", mapShareLocation)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("Status: ", "DocumentSnapshot successfully updated!");
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("Status: ", "Error updating document", e);
                                        }
                                    });
                                }
                            }
                        }
                    }
                });

        //get list location update of friends and set marker
        getListLocationFriends(firestore, new ListSuggestCallback() {
            @Override
            public void getListNameFriends(ArrayList<Marker> listNameFriends) {

            }
        });
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

            if (infoUser != null) {
                if (infoUser.getHeartRate() != 0) {
                   /* SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy",  Locale.US);
                    Date date = null;
                    try {
                        date = sdf.parse(infoUser.getBirthday());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    long dateTime = date.getTime();
                    GenerateID generateID = new GenerateID();
                    long temp = Long.valueOf(generateID.generateTimeID());
                    long oldUser = temp - dateTime;
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(oldUser);
                    int mYear = calendar.get(Calendar.YEAR);*/
                    rCalories = (float) Calculator.netCalorieBurned(infoUser.getWeight(),
                            Calculator.vO2max(30, (int) infoUser.getHeartRate()), rDisaTance, 0, false);
                }
                rCalories = (float) Calculator.netCalorieBurned(infoUser.getWeight(),
                        42, rDisaTance, 0, false);
            }
            if (rPace > maxPace) {
                maxPace = rPace;
            }
            runningContract.setupViewRunning(RoundAvoid(rDisaTance, 2), RoundAvoid(rPace, 2), RoundAvoid(rCalories, 1));
            polylineBetweenTwoPoint(listPoint);
            //save data
            LocationTObject iLocation = new LocationTObject();
            iLocation.setLatitudeValue(mLocation.getLatitude());
            iLocation.setLongitudeValue(mLocation.getLongitude());
            iLocation.setTimeUpdate(Double.parseDouble(generateID.generateTimeID()));
            saveLocationData(ID, firestore, iLocation);
        } else {
            LatLng myLocation;
            myLocation = new LatLng(location.getLatitude(), location.getLongitude());
            runningContract.getMap().addMarker(new MarkerOptions().position(myLocation).title("Bắt đầu"));
        }
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
    }

    /**
     * Move camera to location point
     *
     * @param location
     */
    public void moveCamera(Location location) {
        LatLng latLng;
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
        runningContract.getMap().animateCamera(cameraUpdate);
    }

    /**
     * @param list
     */
    public void polylineBetweenTwoPoint(ArrayList<LatLng> list) {
        runningContract.getMap().clear();
        runningContract.getMap().addMarker(new MarkerOptions().position(list.get(0)).title("Bắt đầu"));
        runningContract.getMap().addPolyline(new PolylineOptions()
                .addAll(list)
                .color(Color.BLUE)
                .width(10));
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
                        if (ActivityCompat.checkSelfPermission(runningContract.getMainActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        /**
         * Check share location value before update my location value or no
         * */

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
            if (infoUser != null) {
                if (infoUser.getHeartRate() != 0) {
                   /* SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = null;
                    try {
                        date = sdf.parse(infoUser.getBirthday());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    long dateTime = date.getTime();
                    GenerateID generateID = new GenerateID();
                    long temp = Long.valueOf(generateID.generateTimeID());
                    long oldUser = temp - dateTime;
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(oldUser);
                    int mYear = calendar.get(Calendar.YEAR);*/
                    rCalories = (float) Calculator.netCalorieBurned(infoUser.getWeight(),
                            Calculator.vO2max(30, (int) infoUser.getHeartRate()), rDisaTance, 0, false);
                }
                rCalories = (float) Calculator.netCalorieBurned(infoUser.getWeight(),
                        42, rDisaTance, 0, false);
            }
            if (rPace > maxPace) {
                maxPace = rPace;
            }
            runningContract.setupViewRunning(RoundAvoid(rDisaTance, 2),
                    RoundAvoid(rPace, 2), RoundAvoid(rCalories, 1));

            LocationTObject locationObject = new LocationTObject(mLocation.getLatitude(),
                    mLocation.getLongitude(), Double.parseDouble(generateID.generateTimeID()));
            saveLocationData(ID, firestore, locationObject);
        }
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();

    }

    /**
     * @param context
     * @return true if connect internet, false if don't connect internet
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
     * @param firestore
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
     * Save data location of user
     *
     * @param ID
     * @param firestore
     * @param location
     */
    private void saveLocationData(String ID, FirebaseFirestore firestore, LocationTObject location) {
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
     * Save history
     *
     * @param ID
     * @param firestore
     * @param resultObject
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
     * Get id history nearer
     *
     * @param firestore
     * @param idHistoryCallback
     */
    public void getIdHistory(final FirebaseFirestore firestore, final IdHistoryCallback idHistoryCallback) {

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
                        try {
                            histories.add(document.getData());
                            idHistoryCallback.onSuccess(histories);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    /**
     * @param firestore
     * @param locationCallback
     */
    public void getDataLocation(final FirebaseFirestore firestore, final LocationHistoryCallback locationCallback) {

        getIdHistory(firestore, new IdHistoryCallback() {
            @Override
            public void onSuccess(List<Map<String, Object>> histories) {
                Map<String, Object> lastHistory = histories.get(0);
                String historyID = lastHistory.get("id").toString();

                firestore.collection("users").
                        document(currentUser.getUid())
                        .collection("histories")
                        .document(historyID).collection("locations")
                        .orderBy("timeUpdate")
                        .get()

                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    try {
                                        List<LocationObject> locationList = task.getResult().toObjects(LocationObject.class);
                                        locationCallback.dataLocation(locationList);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
            }
        });
    }

    /**
     * Get data tracking history of user
     *
     * @param id
     * @param firestore
     * @param trackingHistoryCallback
     */
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
                            try {
                                List<ResultObject> resultObject = task.getResult().toObjects(ResultObject.class);
                                trackingHistoryCallback.onSuccessTrackingData(resultObject);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    /**
     * Get list data location history of user
     *
     * @param id
     * @param firestore
     * @param locationCallback
     */
    public void getListLocationHistory(String id, final FirebaseFirestore firestore, final LocationHistoryCallback locationCallback) {

        firestore.collection("users").
                document(currentUser.getUid())
                .collection("histories")
                .document(id).collection("locations").orderBy("timeUpdate")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            try {
                                List<LocationObject> locationList = task.getResult().toObjects(LocationObject.class);
                                locationCallback.dataLocation(locationList);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    /**
     * Get id friends
     *
     * @param firestore
     * @param idFriendsCallback
     */
    public void getIdFriends(FirebaseFirestore firestore, final IdFriendsCallback idFriendsCallback) {
        final List<Map<String, Object>> friends = new ArrayList<>();
        firestore.collection("users")
                .document(currentUser.getUid())
                .collection("friends")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                friends.add(document.getData());
                            }
                            idFriendsCallback.onSuccess(friends);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    /**
     * heck share location friends
     *
     * @param idFriend
     * @param firestore
     * @param checkShareCallback
     */
    private void checkShareLocation(String idFriend, FirebaseFirestore firestore, final CheckShareCallback checkShareCallback) {
        DocumentReference docRef = firestore.collection("users")
                .document(idFriend);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                ShareLocationObject shareLocationObject = documentSnapshot.toObject(ShareLocationObject.class);
                Map<String, Object> user = documentSnapshot.getData();
                if (user.containsKey("sharelocation"))
                    checkShareCallback.successShare((boolean) user.get("sharelocation"));
            }
        });
    }

    /**
     * Set marker of friends
     *
     * @param locationObject
     * @param nameFriends
     */
    private void setMarker(LocationObject locationObject, String nameFriends, ListSuggestCallback listSuggestCallback) {

        //Set view map icon
        if (runningContract != null) {
            runningContract.getMapShare().addMarker(new MarkerOptions().position(new LatLng(locationObject.getLatitudeValue(), locationObject.getLongitudeValue()))
                    .title(nameFriends));

            CameraUpdate cameraUpdateIcon = CameraUpdateFactory.newLatLngZoom((new LatLng(locationObject.getLatitudeValue(), locationObject.getLongitudeValue())), 15);
            runningContract.getMapShare().animateCamera(cameraUpdateIcon);
        }

        //Set view full map friends
        if (viewFullFriends.getActivityViewFull() != null) {
            viewFullFriends.getActivityViewFull().clear();
            int k = 0;
            for (Marker marker : listMarker) {
                if (marker.getTitle().equalsIgnoreCase(nameFriends))
                    k++;
            }
            if (k < 1) {
/*                listMarker.add(viewFullFriends.getActivityViewFull().addMarker(new MarkerOptions().position(new LatLng(locationObject.getLatitudeValue(), locationObject.getLongitudeValue()))
                        .title(nameFriends)));*/
                listMarker.add(viewFullFriends.getActivityViewFull().addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.runner))
                        .title("Bắt đầu")
                        .position(new LatLng(locationObject.getLatitudeValue(), locationObject.getLongitudeValue())).title(nameFriends)
                ));
            }
            listSuggestCallback.getListNameFriends(listMarker);
            if (temp == 0) {
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom((new LatLng(locationObject.getLatitudeValue(), locationObject.getLongitudeValue())), 15);
                viewFullFriends.getActivityViewFull().animateCamera(cameraUpdate);
                temp++;
            }
        }
    }

    /**
     * Search marker after move camera
     *
     * @param nameMarker
     */
    public void searchMarker(String nameMarker) {

        //Check list marker is null or not null
        if (listMarker != null) {
            //Search marker in list marker
            for (Marker m : listMarker) {
                if (m.getTitle().equalsIgnoreCase(nameMarker)) {
                    LatLng location = m.getPosition();
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, 15);
                    viewFullFriends.getActivityViewFull().animateCamera(cameraUpdate);
                }
            }
        }
    }

    /**
     * Get location update friends
     *
     * @param firestore
     */
    public void getListLocationFriends(final FirebaseFirestore firestore, final ListSuggestCallback listSuggestCallback) {
        listMarker = new ArrayList<>();
        getIdFriends(firestore, new IdFriendsCallback() {
            @Override
            public void onSuccess(List<Map<String, Object>> idFriends) {
                for (int i = 0; i < idFriends.size(); i++) {
                    //Get id of friend
                    Map<String, Object> lastIdFriend = idFriends.get(i);

                    DocumentReference friendRef = (DocumentReference) lastIdFriend.get("friend");
                    final String idFriend = friendRef.getId();

//                    final String idFriend = lastIdFriend.get("uid").toString();
//                    final String nameFriends = lastIdFriend.get("displayName").toString();

                    friendRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String idFriend = documentSnapshot.getId();

                            Map<String, Object> friend = documentSnapshot.getData();

                            if (friend.get("sharelocation") != null) {
                                if ((boolean) friend.get("sharelocation")) {
                                    if (friend.get("updatelocation") != null) {
                                        Map<String, Object> updateLocation = (Map<String, Object>) friend.get("updatelocation");
                                        Double latitude = Double.valueOf(updateLocation.get("latitudeValue").toString());
                                        Double longitude = Double.valueOf(updateLocation.get("longitudeValue").toString());
                                        LocationObject locationObject = new LocationObject(latitude, longitude);

                                        String nameFriends = friend.get("displayName").toString();
                                        setMarker(locationObject, nameFriends, listSuggestCallback);


                                    }

                                }
                            }
                        }
                    });


//                    checkShareLocation(idFriend, firestore, new CheckShareCallback() {
//                        @Override
//                        public void successShare(Boolean status) {
//                            //if is true get location update of friend
//                            if (status) {
//                                firestore.collection("users")
//                                        .document(idFriend).get()
//                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                                            @Override
//                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                                if (documentSnapshot.getData().get("updatelocation") != null) {
//                                                    Map<String, Object> locationMap = (Map<String, Object>) documentSnapshot.getData().get("updatelocation");
//                                                    double latitude = Double.valueOf(locationMap.get("latitude").toString());
//                                                    double longitude = Double.valueOf(locationMap.get("longitude").toString());
//                                                    Log.d(TAG,locationMap.get("latitude").toString()+","+locationMap.get("longitude").toString());
//                                                    LocationObject locationObject = new LocationObject(latitude, longitude);
//                                                    String nameFriends = documentSnapshot.get("displayName").toString();
//                                                    setMarker(locationObject, nameFriends, listSuggestCallback);
//                                                }
//                                            }
//                                        });
//                            }
//                        }
//                    });
                }
            }
        });
    }

    /**
     * Get information user
     *
     * @param firestore
     * @param inforUserCallback
     */
    public void getInforUser(final FirebaseFirestore firestore, final InforUserCallback inforUserCallback) {
        DocumentReference docRefInfo = firestore.collection("usersData")
                .document(currentUser.getUid());
        docRefInfo.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                InfoUserObject infoUserObject = documentSnapshot.toObject(InfoUserObject.class);
                inforUserCallback.successInfor(infoUserObject);
            }
        });
    }
}

