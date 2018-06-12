package runningtracker.registerinfomation;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import runningtracker.NavigationHost;
import runningtracker.R;
import runningtracker.dashboard.DashboardFragment;
import runningtracker.profile.ProfileFragment;

import static android.app.Activity.RESULT_OK;

public class RegisterInformationFragment extends Fragment {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private final int PICK_IMAGE_REQUEST = 71;
    @BindView(R.id.profile_image_view)
    CircleImageView profileImageView;
    @BindView(R.id.name_layout)
    TextInputLayout nameLayout;
    @BindView(R.id.username_layout)
    TextInputLayout usernameLayout;
    @BindView(R.id.name_edit_text)
    TextInputEditText nameEditText;
    @BindView(R.id.username_edit_text)
    TextInputEditText usernameEditText;
    @BindView(R.id.heart_rate_edit_text)
    TextInputEditText heartRateEditText;
    @BindView(R.id.height_edit_text)
    TextInputEditText heightEditText;
    @BindView(R.id.weight_edit_text)
    TextInputEditText weightEditText;
    @BindView(R.id.birthday_edit_text)
    TextInputEditText birthdayEditText;
    @BindView(R.id.gioi_tinh_spinner)
    Spinner gioiTinhSpinner;


    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseStorage mStorage;
    StorageReference mStorageRef;
    String email;
    String password;
    Uri uri;
    private DatePickerDialog mDatePickerDialog;
    private final static String TAG = "register information";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_information, container, false);
        ButterKnife.bind(this, view);
        setUpToolbar();
        setDateTimeField();
        setUpSpinner();
        loadUserInformation();
        ((NavigationHost) getActivity()).enableBottomNav(false);
        return view;
    }

    private void setUpSpinner() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.gioi_tinh, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        gioiTinhSpinner.setAdapter(adapter);
        gioiTinhSpinner.setSelection(0);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFirebase();

    }

    private void initFirebase() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    @OnClick(R.id.open_image_button)
    public void openImage() {
        chooseImage();
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            //  Picasso.get().load(filepath).resize(150,150).centerCrop().into(profileImageView);

            StorageReference filePath = mStorageRef.child("Photos").child(mAuth.getCurrentUser().getUid());
            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getActivity(), "Tải ảnh lên thành công", Toast.LENGTH_SHORT).show();
                }
            });


            Glide
                    .with(this)
                    .load(uri)
                    .into(profileImageView);

        }
    }

    private void setUpToolbar() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });
    }

    @OnClick(R.id.register_button)
    public void registerUser() {
        ((NavigationHost) getActivity()).hideSoftKeyboard();
        showProgressDialog();
        updateInformation();

    }

    void loadUserInformation() {
        if (mAuth.getCurrentUser() != null) {
            StorageReference storageReference = mStorageRef.child("Photos").child(mAuth.getCurrentUser().getUid());
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Log.d(TAG, uri.toString());
                    loadAvatar(uri);
                }
            });
            birthdayEditText.setKeyListener(null);

            db.collection("users").document(mAuth.getCurrentUser().getUid()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().getData() != null) {

                                    Map<String, Object> user = task.getResult().getData();
                                    if (user.get("username") != null)
                                        usernameEditText.setText(user.get("username").toString());
                                    if (user.get("displayName") != null)
                                        nameEditText.setText(user.get("displayName").toString());

                                }

                            }
                        }
                    });


            db.collection("usersData").document(mAuth.getCurrentUser().getUid()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().getData() != null) {
                                    Map<String, Object> userData = task.getResult().getData();
                                    heightEditText.setText(userData.get("height").toString());
                                    weightEditText.setText(userData.get("weight").toString());
                                    birthdayEditText.setText(userData.get("birthday").toString());
                                    heartRateEditText.setText(userData.get("heartRate").toString());
                                }
                            }
                        }
                    });


        }

    }

    private void updateInformation() {

        db.collection("users")
                .whereEqualTo("username", usernameEditText.getText().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().getDocuments().size() > 0) {
                                usernameLayout.setError("Tên tài khoản đã tồn tại");
                            } else {
                                String uid = mAuth.getCurrentUser().getUid();
                                Map<String, Object> userInfo = new HashMap<>();
                                userInfo.put("username", usernameEditText.getText().toString());
                                userInfo.put("displayName", nameEditText.getText().toString());
                                db.collection("users").document(uid).set(userInfo)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Map<String, Object> userInfo = new HashMap<>();
                                                    userInfo.put("heartRate", heartRateEditText.getText().toString());
                                                    userInfo.put("weight", weightEditText.getText().toString());
                                                    userInfo.put("height", heightEditText.getText().toString());
                                                    userInfo.put("birthday", birthdayEditText.getText().toString());
                                                    userInfo.put("sex", gioiTinhSpinner.getSelectedItem().toString());
                                                    db.collection("usersData").document(mAuth.getCurrentUser().getUid()).set(userInfo)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        hideProgressDialog();
                                                                        ((NavigationHost) getActivity()).navigateTo(new ProfileFragment(), false);
                                                                        ((NavigationHost) getActivity()).setSelectedItem();
                                                                    }
                                                                }
                                                            });
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });


    }

    private void setDateTimeField() {
        birthdayEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                mDatePickerDialog.show();
                return false;
            }
        });
        Calendar newCalendar = Calendar.getInstance();
        mDatePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat sd = new SimpleDateFormat("dd-MM-yyyy");
                final Date startDate = newDate.getTime();
                String fdate = sd.format(startDate);

                birthdayEditText.setText(fdate);

            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        mDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

    }


    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((NavigationHost) getActivity()).enableBottomNav(true);

    }

    private void loadAvatar(Uri uri) {
        Glide.with(Objects.requireNonNull(getContext()))
                .load(uri)
                .into(profileImageView);
    }
}