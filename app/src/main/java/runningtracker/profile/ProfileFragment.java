package runningtracker.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;

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


public class ProfileFragment extends Fragment implements ProfileContract.View {
    private ProfilePresenter presenter;
    FirebaseFirestore firestore;
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
    @BindView(R.id.name_textview)
    TextView nameTextView;
    private final static String TAG = "ProfileFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFirebase();
        myFragment = this;
        loadUserInfo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        firestore = FirebaseFirestore.getInstance();
        mCurrentId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        presenter = new ProfilePresenter(UsersRepository.getInstance(firestore), this);

        firestore.collection("users").document(mCurrentId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    currentUser = task.getResult().toObject(User.class);
                }
            }
        });
        // ((NavigationHost) getActivity()).enableBottomNav(true);

        return view;
    }

    @OnClick(R.id.button_logout)
    public void logout() {
        presenter.logout();
    }

    @Override
    public void showLogin() {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
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
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    void loadUserInfo() {
        final StorageReference filePath = mStorageRef.child("Photos").child(mAuth.getCurrentUser().getUid());
        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                loadAvatar(uri);
            }
        });


        db.collection("users").document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {

                            Map<String, Object> user = task.getResult().getData();

                            String username = user.get("username").toString();
                            String name = user.get("displayName").toString();
                            usernameTextView.setText(username);
                            nameTextView.setText(name);

                        }
                    }
                });

    }

    public void loadAvatar(Uri uri) {
        Glide
                .with(this)
                .load(uri)
                .into(avatarImageView);
    }

}

