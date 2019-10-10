package com.runningtracker.dashboard;

import com.runningtracker.base.BasePresenter;
import com.runningtracker.base.BaseView;

/**
 * Created by Anh Tu on 2/3/2018.
 */

public interface DashBoardContract {
    interface View extends BaseView<Presenter> {

    }

    interface Presenter extends BasePresenter {

    }
}
