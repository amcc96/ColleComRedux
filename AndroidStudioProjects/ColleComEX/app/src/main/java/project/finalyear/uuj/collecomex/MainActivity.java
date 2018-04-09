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
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private String m_Text = "";
    final Context contextNew = this;

    /**
     * Creates a sub menu on the app's main screen, with the default '3 dots' icon.
     * Returns a boolean value so that other reactions can be built on it in future.
     * The menu that is passed in must be the menu where the options menu is required to appear.
     * True is returned in all cases of this method to prevent crashing.
     *
     * @param menu menu variable where the sub menu is to appear
     * @return     boolean value to enable future building on this method
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.sub_menu, menu);
        return true;
    }//end onCreateOptionsMenu

    /**
     * Returns a boolean value that can be used to build on further reactions to any selected option.
     * The item must be a valid MenuItem object. This item's ID determines the output of this
     * method via a switch/case, with R.id.refresh performing compareItem and getTableAsString methods, R.id.credits
     * displaying a dialog box with the app's credits, and R.id.help displaying a dialog box
     * with a tutorial on how to use the app.
     * True is returned regardless of the item input, in case an error occurs and the default case
     * is called.
     *
     * @param item an absolute MenuItem with a valid ID
     * @return     boolean value; true
     */
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

    /**
     * The applications 'main method' which runs any time the app is opened.
     * This method begins by running enableStrictMode(), and creating an onClickListener
     * for the action button on the main screen. This action button creates a dialog box
     * where the user may input a valid URL and click 'OK' for the system to begin
     * scraping relevant information from the URL destination. There is a check within
     * this dialog box to ensure that the URL is valid, and if it is not, the app displays
     * a new dialog box informing the user that the URL was not valid and that they should try
     * again.
     * This method then begins the TrackerMessagingService service before calling getTableAsString()
     *
     * @param savedInstanceState    enables the restoration of a previous state
     */
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
                    }//end onClick
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

    /**
     * This method sets the network policy to allow all possible permissions
     */
    private void enableStrictMode(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }//end enableStrictMode

    /**
     * This method is used to read and print the SQLite database contents.
     * Using rawQuery(), the contents of the table are output into a Cursor object,
     * which is then converted into a String[]. From there, the contents of the String[]
     * are printed into dynamically created Views and added to the TableLayout constraint
     * in content_main.xml.
     * Each item from the database is output as a row in the TableLayout as text in a
     * TextView, with a delete button next to them. This delete button is created at the
     * same time as the other Views and has a set tag equal to the ID of its opposing item.
     * Using this tag, SQL is executed to delete that row from the table, based on its ID,
     * after the user confirms the action from a dialog box.
     *
     * @param db            the readable database object
     * @param tableName     the name of the table that is to be searched and output
     */
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
        try {
            if (allRows.moveToFirst() && strTag.moveToFirst()) {
                final String[] columnNames = allRows.getColumnNames();
                final String[] tags = strTag.getColumnNames();
                do {
                    for (String name : columnNames) {
                        //PRINT TO TEXTVIEW
                        tableString += (allRows.getString(allRows.getColumnIndex(name)));
                        tableString += "\n";
                        Log.v("tableString", tableString);
                    }//end for table output
                    for (String tag : tags) {
                        tagString = (strTag.getString(strTag.getColumnIndex(tag)));
                        Log.e("Tag in Loop", tagString);
                    }//end for tag

                    //CREATE TABLEROW
                    TableRow myRow = new TableRow(this);
                    myRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));

                    //CREATE IMAGEBUTTON
                    final ImageButton myImage = new ImageButton(this);
                    myImage.setLayoutParams(new TableRow.LayoutParams(172, 177));
                    myImage.setTag(tagString);
                    myImage.setImageResource(R.drawable.delete_icon);
                    myImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String tagTitle = "";
                            AlertDialog.Builder builder2 = new AlertDialog.Builder(contextNew);
                            Cursor sqlTitle = db.rawQuery("SELECT " + Contract.Tracked.COLUMN_NAME_TITLE + " FROM " + tableName + " WHERE " + Contract.Tracked._ID + " = '" + myImage.getTag().toString() + "'", null);
                            if (sqlTitle.moveToFirst()) {
                                final String[] titles = sqlTitle.getColumnNames();
                                do {
                                    for (String title : titles) {
                                        tagTitle = String.format(sqlTitle.getString(sqlTitle.getColumnIndex(title)));
                                        Log.e("Tag in Loop", tagTitle);
                                    }//end for tag
                                } while (sqlTitle.moveToNext());//end do while
                            }//end if
                            builder2.setTitle("Delete " + tagTitle + "?");
                            Log.e("Button", myImage.getTag().toString() + " clicked");
                            builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface2, int i) {
                                    db.execSQL("DELETE FROM " + tableName + " WHERE " + Contract.Tracked._ID + " = '" + myImage.getTag().toString() + "'");
                                    Log.e("Query info", tableName + " " + myImage.getTag().toString());
                                    getTableAsString(db, Contract.Tracked.TABLE_NAME);
                                }//end onClick
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
                            Cursor sqlURL = db.rawQuery("SELECT " + Contract.Tracked.COLUMN_NAME_URL + " FROM " + tableName + " WHERE " + Contract.Tracked._ID + " = " + myImage.getTag().toString(), null);
                            if (sqlURL.moveToFirst()) {
                                final String[] urls = sqlURL.getColumnNames();
                                do {
                                    for (String url : urls) {
                                        urlClick = (sqlURL.getString(sqlURL.getColumnIndex(url)));
                                        Log.e("URL to open", urlClick);
                                    }//end for
                                } while (sqlURL.moveToNext());
                            }//end if
                            openItem(urlClick);
                            sqlURL.close();
                        }//end onClick
                    });//end onClickListener
                    Log.e("Button tag", myImage.getTag().toString());

                    //ADD NEW VIEWS
                    myView.setText(tableString);
                    //myLayout.addView(myView);
                    myRow.addView(myImage);
                    myRow.addView(myView);
                    myLayout.addView(myRow, new TableLayout.LayoutParams(TabLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));

                    tagString = "";
                    tableString = "";
                    Log.e("print", "Layouts should be added");
                } while (allRows.moveToNext() && strTag.moveToNext());
            }//end if
        }catch(Exception e){
            Log.v("getTableAsString()", "Caught: " +e.toString());
        }//end try catch
    }//end GETTABLEASSTRING

    /**
     *  This method opens the passed URL in the user's default web browser.
     *
     * @param url   valid url passed to the web browser
     */
    private void openItem(String url){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        try {
            startActivity(intent);
        }catch(Exception i){
            Log.v("openItem()", "Caught:" + i.toString());
        }//end try catch
    }//emd openItem

}//END MAIN ACTIVITY