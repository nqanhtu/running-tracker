package com.runningtracker.model.modelrunning;

/**
 * Created by ngqan on 1/26/2018.
 */

public class User {
    private String username;

    public Integer getId() {
        return id;
    }

    private Integer id;
    private String email;
    private String password;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
