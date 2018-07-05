package runningtracker.friendslist;

import android.net.Uri;
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

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import runningtracker.R;
import runningtracker.data.model.Friend;


public class FriendsListFragment extends Fragment {
    @BindView(R.id.friends_recycler_view)
    RecyclerView mRecyclerView;
    FirebaseStorage mStorage;
    StorageReference mStorageRef;

    private static final String TAG = "FriendsABC";
    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private RecyclerView.LayoutManager mLayoutManager;

    private FirestoreRecyclerAdapter firestoreRecyclerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friends_list, container, false);
        ButterKnife.bind(this, view);
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        showFriendsList();
        return view;
    }

    public void showFriendsList() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        Query query = db.collection("users")
                .document(Objects.requireNonNull(mAuth.getUid()))
                .collection("friends");

        FirestoreRecyclerOptions<Friend> options = new FirestoreRecyclerOptions.Builder<Friend>()
                .setQuery(query, Friend.class)
                .build();

        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<Friend, FriendsHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FriendsHolder holder, int position, @NonNull Friend friend) {


                friend.getFriend().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Log.d(TAG, documentSnapshot.getData().toString());
                        holder.displayNameTextView.setText(documentSnapshot.getData().get("displayName").toString());
                        holder.usernameTextView.setText(documentSnapshot.getData().get("username").toString());
                    }
                });
//                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Photos").child(friend.getUid());
//                storageReference.getDownloadUrl()
//                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
//                            @Override
//                            public void onSuccess(Uri uri) {
//                                Log.d(TAG, uri.toString());
//                                loadAvatar(uri, holder.userImg);
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.d(TAG, "Load image fail");
//                            }
//                        });
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

    private void loadAvatar(Uri uri, ImageView avatarImageView) {
        Glide.with(Objects.requireNonNull(getContext()))
                .load(uri)
                .into(avatarImageView);
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
}
