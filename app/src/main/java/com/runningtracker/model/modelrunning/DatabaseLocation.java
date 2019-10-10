package com.runningtracker.model.modelrunning;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

import com.runningtracker.data.model.running.LocationObject;

public class DatabaseLocation extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "dbRunning";
    private static final int DATABASE_VERSION = 1;

    // dbLoaction Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_Latitude = "Latitude";
    private static final String KEY_Longitude = "Longitude";
    private static final String TABLE_LOCATION ="Location" ;

    public DatabaseLocation(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_LOCATION + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_Latitude + " REAL,"
                + KEY_Longitude + " REAL" + ")";
        sqLiteDatabase.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);

        // Create tables again
        onCreate(sqLiteDatabase);
    }
    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new location
    public void addLocation(LocationObject location) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        //values.put();
        values.put(KEY_Latitude, location.getLatitudeValue());
        values.put(KEY_Longitude, location.getLongitudeValue());

        // Inserting Row
        db.insert(TABLE_LOCATION, null, values);
        db.close(); // Closing database connection
    }
    // Getting All Location
    public ArrayList<LocationObject> getAllLocation() {
        ArrayList<LocationObject> locationList = new ArrayList<LocationObject>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_LOCATION;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                LocationObject location = new LocationObject();
                location.setLatitudeValue(cursor.getDouble(1));
                location.setLongitudeValue(cursor.getDouble(2));
                // Adding contact to list
                locationList.add(location);
            } while (cursor.moveToNext());
        }

        // return
        return locationList;
    }
    //Delete all location
    public  void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_LOCATION);
    }
}
