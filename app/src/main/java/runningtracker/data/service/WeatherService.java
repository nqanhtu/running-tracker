package runningtracker.data.service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import runningtracker.data.model.weather.Weather;


/**
 * Created by Anh Tu on 2/22/2018.
 */

public interface WeatherService {
    String KEY_ID = "ba283cc3f6ad524b";
    @GET("/api/"+KEY_ID+"/conditions/lang:VU/q/{latlong}.json")
    Call<Weather> getWeather(@Path("latlong") String latlong);
}
