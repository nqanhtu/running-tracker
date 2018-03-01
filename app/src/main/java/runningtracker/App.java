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
        mDaoSession = new DaoMaster(
                new DaoMaster.DevOpenHelper(this, "greendao_demo.db").getWritableDb()).newSession();
        Date date = new Date();

        // Location CREATION FOR DEMO PURPOSE
        if(mDaoSession.getSuggestLocationDao().loadAll().size() == 0){
            mDaoSession.getSuggestLocationDao().insert(new SuggestLocation(1L,10.737070, 106.676086, 1));
            mDaoSession.getSuggestLocationDao().insert(new SuggestLocation(2L,10.745138, 106.674749, 2));
            mDaoSession.getSuggestLocationDao().insert(new SuggestLocation(3L,10.767185, 106.656699, 3));
            mDaoSession.getSuggestLocationDao().insert(new SuggestLocation(4L,10.787617, 106.729818, 4));
            mDaoSession.getSuggestLocationDao().insert(new SuggestLocation(5L,10.736814, 106.676904, 5));
            mDaoSession.getSuggestLocationDao().insert(new SuggestLocation(6L,10.728338, 106.682085, 6));
        }
    }
    public static DaoSession getDaoSession() {
        return mDaoSession;
    }
}

