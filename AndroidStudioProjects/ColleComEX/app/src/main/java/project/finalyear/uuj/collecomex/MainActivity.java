package project.finalyear.uuj.collecomex;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.BaseColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.jsoup.Jsoup.connect;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //WRITE
        Context contextNew = this;
        Contract.TrackerDbHelper mDbHelper = new Contract.TrackerDbHelper(contextNew);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Log.e("db", "Write Database Created");
        //START PARSER
        enableStrictMode(); //PARSER LINE 1
        long newRowId = db.insert(Contract.Tracked.TABLE_NAME, null, Parser.itemRetrieve()); //PARSER LINE 2
        //END PARSER
        Log.i("Parser", "Parser run ended");
        //contextNew.deleteDatabase(mDbHelper.getDatabaseName()); //FAILSAFE DELETE DATABASE

        //READ
        SQLiteDatabase dbRead = mDbHelper.getReadableDatabase();
        //GET TABLE AS STRING
        getTableAsString(dbRead, Contract.Tracked.TABLE_NAME);

        String[] projection = {
                BaseColumns._ID,
                Contract.Tracked.COLUMN_NAME_TITLE,
                Contract.Tracked.COLUMN_NAME_PRICE,
                Contract.Tracked.COLUMN_NAME_STOCK
        };
        //String selection = Contract.Tracked.COLUMN_NAME_TITLE + " = ?";
        String selection = null;
        String[] selectionArgs = null; //filter results
        String sortOrder = null;
        //Contract.Tracked.COLUMN_NAME_PRICE + " £9.99";//sort results
        Cursor cursor = dbRead.query(
                Contract.Tracked.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );


        List<String> itemIds = new ArrayList<>();
        while (cursor.moveToNext()) {
            String itemId = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Tracked.COLUMN_NAME_TITLE));
            itemId += "\n" + cursor.getString(cursor.getColumnIndexOrThrow(Contract.Tracked.COLUMN_NAME_PRICE));
            itemId += "\n" + cursor.getString(cursor.getColumnIndexOrThrow(Contract.Tracked.COLUMN_NAME_STOCK));
            itemIds.add(itemId);
        }//end while
        cursor.close();

    }//end onCreate

    public void enableStrictMode(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
    }//end enableStrictMode

    String TAG = "DbHelper";

    public void getTableAsString(SQLiteDatabase db, String tableName) {
        Log.d(TAG, "getTableAsString called");
        String tableString = "";
        String img = "";
        int imageResource;
        Drawable res = null;

            //String.format("Table %s:\n", tableName)
            Cursor allRows = db.rawQuery("SELECT " + Contract.Tracked.COLUMN_NAME_TITLE + ", " + Contract.Tracked.COLUMN_NAME_PRICE + ", " + Contract.Tracked.COLUMN_NAME_STOCK + " FROM " + tableName, null);
            //Cursor images = db.rawQuery("SELECT " + Contract.Tracked.COLUMN_NAME_IMAGE + " FROM " +tableName, null);
            //org.jsoup.nodes.Document doc = Jsoup.connect("https://www.amazon.co.uk/Magic-Gathering-14441-Kaladesh-Bundle/dp/B01LDELE0Q/ref=sr_1_1?ie=UTF8&qid=1522766138&sr=8-1").get();
            //Elements image = doc.select("img#landingImage");
            //String imgSrc = image.attr("src");
            //InputStream input = new java.net.URL(imgSrc).openStream();
            //Bitmap bitmap = BitmapFactory.decodeStream(input);

            if (allRows.moveToFirst()) {
                String[] columnNames = allRows.getColumnNames();
                do {
                    for (String name : columnNames) {
                        //PRINT TO TEXTVIEW
                        //tblPrint = myView.getText();
                        //myView.setText(tblPrint + getTableAsString(dbRead, Contract.Tracked.TABLE_NAME));
                        tableString += String.format(allRows.getString(allRows.getColumnIndex(name)));
                        tableString += "\n";
                        Log.v("tableString", tableString);
                        //img= String.format(images.getString(images.getColumnIndex(name)));
                        //imageResource = getResources().getIdentifier(img, null, getPackageName());
                        //res = getResources().getDrawable(imageResource);
                        //"%s: %s\n", name,
                    }//end for
                    TableLayout myLayout = findViewById(R.id.tblTrackerList);
                    //CREATE TEXTVIEW
                    TextView myView = new TextView(this);
                    myView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));


                    //CREATE IMAGEBUTTON
                    ImageButton myImage = new ImageButton(this);
                    myImage.setLayoutParams(new TableRow.LayoutParams(172, 77));
                    //myImage.setImageBitmap(bitmap);

                    //CREATE TABLE
                    //TableLayout myTable = new TableLayout(this);
                    //myTable.setLayoutParams(new TableLayout.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT));

                    //CREATE TABLEROW
                    TableRow myRow = new TableRow(this);
                    myRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));

                    //ADD NEW VIEWS
                    myView.setText(tableString);
                    //myLayout.addView(myView);
                    myRow.addView(myImage);
                    myRow.addView(myView);
                    myLayout.addView(myRow, new TableLayout.LayoutParams(TabLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));
                    //myView.setText(tableString);
                    //myTable.addView(myRow);

                    tableString = "";
                    img = "";
                    res = null;
                    imageResource = 0;
                    Log.e("print", "Layouts should be added");
                } while (allRows.moveToNext());
            }

            //return tableString;
    }//end GETTABLEASSTRING

}//END MAIN ACTIVITY