package runningtracker.running;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;

import runningtracker.R;
import runningtracker.common.InitializationFirebase;
import runningtracker.running.model.ListSuggestCallback;

public class ViewFullFriendsActivity extends AppCompatActivity implements OnMapReadyCallback {

    /**
     * auto search
     * */
    private SearchView searchView;
    private SearchView.SearchAutoComplete  mSearchAutoComplete;
    private static GoogleMap map;
    private PresenterRunning preRunning;
    private FirebaseFirestore firestore;
    private  Toolbar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_full_friend);
        MapFragment mapFragmentViewShare = (MapFragment) getFragmentManager().findFragmentById(R.id.mapViewFull);
        mapFragmentViewShare.getMapAsync(this);

        preRunning = new PresenterRunning();
        InitializationFirebase initializationFirebase = new InitializationFirebase();
        firestore = initializationFirebase.createFirebase();

        actionBar = findViewById(R.id.tbAutoSearch);
        actionBar.setTitle("Tìm kiếm bạn bè");
        actionBar.setNavigationIcon(R.drawable.ic_android_back_white_24dp);
        setSupportActionBar(actionBar);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        actionBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        // Inflate navigation menu from the resources by using the menu inflater.
        getMenuInflater().inflate(R.menu.search_auto, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(item);
        mSearchAutoComplete =  searchView
                .findViewById(android.support.v7.appcompat.R.id.search_src_text);
        mSearchAutoComplete.setDropDownBackgroundResource(R.color.colorBackgroundWhite);
        mSearchAutoComplete.setDropDownAnchor(R.id.action_search);
        mSearchAutoComplete.setThreshold(0);
        final ArrayList<String> listSuggest = new ArrayList<>();
        preRunning.getListLocationFriends(firestore, new ListSuggestCallback() {
            @Override
            public void getListNameFriends(ArrayList<Marker> listMarker) {
                if (listMarker != null) {
                    if (listMarker.size() > 0) {
                        for (int i = 0; i < listMarker.size(); i++) {
                            listSuggest.add(listMarker.get(i).getTitle());
                        }
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_list_item_1, listSuggest);
                mSearchAutoComplete.setAdapter(adapter);
            }
        });
        //Listen event from mSearchAutoComplete
        mSearchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long id) {
                String nameFriend = (String)adapterView.getItemAtPosition(itemIndex);
                preRunning.searchMarker(nameFriend);
            }
        });
        return true;
    }

    public Context getActivity() {
        return ViewFullFriendsActivity.this;
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
        map = googleMap;
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                finish();
            }
        });
    }
    public GoogleMap getActivityViewFull(){
        return map;
    }
}
