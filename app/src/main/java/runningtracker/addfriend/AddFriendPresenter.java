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

/**
 * Created by Anh Tu on 3/10/2018.
 */

public class AddFriendPresenter implements AddFriendContract.Presenter {

    private static final String TAG = "AddFriends";

    private FirebaseFirestore db;
    private final AddFriendContract.View mAddFriendView;

    public AddFriendPresenter(FirebaseFirestore firestore, @NonNull AddFriendContract.View mAddFriendView) {
        this.mAddFriendView = mAddFriendView;
        this.db = firestore;
    }

    @Override
    public void start() {
        loadAddFriends();
    }

    private void loadAddFriends() {
        db.collection("users/"+getCurrentUserUid()+"/friendRequestsSent")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<User> friends = task.getResult().toObjects(User.class);
                            mAddFriendView.showFriendsList(friends);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public void addFriend(String email) {
        db.collection("users").whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Map<String, Object> friend = new HashMap<>();
                                friend.put("displayName", document.getData().get("displayName"));
                                friend.put("email", document.getData().get("email"));
                                db.collection("users/"+getCurrentUserUid()+"/friendRequestsSent").document(document.getId())
                                        .set(friend).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        loadAddFriends();
                                    }
                                });
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private String getCurrentUserUid(){
        return  FirebaseAuth.getInstance().getUid();
    }
}
