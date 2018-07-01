package runningtracker.running;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import runningtracker.R;
import runningtracker.common.GenerateID;
import runningtracker.common.InitializationFirebase;
import runningtracker.common.MyLocation;
import runningtracker.data.model.Friend;
import runningtracker.data.model.User;
import runningtracker.data.model.running.IdHistory;
import runningtracker.data.model.running.ResultObject;
import runningtracker.model.modelrunning.BodilyCharacteristicObject;
import runningtracker.fitnessstatistic.Calculator;
import runningtracker.running.model.RunningContract;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;


public class RunningActivity extends AppCompatActivity implements RunningContract, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks {

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final String TAG = "RunningActivity123";
    private GoogleMap mMap, mMapShareLocation;
    Date startCurrentTime, stopCurrentTime;
    float rGrossCalorie;
    TextView txtTimer;
    long lStartTime, rUpdateTime, lPauseTime, lSystemTime = 0L;
    Handler handler = new Handler();
    boolean isRun;
    private String timeRunning;
    private ImageView statusConnect;
    private User mCurrentUser;
    private FirebaseAuth mAuth;

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            lSystemTime = SystemClock.uptimeMillis() - lStartTime;
            long lUpdateTime = lPauseTime + lSystemTime;
            rUpdateTime = lUpdateTime;
            long secs = (lUpdateTime / 1000);
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
    private Boolean checkConnect;

    LocationManager locationManager;
    LocationListener locListener;
    String locationProvider;

    private IdHistory idHistory;
    private FirebaseFirestore firestore;
    private Toolbar actionBar;

