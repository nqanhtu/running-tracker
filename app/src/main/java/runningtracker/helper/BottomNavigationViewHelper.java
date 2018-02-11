package runningtracker.helper;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import java.lang.reflect.Field;

import butterknife.BindView;
import runningtracker.R;
import runningtracker.ui.dashboard.DashBoardActivity;
import runningtracker.ui.profile.ProfileActivity;
import runningtracker.ui.notification.NotificationActivity;

public class BottomNavigationViewHelper {
    @SuppressLint("RestrictedApi")
    public static void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                //noinspection RestrictedApi
                item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                //noinspection RestrictedApi
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("BNVHelper", "Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
            Log.e("BNVHelper", "Unable to change value of shift mode", e);
        }
    }

    public static void enableNavigation(final Context context, BottomNavigationView view, final ActivityOptions options){
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.navigation_dashboard:
                        Intent intent1 = new Intent(context, DashBoardActivity.class);//ACTIVITY_NUM = 0
                        context.startActivity(intent1,options.toBundle());
                        break;

                    case R.id.navigation_profile:
                        Intent intent2  = new Intent(context, ProfileActivity.class);//ACTIVITY_NUM = 1
                        context.startActivity(intent2,options.toBundle());
                        break;

                    case R.id.navigation_notifications:
                        Intent intent3 = new Intent(context, NotificationActivity.class);//ACTIVITY_NUM = 2
                        context.startActivity(intent3,options.toBundle());
                        break;
                }


                return false;
            }
        });
    }
}