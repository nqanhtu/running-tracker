package runningtracker.addfriend;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import runningtracker.Adapter.FriendsListAdapter;
import runningtracker.data.model.Friend;
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

    FirebaseFirestore db;

    public AddFriendPresenter(UsersRepository mUsersRepository, @NonNull AddFriendContract.View mAddFriendView) {
        this.mUsersRepository = mUsersRepository;
        this.mAddFriendView = mAddFriendView;
    }

    @Override
    public void start() {
        loadAddFriends();
    }

    private void loadAddFriends() {
//        mUsersRepository.getFriendRequestsSent(currentUser.getUid(), new UsersDataSource.LoadUsersCallback() {
//            @Override
//            public void onUsersLoaded(List<Friend> friends) {
//                mAddFriendView.showFriendsList(friends);
//            }
//
//            @Override
//            public void onDataNotAvailable() {
//
//            }
//        });
        db = FirebaseFirestore.getInstance();
        db.collection("users").document(currentUser.getUid()).collection("friendRequests")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Friend> friends = task.getResult().toObjects(Friend.class);

                            mAddFriendView.showFriendsList(friends);

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });

    }

    @Override
    public void addFriend(String email) {
        mUsersRepository.getUserByEmail(email, new UsersDataSource.GetUserCallback() {
            @Override
            public void onUserLoaded(User user) {
                mUsersRepository.addFriendRequest(currentUser, user.getUid());
                mUsersRepository.addFriendRequestSent(user, currentUser.getUid());
            }

            @Override
            public void onDataNotAvailable() {

            }
        });

    }

}
