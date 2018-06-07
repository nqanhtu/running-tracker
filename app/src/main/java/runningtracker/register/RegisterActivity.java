package runningtracker.register;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import runningtracker.R;
import runningtracker.common.InitializationFirebase;
import runningtracker.data.repository.UsersRepository;
import runningtracker.home.HomeActivity;
import runningtracker.registerinfomation.RegisterInformationActivity;

/**
 * Created by Anh Tu on 2/27/2018.
 */

public class RegisterActivity extends AppCompatActivity implements RegisterContract.View {
    //    @BindView(R.id.editName)
//    EditText mName;
    @BindView(R.id.editEmail)
    EditText mEmail;
    @BindView(R.id.editPassword)
    EditText mPassword;
    @BindView(R.id.password_text_input)
    TextInputLayout passwordTextInput;
//    @BindView(R.id.editBirthday)
//    EditText mBirthday;
//    @BindView(R.id.editHeight)
//    EditText mHeight;
//    @BindView(R.id.editWeight)
//    EditText mWeight;
//    @BindView(R.id.editHeartRate)
//    EditText mHeartRate;

    private static final String TAG = "EmailPassword";
    private RegisterPresenter presenter;
    private FirebaseFirestore firestore;
    private InitializationFirebase initializationFirebase;
    private DatePickerDialog mDatePickerDialog;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_register);
        ButterKnife.bind(this);

        initializationFirebase = new InitializationFirebase();
        firestore = initializationFirebase.createFirebase();
        presenter = new RegisterPresenter(UsersRepository.getInstance(firestore), this);
//        setDateTimeField();

//        mBirthday.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                mDatePickerDialog.show();
//                return false;
//            }
//        });
        setUpSupportActionBar();

        mPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (isPasswordValid(mPassword.getText())) {
                    passwordTextInput.setError(null); //Clear the error
                }
                return false;
            }
        });

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
        startActi();
        //presenter.createAccount(mEmail.getText().toString(), mPassword.getText().toString());
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

    @Override
    public void makeToast(String text) {
        Toast.makeText(RegisterActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean validateForm() {
        boolean valid = true;
        String email = mEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmail.setError("Required.");
            valid = false;
        } else {
            mEmail.setError(null);
        }

        String password = mPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPassword.setError("Required.");
            valid = false;
        } else {
            mPassword.setError(null);
        }

        return valid;
    }

    @Override
    public void startHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

//    private void setDateTimeField() {
//
//        Calendar newCalendar = Calendar.getInstance();
//        mDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
//
//            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                Calendar newDate = Calendar.getInstance();
//                newDate.set(year, monthOfYear, dayOfMonth);
//                SimpleDateFormat sd = new SimpleDateFormat("dd-MM-yyyy");
//                final Date startDate = newDate.getTime();
//                String fdate = sd.format(startDate);
//
//                mBirthday.setText(fdate);
//
//            }
//        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
//        mDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
//
//    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void setUpSupportActionBar() {
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Đăng Ký Tài Khoản");
    }

    /*
    In reality, this will have more complex logic including, but not limited to, actual
    authentication of the username and password.
 */
    private boolean isPasswordValid(@Nullable Editable text) {
        return text != null && text.length() >= 8;
    }

    private void startActi() {
        Intent intent = new Intent(this, RegisterInformationActivity.class);
        startActivity(intent);
    }
}


