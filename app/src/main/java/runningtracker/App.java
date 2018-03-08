package runningtracker;

import android.app.Application;
import java.util.Date;
import runningtracker.model.modelrunning.DaoMaster;
import runningtracker.model.modelrunning.DaoSession;
import runningtracker.data.model.suggest_place.SuggestLocation;


public class  App extends Application{
    static DaoSession mDaoSession;
    @Override
    public void onCreate(){
        super.onCreate();

    }
    public static DaoSession getDaoSession() {
        return mDaoSession;
    }
}

