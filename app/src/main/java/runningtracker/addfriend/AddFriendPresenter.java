package runningtracker.addfriend;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import runningtracker.data.model.User;
import runningtracker.data.source.UsersDataSource;
import runningtracker.data.source.UsersRepository;

/**
 * Created by Anh Tu on 3/10/2018.
 */

public class AddFriendPresenter implements AddFriendContract.Presenter {

    private static final String TAG = "AddFriends";
    private final UsersRepository mUsersRepository;

    private final AddFriendContract.View mAddFriendView;

    public AddFriendPresenter(UsersRepository mUsersRepository, @NonNull AddFriendContract.View mAddFriendView) {
        this.mUsersRepository = mUsersRepository;
        this.mAddFriendView = mAddFriendView;
    }

    @Override
    public void start() {
        loadAddFriends();
    }

    private void loadAddFriends() {

        mUsersRepository.getFriendRequestsSent(getCurrentUserUid(), new UsersDataSource.LoadUsersCallback() {
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
      
    }

    private String getCurrentUserUid(){
        return  FirebaseAuth.getInstance().getUid();
    }
}
