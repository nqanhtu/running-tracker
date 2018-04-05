package runningtracker.profile;

import runningtracker.base.BasePresenter;

/**
 * Created by Anh Tu on 3/22/2018.
 */

public interface ProfileContract {
    interface View {
        void showLogin();

    }

    interface Presenter extends BasePresenter {
        void logout();
    }
}
