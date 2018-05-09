package runningtracker.friendrequests;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import runningtracker.Adapter.FriendRequestsAdapter;
import runningtracker.R;
import runningtracker.addfriend.AddFriendFragment;
import runningtracker.common.InitializationFirebase;
import runningtracker.data.model.Friend;
import runningtracker.data.model.User;
import runningtracker.data.repository.UsersRepository;


public class FriendRequestsFragment extends Fragment implements FriendRequestsContract.View {
    private static final String TAG = "FriendRequests";
    @BindView(R.id.friend_requests_recycler_view)
    RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private FriendRequestsPresenter mFriendRequestsPresenter;
    private InitializationFirebase initializationFirebase;
    FirestoreRecyclerAdapter firestoreRecyclerAdapter;
    private Dialog mDialog;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    User currentUser ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_requests, container, false);
        ButterKnife.bind(this, view);

        mFriendRequestsPresenter = new FriendRequestsPresenter(UsersRepository.getInstance(db), this);
       /// mFriendRequestsPresenter.start();
        Log.d(TAG,"Co chay vao day 2");
        init();
        showFriendsList();
        return view;
    }


    private void init() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = new User(mAuth.getCurrentUser());
    }

    public void showFriendsList() {
        Query query = db.collection("users")
                .document(Objects.requireNonNull(mAuth.getUid()))
                .collection("friendRequests");

        FirestoreRecyclerOptions<Friend> options = new FirestoreRecyclerOptions.Builder<Friend>()
                .setQuery(query, Friend.class)
                .build();
        Log.d(TAG,"Co chay vao day");

        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<Friend, FriendsHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendsHolder holder, int position, @NonNull Friend friend) {
                holder.displayNameTextView.setText(friend.getDisplayName());
                holder.emailTextView.setText(friend.getEmail());
                holder.friend = friend;
            }


            @NonNull
            @Override
            public FriendsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_friend, parent, false);

                final FriendsHolder viewHolder = new FriendsHolder(view);

                mDialog = new Dialog(getContext());
                mDialog.setContentView(R.layout.dialog);
                Objects.requireNonNull(mDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                viewHolder.item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        TextView dialogName = mDialog.findViewById(R.id.name_textview);
                        TextView dialogEmail = mDialog.findViewById(R.id.email_textview);
                        Button buttonAccept = mDialog.findViewById(R.id.accept_button);
                        Button buttonReject = mDialog.findViewById(R.id.reject_button);


                        final Friend friend = viewHolder.friend;
                        dialogName.setText(friend.getDisplayName());
                        dialogEmail.setText(friend.getEmail());


                        mDialog.show();
                        buttonAccept.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                db.collection("users").document(currentUser.getUid())
                                        .collection("friends").document(friend.getUid()).set(friend)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getContext(), "Đã đồng ý lời mời kết bạn", Toast.LENGTH_SHORT).show();

                                            }
                                        });

                                db.collection("users").document(friend.getUid())
                                        .collection("friends").document(currentUser.getUid()).set(currentUser)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                Log.d(TAG, "DocumentSnapshot successfully writed!");
                                            }
                                        });

                                db.collection("users").document(currentUser.getUid())
                                        .collection("friendRequests").document(friend.getUid()).delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error deleting document", e);
                                            }
                                        });

                                db.collection("users").document(friend.getUid())
                                        .collection("friendRequestsSent").document(currentUser.getUid()).delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error deleting document", e);
                                            }
                                        });

                            }
                        });
                        buttonReject.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                db.collection("users").document(currentUser.getUid())
                                        .collection("friendRequests").document(friend.getUid()).delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                                Toast.makeText(getContext(), "Đã hủy lời mời kết bạn", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error deleting document", e);
                                            }
                                        });

                            }
                        });


                    }
                });
                return viewHolder;
            }
        };
        // specify an adapter (see also next example)
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(firestoreRecyclerAdapter);

    }

    public class FriendsHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_friend)
        ConstraintLayout item;
        @BindView(R.id.displayNameTextView)
        TextView displayNameTextView;
        @BindView(R.id.userImg)
        ImageView userImg;
        @BindView(R.id.emailTextView)
        TextView emailTextView;

        Friend friend;

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
