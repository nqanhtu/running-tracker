package runningtracker.register;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import runningtracker.addfriend.AddFriendContract;
import runningtracker.common.MyFirestore;
import runningtracker.data.model.User;
import runningtracker.data.repository.UsersRepository;

/**
 * Created by Anh Tu on 3/21/2018.
 */

public class RegisterPresenter implements RegisterContract.Presenter {

    private static final String TAG = "RegisterUser";
    private final UsersRepository mUsersRepository;
    private final RegisterContract.View mRegisterView;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;


    public RegisterPresenter(UsersRepository mUsersRepository, RegisterContract.View mRegisterView) {
        this.mUsersRepository = mUsersRepository;
        this.mRegisterView = mRegisterView;
    }

    @Override
    public void start() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    @Override
    public void createAccount(final String displayName, final String email, String password, final String birthday, final double height, final double weight, final double heartRate) {
        Log.d(TAG, "createAccount:" + email);
        if (!mRegisterView.validateForm()) {
            return;
        }

        mRegisterView.showProgressDialog();

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

                            //update user auth infomation
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(displayName)
                                    .setPhotoUri(Uri.parse("https://firebasestorage.googleapis.com/v0/b/running-assistant-1133.appspot.com/o/boy.png"))
                                    .build();

                            firebaseUser.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "User profile updated.");
                                                Map<String, Object> userMap = new HashMap<>();
                                                userMap.put("displayName", firebaseUser.getDisplayName());
                                                userMap.put("uid", firebaseUser.getUid());
                                                userMap.put("photoUrl", firebaseUser.getPhotoUrl().toString());
                                                userMap.put("email", firebaseUser.getEmail());

                                                db.collection("users").document(firebaseUser.getUid())
                                                        .set(userMap)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {

                                                                Map<String, Object> userMap = new HashMap<>();
                                                                userMap.put("birthday", birthday);
                                                                userMap.put("height", height);
                                                                userMap.put("weight", weight);
                                                                userMap.put("heartRate", heartRate);
                                                                db.collection("usersData").document(firebaseUser.getUid())
                                                                        .set(userMap)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                Log.d(TAG, "User data writed.");
                                                                            }
                                                                        });
                                                            }
                                                        });
                                            }
                                        }
                                    });
                            mRegisterView.makeToast("Authentication success.");

                            mRegisterView.startHome();
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            mRegisterView.makeToast("Authentication failed.");
                        }

                        // [START_EXCLUDE]
                        mRegisterView.hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }

}
