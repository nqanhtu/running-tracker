package runningtracker.data.source.suggestlocation;


import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import runningtracker.data.model.suggestplace.SuggestLocation;


public class SuggestRepository {
    private static final String TAG = "suggestlocations" ;

    public SuggestRepository() {}

    /**
     * @Function: get all suggest location
    * */
    public void getAllSuggestLocation(final SuggetsCallback suggetsCallback){
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        db.collection(TAG).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){

                    List<SuggestLocation> suggestLocationList = task.getResult().toObjects(SuggestLocation.class);
                    suggetsCallback.onSuccess(suggestLocationList);
                }
                else {
                    Log.d(TAG,"Get an error");
                }
            }
        });
    }
}