    /**
     * create method set value calories
     */
    int checkedItems = -1;
    public static int setupCalories = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);
        presenterRunning = new PresenterRunning(this);
        myLocation = new MyLocation();

        initializeUI();
        init();

        /**
         *  online onCreate
         * */
        presenterRunning.initialization();
        txtTimer = findViewById(R.id.textValueDuration);
        statusConnect = findViewById(R.id.iconStatus);

        checkConnect = presenterRunning.isConnected(this);

        InitializationFirebase initializationFirebase = new InitializationFirebase();
        firestore = initializationFirebase.createFirebase();

        GenerateID generateID = new GenerateID();
        idHistory = new IdHistory();
        idHistory.id = generateID.generateTimeID();

        /**
         * Save file day help select and chosen data
         * */
        presenterRunning.saveHistory(idHistory.id, firestore);

        presenterRunning.createLocationCallback(checkConnect, idHistory.id, firestore);
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
                presenterRunning.onLocationChangedOffline(location, idHistory.id, firestore);
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap = googleMap;
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            Location L = myLocation.getMyLocation(this);
            if (L != null) {
                presenterRunning.moveCamera(L);
            }
        } else requestPermissions();
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                MyLocation myLocation = new MyLocation();
                sendNotification(myLocation.getMyLocation(getMainActivity()).getLatitude(),
                        myLocation.getMyLocation(getMainActivity()).getLongitude());
            }
        });

    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            Snackbar.make(
                    findViewById(R.id.activity_runnning),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(RunningActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(RunningActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
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
    public void startTime() {
        if (isRun)
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
    public void stopTime() {
        if (!isRun)
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
    public GoogleMap getMapShare() {
        return mMapShareLocation;
    }

    @Override
    public void pauseTime() {
        if (!isRun)
            return;
        isRun = false;
        lPauseTime += lSystemTime;
        handler.removeCallbacks(runnable);
    }

    @Override
    public void setupViewRunning(float mDistanceValue, float mPaceValue, float mCalorie) {
        TextView txtDistance = findViewById(R.id.textValueDistance);
        TextView txtNetCalorie = findViewById(R.id.textValueCalorie);
        TextView txtPace = findViewById(R.id.textValuePace);
        txtDistance.setText(Float.toString(mDistanceValue));
        txtNetCalorie.setText(Float.toString(mCalorie));
        String rMin = "";
        String rSec = "";
        if (mPaceValue > 0.0) {
            Log.d(TAG, "index=" + mPaceValue);
            int rA = (int) (mPaceValue);
            int rB = (int) ((mPaceValue - rA) * 100);
            if (rA > 999) {
                rSec = String.valueOf(rB);
                String rPace = "999:" + rSec;
                txtPace.setText(rPace);
            } else {
                rMin = String.valueOf(rA);
                rSec = String.valueOf(rB);
                String rPace = rMin + ":" + rSec;
                txtPace.setText(rPace);
            }
        } else {
            txtPace.setText("00:00");
        }
    }

    /**
     * Create menu setting item
     */
    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate navigation menu from the resources by using the menu inflater.
        getMenuInflater().inflate(R.menu.navigation_menu, menu);
        return true;
    }

    /**
     * @param item
     * @return true or false
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        actionBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        switch (item.getItemId()) {
            case R.id.setting:
                createDialogCalories();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initializeUI() {
        //Toolbar
        actionBar = findViewById(R.id.actionbarTracking);
        actionBar.setNavigationIcon(R.drawable.ic_android_back_white_24dp);
        setSupportActionBar(actionBar);

        //Create map tracking
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Create map view small location share of friends
        MapFragment mapFragmentShare = (MapFragment) getFragmentManager().findFragmentById(R.id.mapShareLocation);
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
                     * Get touch event on map fragment
                     * */
                    mMapShareLocation.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                        @Override
                        public void onMapLongClick(LatLng latLng) {
                            Intent intent0 = new Intent(getMainActivity(), ViewFullFriendsActivity.class);
                            startActivity(intent0);
                        }
                    });
                }
            }
        });
    }


    /**
     * send data to ResultActivity
     */
    private void sendDataToResult() {
        //calculator grossCalorieBurned
        rGrossCalorie = 0;
        if (m_Bodily != null) {
            rGrossCalorie = (float) Calculator.grossCalorieBurned(presenterRunning.getCalories(), m_Bodily.getRestingMetabolicRate(), rUpdateTime / 3600000);
            rGrossCalorie = presenterRunning.RoundAvoid(rGrossCalorie, 2);
        }
        Intent nextActivity = new Intent(RunningActivity.this, ResultActivity.class);
        nextActivity.putExtra("duration", timeRunning);
        nextActivity.putExtra("distance", presenterRunning.RoundAvoid(presenterRunning.getDisaTance(), 2));
        nextActivity.putExtra("avgPace", presenterRunning.RoundAvoid(presenterRunning.getPace(), 2));
        nextActivity.putExtra("maxPace", presenterRunning.RoundAvoid(presenterRunning.getMaxPace(), 2));
        nextActivity.putExtra("netCalorie", presenterRunning.RoundAvoid(presenterRunning.getCalories(), 1));
        nextActivity.putExtra("grossCalorie", presenterRunning.RoundAvoid(rGrossCalorie, 1));

        saveRunning();
        startActivity(nextActivity);
    }

    public void saveRunning() {
        ResultObject resultObject;
        resultObject = new ResultObject(timeRunning, presenterRunning.RoundAvoid(presenterRunning.getDisaTance(), 2),
                presenterRunning.RoundAvoid(presenterRunning.getPace(), 2),
                presenterRunning.RoundAvoid(presenterRunning.getMaxPace(), 2),
                presenterRunning.RoundAvoid(presenterRunning.getCalories(), 1),
                presenterRunning.RoundAvoid(rGrossCalorie, 2));
        //push data to firebase
        presenterRunning.saveHistoryRunningData(idHistory.id, firestore, resultObject);
    }

    public void onClickStartButton(View startButton) {

        //Perform animation
        ImageButton pauseButton = findViewById(R.id.pauseButton);
        ImageButton stopButton = findViewById(R.id.stopButton);

        Animation pauseButtonAnimation = AnimationUtils.loadAnimation(RunningActivity.this, R.anim.pause_button_separation);
        Animation stopButtonAnimation = AnimationUtils.loadAnimation(RunningActivity.this, R.anim.stop_button_separation);
        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
        stopButton.setEnabled(true);
        startButton.setVisibility(View.INVISIBLE);
        pauseButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.VISIBLE);
        stopButton.startAnimation(stopButtonAnimation);

        startTime();
        if (checkConnect) {
            statusConnect.setImageResource(R.drawable.ic_online);
            presenterRunning.startLocationUpdates();
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            statusConnect.setImageResource(R.drawable.ic_offline);
            locationManager.requestLocationUpdates(locationProvider, 0, 0, locListener);
        }
        //Get start time
        startCurrentTime = Calendar.getInstance().getTime();
    }

    public void onClickPauseButton(View pauseButton) {
        //Perform animation
        ImageButton resumeButton = findViewById(R.id.resumeButton);
        ImageButton stopButton = findViewById(R.id.stopButton);
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
        if (checkConnect) {
            presenterRunning.stopLocationUpdates();
        } else {
            locationManager.removeUpdates(locListener);
        }
    }

    public void onClickResumeButton(View resumeButton) {
        //Perform animation
        ImageButton pauseButton = findViewById(R.id.pauseButton);
        ImageButton stopButton = findViewById(R.id.stopButton);
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
        if (checkConnect) {
            presenterRunning.startLocationUpdates();
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(locationProvider, 0, 0, locListener);
        }
    }

    public void onClickStopButton(View view) throws JSONException {
        if (checkConnect) {
            presenterRunning.stopLocationUpdates();
        } else {
            locationManager.removeUpdates(locListener);
        }
        stopTime();
        stopCurrentTime = Calendar.getInstance().getTime();
        sendDataToResult();
    }

    /**
     * Create dialog setting calories before tracking of user
     */
    private void createDialogCalories() {

        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        final String[] arrayAdater = {"1000", "2000"};

        mBuilder.setTitle("Thiết Lập Calories");
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_chose_calories, null);
        mBuilder.setView(dialogView);
        final EditText editTextCalories = dialogView.findViewById(R.id.edtSetupCalories);

        mBuilder.setSingleChoiceItems(arrayAdater, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                checkedItems = i;
            }
        });
        mBuilder.setCancelable(false);

        mBuilder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                /**
                 * check condition setup calories from dialog
                 * */
                //mBuilder.set(R.layout.dialog_chose_calories);
                String temp = String.valueOf(editTextCalories.getText());
                int calories = -1;
                if (!temp.equals("")) {
                    calories = Integer.parseInt(temp);
                }

                if (checkedItems >= 0 && calories > 0) {
                    setupCalories = calories;
                } else if (checkedItems >= 0) {
                    int tempCalories = Integer.parseInt(arrayAdater[checkedItems]);
                    setupCalories = tempCalories;
                } else if (calories > 0) {
                    setupCalories = calories;
                } else {
                    setupCalories = 0;

                }
            }
        });

        mBuilder.setNeutralButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.cancel();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    /**
     * Sent notification to friends
     *
     * @param latitudeValue
     * @param longitudeValue
     */
    public void sendNotification(final double latitudeValue, final double longitudeValue) {

        final String message = "Đang gặp sự cố!!";

        firestore.collection("users").document(mCurrentUser.getUid()).collection("friends").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                Friend friend = documentSnapshot.toObject(Friend.class);
                                Map<String, Object> notificationMessage = new HashMap<>();
                                notificationMessage.put("message", message);
                                notificationMessage.put("from", mCurrentUser.getUid());
                                notificationMessage.put("fromName", mCurrentUser.getDisplayName());
                                notificationMessage.put("latitudeValue", latitudeValue);
                                notificationMessage.put("longitudeValue", longitudeValue);
                                firestore.collection("users/" + friend.getUid() + "/notifications").add(notificationMessage)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Log.d(TAG, "Ghi data thanh cong");
                                                sendToast("Đã gửi thông báo");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                sendToast("Gửi thông báo không thành công");
                                            }
                                        });
                            }
                        }
                    }
                });
    }

    private void sendToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator.hasVibrator()) {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                VibrationEffect effect = VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE);
                vibrator.vibrate(effect);
            } else {
                vibrator.vibrate(200);
            }

        }
    }

    /**
     * create objects
     */
    public void init() {
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        getCurrentUser();

    }

    public void getCurrentUser() {
        firestore.collection("users").document(mAuth.getCurrentUser().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            mCurrentUser = task.getResult().toObject(User.class);
                            Log.d(TAG, task.getResult().getData().toString());
                        }
                    }
                });
    }
}