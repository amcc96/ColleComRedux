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
import android.view.Menu;
import android.view.MenuItem;
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
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.sub_menu, menu);
        return true;
    }//end onCreateOptionsMenu

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        final Contract.TrackerDbHelper mDbHelper = new Contract.TrackerDbHelper(contextNew);
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final SQLiteDatabase dbRead = mDbHelper.getReadableDatabase();
        switch(item.getItemId()){
            case R.id.refresh:
                Parser.compareItem(dbRead);
                getTableAsString(db, Contract.Tracked.TABLE_NAME);
                break;
                //END REFRESH BUTTON
            case R.id.credits:
                final AlertDialog.Builder creditsBuilder = new AlertDialog.Builder(contextNew);
                creditsBuilder.setTitle("Credits");
                final TextView creditsText = new TextView(contextNew);
                creditsText.setText("\n   Andrew McCollam (B00665460)\n   Final Year Project 2017/18 \n   Mentor: Roy Steritt");
                creditsBuilder.setView(creditsText);
                creditsBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }//end onClick
                });//end setPositiveButton
                creditsBuilder.show();
                //END CREDITS BUTTON
                break;
            case R.id.help:
                final AlertDialog.Builder helpBuilder = new AlertDialog.Builder(contextNew);
                helpBuilder.setTitle("Help");
                final TextView helpText = new TextView(contextNew);
                helpText.setText("\n    How to use this app:\n     1) Copy a URL from either eBay or Amazon. \n          This can be found in each app's \n         'share' button " +
                                "\n\n     2) Click on the '+' in the bottom right of this \n         app's main screen \n\n    3) Paste the link in the dialog box, and click 'OK'" +
                                "\n\n     4) Tracking will now begin automatically \n\n     5) To delete an item, click on the button \n         to the left of it" +
                                "\n\n     6) To open an item in your browser, click \n           on the item's text");
                helpBuilder.setView(helpText);
                helpBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }//end onClick
                });//end setPositiveButton
                helpBuilder.show();
                //END HELP BUTTON
                break;
                default:
                    return super.onOptionsItemSelected(item);
        }//end switch
        return true;
    }//end onOptionsItemSelected

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
                        int index = m_Text.indexOf("https://");

                        String url = m_Text.substring(index, m_Text.length());
                        String sourceCheck = m_Text.substring(index, index+13);
                        Log.e("MainActivity.java src", sourceCheck);
                        if(sourceCheck.equalsIgnoreCase("https://www.a") || sourceCheck.equalsIgnoreCase("https://rover") || sourceCheck.equalsIgnoreCase("https://www.e")) {
                            long newRowId = db.insert(Contract.Tracked.TABLE_NAME, null, Parser.itemRetrieve(m_Text)); //PARSER LINE 2
                            //Parser.compareItem(dbRead);
                            getTableAsString(db, Contract.Tracked.TABLE_NAME);
                            Log.e("INPUT TEXT", m_Text);
                        }else {
                            dialogInterface.cancel();
                            final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(contextNew);
                            alertBuilder.setTitle("ALERT!");
                            final TextView alertText = new TextView(contextNew);
                            alertText.setText("\n   Error: Invalid URL, please use from either \n   eBay or Amazon");
                            alertBuilder.setView(alertText);
                            alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }//end onClick
                            });//end setPositiveButton
                            alertBuilder.show();
                        }//end if else
                    }//end onClick
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
                                        urlClick = (sqlURL.getString(sqlURL.getColumnIndex(url)));
                                        Log.e("URL to open", urlClick);
                                    }
                                }while(sqlURL.moveToNext());
                            }//end if
                            openItem(urlClick);
                            sqlURL.close();
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