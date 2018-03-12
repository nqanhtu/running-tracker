package runningtracker.common;


import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class InitializationFirebase {

    public InitializationFirebase() {
    }

    /**
     * @Function: Create fire base method
     * @Param: Fire base method
    * */
    public FirebaseFirestore createFirebase(){
        FirebaseFirestore firestore;

        firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);
        return firestore;
    }
}
