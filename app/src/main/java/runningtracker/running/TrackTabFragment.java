package runningtracker.running;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import runningtracker.R;
import runningtracker.common.InitializationFirebase;
import runningtracker.common.MyLocation;
import runningtracker.data.model.running.IdHistory;
import runningtracker.data.model.running.LocationObject;

import static runningtracker.running.ResultActivity.tabFragmentLayouts;

public class TrackTabFragment extends Fragment implements OnMapReadyCallback {
    public TrackTabFragment() {
    }

    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private static PresenterRunning presenterRunning;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the gridview_dashboard_item for this fragment
        View inflatedLayout = inflater.inflate(R.layout.activity_result_tab_map, container, false);
        tabFragmentLayouts.add((ViewGroup) inflatedLayout);

        presenterRunning = new PresenterRunning();

        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            mapFragment.getMapAsync(this);
        }
        // R.id.map is a FrameLayout, not a Fragment
        getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();

        return inflatedLayout;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        mMap.setMyLocationEnabled(true);
        MyLocation myLocation = new MyLocation();
        Location L = myLocation.getMyLocation(getActivity());
        if (L != null) {
            LatLng latLng;
            latLng = new LatLng(L.getLatitude(), L.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
            mMap.moveCamera(cameraUpdate);
        }
        drawDistanceHistory();
    }
    /**
     * Draw distance history of user
    * */
    public void drawDistanceHistory(){

        IdHistory idHistory = new IdHistory();
        InitializationFirebase initializationFirebase = new InitializationFirebase();
        FirebaseFirestore firestore  = initializationFirebase.createFirebase();

        presenterRunning.getDataLocation(idHistory.id, firestore, new LocationHistoryCallback() {
            @Override
            public void dataLocation(List<LocationObject> locationObject) {

                List<Marker> originMarkers = new ArrayList<>();
                List<Marker> destinationMarkers = new ArrayList<>();
                Polygon polygon;

                originMarkers.add(mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                        .title("Start Tracking")
                        .position(new LatLng(locationObject.get(0).getLatitudeValue(), locationObject.get(0).getLongitudeValue()))));

                for(int i = 0; i < locationObject.size() - 1; i++) {

                    polygon = mMap.addPolygon(new PolygonOptions()
                            .add(new LatLng(locationObject.get(i).getLatitudeValue(), locationObject.get(i).getLongitudeValue()),
                                    new LatLng(locationObject.get(i + 1).getLatitudeValue(), locationObject.get(i + 1).getLongitudeValue()))
                            .strokeColor(Color.BLUE)
                            .fillColor(Color.BLACK));
                }

                destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                        .title("End Tracking")
                        .position(new LatLng(locationObject.get(locationObject.size() - 1).getLatitudeValue(), locationObject.get(locationObject.size() - 1).getLongitudeValue()))));
            }
        });

    }
}
