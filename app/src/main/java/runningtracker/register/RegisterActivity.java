package runningtracker.register;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import runningtracker.R;
import runningtracker.common.InitializationFirebase;
import runningtracker.data.repository.UsersRepository;

/**
 * Created by Anh Tu on 2/27/2018.
 */

public class RegisterActivity extends AppCompatActivity implements RegisterContract.View {
    @BindView(R.id.editName)
    EditText mName;
    @BindView(R.id.editEmail)
    EditText mEmail;
    @BindView(R.id.editPassword)
    EditText mPassword;
    @BindView(R.id.editBirthday)
    EditText mBirthday;
    @BindView(R.id.editHeight)
    EditText mHeight;
    @BindView(R.id.editWeight)
    EditText mWeight;
    @BindView(R.id.editHeartRate)
    EditText mHeartRate;

    private static final String TAG = "EmailPassword";
    private RegisterPresenter presenter;
    private FirebaseFirestore firestore;
    private InitializationFirebase initializationFirebase;
    private DatePickerDialog mDatePickerDialog;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        initializationFirebase = new InitializationFirebase();
        firestore = initializationFirebase.createFirebase();
        presenter = new RegisterPresenter(UsersRepository.getInstance(firestore), this);
        setDateTimeField();

        mBirthday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                mDatePickerDialog.show();
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
        presenter.createAccount(mName.getText().toString(), mEmail.getText().toString(), mPassword.getText().toString(), mBirthday.getText().toString(), Double.valueOf(mHeight.getText().toString()), Double.valueOf(mWeight.getText().toString()), Double.valueOf(mHeartRate.getText().toString()));
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

    private void setDateTimeField() {

        Calendar newCalendar = Calendar.getInstance();
        mDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat sd = new SimpleDateFormat("dd-MM-yyyy");
                final Date startDate = newDate.getTime();
                String fdate = sd.format(startDate);

                mBirthday.setText(fdate);

            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        mDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

    }
}


