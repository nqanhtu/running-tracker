package com.runningtracker.model.service;

import com.runningtracker.model.modelrunning.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface UserService {
    @POST("/api/users")
    Call<User> createAccount(@Body User user);
}
