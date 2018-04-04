package project.finalyear.uuj.collecomex;

import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    private String m_Text = "";
    final Context contextNew = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        final Contract.TrackerDbHelper mDbHelper = new Contract.TrackerDbHelper(contextNew);
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final SQLiteDatabase dbRead = mDbHelper.getReadableDatabase();
        //mDbHelper.onCreate(db);
        //mDbHelper.deleteTable(db);
        enableStrictMode(); //PARSER LINE 1
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(contextNew);
                builder.setTitle("Enter URL Here:");
                m_Text = "";
                //Set up input
                final EditText input = new EditText(contextNew);
                //Type of input expected
                input.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT);
                builder.setView(input);

                //Buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        m_Text = input.getText().toString();
                        long newRowId = db.insert(Contract.Tracked.TABLE_NAME, null, Parser.itemRetrieve(m_Text)); //PARSER LINE 2
                        getTableAsString(db, Contract.Tracked.TABLE_NAME);
                        Log.e("INPUT TEXT", m_Text);
                    }
                });//END POSITIVE LISTENER
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });//END NEGATIVE LISTENER
                builder.show();
            }
        });//END ONCLICKLISTENER

        Log.e("Parser", "Parser run ended");
        //contextNew.deleteDatabase(mDbHelper.getDatabaseName()); //FAILSAFE DELETE DATABASE

        //GET TABLE AS STRING
        getTableAsString(dbRead, Contract.Tracked.TABLE_NAME);

    }//end onCreate

    public void enableStrictMode(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
    }//end enableStrictMode

    String TAG = "DbHelper";


    public void getTableAsString(final SQLiteDatabase db, final String tableName) {
        Log.d(TAG, "getTableAsString called");
        String tableString = "";
        String tagString = "";
        String img = "";
        int imageResource;
        Drawable res = null;
        TableLayout myLayout = findViewById(R.id.tblTrackerList);
        myLayout.removeAllViews();
            //String.format("Table %s:\n", tableName)
        Cursor allRows = db.rawQuery("SELECT " + Contract.Tracked.COLUMN_NAME_TITLE + ", " + Contract.Tracked.COLUMN_NAME_PRICE + ", " + Contract.Tracked.COLUMN_NAME_STOCK + " FROM " + tableName, null);
        Cursor strTag = db.rawQuery("SELECT "+ Contract.Tracked.COLUMN_NAME_TITLE + " FROM "+ tableName, null);
            //Cursor images = db.rawQuery("SELECT " + Contract.Tracked.COLUMN_NAME_IMAGE + " FROM " +tableName, null);
            //org.jsoup.nodes.Document doc = Jsoup.connect("https://www.amazon.co.uk/Magic-Gathering-14441-Kaladesh-Bundle/dp/B01LDELE0Q/ref=sr_1_1?ie=UTF8&qid=1522766138&sr=8-1").get();
            //Elements image = doc.select("img#landingImage");
            //String imgSrc = image.attr("src");
            //InputStream input = new java.net.URL(imgSrc).openStream();
            //Bitmap bitmap = BitmapFactory.decodeStream(input);

            if (allRows.moveToFirst() && strTag.moveToFirst()) {
                String[] columnNames = allRows.getColumnNames();
                String[] tags = strTag.getColumnNames();
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
                    }//end for table output

                    for (String tag : tags){
                        tagString = String.format(strTag.getString(strTag.getColumnIndex(tag)));
                        Log.e("Tag in Loop", tagString);
                    }//end for tag

                    //CREATE TEXTVIEW
                    TextView myView = new TextView(this);
                    myView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

                    //CREATE IMAGEBUTTON
                    final Button myImage = new Button(this);
                    myImage.setLayoutParams(new TableRow.LayoutParams(172, 177));
                    myImage.setTag(tagString);
                    myImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder builder2 = new AlertDialog.Builder(contextNew);
                            builder2.setTitle("Delete "+myImage.getTag()+"?");
                            Log.e("Button", myImage.getTag().toString()+" clicked");
                            builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface2, int i) {
                                    db.execSQL("DELETE FROM "+ tableName + " WHERE " + Contract.Tracked.COLUMN_NAME_TITLE +" = '"+myImage.getTag().toString()+"'");
                                    Log.e("Query info", tableName + " " + myImage.getTag().toString());
                                    getTableAsString(db, Contract.Tracked.TABLE_NAME);
                                }
                            });//END setPositiveButton
                            builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface2, int i) {
                                    dialogInterface2.cancel();
                                }
                            });//END setNegativeButton
                            builder2.show();

                        }// END onClick
                    });//END onClickListener
                    Log.e("Button tag", myImage.getTag().toString());
                    //myImage.setImageBitmap(bitmap);

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

                    tagString = "";
                    tableString = "";
                    img = "";
                    res = null;
                    imageResource = 0;
                    Log.e("print", "Layouts should be added");
                } while (allRows.moveToNext() &&strTag.moveToNext());
            }

            //return tableString;
    }//end GETTABLEASSTRING

}//END MAIN ACTIVITY