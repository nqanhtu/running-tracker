package runningtracker.running;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import runningtracker.running.model.LocationHistoryCallback;

import static runningtracker.running.ResultActivity.idDateHistory;
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

        InitializationFirebase initializationFirebase = new InitializationFirebase();
        FirebaseFirestore firestore  = initializationFirebase.createFirebase();
        /**
         * If value default
        * */
        if(idDateHistory == null) {
            presenterRunning.getDataLocation(firestore, new LocationHistoryCallback() {
                @Override
                public void dataLocation(List<LocationObject> locationObject) {
                    if (locationObject.size() > 1) {
                        List<Marker> originMarkers = new ArrayList<>();
                        List<Marker> destinationMarkers = new ArrayList<>();

                        originMarkers.add(mMap.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                                .title("Start Tracking")
                                .position(new LatLng(locationObject.get(0).getLatitudeValue(), locationObject.get(0).getLongitudeValue()))));
                        int sizeObject = locationObject.size();
                        List<LatLng> polygon = new ArrayList<>();
                        for (int i = 0; i < sizeObject; i++) {
                            polygon.add(new LatLng(locationObject.get(i).getLatitudeValue(), locationObject.get(i).getLongitudeValue()));
                        }
                        mMap.addPolygon(new PolygonOptions()
                                .addAll(polygon)
                                .strokeColor(Color.BLUE)
                                .fillColor(Color.WHITE));
                        destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                                .title("End Tracking")
                                .position(new LatLng(locationObject.get(locationObject.size() - 1).getLatitudeValue(), locationObject.get(locationObject.size() - 1).getLongitudeValue()))));
                    }
                }
            });
            /**
             * If user chose date history tracking
            * */
        }else{
            presenterRunning.getListLocationHistory(idDateHistory, firestore, new LocationHistoryCallback() {
                @Override
                public void dataLocation(List<LocationObject> locationObject) {

                    List<Marker> originMarkers = new ArrayList<>();
                    List<Marker> destinationMarkers = new ArrayList<>();

                    originMarkers.add(mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                            .title("Start Tracking")
                            .position(new LatLng(locationObject.get(0).getLatitudeValue(), locationObject.get(0).getLongitudeValue()))));

                    int sizeObject = locationObject.size();
                    List<LatLng> polygon = new ArrayList<>();
                    for (int i = 0; i < sizeObject; i++) {
                        polygon.add(new LatLng(locationObject.get(i).getLatitudeValue(), locationObject.get(i).getLongitudeValue()));
                    }
                    mMap.addPolygon(new PolygonOptions()
                            .addAll(polygon)
                            .strokeColor(Color.BLUE)
                            .fillColor(Color.WHITE));

                    destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                            .title("End Tracking")
                            .position(new LatLng(locationObject.get(locationObject.size() - 1).getLatitudeValue(), locationObject.get(locationObject.size() - 1).getLongitudeValue()))));
                }
            });
        }

    }
}
