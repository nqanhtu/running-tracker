package runningtracker.ui.dashboard;

import android.app.ActivityOptions;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import runningtracker.R;
import runningtracker.helper.BottomNavigationViewHelper;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Anh Tu on 2/3/2018.
 */

public class DashBoardActivity extends AppCompatActivity implements DashBoardContract.View {


    @BindView(R.id.location_address_view)
    TextView mLocationAddress;
    @BindView(R.id.viewDashboard) View view;
    private DashBoardContract.Presenter mPresenter;
    Geocoder geocoder;
    List<Address> addresses;
    Double latitude = 10.744806;
    Double longitude = 106.684208;


    @Override
    public void setPresenter(@NonNull DashBoardContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);
        setupBottomNavigationView();
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude, longitude,1);
            String address = addresses.get(0).getSubAdminArea();
            String city = addresses.get(0).getAdminArea();
            mLocationAddress.setText(address+", "+city);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setupBottomNavigationView(){
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        ActivityOptions options = ActivityOptions
                .makeSceneTransitionAnimation(this, view, "transitionNav");
        BottomNavigationViewHelper.enableNavigation(DashBoardActivity.this, bottomNavigationView,options);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
        startActivity(intent);
    }

}
