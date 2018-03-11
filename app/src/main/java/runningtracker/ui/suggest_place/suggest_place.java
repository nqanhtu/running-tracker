package runningtracker.ui.suggest_place;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.OnClick;
import runningtracker.R;
import runningtracker.data.model.suggest_place.SuggestLocation;
import runningtracker.model.suggets_place.ItemSuggest;
import runningtracker.model.suggets_place.Route;
import runningtracker.presenter.suggest_place.DirectionFinder;
import runningtracker.presenter.suggest_place.DirectionFinderListener;
import runningtracker.presenter.suggest_place.SuggestCallback;
import runningtracker.repository.suggestlocation.SuggestRepository;


public class suggest_place extends AppCompatActivity implements OnMapReadyCallback, DirectionFinderListener {
    private GoogleMap mMap;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    private Toolbar toolbarTitle;
    //
    String[] listItems;
    boolean[] checkedItems;
    ArrayList<Integer> mUserItems = new ArrayList<>();
    DirectionFinder directionFinder;
    //
    private ArrayList<ItemSuggest> ListItemSuggests = new ArrayList<>();
    private List<Location> listLocation;
    Geocoder geocoder;
    List<Address> addresses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest_place);

        directionFinder = new DirectionFinder();
        listLocation = new ArrayList<>();
        toolbarTitle =  (Toolbar) findViewById(R.id.tlbLocationName);

        ButterKnife.bind(this);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        listItems = getResources().getStringArray(R.array.suggets_item);
        checkedItems = new boolean[listItems.length];
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
        mMap.setMyLocationEnabled(true);
        LatLng latLng = new LatLng(getMyLocation().getLatitude(), getMyLocation().getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        mMap.animateCamera(cameraUpdate);
    }

    @OnClick(R.id.btnSuggets)
    public void startHomeActivity() {
        sendRequest();
    }


    private void sendRequest() {
        mMap.clear();
        String origin = getMyLocation().getLatitude()+","+getMyLocation().getLongitude();
        String destination = listLocation.get(0).getLatitude()+","+listLocation.get(0).getLongitude();
        //SuggestRepository suggestRepository = new SuggestRepository();
        if (origin.isEmpty()) {
            Toast.makeText(this, "Please enter origin address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            new DirectionFinder(this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
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
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    /**
     * @param: list route
     * @return: view direction on map
    * */
    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            geocoder = new Geocoder(this, Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(listLocation.get(0).getLatitude(),
                        listLocation.get(0).getLongitude(),1);
                String address = addresses.get(0).getFeatureName()+addresses.get(0).getPostalCode()+","+
                        addresses.get(0).getSubAdminArea()+","+ addresses.get(0).getAdminArea();

                setSupportActionBar(toolbarTitle);
                getSupportActionBar().setTitle(address);
            } catch (IOException e) {
                e.printStackTrace();
            }

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }

    /**
    * Create view list choice location
    * @return: list location
    */
    public void getListItem(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(suggest_place.this);
        mBuilder.setTitle("Choice suggets location");
        mBuilder.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                if (isChecked) {
                    if (!mUserItems.contains(position)) {
                        mUserItems.add(position);
                    }
                } else if (mUserItems.contains(position)) {
                    mUserItems.remove(position);
                }
                if(isChecked){
                    mUserItems.add(position);
                }else{
                    mUserItems.remove((Integer.valueOf(position)));
                }
            }
        });

        mBuilder.setCancelable(false);

        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                String item = "";
                for (int i = 0; i < listItems.length; i++) {
                    ItemSuggest itemSuggest = new ItemSuggest();
                    if(checkedItems[i] == true) {
                        itemSuggest.setPosition(i);
                        ListItemSuggests.add(itemSuggest);
                    }
                }

                directionFinder.setMarkerLocation(ListItemSuggests, mMap, new SuggestCallback() {
                    @Override
                    public void getListLocation(List<Location> locationList) {
                        listLocation = locationList;
                    }
                });
            }
        });

        mBuilder.setNeutralButton("CLEAR ALL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                for (int i = 0; i < checkedItems.length; i++) {
                    checkedItems[i] = false;
                    mUserItems.clear();
                }
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    /**
     *@return: Location of user
    * */
    public Location getMyLocation() {
        LocationManager rLocationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        List<String> providers = rLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION) !=
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

}

