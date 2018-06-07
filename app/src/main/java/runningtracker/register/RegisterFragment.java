package runningtracker.register;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import runningtracker.NavigationHost;
import runningtracker.R;
import runningtracker.registerinfomation.RegisterInformationFragment;

public class RegisterFragment extends Fragment {
    @BindView(R.id.email_edit_text)
    EditText emailEditText;
    @BindView(R.id.password_edit_text)
    EditText passwordEditText;
    @BindView(R.id.password_text_input)
    TextInputLayout passwordTextInput;
    @BindView(R.id.email_text_input)
    TextInputLayout emailTextInput;
    private static final String TAG = "EmailPassword";
    FirebaseAuth mAuth;
    FirebaseFirestore db;


    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.bind(this, view);
        initFirebase();
        setUpToolbar();
        disableError();
        return view;
    }

    private void setUpToolbar() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });
    }

    private boolean isPasswordValid(@Nullable EditText text) {
        return text != null && text.length() >= 8;
    }

    private boolean isEmailValid(@Nullable EditText emailEditText) {
        assert emailEditText != null;
        String email = emailEditText.getText().toString();
        boolean isValid = false;
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    @OnClick(R.id.buttonRegister)
    public void startSignUpActivity() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if (validateForm()) {
            // [START create_user_with_email]
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                final FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                assert firebaseUser != null;
                                startRegisterInformation();

                                //update user auth infomation
//                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
//                                    .setDisplayName(displayName)
//                                    .setPhotoUri(Uri.parse("https://firebasestorage.googleapis.com/v0/b/running-assistant-1133.appspot.com/o/boy.png"))
//                                    .build();
//
//                            firebaseUser.updateProfile(profileUpdates)
//                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            if (task.isSuccessful()) {
//                                                Log.d(TAG, "User profile updated.");
//                                                Map<String, Object> userMap = new HashMap<>();
//                                                userMap.put("displayName", firebaseUser.getDisplayName());
//                                                userMap.put("uid", firebaseUser.getUid());
//                                                userMap.put("photoUrl", firebaseUser.getPhotoUrl().toString());
//                                                userMap.put("email", firebaseUser.getEmail());
//
//                                                db.collection("users").document(firebaseUser.getUid())
//                                                        .set(userMap)
//                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                            @Override
//                                                            public void onSuccess(Void aVoid) {
//
//                                                                Map<String, Object> userMap = new HashMap<>();
//                                                                userMap.put("birthday", birthday);
//                                                                userMap.put("height", height);
//                                                                userMap.put("weight", weight);
//                                                                userMap.put("heartRate", heartRate);
//                                                                db.collection("usersData").document(firebaseUser.getUid())
//                                                                        .set(userMap)
//                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                                            @Override
//                                                                            public void onSuccess(Void aVoid) {
//                                                                                Log.d(TAG, "User data writed.");
//                                                                            }
//                                                                        });
//                                                            }
//                                                        });
//                                            }
//                                        }
//                                    });
                                // mRegisterView.makeToast("Authentication success.");

                                //  mRegisterView.startHome();
                            } else {
                                //   Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                //  mRegisterView.makeToast("Authentication failed.");
                            }

                            // [START_EXCLUDE]
                            //   mRegisterView.hideProgressDialog();
                            // [END_EXCLUDE]
                        }
                    });
            // [END create_user_with_email]
        }
        //  ((NavigationHost) Objects.requireNonNull(getActivity())).navigateTo(new RegisterInformationFragment(),true);
    }

    private void startRegisterInformation() {
        ((NavigationHost) getActivity()).navigateTo(new RegisterInformationFragment(), true);
    }

    public boolean validateForm() {
        boolean valid = true;
        String email = emailEditText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailTextInput.setError("Bắt buộc.");
            valid = false;
        } else if (!isEmailValid(emailEditText)) {
            emailTextInput.setError("Email không đúng.");
            valid = false;
        } else {
            emailTextInput.setError(null);
        }

        String password = passwordEditText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordTextInput.setError("Bắt buộc.");
            valid = false;
        } else if (!isPasswordValid(passwordEditText)) {
            passwordTextInput.setError("Mật khẩu phải dài hơn 8 ký tự.");
            valid = false;
        } else {
            passwordTextInput.setError(null);
        }
        return valid;
    }

    private void disableError() {

        emailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    emailTextInput.setErrorEnabled(false);
                }
            }
        });

        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    passwordTextInput.setErrorEnabled(false);
                }
            }
        });
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }
}
