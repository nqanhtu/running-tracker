package runningtracker.Presenter.PresenterRunning;


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
    public void saveRunnig(){
        resAPICommon.RestPostClient(viewRunning.getMainActivity(),"", viewRunning.getValueRunning());
    }

    @Override
    public void getData() {
        resAPICommon.RestGetClient("http://192.168.43.226:8000/api/user", viewRunning.getMainActivity());
    }
}
