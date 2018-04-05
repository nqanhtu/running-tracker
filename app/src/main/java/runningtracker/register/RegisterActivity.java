package runningtracker.register;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import runningtracker.R;
import runningtracker.common.InitializationFirebase;
import runningtracker.data.repository.UsersRepository;

/**
 * Created by Anh Tu on 2/27/2018.
 */

public class RegisterActivity extends AppCompatActivity implements RegisterContract.View {
    @BindView(R.id.editEmail)
    EditText mEmail;
    @BindView(R.id.editPassword)
    EditText mPassword;
    private static final String TAG = "EmailPassword";
    private RegisterPresenter presenter;
    private FirebaseFirestore firestore;
    private InitializationFirebase initializationFirebase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        initializationFirebase = new InitializationFirebase();
        firestore = initializationFirebase.createFirebase();
        presenter = new RegisterPresenter(UsersRepository.getInstance(firestore),this);

    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        presenter.start();
    }

    @OnClick(R.id.buttonRegister)
    public void createAccount() {
        presenter.createAccount(mEmail.getText().toString(), mPassword.getText().toString());
    }

    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    @Override
    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

}
