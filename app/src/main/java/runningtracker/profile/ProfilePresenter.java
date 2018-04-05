package runningtracker.profile;

import com.google.firebase.auth.FirebaseAuth;

import runningtracker.addfriend.AddFriendContract;
import runningtracker.base.BasePresenter;
import runningtracker.data.repository.UsersRepository;

/**
 * Created by Anh Tu on 3/22/2018.
 */

public class ProfilePresenter implements ProfileContract.Presenter {

    private static final String TAG = "AddFriends";
    private final UsersRepository mUsersRepository;
    private final ProfileContract.View mProfileView;

    public ProfilePresenter(UsersRepository mUsersRepository, ProfileContract.View mProfileView) {
        this.mUsersRepository = mUsersRepository;
        this.mProfileView = mProfileView;
    }

    @Override
    public void start() {

    }

    @Override
    public void logout() {
        FirebaseAuth.getInstance().signOut();
        mProfileView.showLogin();
    }


}
