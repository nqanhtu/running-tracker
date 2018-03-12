package runningtracker.addfriend;

import java.util.List;

import runningtracker.base.BasePresenter;
import runningtracker.data.model.User;

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
