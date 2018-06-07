package runningtracker.register;

import java.util.List;

import runningtracker.base.BasePresenter;
import runningtracker.data.model.User;

/**
 * Created by Anh Tu on 3/21/2018.
 */

public interface RegisterContract {
    interface View {
        void showProgressDialog();

        void hideProgressDialog();

        void makeToast(String text);

        boolean validateForm();

        void startHome();
    }

    interface Presenter extends BasePresenter {
        void createAccount(String email, String password);
    }
}
