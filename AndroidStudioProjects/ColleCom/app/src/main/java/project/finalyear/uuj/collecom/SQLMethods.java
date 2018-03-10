package project.finalyear.uuj.collecom;

/**
 * Created by Andrew on 01/03/2018.
 */

public class SQLMethods {

    private static final String SQL_AddItem =
            "CREATE TABLE " + Contract.Tracked.TABLE_NAME + " (" +
                    Contract.Tracked._ID + " INTEGER PRIMARY KEY," +
                    Contract.Tracked.COLUMN_NAME_TITLE + " TEXT," +
                    Contract.Tracked.COLUMN_NAME_IMAGE + " TEXT," +
                    Contract.Tracked.COLUMN_NAME_PRICE + " TEXT," +
                    Contract.Tracked.COLUMN_NAME_STOCK + " TEXT," +
                    Contract.Tracked.COLUMN_NAME_OLDPRICE + "TEXT," +
                    Contract.Tracked.COLUMN_NAME_OLDSTOCK + "TEXT)";

    private static final String SQL_DeleteItem =
            "DROP TABLE IF EXISTS " + Contract.Tracked.TABLE_NAME;


}//end SQLMethods class
