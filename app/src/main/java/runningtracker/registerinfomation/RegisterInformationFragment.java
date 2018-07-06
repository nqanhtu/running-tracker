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
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import runningtracker.NavigationHost;
import runningtracker.R;
import runningtracker.home.HomeActivity;
import runningtracker.profile.ProfileFragment;

import static android.app.Activity.RESULT_OK;

public class RegisterInformationFragment extends DialogFragment {
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
    Map<String, Object> userMap;
    Map<String, Object> userDataMap;

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseStorage mStorage;
    StorageReference mStorageRef;
    Uri uri;
    private DatePickerDialog mDatePickerDialog;
    private final static String TAG = "register information";

    String oldUsername;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_information, container, false);
        ButterKnife.bind(this, view);
        setUpToolbar();
        setDateTimeField();
        setUpSpinner();
//        loadUserInformation();
//        ((NavigationHost) Objects.requireNonNull(getActivity())).enableBottomNav(false);
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
        userMap = new HashMap<>();
        userDataMap = new HashMap<>();
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
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                getActivity().finish();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    @OnClick(R.id.register_button)
    public void registerUser() {
        if (validateForm()) {
            showProgressDialog();
            updateInformation();
        } else hideProgressDialog();

    }

    private void loadUserInformation() {


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
                                    userMap = task.getResult().getData();
                                    if (userMap.containsKey("username"))
                                        usernameEditText.setText(userMap.get("username").toString());
                                    else userMap.put("username", "");
                                    if (userMap.containsKey("displayName"))
                                        nameEditText.setText(userMap.get("displayName").toString());
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
                                    userDataMap = task.getResult().getData();
                                    if (userDataMap.containsKey("height"))
                                        heightEditText.setText(userDataMap.get("height").toString());
                                    if (userDataMap.containsKey("weight"))
                                        weightEditText.setText(userDataMap.get("weight").toString());
                                    if (userDataMap.containsKey("birthday"))
                                        birthdayEditText.setText(userDataMap.get("birthday").toString());
                                    if (userDataMap.containsKey("heartRate"))
                                        heartRateEditText.setText(userDataMap.get("heartRate").toString());
                                }
                            }
                        }
                    });

        }

    }

    private void updateInformation() {
        db.collection("users")
                .whereEqualTo("username", usernameEditText.getText().toString()).limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        List<DocumentSnapshot> doc = queryDocumentSnapshots.getDocuments();

                        boolean flag = true;
                        if (doc.size() > 0) {
                            if (!doc.get(0).getData().get("username").toString().equals(userMap.get("username").toString())) {
                                usernameEditText.setError("Đã tồn tại");
                                flag = false;
                                hideProgressDialog();
                            }
                        }

                        if (flag) {
                            userMap.put("username", usernameEditText.getText().toString());
                            userMap.put("displayName", nameEditText.getText().toString());
                            db.collection("users").document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).set(userMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                userDataMap.put("heartRate", Double.valueOf(heartRateEditText.getText().toString()));
                                                userDataMap.put("weight", Double.valueOf(weightEditText.getText().toString()));
                                                userDataMap.put("height", Double.valueOf(heightEditText.getText().toString()));
                                                userDataMap.put("birthday", birthdayEditText.getText().toString());
                                                userDataMap.put("sex", gioiTinhSpinner.getSelectedItem().toString());
                                                db.collection("usersData").document(mAuth.getCurrentUser().getUid()).set(userDataMap)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    hideProgressDialog();
                                                                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                                                                    startActivity(intent);
                                                                } else {
                                                                    Log.d(TAG, task.getException().toString());
                                                                }
                                                            }
                                                        });
                                            } else {
                                                Log.d(TAG, task.getException().toString());
                                            }
                                        }
                                    });
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


    public boolean validateForm() {
        final boolean[] valid = {true};
        String username = usernameEditText.getText().toString();
        String name = nameEditText.getText().toString();
        String height = (heightEditText.getText().toString());
        String weight = (weightEditText.getText().toString());
        String heartRate = (heartRateEditText.getText().toString());
        String birthday = birthdayEditText.getText().toString();


        if (TextUtils.isEmpty(username)) {
            usernameLayout.setError("Bắt buộc.");
            valid[0] = false;
        }


        if (TextUtils.isEmpty(name)) {
            nameLayout.setError("Bắt buộc.");
            valid[0] = false;
        }

        if (TextUtils.isEmpty(height)) {
            heightEditText.setError("Bắt buộc.");
            valid[0] = false;
        }
        if (TextUtils.isEmpty(weight)) {
            weightEditText.setError("Bắt buộc.");
            valid[0] = false;
        }
        if (TextUtils.isEmpty(birthday)) {
            birthdayEditText.setError("Bắt buộc.");
            valid[0] = false;
        }


        return valid[0];
    }


//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        ((NavigationHost) getActivity()).enableBottomNav(true);
//    }

    private void loadAvatar(Uri uri) {
        Glide.with(Objects.requireNonNull(getContext()))
                .load(uri)
                .into(profileImageView);
    }
}