package runningtracker.presenter.main;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import runningtracker.model.DataCallback;
import runningtracker.model.ResAPICommon;
import runningtracker.model.modelrunning.DatabaseWeather;
import runningtracker.model.modelrunning.LocationObject;
import runningtracker.model.modelrunning.WeatherObject;
import runningtracker.running.ViewMain;


import static android.content.ContentValues.TAG;
import static android.content.Context.LOCATION_SERVICE;

public class LogicMain implements Main {
    private SettingsClient mSettingsClient;
    private LocationSettingsRequest mLocationSettingsRequest;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 3000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private LocationRequest mLocationRequest;
    ViewMain main;
    ResAPICommon resAPICommon;
    WeatherObject weatherObject;
    DatabaseWeather databaseWeather;
    LocationObject locationObject;

    public LogicMain(ViewMain main) {
        this.main = main;
     /*   resAPICommon = new ResAPICommon();
        weatherObject = new WeatherObject();*/
    }
     public LogicMain(){
         resAPICommon = new ResAPICommon();
         weatherObject = new WeatherObject();
     }

    @Override
    public boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean checkTurnOnLocation() {
        final int[] tmp = {1};
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener((Activity) main.getMainActivity(), new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        tmp[0] = 1;
                    }
                })
                .addOnFailureListener((Activity) main.getMainActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                try {
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult((Activity) main.getMainActivity(), REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                        }
                    }
                });
        if (tmp[0] == 1) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void initialization() {
        mSettingsClient = LocationServices.getSettingsClient(main.getMainActivity());
        databaseWeather = new DatabaseWeather(main.getMainActivity());
        databaseWeather.deleteAll();
    }

    @Override
    public void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    @Override
    public void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    //check internet and gps
    @Override
    public int checkStartRunning() {
        boolean checkInternet, checkGPS;
        checkInternet = isConnected(main.getMainActivity());
        checkGPS = checkTurnOnLocation();
        do {
            if (checkInternet == true && checkGPS == true) {
                return 1;
            } else if (checkGPS == true) {
                return 2;
            } else {
                checkGPS = checkTurnOnLocation();
            }

        } while (true);
    }

    @Override
    public void onNavigationActivity() {
        int startActivity;
        startActivity = checkStartRunning();
        if (startActivity == 1) {
            main.navigationRunning();
        }
        if (startActivity == 2) {
            main.navigationRunningOffline();
        }
    }

    @Override
    public void getWeatherAPI(double latitude, double longitude) {
        String url = "http://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=4649c1a62014cc396d0e6e55f7245313";
        resAPICommon.RestGetClient(url, main.getMainActivity(),
                new DataCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        try {
                            weatherObject.setName(result.getString("name"));
                            String day = result.getString("dt");

                            long l = Long.valueOf(day);
                            Date date = new Date(l * 1000L);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE yyyy-MM-dd");
                            String dayFormat = simpleDateFormat.format(date);
                            weatherObject.setDay(dayFormat);

                            JSONArray jsonArrayWeather = result.getJSONArray("weather");
                            JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                            weatherObject.setMain(jsonObjectWeather.getString("main"));
                            weatherObject.setDescription(jsonObjectWeather.getString("description"));
                            weatherObject.setIcon(jsonObjectWeather.getString("icon"));

                            JSONObject jsonObjectMain = result.getJSONObject("main");
                            String temp = jsonObjectMain.getString("temp");
                            Double a = Double.valueOf(temp);
                            a = a - 273.15;
                            String iTemp = String.valueOf(a.intValue());
                            weatherObject.setTemp(iTemp);

                            databaseWeather.addNewWeather(weatherObject);
                            ArrayList<WeatherObject> arrayList = new ArrayList<>();
                            arrayList = databaseWeather.getAllWeather();
                            arrayList.size();
                            Toast.makeText(main.getMainActivity(), "" + weatherObject.getName(), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }

    //get my location if it have GPS
    public Location getMyLocation() {
        LocationManager rLocationManager = (LocationManager) main.getMainActivity().getSystemService(LOCATION_SERVICE);
        List<String> providers = rLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission( main.getMainActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( main.getMainActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            Location myLocation = rLocationManager.getLastKnownLocation(provider);
            if (myLocation == null) {
                continue;
            }
            if (bestLocation == null || myLocation.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = myLocation;
            }
        }
        //test location
/*        locationObject = new LocationObject();
        locationObject.setLatitudeValue(bestLocation.getLatitude());
        locationObject.setLongitudeValue(bestLocation.getLongitude());*/
        return bestLocation;
    }
    @Override
    public void supPortWeather() {
        final Handler handler = new Handler();
        final Timer timer = new Timer();
        final Location[] finalBestLocation = new Location[1];
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @SuppressWarnings("unchecked")
                    public void run() {
                        try {
                            finalBestLocation[0] = getMyLocation();
                            getWeatherAPI(finalBestLocation[0].getLatitude(), finalBestLocation[0].getLongitude());
                            Log.e(TAG, "N: "+weatherObject.getName());
                            if(weatherObject != null){
                                timer.cancel();
                            }
                        }
                        catch (Exception e) {
                            Log.e(TAG, ""+e);
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 2000);
    }
}
