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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import runningtracker.R;
import runningtracker.common.InitializationFirebase;
import runningtracker.data.model.Friend;
import runningtracker.data.model.User;
import runningtracker.data.repository.UsersRepository;


public class FriendRequestsFragment extends Fragment {
    private static final String TAG = "FriendRequests";
    @BindView(R.id.friend_requests_recycler_view)
    RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    FirestoreRecyclerAdapter firestoreRecyclerAdapter;
    private Dialog mDialog;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_requests, container, false);
        ButterKnife.bind(this, view);
        init();
        showFriendsList();
        return view;
    }

    private void init() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    public void showFriendsList() {
        Query query = db.collection("users")
                .document(Objects.requireNonNull(currentUser.getUid()))
                .collection("friendRequests");

        FirestoreRecyclerOptions<Friend> options = new FirestoreRecyclerOptions.Builder<Friend>()
                .setQuery(query, Friend.class)
                .build();

        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<Friend, FriendsHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendsHolder holder, int position, @NonNull Friend friend) {
                holder.displayNameTextView.setText(friend.getDisplayName());
                holder.usernameTextView.setText(friend.getUsername());
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
                        dialogEmail.setText(friend.getUsername());


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

                                db.collection("users").document(currentUser.getUid()).get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                Friend user = documentSnapshot.toObject(Friend.class);
                                                assert user != null;
                                                db.collection("users").document(friend.getUid())
                                                        .collection("friends").document(currentUser.getUid()).set(user);
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
                                mDialog.hide();
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
                                mDialog.hide();

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
        @BindView(R.id.display_name_text_view)
        TextView displayNameTextView;
        @BindView(R.id.user_image_view)
        ImageView userImg;
        @BindView(R.id.username_text_view)
        TextView usernameTextView;
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
