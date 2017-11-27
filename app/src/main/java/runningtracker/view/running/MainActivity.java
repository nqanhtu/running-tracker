package runningtracker.view.running;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
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
import runningtracker.R;
import runningtracker.model.modelrunning.BodilyCharacteristicObject;
import runningtracker.model.modelrunning.DatabaseLocation;
import runningtracker.presenter.running.LogicRunning;
import runningtracker.presenter.fitnessstatistic.Calculator;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import org.json.JSONException;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements ViewRunning, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks{
    private static final String TAG = MainActivity.class.getSimpleName();
    private GoogleMap mMap;
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
    BodilyCharacteristicObject m_Bodily;
    private LogicRunning logicRunning;
    private final DatabaseLocation mQuery = new DatabaseLocation(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);
        logicRunning = new LogicRunning(this);

        initializeUI();
        logicRunning.initialization();
        txtTimer = (TextView) findViewById(R.id.textValueDuration);

        logicRunning.createLocationCallback();
        logicRunning.createLocationRequest();
        logicRunning.buildLocationSettingsRequest();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(logicRunning.checkPermissions()) {
            mMap = googleMap;
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            Location L = logicRunning.getMyLocation();
            if(L != null) {
                logicRunning.moveCamera(L);
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
    //khoan hãy xóa
   /* @Override
    public JSONObject getValueRunning(){
        JSONObject jsonObject = new JSONObject();
        try{
            //jsonObject.put("RunningSessionID",1);
            jsonObject.put("Userid", 1);
            jsonObject.put("Runontreadmill", 0);
            jsonObject.put("Roadgradient", 0);
            jsonObject.put("Grosscalorieburned", logicRunning.RoundAvoid(rGrossCalorie,1));
            jsonObject.put("Netcalorieburned", logicRunning.RoundAvoid(rCalories,1));
            jsonObject.put("Distanceinkm", logicRunning.RoundAvoid(rDisaTance,2));
            jsonObject.put("Starttimestamp", startCurrentTime);
            jsonObject.put("Finishtimestamp", stopCurrentTime);
            return jsonObject;
        }catch (JSONException e){
            throw new RuntimeException(e);
        }
    }
*/
    @Override
    public Context getMainActivity() {
        return MainActivity.this;
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
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    //send data to ResultActivity
    private void sendDataToResult() throws JSONException {
        //calculator grossCalorieBurned
        rGrossCalorie = 0;
        if(m_Bodily != null) {
            rGrossCalorie = (float) Calculator.grossCalorieBurned(logicRunning.getCalories(), m_Bodily.getRestingMetabolicRate(), rUpdateTime /3600000);
            rGrossCalorie = logicRunning.RoundAvoid(rGrossCalorie, 2);
        }
        Intent nextActivity = new Intent(MainActivity.this, ResultActivity.class);
        nextActivity.putExtra("duration", rUpdateTime);
        nextActivity.putExtra("distance", logicRunning.RoundAvoid(logicRunning.getDisaTance(),2));
        nextActivity.putExtra("avgPace", logicRunning.RoundAvoid(logicRunning.getPace(),2));
        nextActivity.putExtra("maxPace", logicRunning.RoundAvoid(logicRunning.getMaxPace(),2));
        nextActivity.putExtra("netCalorie", logicRunning.RoundAvoid(logicRunning.getCalories(),1));
        nextActivity.putExtra("grossCalorie", logicRunning.RoundAvoid(rGrossCalorie,1));
        //logicRunning.saveRunning();
        startActivity(nextActivity);
    }

    public void onClickStartButton(View startButton) {
        // Perform animation
        ImageButton pauseButton = (ImageButton) findViewById(R.id.pauseButton);
        ImageButton stopButton = (ImageButton) findViewById(R.id.stopButton);

        Animation pauseButtonAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.pause_button_separation);
        Animation stopButtonAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.stop_button_separation);
        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
        stopButton.setEnabled(true);
        startButton.setVisibility(View.INVISIBLE);
        pauseButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.VISIBLE);
        pauseButton.startAnimation(pauseButtonAnimation);
        stopButton.startAnimation(stopButtonAnimation);

        //mQuery.deleteAll();
        startTime();
        logicRunning.startLocationUpdates();
        //refreshMap(mMap);
        // Get start time
        startCurrentTime = Calendar.getInstance().getTime();
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

        pauseTime();
        logicRunning.stopLocationUpdates();
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

        startTime();
        logicRunning.startLocationUpdates();
    }

    public void onClickStopButton(View view) throws JSONException {
        logicRunning.stopLocationUpdates();
        stopTime();
        stopCurrentTime = Calendar.getInstance().getTime();
        sendDataToResult();
    }
}