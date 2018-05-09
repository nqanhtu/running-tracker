package runningtracker.addfriend;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import runningtracker.R;
import runningtracker.common.InitializationFirebase;
import runningtracker.data.model.Friend;
import runningtracker.data.repository.UsersRepository;
import runningtracker.friendslist.FriendsListFragment;


public class AddFriendFragment extends Fragment implements AddFriendContract.View {
    private static final String TAG = "AddFriends";
    @BindView(R.id.add_friends_recycler_view)
    RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private AddFriendPresenter mAddFriendPresenter;
    private InitializationFirebase initializationFirebase;
    FirestoreRecyclerAdapter firestoreRecyclerAdapter;
    @BindView(R.id.friend_email_edit_text)
    EditText friendEmailEditText;
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_friend, container, false);
        ButterKnife.bind(this, view);

            mAddFriendPresenter = new AddFriendPresenter(UsersRepository.getInstance(db), this);
            mAddFriendPresenter.start();
        init();
        showFriendsList();

        return view;
    }

    @OnClick(R.id.add_friend_button)
    public void addFriend() {
        mAddFriendPresenter.addFriend(friendEmailEditText.getText().toString());
        Toast.makeText(getContext(), "Đã gửi lời mời kết bạn", Toast.LENGTH_SHORT).show();
        friendEmailEditText.setText("");
    }


    private void init() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    public void showFriendsList() {
        Query query = db.collection("users")
                .document(Objects.requireNonNull(mAuth.getUid()))
                .collection("friendRequestsSent");

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
