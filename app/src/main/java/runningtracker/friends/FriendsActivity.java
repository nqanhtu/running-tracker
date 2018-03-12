package runningtracker.friends;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import runningtracker.Adapter.ViewPagerAdapter;
import runningtracker.R;
import runningtracker.friendrequests.FriendRequestsFragment;
import runningtracker.addfriend.AddFriendFragment;
import runningtracker.friendslist.FriendsListFragment;

public class FriendsActivity extends AppCompatActivity {

    @BindView(R.id.tab_friend) TabLayout tabLayout;
    @BindView(R.id.friend_view_pager) ViewPager viewPager;
    private ViewPagerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        ButterKnife.bind(this);

        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new FriendsListFragment(),"Danh sách");
        adapter.addFragment(new FriendRequestsFragment(),"Lời mời");
        adapter.addFragment(new AddFriendFragment(),"Thêm bạn");
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



    }

}
