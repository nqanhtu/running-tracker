package runningtracker.model.modelrunning;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;



public class DatabaseWeather extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "db_Running";
    private static final int DATABASE_VERSION = 1;

    private static final String KEY_id = "id";
    private static final String KEY_Name = "name";
    private static final String KEY_Main = "main";
    private static final String KEY_Description = "description";
    private static final String KEY_Icon = "icon";
    private static final String KEY_Temp = "temp";
    private static final String KEY_Day = "day";
    private static final String TABLE_WEATHER ="Weather1" ;

    public DatabaseWeather(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_WEATHER + "("
                + KEY_id + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_Name + " TEXT,"
                + KEY_Main + " TEXT,"  + KEY_Description +  " TEXT," + KEY_Icon + " TEXT," + KEY_Temp + " TEXT,"
                + KEY_Day + " TEXT" + ")";
        sqLiteDatabase.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_WEATHER);

        // Create tables again
        onCreate(sqLiteDatabase);
    }
    // Adding new location
    public void addNewWeather(WeatherObject weatherObject) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        //values.put();
        values.put(KEY_Name, weatherObject.getName());
        values.put(KEY_Main, weatherObject.getMain());
        values.put(KEY_Description, weatherObject.getDescription());
        values.put(KEY_Icon, weatherObject.getIcon());
        values.put(KEY_Temp, weatherObject.getTemp());
        values.put(KEY_Day, weatherObject.getDay());
        // Inserting Row
        db.insert(TABLE_WEATHER, null, values);
        db.close(); // Closing database connection
    }
    //get all weather
    public ArrayList<WeatherObject> getAllWeather() {
        ArrayList<WeatherObject> weatherObjectsList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_WEATHER;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                WeatherObject weatherObject = new WeatherObject();

                weatherObject.setName(cursor.getString(1));
                weatherObject.setMain(cursor.getString(2));
                weatherObject.setDescription(cursor.getString(3));
                weatherObject.setIcon(cursor.getString(4));
                weatherObject.setTemp(cursor.getString(5));
                weatherObject.setDay(cursor.getString(6));
                // Adding contact to list
                weatherObjectsList.add(weatherObject);
            } while (cursor.moveToNext());
        }

        // return
        return weatherObjectsList;
    }
    //get weather follow day
    public WeatherObject getDay(String day) {
        String selectQuery = "SELECT  * FROM" + TABLE_WEATHER + "WHERE " + KEY_Day + "= N'" + day + "'";
        WeatherObject weatherObject = new WeatherObject();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null) {
            cursor.moveToFirst();
            weatherObject.setName(cursor.getString(1));
            weatherObject.setMain(cursor.getString(2));
            weatherObject.setDescription(cursor.getString(3));
            weatherObject.setIcon(cursor.getString(4));
            weatherObject.setTemp(cursor.getString(5));
            weatherObject.setDay(cursor.getString(6));
        }
        return weatherObject;
    }
    //Delete all Weather
    public  void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_WEATHER);
    }

}
