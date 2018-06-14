package runningtracker.friends;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import runningtracker.Adapter.ViewPagerAdapter;
import runningtracker.R;
import runningtracker.friendrequests.FriendRequestsFragment;
import runningtracker.addfriend.AddFriendFragment;
import runningtracker.friendslist.FriendsListFragment;

public class FriendsActivity extends AppCompatActivity {

    @BindView(R.id.tab_friend)
    TabLayout tabLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.friend_view_pager)
    ViewPager viewPager;
    private ViewPagerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        ButterKnife.bind(this);

        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new FriendsListFragment(), "Danh sách");
        adapter.addFragment(new FriendRequestsFragment(), "Lời mời");
        adapter.addFragment(new AddFriendFragment(), "Thêm bạn");
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }

}
