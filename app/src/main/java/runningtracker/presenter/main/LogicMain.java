package runningtracker.presenter.main;


import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
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

import runningtracker.view.main.ViewMain;

public class LogicMain implements Main {
    private SettingsClient mSettingsClient;
    private LocationSettingsRequest mLocationSettingsRequest;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 3000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private LocationRequest mLocationRequest;
    ViewMain main;
    public LogicMain(ViewMain main){
        this.main = main;
    }

    @Override
    public boolean isConnected(Context context) {
        ConnectivityManager cm =(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo =cm.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isConnectedOrConnecting()){
            return true;
        }
        else{
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
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult()
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult((Activity) main.getMainActivity(), REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    /*Log.i(TAG, "PendingIntent unable to execute request.");*/
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                               /* Log.e(TAG, errorMessage);*/
                                Toast.makeText(main.getMainActivity(), errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });
        if(tmp[0] == 1) {
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public void initialization() {
        mSettingsClient = LocationServices.getSettingsClient(main.getMainActivity());
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

    @Override
    public int checkStartRunning() {
        boolean checkInternet, checkGPS;
        checkInternet = isConnected(main.getMainActivity());
        checkGPS = checkTurnOnLocation();
        do{
            if(checkInternet == true && checkGPS == true){
                return 1;
            }
            else if(checkGPS ==true){
                return 2;
            }
            else{
                checkGPS = checkTurnOnLocation();
            }

        }while(true);
    }

    @Override
    public void onNavigationActivity() {
        int startActivity;
        startActivity = checkStartRunning();
        if(startActivity == 1){
            main.navigationRunning();
        }
        if(startActivity == 2){
            main.navigationRunningOffline();
        }
    }
}
