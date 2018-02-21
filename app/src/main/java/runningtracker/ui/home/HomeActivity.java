package runningtracker.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

import butterknife.ButterKnife;
import runningtracker.R;
import runningtracker.ui.dashboard.DashboardFragment;
import runningtracker.ui.notification.NotificationsFragment;
import runningtracker.ui.profile.ProfileFragment;

public class HomeActivity extends AppCompatActivity{

    AHBottomNavigation bottomNavigation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        bottomNavigation= (AHBottomNavigation) findViewById(R.id.navigation);
        createNavItems();
        // Default fragment
        DashboardFragment dashboardFragment =new DashboardFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_container,dashboardFragment).commit();

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                if (position==0)
                {
                    DashboardFragment dashboardFragment =new DashboardFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_container,dashboardFragment).commit();
                }else  if (position==1)
                {
                    ProfileFragment profileFragment =new ProfileFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_container,profileFragment).commit();
                }else  if (position==2)
                {
                    NotificationsFragment notificationsFragment =new NotificationsFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_container,notificationsFragment).commit();
                }
                return true;
            }
        });
    }

    private void createNavItems()
    {

// Create items
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Dashboard", R.drawable.ic_dashboard_black_24dp);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Profile", R.drawable.ic_profile_24dp);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("Notifications", R.drawable.ic_notifications_black_24dp);
// Add items
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);

        //set properties
        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#FEFEFE"));

        //set current item
        bottomNavigation.setCurrentItem(0);

    }

}
