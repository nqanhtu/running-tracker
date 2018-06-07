package runningtracker.data.service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import runningtracker.data.model.weather.OpenWeather;


/**
 * Created by Anh Tu on 2/22/2018.
 */

public interface WeatherService {
    String KEY_ID = "209d611e0e9bfcb2a0bdfbee60070680";

    @GET("/data/2.5/weather?units=metric&lang=vi&APPID=" + KEY_ID)
    Call<OpenWeather> getWeather(@Query("lat") String lat, @Query("lon") String lon);
}
