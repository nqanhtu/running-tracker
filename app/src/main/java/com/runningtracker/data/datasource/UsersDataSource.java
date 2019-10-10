package com.runningtracker.data.datasource;

import androidx.annotation.NonNull;

import java.util.List;

import com.runningtracker.data.model.User;

/**
 * Created by Anh Tu on 3/12/2018.
 */

public interface UsersDataSource {
    interface LoadUsersCallback {

        void onUsersLoaded(List<User> users);

        void onDataNotAvailable();
    }

    interface GetUserCallback {

        void onUserLoaded(User user);

        void onDataNotAvailable();
    }

    void getFriendRequests(@NonNull String uid, @NonNull LoadUsersCallback callback);

    void getFriendRequestsSent(@NonNull String uid, @NonNull LoadUsersCallback callback);

    void getFriends(@NonNull String uid, @NonNull LoadUsersCallback callback);

    void getUserByEmail(@NonNull String email, @NonNull GetUserCallback callback);

    void addFriendRequestSent(@NonNull User user, @NonNull String currentUid);

    void addFriendRequest(@NonNull User currentUser, @NonNull String uid);

    void deleteFriendRequestSent(@NonNull User user, @NonNull String currentUid);

    void deleteFriendRequest(@NonNull User currentUser, @NonNull String uid);

    void acceptFriendRequest(@NonNull User currentUser, @NonNull String uid);

    void createAccount(@NonNull User user);


}
