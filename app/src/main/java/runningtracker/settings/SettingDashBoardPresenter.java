package runningtracker.settings;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import runningtracker.data.model.setting.ShareLocationObject;

public class SettingDashBoardPresenter {

    private FirebaseUser currentUser;

    public SettingDashBoardPresenter() {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

    }
    /**
     * Update value document sharelocation of user
    * */
    public  void updateValueShareLocation(FirebaseFirestore firestore, ShareLocationObject value){


        DocumentReference shareLocation = firestore.collection("users")
                .document(currentUser.getUid())
                .collection("sharelocation")
                .document("1");

        shareLocation
                .update("isShareLocation", value.getShareLocation())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Status: ", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Status: ", "Error updating document", e);
                    }
                });
    }
}