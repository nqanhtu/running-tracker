package runningtracker.ui.dashboard;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.BuildConfig;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import runningtracker.Adapter.OptionAdapter;
import runningtracker.R;
import runningtracker.data.model.Option;
import runningtracker.data.model.weather.Weather;
import runningtracker.data.service.WeatherService;
import runningtracker.network.WeatherGenerator;
import runningtracker.presenter.main.LogicMain;
import runningtracker.ui.suggest_place.suggest_place;
import runningtracker.view.running.MainActivity;
import runningtracker.view.running.MainActivityOffline;

import static com.google.common.base.Preconditions.checkNotNull;

public class DashboardFragment extends Fragment implements DashBoardContract.View {

    private DashBoardContract.Presenter mPresenter;

    ArrayList<Option> options = new ArrayList<>();
    private FusedLocationProviderClient mFusedLocationClient;
    private static final String TAG = "ABC";
    protected Location mLastLocation;



    private String mLatitudeLabel;
    private String mLongitudeLabel;
    @BindView(R.id.weather) TextView weatherText;
    @BindView(R.id.temp_c) TextView tempcText;
    @BindView(R.id.locationTextView) TextView locationText;

    @BindView(R.id.main_activity_container) View container;
    @BindView(R.id.weatherIcon)
    ImageView weatherIcon;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    LogicMain main;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        ButterKnife.bind(this, view);

        initGridView(view);
        mLatitudeLabel = getResources().getString(R.string.latitude_label);
        mLongitudeLabel = getResources().getString(R.string.longitude_label);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!checkPermissions()) {
            requestPermissions();
        } else {
            getLastLocation();
        }
    }

    private void initGridView(View view) {
        GridView gridView = (GridView) view.findViewById(R.id.gridview);
        options.add(new Option("Calories", R.drawable.ic_setup_calories));
        options.add(new Option("Lịch sử chạy", R.drawable.ic_history));
        options.add(new Option("Khu vực nguy hiểm", R.drawable.ic_stopwatch));
        options.add(new Option("Bạn bè", R.drawable.ic_friends));
        options.add(new Option("Bắt đầu chạy", R.drawable.ic_running));
        options.add(new Option("Địa điểm chạy", R.drawable.ic_map));

        final OptionAdapter optionAdapter = new OptionAdapter(getActivity(), options);
        gridView.setAdapter(optionAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        main.onNavigationActivity();
                        break;
                    case 5:
                        Intent intent = new Intent(getActivity(), suggest_place.class);
                        startActivity(intent);
                        break;
                }
            }
        });

    }

    @Override
    public Context getMainActivity() {

            return getActivity();
    }

    @Override
    public void navigationRunning() {
        Intent nextActivity = new Intent(getActivity(), MainActivity.class);
        startActivity(nextActivity);
    }

    @Override
    public void navigationRunningOffline() {
        Intent nextActivity = new Intent(getActivity(), MainActivityOffline.class);
        startActivity(nextActivity);
    }

    @Override
    public void setPresenter(@NonNull DashBoardContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }


    /**
     * Provides a simple way of getting a device's location and is well suited for
     * applications that do not require a fine-grained location and that do not need location
     * updates. Gets the best and most recent location currently available, which may be null
     * in rare cases when a location is not available.
     * <p>
     * Note: this method should be called after location permission has been granted.
     */
    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastLocation = task.getResult();
                            getWeather(mLastLocation);
                        } else {
                            Log.w(TAG, "getLastLocation:exception", task.getException());
                            showSnackbar(getString(R.string.no_location_detected));
                        }
                    }
                });
    }

    private void getWeather(Location mLastLocation) {
        WeatherService weatherService = WeatherGenerator.createService(WeatherService.class);
        String latlong = String.valueOf(mLastLocation.getLatitude())+"," +String.valueOf(mLastLocation.getLongitude());
        Call<Weather> call = weatherService.getWeather(latlong);

        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        String result = null;

        try {
            List<Address> addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);

            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String zip = addresses.get(0).getPostalCode();
            String country = addresses.get(0).getCountryName();
            locationText.setText(state +  " " + city);

        } catch (IOException e) {
            e.printStackTrace();
        }
        call.enqueue(new Callback<Weather>() {
            @Override
            public void onResponse(Call<Weather> call, Response<Weather> response) {
                if (response.isSuccessful()) {
                    Log.d("res",response.body().toString());

                    weatherText.setText(response.body().getCurrentObservation().getWeather());
                    tempcText.setText(String.valueOf(response.body().getCurrentObservation().getTempC()));



                    loadWeatherIcon(response.body().getCurrentObservation().getIconUrl());
                }
            }

            @Override
            public void onFailure(Call<Weather> call, Throwable t) {

            }
        });


    }

    private void loadWeatherIcon(String iconUrl) {
        Picasso
                .with(getActivity())
                .load(iconUrl)
                .into(weatherIcon);
    }

    /**
     * Shows a {@link Snackbar} using {@code text}.
     *
     * @param text The Snackbar text.
     */
    private void showSnackbar(final String text) {
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(getView().findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");

            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            startLocationPermissionRequest();
                        }
                    });

        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            startLocationPermissionRequest();
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getLastLocation();
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }
}
