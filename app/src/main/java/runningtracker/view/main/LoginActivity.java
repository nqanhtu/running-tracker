package runningtracker.view.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import butterknife.ButterKnife;
import butterknife.OnClick;
import runningtracker.R;
import runningtracker.view.running.MainActivity;

public class LoginActivity extends AppCompatActivity {

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        // TODO Use fields...
    }

    @OnClick(R.id.buttonHome)
    public void startHomeActivity() {
        Intent intent = new Intent(this, MainActivityHome.class);
        startActivity(intent);
    }

    @OnClick(R.id.buttonRegister)
    public void startSignUpActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}
