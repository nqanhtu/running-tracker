package runningtracker.addfriend;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

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
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void start() {
        loadAddFriends();
    }

    private void loadAddFriends() {

    }

    @Override
    public void addFriend(String email) {
//        mUsersRepository.getUserByEmail(email, new UsersDataSource.GetUserCallback() {
//            @Override
//            public void onUserLoaded(User user) {
//                mUsersRepository.addFriendRequest(currentUser, user.getUid());
//                mUsersRepository.addFriendRequestSent(user, currentUser.getUid());
//            }
//
//            @Override
//            public void onDataNotAvailable() {
//
//            }
//        });
        db.collection("users").whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Friend> users = task.getResult().toObjects(Friend.class);

                            if (!users.isEmpty()) {
                                final Friend user = users.get(0);

                                db.collection("users").document(currentUser.getUid())
                                        .collection("friendRequestsSent").document(user.getUid()).set(user);

                                db.collection("users").document(currentUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        db.collection("users").document(user.getUid())
                                                .collection("friendRequests").document(currentUser.getUid()).set(documentSnapshot.getData());
                                    }
                                });


                            }


                        }
                    }
                });
    }

}
