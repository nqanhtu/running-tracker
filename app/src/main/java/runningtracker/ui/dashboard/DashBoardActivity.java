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
import android.widget.AdapterView;
import android.widget.GridView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import runningtracker.Adapter.OptionAdapter;
import runningtracker.R;
import runningtracker.data.model.Option;
import runningtracker.helper.BottomNavigationViewHelper;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Anh Tu on 2/3/2018.
 */

public class DashBoardActivity extends AppCompatActivity implements DashBoardContract.View {


    @BindView(R.id.viewDashboard) View view;
    private DashBoardContract.Presenter mPresenter;
    Geocoder geocoder;
    List<Address> addresses;
    Double latitude = 10.744806;
    Double longitude = 106.684208;
    ArrayList<Option> options = new ArrayList<>();
    @BindView(R.id.gridview) GridView gridView;

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
        } catch (IOException e) {
            e.printStackTrace();
        }
        initGridView();

    }

    private void initGridView() {
        options.add(new Option("Calories", R.drawable.ic_setup_calories));
        options.add(new Option("Lịch sử chạy", R.drawable.ic_history));
        options.add(new Option("Khu vực nguy hiểm", R.drawable.ic_stopwatch));
        options.add(new Option("Bạn bè", R.drawable.ic_friends));
        options.add(new Option("Bắt đầu chạy", R.drawable.ic_running));
        options.add(new Option("Địa điểm chạy", R.drawable.ic_map));

        final OptionAdapter optionAdapter = new OptionAdapter(this, options);
        gridView.setAdapter(optionAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 5:
                        break;

                    case 6:
                        break;
                }
            }
        });

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
