package runningtracker.ui.friendrequests;

import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Created by Anh Tu on 3/10/2018.
 */

public class FriendRequestsPresenter implements FriendRequestsContract.Presenter {

    private static final String TAG = "AddFriends";
    private final FirebaseFirestore db;
    private final FriendRequestsContract.View mFriendRequestsView;
    private final String stt = "haha";

    public FriendRequestsPresenter(FirebaseFirestore db, FriendRequestsContract.View mFriendRequestsView) {
        this.db = db;
        this.mFriendRequestsView = mFriendRequestsView;
    }


    @Override
    public void start() {

    }


}
