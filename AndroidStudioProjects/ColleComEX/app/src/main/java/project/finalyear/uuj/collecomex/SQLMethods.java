/*
package project.finalyear.uuj.collecomex;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

*/
/**
 * Created by Andrew on 17/03/2018.
 *//*


public class SQLMethods {

    private static final String SQL_CreateTable =
            "CREATE TABLE " + Contract.Tracked.TABLE_NAME + " (" +
                    Contract.Tracked._ID + " INTEGER PRIMARY KEY," +
                    Contract.Tracked.COLUMN_NAME_TITLE + " TEXT," +
                    Contract.Tracked.COLUMN_NAME_IMAGE + " TEXT," +
                    Contract.Tracked.COLUMN_NAME_PRICE + " TEXT," +
                    Contract.Tracked.COLUMN_NAME_STOCK + " TEXT," +
                    Contract.Tracked.COLUMN_NAME_OLDPRICE + "TEXT," +
                    Contract.Tracked.COLUMN_NAME_OLDSTOCK + "TEXT)";

    private static final String SQL_DeleteTable =
            "DROP TABLE IF EXISTS " + Contract.Tracked.TABLE_NAME;

    private static final String SQL_AddItem =
            "INSERT INTO " + Contract.Tracked.TABLE_NAME + "("+
                    Contract.Tracked.COLUMN_NAME_TITLE + ", "+
                    Contract.Tracked.COLUMN_NAME_IMAGE + ", " +
                    Contract.Tracked.COLUMN_NAME_PRICE + ", " +
                    Contract.Tracked.COLUMN_NAME_STOCK + ", " +
                    Contract.Tracked.COLUMN_NAME_OLDPRICE + ", " +
                    Contract.Tracked.COLUMN_NAME_OLDSTOCK + ") VALUES" +
                    "'Title', 'Image', 'Price', 'Stock', 'Old Price', 'Old Stock');";

}//end SQLMethods

public class TrackerDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Tracker.db";

    public TrackerDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db){
        db.execSQL(SQL_CREATE_ENTRIES);
    }
}

//each add item adds a row, create delete row method | Must read from row, identify by name or ID?*/
