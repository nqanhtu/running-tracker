package runningtracker.register;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import runningtracker.addfriend.AddFriendContract;
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
    public void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
//        if (!validateForm()) {
//            return;
//        }

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
                            User user = new User(firebaseUser);
//                            Toast.makeText(RegisterActivity.this, "Authentication success.",
//                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
//                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
                        }

                        // [START_EXCLUDE]
                        mRegisterView.hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }


//    private boolean validateForm() {
//        boolean valid = true;
//
//        String email = mEmail.getText().toString();
//        if (TextUtils.isEmpty(email)) {
//            mEmail.setError("Required.");
//            valid = false;
//        } else {
//            mEmail.setError(null);
//        }
//
//        String password = mPassword.getText().toString();
//        if (TextUtils.isEmpty(password)) {
//            mPassword.setError("Required.");
//            valid = false;
//        } else {
//            mPassword.setError(null);
//        }
//
//        return valid;
//    }
}
