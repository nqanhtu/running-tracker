package runningtracker.suggestplace;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import runningtracker.R;
import runningtracker.data.model.suggestplace.Place;
import runningtracker.model.suggets_place.ItemSuggest;
import runningtracker.model.suggets_place.Route;


public class SuggestPlaceActivity extends AppCompatActivity implements OnMapReadyCallback, DirectionFinderListener {
    private static GoogleMap mMap;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;

    @BindView(R.id.toolbar)
    Toolbar toolbarTitle;

    private String[] listItems;
    int checkedItems = -1;
    private ArrayList<Integer> mUserItems = new ArrayList<>();
    private DirectionFinderPresenter suggestPre;

    private ArrayList<ItemSuggest> ListItemSuggests;
    private static List<Location> listLocation;
    private Location myLocation;
    @BindView(R.id.places_text_view)
    TextView placeTextView;
    private static Thread threadRealWalk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest_place);
        suggestPre = new DirectionFinderPresenter();
        ButterKnife.bind(this);
        setUpToolbar();
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        listItems = getResources().getStringArray(R.array.suggets_item);
        myLocation = getMyLocation();
        getListItem();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(false);
        LatLng latLng = new LatLng(getMyLocation().getLatitude(), getMyLocation().getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        mMap.animateCamera(cameraUpdate);
    }

    @OnClick(R.id.imgSuggestRoad)
    public void startHomeActivity() {
        sendRequest();
    }

    @OnClick(R.id.imgSuggest)
    public void startSuggestItem() {
        getListItem();
    }

    private void sendRequest() {

        //mMap.clear();
        String origin = "";
        String destination = "";
        try {
            Location minLocation = suggestPre.locationDistanceMin(myLocation, listLocation);
            origin = myLocation.getLatitude() + "," + myLocation.getLongitude();
            destination = minLocation.getLatitude() + "," + minLocation.getLongitude();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (origin.isEmpty()) {
            Toast.makeText(this, "Please enter origin address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            new DirectionFinderPresenter(this, origin, destination).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }
    }

    /**
     * @param: list route
     * @return: view direction on map
     */
    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 15));
            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                    .title(route.startAddress)
                    .position(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()))));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                    .title(route.endAddress)
                    .position(new LatLng(listLocation.get(0).getLatitude(), listLocation.get(0).getLongitude()))));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            polylineOptions.add(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));
            polylineOptions.add(new LatLng(listLocation.get(0).getLatitude(), listLocation.get(0).getLongitude()));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }

    /**
     * Create view list choice location
     *
     * @return: list location
     */
    public void getListItem() {
        listLocation = new ArrayList<>();
        ListItemSuggests = new ArrayList<>();

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SuggestPlaceActivity.this);
        mBuilder.setTitle("Chọn gợi ý");
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                checkedItems = position;

            }
        });

        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {

                mMap.clear();
                ItemSuggest itemSuggest = new ItemSuggest();
                if (checkedItems >= 0) {
                    Place place = new Place(listItems[checkedItems]);
                    placeTextView.setText(place.getName());
                    itemSuggest.setPosition(checkedItems);
                    ListItemSuggests.add(itemSuggest);
                }
                /**
                 * set marker location
                 * */
                suggestPre.setMarkerLocation(ListItemSuggests, mMap, new SuggestCallback() {
                    @Override
                    public void getListLocation(List<Location> locationList) {
                        listLocation = locationList;
                    }
                });
            }
        });

        mBuilder.setNeutralButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                checkedItems = -1;
                mUserItems.clear();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    /**
     * @return: Location of user
     */
    public Location getMyLocation() {
        LocationManager rLocationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        List<String> providers = rLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                            this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {
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
        return bestLocation;
    }

    private void setUpToolbar() {
        toolbarTitle.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}

