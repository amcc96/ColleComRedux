package project.finalyear.uuj.collecomex;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.BaseColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        //String price = "£9.99";
        Context contextNew = this;
        Contract.TrackerDbHelper mDbHelper = new Contract.TrackerDbHelper(contextNew);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Log.e("db", "Write Database Created");
        //start parser
        TextView textView = findViewById(R.id.testValue);
        enableStrictMode();
        //Parser.itemRetrieve(textView);
        long newRowId = db.insert(Contract.Tracked.TABLE_NAME, null, Parser.itemRetrieve());
        //end parser
        Log.i("Parser", "Parser run ended");
        contextNew.deleteDatabase(mDbHelper.getDatabaseName()); //FAILSAFE DELETE DATABASE

        //READ
        SQLiteDatabase dbRead = mDbHelper.getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                Contract.Tracked.COLUMN_NAME_TITLE,
                Contract.Tracked.COLUMN_NAME_PRICE,
                Contract.Tracked.COLUMN_NAME_STOCK
        };
        String selection = Contract.Tracked.COLUMN_NAME_TITLE + " = ?";
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
        while(cursor.moveToNext()){
            String itemId = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Tracked.COLUMN_NAME_TITLE));
            itemId += cursor.getString(cursor.getColumnIndexOrThrow(Contract.Tracked.COLUMN_NAME_PRICE));
            itemId += cursor.getString(cursor.getColumnIndexOrThrow(Contract.Tracked.COLUMN_NAME_STOCK));
            itemIds.add(itemId);
        }//end while
        cursor.close();
        //TextView textView = findViewById(R.id.testValue);

        //int listSize = itemIds.size();
        for(int i=0; i<itemIds.size(); i++) {
            textView.setText(textView.getText() + "\n" + itemIds.get(i));
            Log.d("Hi", "Hello");
        }//end for
    }//end OnCreate()

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void enableStrictMode() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
    }//end enableStrictMode
}

