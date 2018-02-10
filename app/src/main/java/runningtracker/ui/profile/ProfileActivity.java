package runningtracker.ui.profile;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import runningtracker.R;
import runningtracker.helper.BottomNavigationViewHelper;
import runningtracker.ui.dashboard.DashBoardActivity;
import runningtracker.ui.home.HomeActivity;
import runningtracker.ui.notification.NotificationActivity;

/**
 * Created by Anh Tu on 2/3/2018.
 */

public class ProfileActivity extends AppCompatActivity {
    @BindView(R.id.viewProfile)
    View view;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        setupBottomNavigationView();
    }

    private void setupBottomNavigationView(){
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        Menu menu = bottomNavigationView.getMenu();
        ActivityOptions options = ActivityOptions
                .makeSceneTransitionAnimation(this, view, "transitionNav");
        BottomNavigationViewHelper.enableNavigation(ProfileActivity.this, bottomNavigationView,options);
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
        startActivity(intent);
    }
}
