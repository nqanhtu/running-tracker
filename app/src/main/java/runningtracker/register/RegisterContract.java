package runningtracker.register;

import java.util.List;

import runningtracker.base.BasePresenter;
import runningtracker.data.model.User;

/**
 * Created by Anh Tu on 3/21/2018.
 */

public interface RegisterContract {
    interface View {
        public void showProgressDialog();
        public void hideProgressDialog();
    }

    interface Presenter extends BasePresenter {
        void createAccount(String email, String password);
    }
}
