package runningtracker.Presenter.PresenterRunning;


import android.content.Context;
import android.location.Location;
import android.widget.Toast;

import org.json.JSONException;

import runningtracker.Model.ResAPICommon;
import runningtracker.View.ViewRunning;

public class PreLogicRunning implements PreRunning{
    ViewRunning viewRunning;
    ResAPICommon resAPICommon;
    public PreLogicRunning(ViewRunning viewRunning){
        this.viewRunning = viewRunning;
        this.resAPICommon = new ResAPICommon();
    }

    @Override
    public void saveRunnig() throws JSONException {
        resAPICommon.RestPostClient(viewRunning.getMainActivity(),"http://192.168.43.188:8000/runningsession/new", viewRunning.getValueRunning());
    }
    //function get data using ResAPI
    @Override
    public void getData() {
        resAPICommon.RestGetClient("http://192.168.43.188:8000/runningsession/new", viewRunning.getMainActivity());
    }
    //function Distance between 2 location
    @Override
    public float DistanceLocation(Location locationA, Location locationB) {
        float distance;
        distance = locationA.distanceTo(locationB)/1000;// chang to meter to kilometer
        return distance;
    }

}
