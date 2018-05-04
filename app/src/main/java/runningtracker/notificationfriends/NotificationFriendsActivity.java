package runningtracker.notificationfriends;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.CheckedTextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import runningtracker.Adapter.NotificationFriendsAdapter;
import runningtracker.R;
import runningtracker.data.model.Friend;

public class NotificationFriendsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    NotificationFriendsAdapter notificationFriendsAdapter;
    List<Friend> friends;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_friends);
        ButterKnife.bind(this);


        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // specify an adapter (see also next example)
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        int n = 0;
        db.collection("users").document(currentUser.getUid())
                .collection("friends")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {


                        if (task.isSuccessful()) {
                            friends = task.getResult().toObjects(Friend.class);
                            notificationFriendsAdapter = new NotificationFriendsAdapter(friends);
                            mRecyclerView.setAdapter(notificationFriendsAdapter);
                        } else {
                            Log.d("ABC", "Error getting documents: ", task.getException());
                        }
                    }
                });


    }

    @OnClick(R.id.doneButton)
    public void update() {
        List<Friend> friends = notificationFriendsAdapter.getUpdatedFriends();
        Log.d("ABC", String.valueOf(friends.size()));
        for (Friend friend : friends
                ) {
            db.collection("users").document(currentUser.getUid())
                    .collection("friends").document(friend.getUid()).update("notify", friend.isNotify())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("ABC", "DocumentSnapshot successfully updated!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("ABC", "Error updating document", e);
                        }
                    });
        }
    }
}
