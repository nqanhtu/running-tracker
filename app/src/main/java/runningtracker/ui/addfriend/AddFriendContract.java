package runningtracker.ui.addfriend;

import java.util.List;

import runningtracker.data.model.User;
import runningtracker.ui.base.BasePresenter;
import runningtracker.ui.base.BaseView;

/**
 * Created by Anh Tu on 3/10/2018.
 */

public interface AddFriendContract {
    interface View {
        void showFriendsList(List<User> friends);
    }

    interface Presenter extends BasePresenter {
        void addFriend(String email);
    }
}
