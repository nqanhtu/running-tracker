package runningtracker.presenter.presenterrunning;
import android.location.Location;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import runningtracker.model.DataCallback;
import runningtracker.model.modelrunning.BodilyCharacteristicObject;
import runningtracker.model.ResAPICommon;
import runningtracker.model.modelrunning.DatabaseRunningSession;
import runningtracker.model.modelrunning.RunningObject;
import runningtracker.view.viewrunning.ViewRunning;

public class LogicRunning implements Running {
    ViewRunning viewRunning;
    ResAPICommon resAPICommon;
    DatabaseRunningSession dataRunning;
    public LogicRunning(ViewRunning viewRunning){
        this.viewRunning = viewRunning;
        this.resAPICommon = new ResAPICommon();
    }

    @Override
    public void saveRunning() throws JSONException {
        resAPICommon.RestPostClient(viewRunning.getMainActivity(), "http://14.169.228.44/runningsession/new", viewRunning.getValueRunning());
    }

    @Override
    public void getData() {
        //resAPICommon.RestGetClient("http://192.168.43.188:8000/runningsession/new", viewRunning.getMainActivity());
    }

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
    public void getBodilyCharacter(BodilyCharacteristicObject m_Bodily) throws JSONException {
        final BodilyCharacteristicObject finalM_Bodily = m_Bodily;
        ResAPICommon.RestGetClient("" +
                        "http://14.169.228.44/appuser/get/1", viewRunning.getMainActivity(),
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
                            finalM_Bodily.setRestingMetabolicRate((Integer) result.get("RestingMetabolicRate"));
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(viewRunning.getMainActivity(), "Error" +e, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    @Override
    public RunningObject setRunningObject(int runningSessionID, int userID, String startTimestamp, String finishTimestamp, double distanceInKm, int roadGradient, int runOnTreadmill, int netCalorieBurned, int grossCalorieBurned, int flagStatus) {
        RunningObject runningObject = new RunningObject(runningSessionID, userID, startTimestamp, finishTimestamp, distanceInKm, roadGradient, runOnTreadmill, netCalorieBurned, grossCalorieBurned, flagStatus);
        return runningObject;
    }

    @Override
    public boolean SaveRunningSession(RunningObject runningObject) {
       /* if(dataRunning.addNewRunningSession(runningObject))
            return true;*/
        return false;
    }
}
