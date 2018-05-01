package runningtracker.history;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import runningtracker.data.model.running.LocationObject;
import runningtracker.running.IdHistoryCallback;
import runningtracker.running.LocationHistoryCallback;

public class HistoryPresenter {

    private static String TAG = "Error ";
    private FirebaseUser currentUser;

    public HistoryPresenter() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    /**
     * @function: Get id history nearer
     * */
    public void getIdHistory(final FirebaseFirestore firestore, final IdHistoryCallback idHistoryCallback){

        final List<Map<String, Object>> histories = new ArrayList<>();
        firestore.collection("users")
                .document(currentUser.getUid())
                .collection("histories")
                .orderBy("id", Query.Direction.DESCENDING).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        /**
                         * get information history nearer
                         * */
                        histories.add(document.getData());
                        idHistoryCallback.onSuccess(histories);
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    /**
     * @function: Get list id history with limit is 10
     * */
    public void getListIdHistory(final FirebaseFirestore firestore, final IdHistoryCallback idHistoryCallback){

        final List<Map<String, Object>> histories = new ArrayList<>();
        firestore.collection("users")
                .document(currentUser.getUid())
                .collection("histories")
                .orderBy("id", Query.Direction.DESCENDING).limit(5).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        /**
                         * get list information history
                         * */
                        histories.add(document.getData());
                        idHistoryCallback.onSuccess(histories);
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    /**
     * @param: FirebaseFirestore, and callback method get value list location
     */
    public void getDataLocation(final FirebaseFirestore firestore, final LocationHistoryCallback locationCallback) {

        getIdHistory(firestore, new IdHistoryCallback() {
            @Override
            public void onSuccess(List<Map<String, Object>> histories) {
                Map<String, Object> lastHistory = histories.get(0);
                String historyID = lastHistory.get("id").toString();

                firestore.collection("users").
                        document(currentUser.getUid())
                        .collection("histories")
                        .document(historyID).collection("locations")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    List<LocationObject> locationList = task.getResult().toObjects(LocationObject.class);
                                    locationCallback.dataLocation(locationList);
                                }
                            }
                        });
            }
        });
    }

    /**
     * Function convert string date to date long miliseconds
     * @param : String date need convert
     * */
    public long convertStringToLong(String date){
        long dateFomate = 0;
        SimpleDateFormat f = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
        try {
            Date d = f.parse(date);
            dateFomate = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateFomate;
    }
}
