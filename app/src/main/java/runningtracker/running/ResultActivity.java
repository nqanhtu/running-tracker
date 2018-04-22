package runningtracker.running;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
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
import com.google.android.gms.maps.GoogleMap;

import runningtracker.R;


import java.util.ArrayList;
import java.util.HashMap;

public class ResultActivity extends AppCompatActivity {

    private static PresenterRunning presenterRunning;
    public static ArrayList<ViewGroup> tabFragmentLayouts;
    private static GoogleMap mMap;

    public ResultActivity() {
        tabFragmentLayouts = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenterRunning = new PresenterRunning();
        /**
         * set value tab result
        * */
        setContentView(R.layout.activity_result);
        initializeUI();
        StatsTabFragment.setStatsValue(getIntent());


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

        public void addFragment(Fragment fragment, String title) {
            fragmentTitleList.add(title);
            fragmentList.add(fragment);
        }
    }

    public static class StatsTabFragment extends Fragment {
        private static long mDuration;
        private static float mAvgPace, mNetCalorie, mDistance, mMaxPace, mGrossCalorie;
        private HashMap<Integer, View> childViews;

        public StatsTabFragment() {}

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the gridview_dashboard_item for this fragment
            ViewGroup inflatedLayout = (ViewGroup) inflater.inflate(R.layout.activity_result_tab_stats, container, false);
            tabFragmentLayouts.add(inflatedLayout);
            getAllChildViews(inflatedLayout);
            assignValueToView();

            return inflatedLayout;
        }

        /**
         * Get all first-level child Views of parent ViewGroup and put into childViews HashMap.
         * @param parent The parent ViewGroup to get child Views from.
         */
        private void getAllChildViews(ViewGroup parent) {
            childViews = new HashMap<>();
            for(int i = 0; i < parent.getChildCount(); i++) {
                View child = parent.getChildAt(i);
                childViews.put(child.getId(), child);
            }
        }

        static void setStatsValue(Intent intent) {
            mDuration = intent.getLongExtra("duration", 0);
            mAvgPace = intent.getFloatExtra("avgPace", 0);
            mNetCalorie = intent.getFloatExtra("netCalorie", 0);
            mDistance = intent.getFloatExtra("distance", 0);
            mMaxPace = intent.getFloatExtra("maxPace", 0);
            mGrossCalorie = intent.getFloatExtra("grossCalorie", 0);
        }

        private void assignValueToView() {
            long secs = (mDuration / 1000);
            long mins = secs / 60;
            long hour = mins /60;
            secs = secs % 60;

            TextView txtDuration = ((TextView) childViews.get(R.id.textValueDuration));
            TextView txtDistance = ((TextView) childViews.get(R.id.textValueDistance));
            TextView txtAvgPace = ((TextView) childViews.get(R.id.textValueAveragePace));
            TextView txtMaxPace = ((TextView) childViews.get(R.id.textValueMaxPace));
            TextView txtAvgSpeed = ((TextView) childViews.get(R.id.textValueAverageSpeed));
            TextView txtMaxSpeed = ((TextView) childViews.get(R.id.textValueMaxSpeed));
            TextView txtNetCalorie = ((TextView) childViews.get(R.id.textValueNetCalorie));
            TextView txtGrossCalorie = ((TextView) childViews.get(R.id.textValueGrossCalorie));

            txtDuration.setText("" + hour + ":" + String.format("%02d", mins) + ":" + String.format("%02d", secs));
            txtDistance.setText(Float.toString(mDistance));
            txtAvgPace.setText(Float.toString(mAvgPace));
            txtMaxPace.setText(Float.toString(mMaxPace));
            if(mAvgPace > 0.0 && mMaxPace > 0.0) {
                txtAvgSpeed.setText(Float.toString(presenterRunning.RoundAvoid(60 / mAvgPace, 2)));
                txtMaxSpeed.setText(Float.toString(presenterRunning.RoundAvoid(60 / mMaxPace, 2)));
            }
            txtNetCalorie.setText(Float.toString(mNetCalorie));
            txtGrossCalorie.setText(Float.toString(mGrossCalorie));
        }
    }
}