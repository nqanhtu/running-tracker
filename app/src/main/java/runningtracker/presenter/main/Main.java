package runningtracker.Presenter.main;


import android.content.Context;

public interface Main {
    boolean isConnected(Context context);//Check internet
    boolean checkTurnOnLocation();//Check GPS turn on or off
    void initialization();// Create values
    void buildLocationSettingsRequest();
    void createLocationRequest();
    int checkStartRunning();//Check start condition
    void onNavigationActivity();//change view
    void getWeatherAPI(double latitude, double longitude);//get weather api
    void supPortWeather();

}
