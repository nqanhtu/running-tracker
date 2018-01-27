package runningtracker.view.main;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import runningtracker.R;
import runningtracker.model.ServiceGenerator;
import runningtracker.model.modelrunning.User;
import runningtracker.model.service.UserService;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.editEmail) EditText email;
    @BindView(R.id.editPassword) EditText password;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        // TODO Use fields...
    }

    @OnClick(R.id.buttonRegister)
    public void registerAccount(){
        User user = new User(email.getText().toString(), password.getText().toString());
        UserService userService = ServiceGenerator.createService(UserService.class);
        Call<User> call = userService.createAccount(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == 422){
                    Toast.makeText(RegisterActivity.this, "Email này đã được đăng ký!" ,Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(RegisterActivity.this,"Đăng ký thành công!",Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(RegisterActivity.this,"Có gì đó sai sai",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
