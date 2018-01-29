package runningtracker.model.service;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import runningtracker.model.modelrunning.User;


public interface UserService {
    @POST("/api/users")
    Call<User> createAccount(@Body User user);
}
