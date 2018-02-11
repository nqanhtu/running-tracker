package runningtracker.ui.home;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.ButterKnife;
import runningtracker.R;
import runningtracker.ui.dashboard.DashBoardActivity;
import runningtracker.ui.notification.NotificationActivity;
import runningtracker.ui.profile.ProfileActivity;
import runningtracker.view.main.DashboardFragment;

public class HomeActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navigation_dashboard:
                        //tú muốn sài thì bỏ lại DashBoardActivity vào lại, hiện tại bạn đang cần t
                        Intent intent1 = new Intent(HomeActivity.this, DashboardFragment.class);
                        startActivity(intent1);
                        break;
                    case R.id.navigation_profile:
                        Intent intent2 = new Intent(HomeActivity.this,ProfileActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.navigation_notifications:
                        Intent intent3 = new Intent(HomeActivity.this, NotificationActivity.class);
                        startActivity(intent3);
                        break;
                }
                return false;
            }
        });
    }


}
