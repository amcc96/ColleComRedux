package project.finalyear.uuj.collecomex; /**
 * Created by Andrew on 17/03/2018.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
/**
 * Created by Andrew on 01/03/2018.
 */

public final class Contract {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private Contract() {}

    /* Inner class that defines the table contents */
    public static class Tracked implements BaseColumns {
        public static final String TABLE_NAME = "tracked";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_IMAGE = "image";
        public static final String COLUMN_NAME_PRICE = "price";
        public static final String COLUMN_NAME_STOCK = "stock";
        public static final String COLUMN_NAME_OLDSTOCK ="oldStock";
        public static final String COLUMN_NAME_OLDPRICE ="oldPrice";
        public static final String COLUMN_NAME_URL = "url";
    }//end Tracked class

    private static final String SQL_CreateTable =
            "CREATE TABLE " + Contract.Tracked.TABLE_NAME + " (" +
                    Contract.Tracked._ID + " INTEGER PRIMARY KEY," +
                    Contract.Tracked.COLUMN_NAME_TITLE + " TEXT," +
                    Contract.Tracked.COLUMN_NAME_IMAGE + " TEXT," +
                    Contract.Tracked.COLUMN_NAME_PRICE + " TEXT," +
                    Contract.Tracked.COLUMN_NAME_STOCK + " TEXT," +
                    Contract.Tracked.COLUMN_NAME_OLDPRICE + " TEXT," +
                    Contract.Tracked.COLUMN_NAME_OLDSTOCK + " TEXT," +
                    Contract.Tracked.COLUMN_NAME_URL + " TEXT)";

    private static final String SQL_DeleteTable =
            "DROP TABLE IF EXISTS " + Contract.Tracked.TABLE_NAME;

    private static final String SQL_AddItem =
            "INSERT INTO " + Contract.Tracked.TABLE_NAME + "("+
                    Contract.Tracked.COLUMN_NAME_TITLE + ", "+
                    Contract.Tracked.COLUMN_NAME_IMAGE + ", " +
                    Contract.Tracked.COLUMN_NAME_PRICE + ", " +
                    Contract.Tracked.COLUMN_NAME_STOCK + ", " +
                    Contract.Tracked.COLUMN_NAME_OLDPRICE + ", " +
                    Contract.Tracked.COLUMN_NAME_OLDSTOCK + ", " +
                    Contract.Tracked.COLUMN_NAME_URL + " ) VALUES " +
                    "'Title', 'Image', 'Price', 'Stock', 'Old Price', 'Old Stock', 'URL');";

    public static class TrackerDbHelper extends SQLiteOpenHelper {
        public static final int DATABASE_VERSION = 2;
        public static final String DATABASE_NAME = "Tracker.db";


        public TrackerDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CreateTable);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DeleteTable);
            onCreate(db);
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }

        public void deleteTable(SQLiteDatabase db){
            db.execSQL(SQL_DeleteTable);
        }

    }//end TrackerDbHelper
}//end class Contract
