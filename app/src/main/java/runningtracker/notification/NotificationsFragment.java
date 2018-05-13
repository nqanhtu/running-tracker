package runningtracker.notification;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import runningtracker.R;
import runningtracker.addfriend.AddFriendFragment;
import runningtracker.data.model.Friend;
import runningtracker.data.model.Notification;

public class NotificationsFragment extends Fragment {
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    FirebaseUser user;
    FirestoreRecyclerAdapter firestoreRecyclerAdapter;
    private LinearLayoutManager mLayoutManager;
    @BindView(R.id.notiRecyclerView)
    RecyclerView mRecyclerView;
    private static final String TAG = "Notification";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        ButterKnife.bind(this, view);
        init();
        showNotifications();
        return view;
    }

    private void init() {
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();


        firestore.collection("users")
                .document(Objects.requireNonNull(auth.getUid()))
                .collection("notifications").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots
                        ) {
                    Log.d(TAG, documentSnapshot.getData().toString());
                }
            }
        });
    }

    public void showNotifications() {

        Query query = firestore.collection("users")
                .document(Objects.requireNonNull(auth.getUid()))
                .collection("notifications");

        FirestoreRecyclerOptions<Notification> options = new FirestoreRecyclerOptions.Builder<Notification>()
                .setQuery(query, Notification.class)
                .build();

        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<Notification, NotificationHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull NotificationHolder holder, int position, @NonNull Notification notification) {
                holder.fromName.setText(notification.getFromName());
                holder.message.setText(notification.getMessage());
            }

            @NonNull
            @Override
            public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_notification, parent, false);

                return new NotificationHolder(view);
            }
        };
        // specify an adapter (see also next example)
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(firestoreRecyclerAdapter);
    }

    public class NotificationHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.fromNameTextView)
        TextView fromName;
        @BindView(R.id.userImg)
        ImageView userImg;
        @BindView(R.id.messageTextView)
        TextView message;

        public NotificationHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        firestoreRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        firestoreRecyclerAdapter.stopListening();
    }
}
