package project.finalyear.uuj.collecomex;
/**
 * Created by Andrew on 17/03/2018.
 */

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;

public class Parser {

    public static ContentValues itemRetrieve(String givenUrl){
        ContentValues values = new ContentValues();
        Log.e("givenURL", givenUrl);
        int index = givenUrl.indexOf("https://");
        String url = givenUrl.substring(index, givenUrl.length());
        String sourceCheck = givenUrl.substring(index, index+13);
        //String sourceCheck2 = givenUrl.substring(index, index+12);
        Log.e("Source check", sourceCheck);
        //Log.e("Source check 2", sourceCheck2);

        Log.e("New URL", url);

        if(sourceCheck.equalsIgnoreCase("https://www.a")) {
            try {
                Log.e("Souce Checked", "AMAZON");
                System.setProperty("javax.net.ssl.trustStore", "C:/Users/Andrew/AndroidStudioProjects/ColleComEX/wwwamazoncouk.jks");
                Document doc = Jsoup.connect(url).get();
                String title = doc.title();
                Elements name = doc.select("span#productTitle");//name
                Elements price = doc.select("span#priceblock_ourprice");
                Elements stock = doc.select("div#availability");
                Log.e("NAME", name.text());
                Log.e("URL FROM PARSER.JAVA", url);

                for (Element link : name) {
                    values.put(Contract.Tracked.COLUMN_NAME_TITLE, name.text());
                    values.put(Contract.Tracked.COLUMN_NAME_PRICE, price.text());
                    values.put(Contract.Tracked.COLUMN_NAME_STOCK, stock.text());
                    values.put(Contract.Tracked.COLUMN_NAME_URL, url);
                }//for
            }catch (IOException e){
                    Log.i("IOException", e.toString());
            }//catch
        }else if(sourceCheck.equalsIgnoreCase("https://rover")){
            try {
                Log.e("Source Checked", "EBAY");
                System.setProperty("javax.net.ssl.trustStore", "C:/Users/Andrew/AndroidStudioProjects/ColleComEX/wwwebaycom.jks");
                Document doc = Jsoup.connect(url).get();
                String title = doc.title();
                Elements name = doc.select("h1#itemTitle.it-ttl");//name
                int newIndex = name.text().indexOf("Details about ");
                String newName = name.text().substring(newIndex+14, name.text().length());
                Log.e("newName", newName);
                Elements price = doc.select("span#prcIsum");
                Elements stock = doc.select("span#vi-cdown_timeLeft");
                Log.e("NAME", name.text());
                Log.e("PRICE eBay", price.text());
                Log.e("URL FROM PARSER.JAVA", url);

                for (Element link : name) {
                    values.put(Contract.Tracked.COLUMN_NAME_TITLE, newName);
                    values.put(Contract.Tracked.COLUMN_NAME_PRICE, price.text());
                    values.put(Contract.Tracked.COLUMN_NAME_STOCK, stock.text());
                    values.put(Contract.Tracked.COLUMN_NAME_URL, url);
                }//for
            }catch (IOException e){
                Log.i("IOException", e.toString());
            }
        }//end if

            return values;
        }//end itemRetrieve


    public static Boolean compareItem(SQLiteDatabase db){
        Boolean update = false;
        try {
            String strURL = "";
            Cursor url = db.rawQuery("SELECT "+Contract.Tracked.COLUMN_NAME_URL + " FROM " + Contract.Tracked.TABLE_NAME, null);
            if(url.moveToFirst()){
                final String[]urls = url.getColumnNames();
                do {
                    for (String loop : urls) {
                        strURL = String.format(url.getString(url.getColumnIndex(loop)));
                        System.setProperty("javax.net.ssl.trustStore", "C:/Users/Andrew/AndroidStudioProjects/ColleComEX/wwwamazoncouk.jks");
                        Document doc = null;
                        Log.e("URLS.TOSTRING", strURL);
                        doc = Jsoup.connect(strURL).get();
                        Elements newPrice = doc.select("span#priceblock_ourprice");
                        Elements newStock = doc.select("div#availability");

                        String comparePrice = "";
                        String compareStock = "";
                        Cursor sqlPrice = db.rawQuery("SELECT "+Contract.Tracked.COLUMN_NAME_PRICE + " FROM " + Contract.Tracked.TABLE_NAME + " WHERE " + Contract.Tracked.COLUMN_NAME_URL + " = '" + strURL + "'", null);
                        Cursor sqlStock = db.rawQuery("SELECT "+Contract.Tracked.COLUMN_NAME_STOCK + " FROM " + Contract.Tracked.TABLE_NAME + " WHERE " + Contract.Tracked.COLUMN_NAME_URL + " = '" + strURL + "'", null);
                            if(sqlPrice.moveToFirst()  && sqlStock.moveToFirst()) {
                                final String[] prices = sqlPrice.getColumnNames();
                                final String[] stocks = sqlStock.getColumnNames();
                                do {
                                    for (String loop2 : prices) {
                                        comparePrice = String.format(sqlPrice.getString(sqlPrice.getColumnIndex(loop2)));
                                        Log.e("Price in Loop", comparePrice);
                                    }//end for loop
                                    for(String loop2 : stocks){
                                        compareStock = String.format(sqlStock.getString(sqlStock.getColumnIndex(loop2)));
                                        Log.e("Stock in Loop", compareStock);
                                    }//end for loop
                                }while(sqlPrice.moveToNext() && sqlStock.moveToNext());//end do while
                            }//end if
                            Log.e("New Price", newPrice.text());
                            Log.e("Compared Price", comparePrice);
                            for(Element link : newPrice){
                                if(!newPrice.text().equalsIgnoreCase(comparePrice)){
                                    db.execSQL("UPDATE "+ Contract.Tracked.TABLE_NAME +" SET "+Contract.Tracked.COLUMN_NAME_OLDPRICE +" = '"+Contract.Tracked.COLUMN_NAME_PRICE+"', "+Contract.Tracked.COLUMN_NAME_PRICE+" = '"+newPrice.text()+"' WHERE "+ Contract.Tracked.COLUMN_NAME_URL +" = '"+strURL+"';");
                                    update = true;
                                    Log.e("Price Difference", "SQL Executed");
                                }//end if price
                                if(!newStock.text().equalsIgnoreCase(compareStock)){
                                    db.execSQL("UPDATE "+ Contract.Tracked.TABLE_NAME +" SET "+Contract.Tracked.COLUMN_NAME_OLDSTOCK +" = '"+Contract.Tracked.COLUMN_NAME_STOCK+"', "+Contract.Tracked.COLUMN_NAME_STOCK+" = '"+newStock.text()+"' WHERE "+ Contract.Tracked.COLUMN_NAME_URL +" = '"+strURL+"';");
                                    update = true;
                                    Log.e("Stock Difference", "SQL Executed");
                                }//end if stock
                            }//end for
                    }//end URL FOR
                }while(url.moveToNext());
            }//end URL IF
        } catch (IOException e) {
            e.printStackTrace();
        }//end try catch
        return update;
    }//end compareItem

}//end Parser class