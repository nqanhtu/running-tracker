package runningtracker.notification;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import runningtracker.R;

public class MapDangerActivity extends AppCompatActivity implements OnMapReadyCallback {

    private double latitudeValue, longitudeValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_danger);
        init();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions().position(new LatLng(latitudeValue, longitudeValue))
                .title("Vị trí gặp sự cố"));

        CameraUpdate cameraUpdateIcon = CameraUpdateFactory.newLatLngZoom((new LatLng(latitudeValue,longitudeValue)), 16);
        googleMap.animateCamera(cameraUpdateIcon);
    }

    /**
     * Init value of map danger activity
    * */
    private void init(){
        /**set value toolbar*/
        Toolbar toolbar =  findViewById(R.id.actionbarDanger);
        toolbar.setTitle("Vị trí gặp sự cố");
        /**event back notification activity*/
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        /**
         * Create map danger location
         * */
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapDanger);
        mapFragment.getMapAsync(this);
        /**get value form NotificationsFragment*/
        try {
            latitudeValue = getIntent().getDoubleExtra("latitude", 0);
            longitudeValue = getIntent().getDoubleExtra("longitude", 0);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}