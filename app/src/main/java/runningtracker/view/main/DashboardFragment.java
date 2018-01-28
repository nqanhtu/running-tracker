package runningtracker.view.main;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import runningtracker.Adapter.MenuAdapter;
import runningtracker.model.modelrunning.DatabaseLocation;
import runningtracker.model.modelrunning.DatabaseRunningLocation;
import runningtracker.model.modelrunning.DatabaseWeather;
import runningtracker.model.modelrunning.DetailRunningObject;
import runningtracker.model.modelrunning.LocationObject;
import runningtracker.model.modelrunning.MenuObject;
import runningtracker.model.modelrunning.QueryRunningObject;
import runningtracker.model.modelrunning.WeatherObject;
import runningtracker.R;
import runningtracker.Presenter.main.LogicMain;
import runningtracker.view.running.MainActivity;
import runningtracker.view.running.ResultActivity;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DashboardFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment implements  ViewMain {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    LogicMain main;

    ListView lvItemMenu;
    ArrayList<MenuObject> menuObjectArrayList;
    MenuAdapter adapter;
    DatabaseWeather databaseWeather;
    ArrayList<WeatherObject> arrayList;
    //test my location
    DatabaseLocation databaseLocation;
    LocationObject locationObject;
    String messeageWeather;


    private OnFragmentInteractionListener mListener;

    public DashboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DashboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        lvItemMenu = rootView.findViewById(R.id.lvItemMenu);
        menuObjectArrayList = new ArrayList<>();
        locationObject = new LocationObject();
        databaseLocation = new DatabaseLocation(getContext());

        menuObjectArrayList.add(new MenuObject("Thiết lập lượng calories luyện tập", R.drawable.setup_calories));
        menuObjectArrayList.add(new MenuObject("Lịch sử chạy", R.drawable.history));
        menuObjectArrayList.add(new MenuObject("Thiết lập vùng nguy hiểm", R.drawable.timer));
        menuObjectArrayList.add(new MenuObject("Tìm kiếm bạn bè", R.drawable.search_friends));
        menuObjectArrayList.add(new MenuObject("Bắt đầu chạy", R.drawable.start_running));

        adapter = new MenuAdapter(getContext(), R.layout.item_dashboard, menuObjectArrayList);
        lvItemMenu.setAdapter(adapter);
        //Bat su kien click vao dong tren listview
        lvItemMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:
                        Intent nextActivity = new Intent(getContext(), WeatherSuggestion.class);
                        startActivity(nextActivity);
                        break;
                    case 1:
                        //
                        break;
                    case 2:
                        //
                        break;
                    case 3:
                        //
                        break;
                    case 4:
                        //
                        break;
                    case 5:
                        mListener.onStartRunning();
                        break;
                    default :
                        //
                        break;

                }
            }
        });

        //get all location
  /*      ArrayList<DetailRunningObject> detailRunningObjects = new ArrayList<DetailRunningObject>();
        detailRunningObjects = getListLocation();
        if(detailRunningObjects.size() > 0) {
            messeageWeather = suggestionLocation(detailRunningObjects);
        }*/


        //sync list view
        final Handler handler = new Handler();
        final Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @SuppressWarnings("unchecked")
                    public void run() {
                        try {
                            databaseWeather = new DatabaseWeather(getContext());
                            arrayList = new ArrayList<>();
                            arrayList = databaseWeather.getAllWeather();
                            Log.e(TAG, "N: "+arrayList.size());

                            if(arrayList.size() > 0){
                                menuObjectArrayList.add(0,new MenuObject("Gợi ý thời tiết", R.drawable.setup_calories));
                                adapter.notifyDataSetChanged();
                                timer.cancel();
                            }
                        }
                        catch (Exception e) {
                            Log.e(TAG, ""+e);
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 2000);

/*        Button startRunning = (Button)rootView.findViewById(R.id.bnRunning);
        startRunning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onStartRunning();
            }
        });
        return rootView;*/
        return rootView;
    }


    //ham lay ra vi tri gan voi vi tri nguoi dung
    private ArrayList<DetailRunningObject> getListLocation() {
        ArrayList<QueryRunningObject> queryRunningObjectArrayList = new ArrayList<>();
        ArrayList<DetailRunningObject> detailRunningObjects = new ArrayList<>();
        DatabaseRunningLocation location = new DatabaseRunningLocation(getContext());
        try{
            queryRunningObjectArrayList = location.getListLocation(1);
        }catch (Exception e){
            Log.e(TAG, ""+e);
        }

        Location A = new Location("");
        Location B = new Location("");
        A.setLatitude(10.736000);
        A.setLongitude(106.678947);
        DetailRunningObject detail = new DetailRunningObject();
        float minDistance;
        B.setLatitude(queryRunningObjectArrayList.get(0).getLatitudeValue());
        B.setLongitude(queryRunningObjectArrayList.get(0).getLongitudeValue());
        minDistance = A.distanceTo(B);
        detail.setIdLocation(queryRunningObjectArrayList.get(0).getId());

        for(int i = 1; i < queryRunningObjectArrayList.size(); i++){
            float distance;
            B.setLatitude(queryRunningObjectArrayList.get(i).getLatitudeValue());
            B.setLongitude(queryRunningObjectArrayList.get(i).getLongitudeValue());
            distance = A.distanceTo(B);
            if(distance < minDistance){
                minDistance = distance;
                detail.setIdLocation(queryRunningObjectArrayList.get(i).getId());
            }
        }
        if(detail.getIdLocation() > 0){
            detailRunningObjects = location.getListDetailLocation(detail.getIdLocation());
        }
        return detailRunningObjects;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
           // throw new RuntimeException(context.toString()
            //        + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Context getMainActivity() {
        return null;
    }

    @Override
    public void navigationRunning() {

    }

    @Override
    public void navigationRunningOffline() {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
        void onStartRunning();
    }
}
