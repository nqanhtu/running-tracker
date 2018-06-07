package runningtracker.registerinfomation;

import android.app.TimePickerDialog;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.View;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import runningtracker.R;

public class RegisterInformationActivity extends AppCompatActivity {
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

    private DatePickerDialog mDatePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_information);
        ButterKnife.bind(this);
        validate();
        setUpSupportActionBar();
    }

    @OnClick(R.id.register_button)
    public void registerInfomation() {

        // nameEditText.setError();
    }

    @OnClick(R.id.birthday_edit_text)
    public void setBirthday() {
        int day = 1;
        int month = 0;
        int year = 1996;
        mDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                birthdayEditText.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            }
        }, year, month, day);
        mDatePickerDialog.show();
    }

    private void validate() {
//        usernameEditText.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                usernameLayout.setErrorEnabled(false);
//                return false;
//            }
//        });

        usernameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!usernameEditText.getText().toString().equals("")) {
                    usernameLayout.setError("Tên tài khoản đã tồn tại");
                }
            }
        });

    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    private void setUpSupportActionBar() {
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Thông tin tài khoản");
    }


}
