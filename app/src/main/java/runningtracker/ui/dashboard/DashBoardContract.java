package runningtracker.ui.dashboard;

import android.content.Context;

import runningtracker.ui.base.BasePresenter;
import runningtracker.ui.base.BaseView;

/**
 * Created by Anh Tu on 2/3/2018.
 */

public interface DashBoardContract {
    interface View extends BaseView<Presenter> {
        Context getMainActivity();
        void navigationRunning();
        void navigationRunningOffline();

    }

    interface Presenter extends BasePresenter {

    }
}
