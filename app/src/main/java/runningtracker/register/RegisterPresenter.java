package runningtracker.register;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

    private static final String TAG = "AddFriends";
    private final UsersRepository mUsersRepository;
    private final RegisterContract.View mRegisterView;
    private FirebaseAuth mAuth;


    public RegisterPresenter(UsersRepository mUsersRepository, RegisterContract.View mRegisterView) {
        this.mUsersRepository = mUsersRepository;
        this.mRegisterView = mRegisterView;
    }

    @Override
    public void start() {
        mAuth = FirebaseAuth.getInstance();
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
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            assert firebaseUser != null;
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("displayName", displayName);
                            userMap.put("uid", firebaseUser.getUid());
                            userMap.put("email", email);
                            userMap.put("birthday", birthday);
                            userMap.put("height", height);
                            userMap.put("weight", weight);
                            userMap.put("heartRate", heartRate);

                            MyFirestore.getInstance().addDocumentToCollection(firebaseUser.getUid(), "users", userMap);

                            mRegisterView.makeToast("Authentication success.");
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
