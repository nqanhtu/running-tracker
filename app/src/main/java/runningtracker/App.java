package runningtracker;

import android.app.Application;

import java.util.Date;

import runningtracker.model.modelrunning.DaoMaster;
import runningtracker.model.modelrunning.DaoSession;
import runningtracker.model.modelrunning.Note;
import runningtracker.model.modelrunning.SuggestLocation;

public class    App extends Application{
    private DaoSession mDaoSession;
    @Override
    public void onCreate(){
        super.onCreate();
        mDaoSession = new DaoMaster(
                new DaoMaster.DevOpenHelper(this, "greendao_demo.db").getWritableDb()).newSession();
        Date date = new Date();

        // Location CREATION FOR DEMO PURPOSE
        if(mDaoSession.getSuggestLocationDao().loadAll().size() == 0){
            mDaoSession.getSuggestLocationDao().insert(new SuggestLocation(1L,10.7430053,106.6837202));
            mDaoSession.getSuggestLocationDao().insert(new SuggestLocation(2L,21.0227788,105.8194541));
        }
    }
    public DaoSession getDaoSession() {
        return mDaoSession;
    }
}

