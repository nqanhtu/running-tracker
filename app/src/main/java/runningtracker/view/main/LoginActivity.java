package runningtracker.view.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;
import runningtracker.App;
import runningtracker.R;
import runningtracker.model.modelrunning.DaoSession;
import runningtracker.model.modelrunning.Note;
import runningtracker.model.modelrunning.NoteDao;
import runningtracker.view.running.MainActivity;

public class LoginActivity extends AppCompatActivity {

    private NoteDao noteDao;
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);


// get the note DAO
        Note note = ((App)getApplication()).getDaoSession().getNoteDao().load(1L);

        if(note != null){
            Toast.makeText(LoginActivity.this, note.getText() ,Toast.LENGTH_SHORT).show();
        }


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
