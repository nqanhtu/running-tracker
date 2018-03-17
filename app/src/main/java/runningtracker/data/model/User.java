package runningtracker.data.model;

import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Anh Tu on 2/27/2018.
 */

public class User {
    private String displayName;
    private String email;
    private String uid;

    public User() {
    }

    public User(String displayName, String email, String uid){
        this.email = email;
        this.uid = uid;

    }

    public User(FirebaseUser firebaseUser){
        displayName = firebaseUser.getDisplayName();
        email = firebaseUser.getEmail();
        uid = firebaseUser.getUid();
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}
