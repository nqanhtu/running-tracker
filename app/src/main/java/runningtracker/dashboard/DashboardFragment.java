package runningtracker.dashboard;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
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
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import runningtracker.Adapter.OptionAdapter;
import runningtracker.BuildConfig;
import runningtracker.R;
import runningtracker.data.model.Option;
import runningtracker.data.model.weather.OpenWeather;
import runningtracker.data.model.weather.Weather;
import runningtracker.data.service.WeatherService;
import runningtracker.friends.FriendsActivity;
import runningtracker.history.HistoryActivity;
import runningtracker.network.ServiceGenerator;
import runningtracker.network.WeatherGenerator;
import runningtracker.settings.SettingDashBoardActivity;
import runningtracker.suggestplace.SuggestPlaceActivity;

import static com.google.common.base.Preconditions.checkNotNull;

public class DashboardFragment extends Fragment implements DashBoardContract.View {

    private DashBoardContract.Presenter mPresenter;

    ArrayList<Option> options = new ArrayList<>();
    private FusedLocationProviderClient mFusedLocationClient;
    private static final String TAG = "DashBoardLog";
    protected Location mLastLocation;

    OnFragmentInteractionListener mListener;

    @BindView(R.id.weather_text_view)
    AppCompatTextView weatherText;
    @BindView(R.id.temp_text_view)
    AppCompatTextView tempcText;
    @BindView(R.id.location_text_view)
    AppCompatTextView locationText;
    @BindView(R.id.main_activity_container)
    View container;
    @BindView(R.id.weather_icon_image_view)
    ImageView weatherIcon;
    // @BindView(R.id.gridview)
    GridView gridView;

    @BindView(R.id.humidity_text_view)
    TextView humidityTextView;
    @BindView(R.id.wind_text_view)
    TextView windTextView;

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        options.add(new Option("Cài đặt", R.drawable.ic_settings));
        options.add(new Option("Lịch sử chạy", R.drawable.ic_history));
        options.add(new Option("Bạn bè", R.drawable.ic_friends));
        options.add(new Option("Bắt đầu chạy", R.drawable.ic_running));
        options.add(new Option("Địa điểm chạy", R.drawable.ic_map));
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (!checkPermissions()) {
            requestPermissions();
        } else {
            getLastLocation();
        }
        if (savedInstanceState == null) {
            Log.d(TAG, "instance null");
        } else {
            Log.d(TAG, "instance not null");

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        ButterKnife.bind(this, view);

        if (savedInstanceState == null) {
            gridView = view.findViewById(R.id.gridview);
            initGridView();
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    private void initGridView() {

        final OptionAdapter optionAdapter = new OptionAdapter(getActivity(), options);
        gridView.setAdapter(optionAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent intent0 = new Intent(getActivity(), SettingDashBoardActivity.class);
                        startActivity(intent0);
                        break;
                    case 1:
                        Intent intent1 = new Intent(getActivity(), HistoryActivity.class);
                        startActivity(intent1);
                        break;
                    case 2:
                        Intent intent3 = new Intent(getActivity(), FriendsActivity.class);
                        startActivity(intent3);
                        break;
                    case 3:
                        mListener.onStartRunning();
                        break;
                    case 4:
                        Intent intent = new Intent(getActivity(), SuggestPlaceActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });

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

        String latitude = String.valueOf(mLastLocation.getLatitude());
        String longitude = String.valueOf(mLastLocation.getLongitude());
        WeatherService weatherService = ServiceGenerator.createService(WeatherService.class);

        Call<OpenWeather> call = weatherService.getWeather(latitude, longitude);


        call.enqueue(new Callback<OpenWeather>() {
            @Override
            public void onResponse(Call<OpenWeather> call, Response<OpenWeather> response) {
                Log.d("resabc", response.body().toString());

                Weather weather = response.body().getWeather().get(0);

                String weatherDescription = weather.getDescription();
                weatherDescription = weatherDescription.substring(0, 1).toUpperCase() + weatherDescription.substring(1);


                weatherText.setText(weatherDescription);
                tempcText.setText(String.valueOf(response.body().getMain().getTemp()));
                humidityTextView.setText(String.valueOf(response.body().getMain().getHumidity()));
                windTextView.setText(String.valueOf(response.body().getWind().getSpeed()));

                String weatherIcon = weather.getIcon();
                int weatherIconPath = 0;
                switch (weatherIcon) {
                    case "01d":
                        weatherIconPath = R.drawable.d01;
                        break;
                    case "01n":
                        weatherIconPath = R.drawable.n01;
                        break;
                    case "02d":
                        weatherIconPath = R.drawable.d02;
                        break;
                    case "02n":
                        weatherIconPath = R.drawable.n02;
                        break;
                    case "03d":
                        weatherIconPath = R.drawable.d03;
                        break;
                    case "03n":
                        weatherIconPath = R.drawable.n03;
                        break;
                    case "04d":
                        weatherIconPath = R.drawable.d04;
                        break;
                    case "04n":
                        weatherIconPath = R.drawable.n04;
                        break;
                    case "09d":
                        weatherIconPath = R.drawable.d09;
                        break;
                    case "09n":
                        weatherIconPath = R.drawable.n09;
                        break;
                    case "10d":
                        weatherIconPath = R.drawable.d10;
                        break;
                    case "10n":
                        weatherIconPath = R.drawable.n10;
                        break;
                    case "11d":
                        weatherIconPath = R.drawable.d11;
                        break;
                    case "11n":
                        weatherIconPath = R.drawable.n11;
                        break;
                    case "13d":
                        weatherIconPath = R.drawable.d13;
                        break;
                    case "13n":
                        weatherIconPath = R.drawable.n13;
                        break;
                    case "50d":
                        weatherIconPath = R.drawable.d50;
                        break;
                    case "50n":
                        weatherIconPath = R.drawable.n50;
                        break;


                }

                loadWeatherIcon(weatherIconPath);

            }

            @Override
            public void onFailure(Call<OpenWeather> call, Throwable t) {

            }
        });

        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String subCity = addresses.get(0).getSubAdminArea();
            locationText.setText(city + ", " + subCity + ", " + state);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void loadWeatherIcon(int iconId) {
        Picasso
                .get()
                .load(iconId)
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
        int permissionState = ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()),
                Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()),
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(Objects.requireNonNull(getActivity()),
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


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString());
        }

    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onStartRunning();
    }
}
