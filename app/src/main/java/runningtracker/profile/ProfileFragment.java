package runningtracker.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import runningtracker.R;
import runningtracker.common.InitializationFirebase;
import runningtracker.data.repository.UsersRepository;
import runningtracker.login.LoginActivity;
import runningtracker.settings.SettingsActivity;

public class ProfileFragment extends Fragment implements ProfileContract.View {
    private ProfilePresenter presenter;
    FirebaseFirestore firestore;
    @BindView(R.id.editTextMessage)
    EditText text;
    private String mUserId;
    private String mCurrentId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        firestore = FirebaseFirestore.getInstance();
        mCurrentId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        presenter = new ProfilePresenter(UsersRepository.getInstance(firestore), this);


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String mess = Boolean.toString(sharedPref.getBoolean("switch_preference_1", true));



        Toast.makeText(getActivity(),mess,Toast.LENGTH_SHORT ).show();
        return view;
    }

    @OnClick(R.id.button_logout)
    public void logout() {
        presenter.logout();
    }

    @Override
    public void showLogin() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.buttonSendNoti)
    public void sendNotificcation() {

//        String message = text.getText().toString();
//        mUserId ="kHQUyK8nTHYT8FvfgvDWuO4BdcC2";
//        if (!TextUtils.isEmpty(
//                message
//        )) {
//            Map<String, Object> notificationMessage = new HashMap<>();
//            notificationMessage.put("message", message);
//            notificationMessage.put("from", mCurrentId);
//
//            firestore.collection("users/" + mUserId + "/notifications").add(notificationMessage).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                @Override
//                public void onSuccess(DocumentReference documentReference) {
//                    text.setText("");
//                }
//            });
//        }


        Intent intent = new Intent(getActivity(), SettingsActivity.class);
        startActivity(intent);

    }


}
