package runningtracker.friendslist;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import runningtracker.R;
import runningtracker.data.model.Friend;


public class FriendsListFragment extends Fragment {
    @BindView(R.id.friends_recycler_view)
    RecyclerView mRecyclerView;


    private static final String TAG = "Friends";
    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private FirestoreRecyclerAdapter firestoreRecyclerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friends_list, container, false);
        ButterKnife.bind(this, view);
        showFriendsList();
        return view;
    }

    public void showFriendsList() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        //FirebaseUser currentUser = mAuth.getCurrentUser();


        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        //  mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        // mLayoutManager = new LinearLayoutManager(getActivity());
        //  mRecyclerView.setLayoutManager(mLayoutManager);


        Query query = db.collection("users")
                .document(Objects.requireNonNull(mAuth.getUid()))
                .collection("friends");

        FirestoreRecyclerOptions<Friend> options = new FirestoreRecyclerOptions.Builder<Friend>()
                .setQuery(query, Friend.class)
                .build();

        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<Friend, FriendsHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendsHolder holder, int position, @NonNull Friend friend) {
                holder.displayNameTextView.setText(friend.getDisplayName());
                holder.emailTextView.setText(friend.getEmail());
            }


            @NonNull
            @Override
            public FriendsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_friend, parent, false);

                return new FriendsHolder(view);
            }
        };
        // specify an adapter (see also next example)
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(firestoreRecyclerAdapter);

    }

    public class FriendsHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.displayNameTextView)
        TextView displayNameTextView;
        @BindView(R.id.userImg)
        ImageView userImg;
        @BindView(R.id.emailTextView)
        TextView emailTextView;

        public FriendsHolder(View itemView) {
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
