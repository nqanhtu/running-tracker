package runningtracker.presenter.presenterrunning;


import runningtracker.model.ResAPICommon;
import runningtracker.view.ViewRunning;

public class PreLogicRunning implements PreRunning {
    ViewRunning viewRunning;
    ResAPICommon resAPICommon;
    public PreLogicRunning(ViewRunning viewRunning){
        this.viewRunning = viewRunning;
        this.resAPICommon = new ResAPICommon();
    }

    @Override
    public void saveRunning(){
        resAPICommon.RestPostClient(viewRunning.getMainActivity(),"", viewRunning.getValueRunning());
    }

    @Override
    public void getData() {
        resAPICommon.RestGetClient("http://192.168.43.226:8000/api/user", viewRunning.getMainActivity());
    }
}
