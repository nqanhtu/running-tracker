package runningtracker.Presenter.PresenterRunning;


import android.location.Location;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import runningtracker.Model.DataCallback;
import runningtracker.Model.ModelRunning.M_BodilyCharacteristicObject;
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
        //resAPICommon.RestGetClient("http://192.168.43.188:8000/runningsession/new", viewRunning.getMainActivity());
    }
    //function Distance between 2 location
    @Override
    public float DistanceLocation(Location locationA, Location locationB) {
        float distance;
        distance = locationA.distanceTo(locationB)/1000;// chang to meter to kilometer
        return distance;
    }

    @Override
    public float RoundAvoid(double value, int places) {
        double scale = Math.pow(10, places);
        return (float) (Math.round(value * scale) / scale);
    }

    @Override
    public void getBodilyCharacter(M_BodilyCharacteristicObject m_Bodily) throws JSONException {
        final M_BodilyCharacteristicObject finalM_Bodily = m_Bodily;
        ResAPICommon.RestGetClient("http://14.169.146.178/appuser/get/6", viewRunning.getMainActivity(),
                new DataCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        try {
                            finalM_Bodily.setAge((Integer) result.get("Age"));
                            finalM_Bodily.setGender((String) result.get("Gender"));
                            finalM_Bodily.setWeightInKg((Integer) result.get("WeightInKg"));
                            Toast.makeText(viewRunning.getMainActivity(), "Error:  " + finalM_Bodily.getWeightInKg(), Toast.LENGTH_SHORT).show();
                            finalM_Bodily.setHeightInCm((Integer) result.get("HeightInCm"));
                            finalM_Bodily.setVO2max((Integer) result.get("VO2max"));
                            //m_Bodily.setRestingMetabolicRate((float) result.get("RestingMetabolicRate"));
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(viewRunning.getMainActivity(), "Error" +e, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                );
    }
}
