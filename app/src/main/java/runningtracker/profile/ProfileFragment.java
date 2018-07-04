package runningtracker.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import runningtracker.NavigationHost;
import runningtracker.R;
import runningtracker.data.model.User;
import runningtracker.data.repository.UsersRepository;
import runningtracker.home.HomeActivity;
import runningtracker.registerinfomation.RegisterInformationFragment;


public class ProfileFragment extends Fragment {
    //    @BindView(R.id.editTextMessage)
//    EditText text;
    private String mUserId;
    private String mCurrentId;
    private User currentUser;
    ProfileFragment myFragment;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseStorage mStorage;
    StorageReference mStorageRef;
    @BindView(R.id.avatar_image_view)
    CircleImageView avatarImageView;
    @BindView(R.id.username_text_view)
    TextView usernameTextView;
    @BindView(R.id.height_text_view)
    TextView heightTextView;
    @BindView(R.id.weight_text_view)
    TextView weightTextView;
    @BindView(R.id.heart_rate_text_view)
    TextView heartRateTextView;
    @BindView(R.id.name_text_view)
    TextView nameTextView;
    @BindView(R.id.email_text_view)
    TextView emailTextView;
    private final static String TAG = "ProfileFragment";
    Uri filepath;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            Log.d(TAG, "save instance fragment");

        }

        if (savedInstanceState == null) {
            Log.d(TAG, "save instance fragment null");

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        initFirebase();
        loadUserInfo();
        db.collection("users").document(mCurrentId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    currentUser = task.getResult().toObject(User.class);
                }
            }
        });

        return view;
    }

    @OnClick(R.id.button_logout)
    public void logout() {
        FirebaseAuth.getInstance().signOut();
        showLogin();
    }

    public void showLogin() {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        getActivity().finish();
        startActivity(intent);
    }

    @OnClick(R.id.edit_info_button)
    public void editInfo() {
        ((NavigationHost) getActivity()).navigateTo(new RegisterInformationFragment(), true);
        ((NavigationHost) getActivity()).enableBottomNav(false);
        //showDialog();
    }

    private void initFirebase() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mCurrentId = mAuth.getCurrentUser().getUid();
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    void loadUserInfo() {
        StorageReference storageReference = mStorageRef.child("Photos").child(mAuth.getCurrentUser().getUid());
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d(TAG, uri.toString());
                loadAvatar(uri);
            }
        });
//        String email = mAuth.getCurrentUser().getEmail();
//        emailTextView.setText(email);
        db.collection("users").document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().getData() != null) {
                                Map<String, Object> user = task.getResult().getData();
                                String username = "";
                                String name = "";
                                if (user.get("username") != null)
                                    username = user.get("username").toString();
                                if (user.get("displayName") != null)
                                    name = user.get("displayName").toString();
                                usernameTextView.setText(username);
                                nameTextView.setText(name);
                            }
                        }
                    }
                });

        db.collection("usersData").document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().getData() != null) {
                                Map<String, Object> user = task.getResult().getData();
                                String height = "";
                                String weight = "";
                                String heartRate = "";
                                String email = "";
                                if (user.get("email") != null) email = user.get("email").toString();
                                if (user.get("height") != null)
                                    height = user.get("height").toString();
                                if (user.get("weight") != null)
                                    weight = user.get("weight").toString();
                                if (user.get("heartRate") != null)
                                    heartRate = user.get("heartRate").toString();


                                heightTextView.setText(height);
                                weightTextView.setText(weight);
                                heartRateTextView.setText(heartRate);
                                emailTextView.setText(email);
                            }
                        }
                    }
                });
    }

    private void loadAvatar(Uri uri) {
        Glide.with(Objects.requireNonNull(getContext()))
                .load(uri)
                .into(avatarImageView);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);


    }
}

