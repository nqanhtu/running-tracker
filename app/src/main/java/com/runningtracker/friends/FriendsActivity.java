package com.runningtracker.friends;

import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.runningtracker.addfriend.AddFriendFragment;
import com.runningtracker.friendrequests.FriendRequestsFragment;
import com.runningtracker.friendslist.FriendsListFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.runningtracker.adapter.ViewPagerAdapter;
import runningtracker.R;

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
