package runningtracker.addfriend;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import runningtracker.Adapter.FriendsListAdapter;
import runningtracker.R;
import runningtracker.common.InitializationFirebase;
import runningtracker.data.model.User;
import runningtracker.data.source.UsersRepository;


public class AddFriendFragment extends Fragment implements AddFriendContract.View {
    private static final String TAG = "AddFriends";
    @BindView(R.id.add_friends_recycler_view)
    RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private AddFriendPresenter mAddFriendPresenter;
    private InitializationFirebase initializationFirebase;

    @BindView(R.id.friend_email_edit_text)
    EditText friendEmailEditText;
    FirebaseFirestore firestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_friend, container, false);
        ButterKnife.bind(this, view);

        initializationFirebase = new InitializationFirebase();
        firestore = initializationFirebase.createFirebase();
        if(firestore != null) {
            mAddFriendPresenter = new AddFriendPresenter(UsersRepository.getInstance(firestore), this);
            mAddFriendPresenter.start();
        }

        return view;
    }

    @OnClick(R.id.add_friend_button)
    public void addFriend() {
        mAddFriendPresenter.addFriend(friendEmailEditText.getText().toString());
    }


    @Override
    public void showFriendsList(List<User> friends) {
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new FriendsListAdapter(friends);
        mRecyclerView.setAdapter(mAdapter);
    }

}
