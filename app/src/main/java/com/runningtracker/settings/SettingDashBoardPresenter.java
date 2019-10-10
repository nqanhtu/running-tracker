package com.runningtracker.settings;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingDashBoardPresenter {

    private FirebaseUser currentUser;

    public SettingDashBoardPresenter() {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

    }

    /**
     * Update value document sharelocation of user
     */
    public void updateValueShareLocation(FirebaseFirestore firestore, boolean isShareLocation) {
        firestore.collection("users")
                .document(currentUser.getUid())
                .update("sharelocation", isShareLocation).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        });


//        shareLocation
//                .("isShareLocation", ShareLocationObject)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.d("Status: ", "DocumentSnapshot successfully updated!");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w("Status: ", "Error updating document", e);
//                    }
//                });
    }
}
