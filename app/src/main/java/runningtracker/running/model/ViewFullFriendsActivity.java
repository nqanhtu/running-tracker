package runningtracker.running.model;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import runningtracker.R;

public class ViewFullFriendsActivity extends AppCompatActivity implements OnMapReadyCallback {

    /**
     * auto search
     * */
    private SearchView searchView;
    private SearchView.SearchAutoComplete   mSearchAutoComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_full_friend);

        MapFragment mapFragmentViewShare = (MapFragment) getFragmentManager().findFragmentById(R.id.mapViewFull);
        mapFragmentViewShare.getMapAsync(this);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate navigation menu from the resources by using the menu inflater.
        getMenuInflater().inflate(R.menu.search_auto, menu);

        getMenuInflater().inflate(R.menu.search_auto, menu);
        MenuItem item = menu.findItem(R.id.action_search);

        searchView = (SearchView) MenuItemCompat.getActionView(item);
        mSearchAutoComplete =  searchView
                .findViewById(android.support.v7.appcompat.R.id.search_src_text);
        mSearchAutoComplete.setDropDownBackgroundResource(R.drawable.background_login);
        mSearchAutoComplete.setDropDownAnchor(R.id.action_search);
        mSearchAutoComplete.setThreshold(0);
        final ArrayList<String> listSuggest = new ArrayList<>();
        listSuggest.add("asdf");
        listSuggest.add("ndfjihasd");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listSuggest);
        mSearchAutoComplete.setAdapter(adapter);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {return true;}


    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                finish();
            }
        });
    }
}
