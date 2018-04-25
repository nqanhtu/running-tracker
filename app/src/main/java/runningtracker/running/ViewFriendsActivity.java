package runningtracker.running;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import runningtracker.R;
import runningtracker.common.MyLocation;
import runningtracker.data.model.running.LocationObject;

public class ViewFriendsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private PresenterRunning presenterRunning;
    private MyLocation myLocation;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_full_friend);

        presenterRunning = new PresenterRunning();
        myLocation = new MyLocation();
        /**
         * Create map view share location full map of friends
         * */
        MapFragment mapFragmentViewShare = (MapFragment) getFragmentManager().findFragmentById(R.id.mapViewShareLocation);
        mapFragmentViewShare.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        /**
         * Check PERMISSION
        * */
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        /**
         * Create list location friends demo
        * */
        ArrayList<LocationObject> list = new ArrayList<>();
        list.add(new LocationObject(10.736096, 106.674790));
        list.add(new LocationObject(10.738214, 106.677853));
        list.add(new LocationObject(10.734489, 106.678814));
        list.add(new LocationObject(10.738797, 106.681894));

        /**
         * Move camera first friend
        * */
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(list.get(0).getLatitudeValue(), list.get(0).getLongitudeValue()), 15);
        mMap.animateCamera(cameraUpdate);

        /**
         * Set marker friend on map
        * */
        for(int i = 0; i< list.size(); i++){
            mMap.addMarker(new MarkerOptions().position(new LatLng(list.get(i).getLatitudeValue(), list.get(i).getLongitudeValue()))
                    .title("Friend " + (i + 1)));
        }

        /**
         * get event long click on mapFragment
        * */
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Intent nextActivity = new Intent(ViewFriendsActivity.this, RunningActivity.class);
                startActivity(nextActivity);
            }
        });
    }
}
