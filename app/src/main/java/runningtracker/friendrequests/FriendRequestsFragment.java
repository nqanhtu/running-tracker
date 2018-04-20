package runningtracker.friendrequests;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import runningtracker.Adapter.FriendRequestsAdapter;
import runningtracker.R;
import runningtracker.common.InitializationFirebase;
import runningtracker.data.model.User;
import runningtracker.data.repository.UsersRepository;


public class FriendRequestsFragment extends Fragment implements FriendRequestsContract.View {
    private static final String TAG = "FriendRequests";
    @BindView(R.id.friend_requests_recycler_view)
    RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FriendRequestsPresenter mFriendRequestsPresenter;
    private InitializationFirebase initializationFirebase;

    FirebaseFirestore firestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_requests, container, false);
        ButterKnife.bind(this, view);

        initializationFirebase = new InitializationFirebase();
        firestore = initializationFirebase.createFirebase();
        if (firestore != null) {
            mFriendRequestsPresenter = new FriendRequestsPresenter(UsersRepository.getInstance(firestore), this);
            mFriendRequestsPresenter.start();
        }
        return view;
    }

    @Override
    public void showFriendRequests(List<User> friends) {
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new FriendRequestsAdapter(getActivity(),friends);
        mRecyclerView.setAdapter(mAdapter);
    }
}
