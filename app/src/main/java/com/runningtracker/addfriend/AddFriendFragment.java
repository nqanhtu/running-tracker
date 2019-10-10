package com.runningtracker.addfriend;

import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import runningtracker.R;

import com.runningtracker.data.model.Friend;


public class AddFriendFragment extends Fragment {
    private static final String TAG = "AddFriends";
    @BindView(R.id.add_friends_recycler_view)
    RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    FirestoreRecyclerAdapter firestoreRecyclerAdapter;
    @BindView(R.id.username_text_view)
    EditText usernameEditText;
    @BindView(R.id.add_friend_button)
    Button addFriendButton;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    Map<String, Object> currentUser;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_friend, container, false);
        ButterKnife.bind(this, view);
        init();
        showFriendsList();
        return view;
    }


    public void addFriend(final Map<String, Object> newFriend) {

        if (usernameEditText.getText().toString().equals(currentUser.get("username"))) {
            Toast.makeText(getContext(), "Không thể kết bạn với chính bạn", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users").whereEqualTo("username", usernameEditText.getText().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult().size() > 0) {
                            DocumentReference friendRef = task.getResult().getDocuments().get(0).getReference();

                            Log.d(TAG, friendRef.getId());


                            Map<String, Object> friend = new HashMap<>();
                            friend.put("friend", friendRef);

                            db.collection("users").document(mAuth.getCurrentUser().getUid())
                                    .collection("friendRequestsSent").document(friendRef.getId()).set(friend);

                            db.collection("users").document(friendRef.getId())
                                    .collection("friendRequests").document(mAuth.getCurrentUser().getUid()).set(newFriend);

                            Toast.makeText(getContext(), "Đã gửi lời mời kết bạn", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(getContext(), "Người dùng không tồn tại", Toast.LENGTH_SHORT).show();
                    }

                });
        //   usernameEditText.setText("");
    }

    private void init() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        db.collection("users").document(mAuth.getCurrentUser().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            currentUser = task.getResult().getData();
                            final HashMap<String, Object> newFriend = new HashMap<>();
                            newFriend.put("friend", task.getResult().getReference());
                            addFriendButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    addFriend(newFriend);
                                }
                            });
                        }
                    }
                });
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
            protected void onBindViewHolder(@NonNull final FriendsHolder holder, int position, @NonNull Friend friend) {

                if (friend.getFriend() != null) {
                    friend.getFriend().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Log.d(TAG, documentSnapshot.getData().toString());
                            holder.displayNameTextView.setText(documentSnapshot.getData().get("displayName").toString());
                            holder.usernameTextView.setText(documentSnapshot.getData().get("username").toString());
                        }
                    });
                }
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Photos").child(friend.getFriend().getId());
                storageReference.getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.d(TAG, uri.toString());
                                loadAvatar(uri, holder.userImg);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "Load image fail");
                            }
                        });
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

        @BindView(R.id.display_name_text_view)
        TextView displayNameTextView;
        @BindView(R.id.user_image_view)
        CircleImageView userImg;
        @BindView(R.id.username_text_view)
        TextView usernameTextView;

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

    private void loadAvatar(Uri uri, ImageView avatarImageView) {
        Glide.with(Objects.requireNonNull(getContext()))
                .load(uri)
                .into(avatarImageView);
    }
}
