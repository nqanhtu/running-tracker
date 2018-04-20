package runningtracker.friendrequests;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import runningtracker.data.datasource.UsersDataSource;
import runningtracker.data.model.User;
import runningtracker.data.repository.UsersRepository;

/**
 * Created by Anh Tu on 3/10/2018.
 */

public class FriendRequestsPresenter implements FriendRequestsContract.Presenter {

    private static final String TAG = "FriendRequests";
    private final FriendRequestsContract.View mFriendRequestsView;
    private final UsersRepository mUsersRepository;
    private final User currentUser = new User(FirebaseAuth.getInstance().getCurrentUser());


    public FriendRequestsPresenter(UsersRepository mUsersRepository, FriendRequestsContract.View mFriendRequestsView) {
        this.mUsersRepository = mUsersRepository;
        this.mFriendRequestsView = mFriendRequestsView;
    }


    @Override
    public void start() {
        loadFriendRequests();
    }

    private void loadFriendRequests() {
        mUsersRepository.getFriendRequests(currentUser.getUid(), new UsersDataSource.LoadUsersCallback() {
            @Override
            public void onUsersLoaded(List<User> users) {
                mFriendRequestsView.showFriendRequests(users);
            }

            @Override
            public void onDataNotAvailable() {

            }
        });

    }

}
