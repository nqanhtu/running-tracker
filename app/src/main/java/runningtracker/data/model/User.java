package runningtracker.data.model;

import android.util.SparseArray;

import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anh Tu on 2/27/2018.
 */

public class User {
    private String displayName;
    private String email;
    private String uid;
    private double height;
    private double weight;
    private double heartRate;

    public User() {
    }

    public User(String displayName, String email, String uid, double height, double weight, double heartRate) {
        this.displayName = displayName;
        this.email = email;
        this.uid = uid;
        this.height = height;
        this.weight = weight;
        this.heartRate = heartRate;
    }

    public User(FirebaseUser firebaseUser) {
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

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(double heartRate) {
        this.heartRate = heartRate;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("displayName", this.displayName);
        map.put("email", this.email);
        map.put("uid", this.uid);
        map.put("height", this.height);
        map.put("weight", this.weight);
        map.put("heartRate", this.heartRate);
        return map;
    }

}
