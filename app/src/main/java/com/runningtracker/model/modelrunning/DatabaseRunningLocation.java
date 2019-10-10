package com.runningtracker.model.modelrunning;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseRunningLocation extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "Running-Location";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_RUNNINGLOCATION ="RunningLocation" ;
    private static final String TABLE_DETAIL ="DetailRunning" ;

    // dbLoaction Table Columns names
    private static final String KEY_ID = "ID";
    private static final String D_KEY_ID = "ID";
    private static final String KEY_Type = "Type";
    private static final String KEY_Name = "Name";
    //detail
    private static final String KEY_Latitude = "Latitude";
    private static final String KEY_Longitude = "Longitude";
    private static final String KEY_FirstLocation = "FirstLocation";
    private static final String KEY_IDLocation = "IDLocation";

    public DatabaseRunningLocation(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    //Create table runninglocation
    String CREATE_RUNINGLOCATIN_TABLE = "CREATE TABLE " + TABLE_RUNNINGLOCATION + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_Name + " TEXT,"+ KEY_Type + " INTEGER" + ")";

    //Create table detail
    String CREATE_DETAIL_TABLE = "CREATE TABLE " + TABLE_DETAIL + "("
            + D_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_Latitude + " REAL,"
            + KEY_Longitude + " REAL,"
            + KEY_FirstLocation + " INTEGER,"
            + KEY_IDLocation + " INTEGER,"
            + " FOREIGN KEY ("+ KEY_IDLocation +") REFERENCES "+ TABLE_RUNNINGLOCATION +"("+ KEY_ID +"))";
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_DETAIL_TABLE);
        sqLiteDatabase.execSQL(CREATE_RUNINGLOCATIN_TABLE);
        //sqLiteDatabase.execSQL(CREATE_DETAIL_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_RUNNINGLOCATION);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_DETAIL);

        // Create tables again
        onCreate(sqLiteDatabase);
    }


    //function get arraylist location with parameter
    public ArrayList<QueryRunningObject> getListLocation(int type) {
        ArrayList<QueryRunningObject> queryRunningObjectArrayList = new ArrayList<>();
        // Select All Query
        String selectQuery = "select " + KEY_IDLocation + ", "+KEY_Latitude+ ", " + KEY_Longitude+ " from " +TABLE_DETAIL+" where " +KEY_FirstLocation+ " = 1 " +
                "and " +KEY_IDLocation+ " in " + " (select "+KEY_ID+" from "+TABLE_RUNNINGLOCATION+" where " +KEY_FirstLocation+  "=" + type + ")";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                QueryRunningObject queryRunningObject = new QueryRunningObject();
                queryRunningObject.setId(cursor.getInt(0));
                queryRunningObject.setLatitudeValue(cursor.getDouble(1));
                queryRunningObject.setLongitudeValue(cursor.getDouble(2));
                // Adding contact to list
                queryRunningObjectArrayList.add(queryRunningObject);
            } while (cursor.moveToNext());
        }

        // return
        return queryRunningObjectArrayList;
    }
    // Adding new location
    public void addNewRunningLocation(RunningLocationObject runningLocationObject) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        //values.put();
        values.put(KEY_Name, runningLocationObject.getName());
        values.put(KEY_Type, runningLocationObject.getType());

        // Inserting Row
        db.insert(TABLE_RUNNINGLOCATION, null, values);
        db.close(); // Closing database connection
    }
    // Getting All Location

    //Delete all location
    public  void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_RUNNINGLOCATION);
    }
    //

    // Adding new location
    public void addLocation(DetailRunningObject detailRunningObject) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        //values.put();
        values.put(KEY_Latitude, detailRunningObject.getLatitudeValue());
        values.put(KEY_Longitude, detailRunningObject.getLongitudeValue());
        values.put(KEY_FirstLocation, detailRunningObject.getFirstLocation());
        values.put(KEY_IDLocation, detailRunningObject.getIdLocation());

        // Inserting Row
        db.insert(TABLE_DETAIL, null, values);
        db.close(); // Closing database connection
    }
    //get detail list location with id
    public ArrayList<DetailRunningObject> getListDetailLocation(int id) {
        ArrayList<DetailRunningObject> detailRunningObjectArrayList = new ArrayList<>();
        // Select  Query
        String selectQuery = "SELECT  * FROM " + TABLE_DETAIL + " WHERE " + KEY_IDLocation + " = " +id;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DetailRunningObject detailRunningObject = new DetailRunningObject();
                detailRunningObject.setLatitudeValue(cursor.getDouble(1));
                detailRunningObject.setLongitudeValue(cursor.getDouble(2));
                // Adding contact to list
                detailRunningObjectArrayList.add(detailRunningObject);
            } while (cursor.moveToNext());
        }

        // return
        return detailRunningObjectArrayList;
    }
    //Delete all Detail location
    public  void deleteAllDetail(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_DETAIL);
    }

}
