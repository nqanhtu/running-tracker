package runningtracker.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import runningtracker.Adapter.OptionAdapter;
import runningtracker.R;
import runningtracker.data.model.Option;
import runningtracker.presenter.main.LogicMain;
import runningtracker.ui.suggest_place.suggest_place;
import runningtracker.view.running.MainActivity;
import runningtracker.view.running.MainActivityOffline;

import static com.google.common.base.Preconditions.checkNotNull;

public class DashboardFragment extends Fragment implements DashBoardContract.View {

    private DashBoardContract.Presenter mPresenter;
    Geocoder geocoder;
    List<Address> addresses;
    Double latitude = 10.744806;
    Double longitude = 106.684208;
    ArrayList<Option> options = new ArrayList<>();

    LogicMain main;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        initGridView(view);
        return view;
    }


    private void initGridView(View view) {
        GridView gridView = (GridView) view.findViewById(R.id.gridview);
        options.add(new Option("Calories", R.drawable.ic_setup_calories));
        options.add(new Option("Lịch sử chạy", R.drawable.ic_history));
        options.add(new Option("Khu vực nguy hiểm", R.drawable.ic_stopwatch));
        options.add(new Option("Bạn bè", R.drawable.ic_friends));
        options.add(new Option("Bắt đầu chạy", R.drawable.ic_running));
        options.add(new Option("Địa điểm chạy", R.drawable.ic_map));

        final OptionAdapter optionAdapter = new OptionAdapter(getActivity(), options);
        gridView.setAdapter(optionAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        main.onNavigationActivity();
                        break;
                    case 5:
                        Intent intent = new Intent(getActivity(), suggest_place.class);
                        startActivity(intent);
                        break;
                }
            }
        });

    }

    @Override
    public Context getMainActivity() {

            return getActivity();
    }

    @Override
    public void navigationRunning() {
        Intent nextActivity = new Intent(getActivity(), MainActivity.class);
        startActivity(nextActivity);
    }

    @Override
    public void navigationRunningOffline() {
        Intent nextActivity = new Intent(getActivity(), MainActivityOffline.class);
        startActivity(nextActivity);
    }

    @Override
    public void setPresenter(@NonNull DashBoardContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }
}
