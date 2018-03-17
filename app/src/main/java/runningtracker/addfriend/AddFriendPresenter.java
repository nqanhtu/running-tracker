package runningtracker.addfriend;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import runningtracker.data.model.User;
import runningtracker.data.datasource.UsersDataSource;
import runningtracker.data.repository.UsersRepository;

/**
 * Created by Anh Tu on 3/10/2018.
 */

public class AddFriendPresenter implements AddFriendContract.Presenter {

    private static final String TAG = "AddFriends";
    private final UsersRepository mUsersRepository;
    private final AddFriendContract.View mAddFriendView;
    private final User currentUser = new User(FirebaseAuth.getInstance().getCurrentUser());

    public AddFriendPresenter(UsersRepository mUsersRepository, @NonNull AddFriendContract.View mAddFriendView) {
        this.mUsersRepository = mUsersRepository;
        this.mAddFriendView = mAddFriendView;
    }

    @Override
    public void start() {
        loadAddFriends();
    }

    private void loadAddFriends() {
        mUsersRepository.getFriendRequestsSent(currentUser.getUid(), new UsersDataSource.LoadUsersCallback() {
            @Override
            public void onUsersLoaded(List<User> users) {
                mAddFriendView.showFriendsList(users);
            }

            @Override
            public void onDataNotAvailable() {

            }
        });


    }

    @Override
    public void addFriend(String email) {
        mUsersRepository.getUserByEmail(email, new UsersDataSource.GetUserCallback() {
            @Override
            public void onUserLoaded(User user) {
                mUsersRepository.addFriendRequest(currentUser,user.getUid());
                mUsersRepository.addFriendRequestSent(user,currentUser.getUid());
            }

            @Override
            public void onDataNotAvailable() {

            }
        });

    }

}
