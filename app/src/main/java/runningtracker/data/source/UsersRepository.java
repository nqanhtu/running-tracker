package runningtracker.data.source;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import runningtracker.data.model.User;

/**
 * Created by Anh Tu on 3/12/2018.
 */

public class UsersRepository implements UsersDataSource {
    private final FirebaseFirestore db;

    private static UsersRepository INSTANCE = null;

    private UsersRepository(@NonNull FirebaseFirestore firestore) {
        db = firestore;
    }

    public static UsersRepository getInstance(FirebaseFirestore firestore) {
        if (INSTANCE == null) {
            INSTANCE = new UsersRepository(firestore);
        }
        return INSTANCE;
    }

    @Override
    public void getFriendRequests(@NonNull String uid, @NonNull final LoadUsersCallback callback) {
        db.collection("users/"+uid+"/friendRequestsSent")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<User> friends = task.getResult().toObjects(User.class);
                            callback.onUsersLoaded(friends);
                        } else {
                            callback.onDataNotAvailable();
                        }
                    }
                });
    }

    @Override
    public void getFriendRequestsSent(@NonNull String uid, @NonNull final LoadUsersCallback callback) {
        db.collection("users/"+uid+"/friendRequestsSent")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<User> friends = task.getResult().toObjects(User.class);
                            callback.onUsersLoaded(friends);
                        } else {
                            callback.onDataNotAvailable();
                        }
                    }
                });
    }
}
