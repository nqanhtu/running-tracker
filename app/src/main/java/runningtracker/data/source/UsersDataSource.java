package runningtracker.data.source;

import android.support.annotation.NonNull;

import java.util.List;

import runningtracker.data.model.User;

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
}
