package runningtracker.ui.notification;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import runningtracker.R;
import runningtracker.helper.BottomNavigationViewHelper;

/**
 * Created by Anh Tu on 2/3/2018.
 */

public class NotificationsActivity extends AppCompatActivity{
    @BindView(R.id.viewNotification)
    View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ButterKnife.bind(this);
       setupBottomNavigationView();
    }

    private void setupBottomNavigationView(){
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        ActivityOptions options = ActivityOptions
                .makeSceneTransitionAnimation(this, view, "transitionNav");
        BottomNavigationViewHelper.enableNavigation(NotificationsActivity.this, bottomNavigationView,options);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
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
