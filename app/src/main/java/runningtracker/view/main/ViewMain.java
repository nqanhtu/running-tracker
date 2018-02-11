package runningtracker.view.main;


import android.content.Context;
import android.content.Intent;

public interface ViewMain {
    Context getMainActivity();//lay man hinh MainActivityHome
    void navigationRunning();//Chuyen sang activity running có internet
    void navigationRunningOffline();//Chuyen sang activity running offline
}
