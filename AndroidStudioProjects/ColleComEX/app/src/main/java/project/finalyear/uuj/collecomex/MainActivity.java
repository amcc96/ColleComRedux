package project.finalyear.uuj.collecomex;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.*;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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
                        //Parser.compareItem(dbRead);
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
        //contextNew.deleteDatabase(mDbHelper.getDatabaseName()); //FAILSAFE DELETE DATABASE

        Intent i = new Intent(contextNew, TrackerMessagingService.class);
        contextNew.startService(i);

        //GET TABLE AS STRING
        getTableAsString(dbRead, Contract.Tracked.TABLE_NAME);

    }//end onCreate

    private void enableStrictMode(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
    }//end enableStrictMode

    private void getTableAsString(final SQLiteDatabase db, final String tableName) {
        Log.d("Method", "getTableAsString called");
        String tableString = "";
        String tagString = "";
        int imageResource;
        Drawable res = null;
        TableLayout myLayout = findViewById(R.id.tblTrackerList);
        myLayout.removeAllViews();
        Cursor allRows = db.rawQuery("SELECT " + Contract.Tracked.COLUMN_NAME_TITLE + ", " + Contract.Tracked.COLUMN_NAME_PRICE + ", " + Contract.Tracked.COLUMN_NAME_STOCK + " FROM " + tableName, null);
        Cursor strTag = db.rawQuery("SELECT "+ Contract.Tracked._ID + " FROM "+ tableName, null);

            if (allRows.moveToFirst() && strTag.moveToFirst()) {
                final String[] columnNames = allRows.getColumnNames();
                final String[] tags = strTag.getColumnNames();
                do {
                    for (String name : columnNames) {
                        //PRINT TO TEXTVIEW
                        tableString += String.format(allRows.getString(allRows.getColumnIndex(name)));
                        tableString += "\n";
                        Log.v("tableString", tableString);
                    }//end for table output
                    for (String tag : tags){
                        tagString = String.format(strTag.getString(strTag.getColumnIndex(tag)));
                        Log.e("Tag in Loop", tagString);
                    }//end for tag

                    //CREATE TABLEROW
                    TableRow myRow = new TableRow(this);
                    myRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));

                    //CREATE IMAGEBUTTON
                    final Button myImage = new Button(this);
                    myImage.setLayoutParams(new TableRow.LayoutParams(172, 177));
                    myImage.setTag(tagString);
                    myImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String tagTitle = "";
                            AlertDialog.Builder builder2 = new AlertDialog.Builder(contextNew);
                            Cursor sqlTitle = db.rawQuery("SELECT "+Contract.Tracked.COLUMN_NAME_TITLE + " FROM " + tableName + " WHERE " + Contract.Tracked._ID + " = '" + myImage.getTag().toString()+"'", null);
                            if(sqlTitle.moveToFirst()) {
                                final String[] titles = sqlTitle.getColumnNames();
                                do {
                                    for (String title : titles) {
                                        tagTitle = String.format(sqlTitle.getString(sqlTitle.getColumnIndex(title)));
                                        Log.e("Tag in Loop", tagTitle);
                                    }//end for tag
                                }while(sqlTitle.moveToNext());//end do while
                            }//end if
                            builder2.setTitle("Delete "+tagTitle+"?");
                            Log.e("Button", myImage.getTag().toString()+" clicked");
                            builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface2, int i) {
                                    db.execSQL("DELETE FROM "+ tableName + " WHERE " + Contract.Tracked._ID +" = '"+myImage.getTag().toString()+"'");
                                    Log.e("Query info", tableName + " " + myImage.getTag().toString());
                                    getTableAsString(db, Contract.Tracked.TABLE_NAME);
                                }
                            });//END setPositiveButton
                            builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface2, int i) {
                                    dialogInterface2.cancel();
                                }//end onClick
                            });//END setNegativeButton
                            builder2.show();
                        }// END onClick
                    });//END onClickListener

                    //CREATE TEXTVIEW
                    TextView myView = new TextView(this);
                    myView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                    myView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String urlClick = "";
                            Cursor sqlURL = db.rawQuery("SELECT "+Contract.Tracked.COLUMN_NAME_URL + " FROM "+ tableName+ " WHERE " + Contract.Tracked._ID + " = " + myImage.getTag().toString(),null);
                            if(sqlURL.moveToFirst()){
                                final String[] urls = sqlURL.getColumnNames();
                                do{
                                    for(String url : urls){
                                        urlClick = String.format(sqlURL.getString(sqlURL.getColumnIndex(url)));
                                        Log.e("URL to open", urlClick);
                                    }
                                }while(sqlURL.moveToNext());
                            }//end if
                            openItem(urlClick);
                        }//end onClick
                    });//end onClickListener

                    Log.e("Button tag", myImage.getTag().toString());
                    //myImage.setImageBitmap(bitmap);

                    //ADD NEW VIEWS
                    myView.setText(tableString);
                    //myLayout.addView(myView);
                    myRow.addView(myImage);
                    myRow.addView(myView);
                    myLayout.addView(myRow, new TableLayout.LayoutParams(TabLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));

                    tagString = "";
                    tableString = "";
                    Log.e("print", "Layouts should be added");
                } while (allRows.moveToNext() &&strTag.moveToNext());
            }//en if
    }//end GETTABLEASSTRING

    private void openItem(String url){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}//END MAIN ACTIVITY