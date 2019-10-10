package com.runningtracker.friendrequests;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
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

                holder.friend = friend;
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
                                Map<String, Object> friendMap = new HashMap<>();
                                friendMap.put("friend", friend.getFriend());
                                db.collection("users").document(currentUser.getUid())
                                        .collection("friends").document(friend.getFriend().getId()).set(friendMap)
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
                                                Map<String, Object> userFriendMap = new HashMap<>();
                                                userFriendMap.put("friend", documentSnapshot.getReference());
                                                db.collection("users").document(friend.getFriend().getId())
                                                        .collection("friends").document(currentUser.getUid()).set(userFriendMap);
                                            }
                                        });

                                //////////////////////////
                                db.collection("users").document(currentUser.getUid())
                                        .collection("friendRequests").document(friend.getFriend().getId()).delete()
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

                                db.collection("users").document(friend.getFriend().getId())
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
                                        .collection("friendRequests").document(friend.getFriend().getId()).delete()
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
        CircleImageView userImg;
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

    private void loadAvatar(Uri uri, ImageView avatarImageView) {
        Glide.with(Objects.requireNonNull(getContext()))
                .load(uri)
                .into(avatarImageView);
    }
}
