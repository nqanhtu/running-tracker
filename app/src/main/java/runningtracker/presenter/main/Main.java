package runningtracker.presenter.main;


import android.content.Context;

public interface Main {
    boolean isConnected(Context context);//kiem tra co internet hay khong
    boolean checkTurnOnLocation();//Kiem tra da bat vi tri cua thiet bi
    void initialization();// Khoi tao gia tri
    void buildLocationSettingsRequest();
    void createLocationRequest();
    int checkStartRunning();//kiem tra dieu kien bat dau truoc khi bat dau chay
    void onNavigationActivity();//Chuyen hướng activity theo dieu kien
}
