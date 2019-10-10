package com.runningtracker.data.repository;

import androidx.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import com.runningtracker.data.model.User;
import com.runningtracker.data.datasource.UsersDataSource;

/**
 * Created by Anh Tu on 3/12/2018.
 */

public class UsersRepository implements UsersDataSource {
    private final FirebaseFirestore db;

    private static UsersRepository INSTANCE = null;

    private static final String TAG = "Users Repository";

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
        getDocumentsOfCollectionFromUser(uid, callback, "friendRequests");
    }

    @Override
    public void getFriendRequestsSent(@NonNull String uid, @NonNull final LoadUsersCallback callback) {
        getDocumentsOfCollectionFromUser(uid, callback, "friendRequestsSent");
    }

    @Override
    public void getFriends(@NonNull String uid, @NonNull final LoadUsersCallback callback) {
        getDocumentsOfCollectionFromUser(uid, callback, "friends");
    }

    @Override
    public void getUserByEmail(@NonNull String email, @NonNull final GetUserCallback callback) {
        db.collection("users").whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<User> users = task.getResult().toObjects(User.class);
                            Log.d("Lay ra 1 user", users.toString());
                            callback.onUserLoaded(users.get(0));

                        } else {
                            callback.onDataNotAvailable();
                        }
                    }
                });
    }

    @Override
    public void addFriendRequestSent(@NonNull User user, @NonNull String currentUid) {
        addUserToUser(user, currentUid, "friendRequestsSent");
    }

    @Override
    public void addFriendRequest(@NonNull User currentUser, @NonNull String uid) {
        addUserToUser(currentUser, uid, "friendRequests");
    }

    @Override
    public void deleteFriendRequestSent(@NonNull User user, @NonNull String currentUid) {

    }

    @Override
    public void deleteFriendRequest(@NonNull User currentUser, @NonNull String uid) {

    }

    @Override
    public void acceptFriendRequest(@NonNull User user, @NonNull String uid) {
        addUserToUser(user, uid, "friends");
    }

    @Override
    public void createAccount(@NonNull User user) {
        db.collection("users").document(user.getUid()).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "DocumentSnapshot successfully written!");
                } else {
                    Log.w(TAG, "Error writing document");
                }
            }
        });
    }

    private void addUserToUser(@NonNull User user, @NonNull String uid, String collection) {
        db.collection("users").document(uid).collection(collection)
                .document(user.getUid()).set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    private void deleteUserFromUser(@NonNull User user, @NonNull String uid, String collection) {


    }

    private void getDocumentsOfCollectionFromUser(@NonNull String uid, @NonNull final LoadUsersCallback callback, String collection) {
        db.collection("users").document(uid).collection(collection)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            List<User> friends = task.getResult().toObjects(User.class);
                            callback.onUsersLoaded(friends);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            callback.onDataNotAvailable();
                        }
                    }
                });
    }




}