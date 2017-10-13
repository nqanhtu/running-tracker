package runningtracker.model.modelrunning;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Minh Tri on 2017-10-04.
 */

public class M_DatabaseRunningSession extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "dbUserRunning";
    private static final int DATABASE_VERSION = 1;

    private static final String KEY_ID = "RunningSessionID";
    private static final String Key_User = "UserID";
    private static final String KEY_StartTimestamp = "StartTimestamp";
    private static final String KEY_FinishTimestamp = "FinishTimestamp";
    private static final String KEY_DistanceInKm = "DistanceInKm";
    private static final String KEY_RoadGradient = "RoadGradient";
    private static final String KEY_RunOnTreadmill = "RunOnTreadmill";
    private static final String KEY_NetCalorieBurned = "NetCalorieBurned";
    private static final String KEY_GrossCalorieBurned = "GrossCalorieBurned";
    private static final String KEY_Flag = "FlagStatus";
    private static final String TABLE_RUNNINGSESSION ="RunningSession" ;

    public M_DatabaseRunningSession(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_RUNNINGSESSION + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + Key_User + " INTEGER,"
                + KEY_StartTimestamp + " NUMERIC," + KEY_FinishTimestamp + " NUMERIC," + KEY_DistanceInKm + " REAL,"
                + KEY_RoadGradient + " INTEGER," + KEY_RunOnTreadmill + " INTEGER," + KEY_NetCalorieBurned + " INTEGER,"
                + KEY_GrossCalorieBurned + " INTEGER," + KEY_Flag + " INTEGER,"
                + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RUNNINGSESSION);

        // Create tables again
        onCreate(db);
    }
    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */
    //add new data running session
    public  void addNewRunningSession(M_RunningObject m_runningObject){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Key_User, m_runningObject.getUserID());
        values.put(KEY_StartTimestamp, String.valueOf(m_runningObject.getStartTimestamp()));
        values.put(KEY_FinishTimestamp, String.valueOf(m_runningObject.getFinishTimestamp()));
        values.put(KEY_DistanceInKm, m_runningObject.getDistanceInKm());
        values.put(KEY_RoadGradient, m_runningObject.getRoadGradient());
        values.put(KEY_RunOnTreadmill, m_runningObject.getRunOnTreadmill());
        values.put(KEY_NetCalorieBurned, m_runningObject.getNetCalorieBurned());
        values.put(KEY_GrossCalorieBurned, m_runningObject.getGrossCalorieBurned());
        values.put(KEY_Flag, m_runningObject.getFlagStatus());
    }
    //get all Running session to database
    public ArrayList<M_RunningObject> getAllRunningSession(){
        ArrayList<M_RunningObject> runningList = new ArrayList<M_RunningObject>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_RUNNINGSESSION;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                M_RunningObject runningObject = new M_RunningObject();
                runningObject.setRunningSessionID(cursor.getInt(0));
                runningObject.setUserID(cursor.getInt(1));
                runningObject.setStartTimestamp(cursor.getString(2));
                runningObject.setFinishTimestamp(cursor.getString(3));
                runningObject.setDistanceInKm(cursor.getDouble(4));
                runningObject.setRoadGradient(cursor.getInt(5));
                runningObject.setRunOnTreadmill(cursor.getInt(6));
                runningObject.setNetCalorieBurned(cursor.getInt(7));
                runningObject.setGrossCalorieBurned(cursor.getInt(8));
                runningObject.setFlagStatus(cursor.getInt(9));
                // Adding contact to list
                runningList.add(runningObject);
            } while (cursor.moveToNext());
        }

        // return contact list
        return runningList;
    }
    //get data with id
    public M_RunningObject getID(int id) {

        String selectQuery = "SELECT  * FROM" + TABLE_RUNNINGSESSION + "WHERE " + KEY_ID + "= '" + id + "'";
        M_RunningObject m_runningObject = new M_RunningObject();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null)
            cursor.moveToFirst();
            m_runningObject.setRunningSessionID(cursor.getInt(0));
            m_runningObject.setUserID(cursor.getInt(1));
            m_runningObject.setStartTimestamp(cursor.getString(2));
            m_runningObject.setFinishTimestamp(cursor.getString(3));
            m_runningObject.setDistanceInKm(cursor.getDouble(4));
            m_runningObject.setRoadGradient(cursor.getInt(5));
            m_runningObject.setRunOnTreadmill(cursor.getInt(6));
            m_runningObject.setNetCalorieBurned(cursor.getInt(7));
            m_runningObject.setGrossCalorieBurned(cursor.getInt(8));
            m_runningObject.setFlagStatus(cursor.getInt(9));


        // return id Running session
        return m_runningObject;
    }
    //delete all data running session
    public void deleteAllRunningSession(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_RUNNINGSESSION);
    }


}
