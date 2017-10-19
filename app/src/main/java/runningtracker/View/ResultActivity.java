package runningtracker.view;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import runningtracker.R;
import runningtracker.presenter.presenterrunning.PreLogicRunning;

import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity implements ViewRunning {
    long mDuration;
    float mPace, mNetCalorie, mDistance, mMaxPace, mGrossCalorie;
    TextView txtDistance, txtPace, txtMaxPace, txtNetCalorie, txtGrossCalorie, txtDuration, txtSpeed, txtMaxSpeed;
    private PreLogicRunning preLogicRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        preLogicRunning = new PreLogicRunning(this);
        Intent intent = getIntent();
        mDuration = intent.getLongExtra("duration", 0);
        mPace = intent.getFloatExtra("pace", 0);
        mNetCalorie = intent.getFloatExtra("netCalorie", 0);
        mDistance = intent.getFloatExtra("distance", 0);
        mMaxPace = intent.getFloatExtra("maxPace", 0);
        mGrossCalorie = intent.getFloatExtra("grossCalorie", 0);
        initializeUI();

        setContentView(R.layout.activity_result_tab_stats);
        txtDistance = (TextView) findViewById(R.id.textValueDistance);
        txtPace = (TextView) findViewById(R.id.textValueAveragePace);
        txtMaxPace = (TextView) findViewById(R.id.textValueMaxPace);
        txtNetCalorie = (TextView) findViewById(R.id.textValueNetCalorie);
        txtGrossCalorie = (TextView) findViewById(R.id.textValueGrossCalorie);
        txtDuration = (TextView) findViewById(R.id.textValueDuration);
        txtSpeed = (TextView) findViewById(R.id.textValueAverageSpeed);
        txtMaxSpeed = (TextView) findViewById(R.id.textValueMaxSpeed);
        setTextValue();
    }
 //set text view
    public void setTextValue(){
        long secs = (mDuration / 1000);
        long mins = secs / 60;
        long hour = mins /60;
        secs = secs % 60;

        txtDuration.setText("" + hour + ":" + String.format("%02d", mins) + ":" + String.format("%02d", secs));
        txtDistance.setText(Float.toString(mDistance));
        txtPace.setText(Float.toString(mPace));
        txtMaxPace.setText(Float.toString(mMaxPace));
        if(mPace > 0.0 && mMaxPace > 0.0) {
            txtSpeed.setText(Float.toString(preLogicRunning.RoundAvoid(60 / mPace, 2)));
            txtMaxSpeed.setText(Float.toString(preLogicRunning.RoundAvoid(60 / mMaxPace, 2)));
        }
        txtNetCalorie.setText(Float.toString(mNetCalorie));
        txtGrossCalorie.setText(Float.toString(mGrossCalorie));
    }
    private void initializeUI() {
        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Track Result");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.textColorPrimary));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // ViewPager support swiping between tabs
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        // Tabs
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons(tabLayout);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new StatsTabFragment(), "STATS");
        adapter.addFragment(new TrackTabFragment(), "TRACK");
        viewPager.setAdapter(adapter);
    }

    private void setupTabIcons(TabLayout tabLayout) {
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_equalizer_white_48dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_google_maps_white_48dp);
    }

    @Override
    public JSONObject getValueRunning() throws JSONException {
        return null;
    }

    @Override
    public Context getMainActivity() {
        return null;
    }

    @Override
    public void setupViewRunning(float mDistanceValue, float mPaceValue, float mCalorie) {

    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<String> fragmentTitleList = new ArrayList<>();
        private ArrayList<Fragment> fragmentList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

//        @Override
//        public CharSequence getPageTitle(int position) {
//            return fragmentTitleList.get(position);
//        }

        public void addFragment(Fragment fragment, String title) {
            fragmentTitleList.add(title);
            fragmentList.add(fragment);
        }
    }

    public static class StatsTabFragment extends Fragment {
        public StatsTabFragment() {}

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.activity_result_tab_stats, container, false);
        }
    }

    public static class TrackTabFragment extends Fragment {
        public TrackTabFragment() {}

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.activity_result_tab_map, container, false);
        }
    }
}