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
        Log.e("Source check", sourceCheck);
        Log.e("New URL", url);

        if(sourceCheck.equalsIgnoreCase("https://www.a")) {
            try {
                Log.e("Souce Checked", "AMAZON");
                System.setProperty("javax.net.ssl.trustStore", "C:/Users/Andrew/AndroidStudioProjects/ColleComEX/wwwamazoncouk.jks");
                Document doc = Jsoup.connect(url).get();
                String title = doc.title();
                Log.e("doc title", title);
                Elements name = doc.select("span#productTitle");//name
                Elements price = doc.select("span#priceblock_ourprice");
                Elements stock = doc.select("div#availability");
                Log.e("NAME", name.text());
                Log.e("PRICE", price.text());
                Log.e("STOCK", stock.text());
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
                Log.e("doc title", title);
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
            }//end catch
        }else if(sourceCheck.equalsIgnoreCase("https://www.e")){
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
            }//end catch
        }else{
            return null;
        }//end if else
            return values;
    }//end itemRetrieve


    public static Boolean compareItem(SQLiteDatabase db){
        Boolean update = false;
        String sourceCheck = "";
        int index = 0;
        try {
            String strURL = "";
            Cursor crsUrl = db.rawQuery("SELECT "+Contract.Tracked.COLUMN_NAME_URL + " FROM " + Contract.Tracked.TABLE_NAME, null);
            if(crsUrl.moveToFirst()){
                final String[]urls = crsUrl.getColumnNames();
                do {
                    for (String loop : urls) {
                        strURL = crsUrl.getString(crsUrl.getColumnIndex(loop));
                        index = strURL.indexOf("https://");
                        sourceCheck = strURL.substring(index, index+13);
                        if(sourceCheck.equalsIgnoreCase("https://www.a")) {
                            System.setProperty("javax.net.ssl.trustStore", "C:/Users/Andrew/AndroidStudioProjects/ColleComEX/wwwamazoncouk.jks");
                            Document doc = null;
                            Log.e("URLS.TOSTRING", strURL);
                            doc = Jsoup.connect(strURL).get();
                            Elements newPrice = doc.select("span#priceblock_ourprice");
                            Elements newStock = doc.select("div#availability");

                            String comparePrice = "";
                            String compareStock = "";
                            Cursor sqlPrice = db.rawQuery("SELECT " + Contract.Tracked.COLUMN_NAME_PRICE + " FROM " + Contract.Tracked.TABLE_NAME + " WHERE " + Contract.Tracked.COLUMN_NAME_URL + " = '" + strURL + "'", null);
                            Cursor sqlStock = db.rawQuery("SELECT " + Contract.Tracked.COLUMN_NAME_STOCK + " FROM " + Contract.Tracked.TABLE_NAME + " WHERE " + Contract.Tracked.COLUMN_NAME_URL + " = '" + strURL + "'", null);
                            if (sqlPrice.moveToFirst() && sqlStock.moveToFirst()) {
                                final String[] prices = sqlPrice.getColumnNames();
                                final String[] stocks = sqlStock.getColumnNames();
                                do {
                                    for (String loop2 : prices) {
                                        comparePrice = sqlPrice.getString(sqlPrice.getColumnIndex(loop2));
                                        Log.e("Price in Loop", comparePrice);
                                    }//end for loop
                                    for (String loop2 : stocks) {
                                        compareStock = sqlStock.getString(sqlStock.getColumnIndex(loop2));
                                        Log.e("Stock in Loop", compareStock);
                                    }//end for loop
                                }
                                while (sqlPrice.moveToNext() && sqlStock.moveToNext());//end do while
                            }//end if
                            Log.e("New Price", newPrice.text());
                            Log.e("Compared Price", comparePrice);
                            for (Element link : newPrice) {
                                if (!newPrice.text().equalsIgnoreCase(comparePrice)) {
                                    db.execSQL("UPDATE " + Contract.Tracked.TABLE_NAME + " SET " + Contract.Tracked.COLUMN_NAME_OLDPRICE + " = '" + Contract.Tracked.COLUMN_NAME_PRICE + "', " + Contract.Tracked.COLUMN_NAME_PRICE + " = '" + newPrice.text() + "' WHERE " + Contract.Tracked.COLUMN_NAME_URL + " = '" + strURL + "';");
                                    update = true;
                                    Log.e("Price Difference", "SQL Executed");
                                }//end if price
                                if (!newStock.text().equalsIgnoreCase(compareStock)) {
                                    db.execSQL("UPDATE " + Contract.Tracked.TABLE_NAME + " SET " + Contract.Tracked.COLUMN_NAME_OLDSTOCK + " = '" + Contract.Tracked.COLUMN_NAME_STOCK + "', " + Contract.Tracked.COLUMN_NAME_STOCK + " = '" + newStock.text() + "' WHERE " + Contract.Tracked.COLUMN_NAME_URL + " = '" + strURL + "';");
                                    update = true;
                                    Log.e("Stock Difference", "SQL Executed");
                                }//end if stock
                            }//end for
                            sqlPrice.close();
                            sqlStock.close();
                        }else if(sourceCheck.equalsIgnoreCase("https://rover")){
                            System.setProperty("javax.net.ssl.trustStore", "C:/Users/Andrew/AndroidStudioProjects/ColleComEX/wwwebaycom.jks");
                            Document doc = null;
                            Log.e("URLS.TOSTRING", strURL);
                            doc = Jsoup.connect(strURL).get();
                            Elements newPrice = doc.select("span#prcIsum");
                            Elements newStock = doc.select("span#vi-cdown_timeLeft");

                            String comparePrice = "";
                            String compareStock = "";
                            Cursor sqlPrice = db.rawQuery("SELECT " + Contract.Tracked.COLUMN_NAME_PRICE + " FROM " + Contract.Tracked.TABLE_NAME + " WHERE " + Contract.Tracked.COLUMN_NAME_URL + " = '" + strURL + "'", null);
                            Cursor sqlStock = db.rawQuery("SELECT " + Contract.Tracked.COLUMN_NAME_STOCK + " FROM " + Contract.Tracked.TABLE_NAME + " WHERE " + Contract.Tracked.COLUMN_NAME_URL + " = '" + strURL + "'", null);
                            if (sqlPrice.moveToFirst() && sqlStock.moveToFirst()) {
                                final String[] prices = sqlPrice.getColumnNames();
                                final String[] stocks = sqlStock.getColumnNames();
                                do {
                                    for (String loop2 : prices) {
                                        comparePrice = sqlPrice.getString(sqlPrice.getColumnIndex(loop2));
                                        Log.e("Price in Loop", comparePrice);
                                    }//end for loop
                                    for (String loop2 : stocks) {
                                        compareStock = sqlStock.getString(sqlStock.getColumnIndex(loop2));
                                        Log.e("Stock in Loop", compareStock);
                                    }//end for loop
                                }
                                while (sqlPrice.moveToNext() && sqlStock.moveToNext());//end do while
                            }//end if
                            Log.e("New Price", newPrice.text());
                            Log.e("Compared Price", comparePrice);
                            for (Element link : newPrice) {
                                if (!newPrice.text().equalsIgnoreCase(comparePrice)) {
                                    db.execSQL("UPDATE " + Contract.Tracked.TABLE_NAME + " SET " + Contract.Tracked.COLUMN_NAME_OLDPRICE + " = '" + Contract.Tracked.COLUMN_NAME_PRICE + "', " + Contract.Tracked.COLUMN_NAME_PRICE + " = '" + newPrice.text() + "' WHERE " + Contract.Tracked.COLUMN_NAME_URL + " = '" + strURL + "';");
                                    update = true;
                                    Log.e("Price Difference", "SQL Executed");
                                }//end if price
                                if (!newStock.text().equalsIgnoreCase(compareStock)) {
                                    db.execSQL("UPDATE " + Contract.Tracked.TABLE_NAME + " SET " + Contract.Tracked.COLUMN_NAME_OLDSTOCK + " = '" + Contract.Tracked.COLUMN_NAME_STOCK + "', " + Contract.Tracked.COLUMN_NAME_STOCK + " = '" + newStock.text() + "' WHERE " + Contract.Tracked.COLUMN_NAME_URL + " = '" + strURL + "';");
                                    update = true;
                                    Log.e("Stock Difference", "SQL Executed");
                                }//end if stock
                            }//end for
                            sqlPrice.close();
                            sqlStock.close();
                        }else if(sourceCheck.equalsIgnoreCase("https://www.e")){
                            System.setProperty("javax.net.ssl.trustStore", "C:/Users/Andrew/AndroidStudioProjects/ColleComEX/wwwebaycom.jks");
                            Document doc = null;
                            Log.e("URLS.TOSTRING", strURL);
                            doc = Jsoup.connect(strURL).get();
                            Elements newPrice = doc.select("span#prcIsum");
                            Elements newStock = doc.select("span#vi-cdown_timeLeft");

                            String comparePrice = "";
                            String compareStock = "";
                            Cursor sqlPrice = db.rawQuery("SELECT " + Contract.Tracked.COLUMN_NAME_PRICE + " FROM " + Contract.Tracked.TABLE_NAME + " WHERE " + Contract.Tracked.COLUMN_NAME_URL + " = '" + strURL + "'", null);
                            Cursor sqlStock = db.rawQuery("SELECT " + Contract.Tracked.COLUMN_NAME_STOCK + " FROM " + Contract.Tracked.TABLE_NAME + " WHERE " + Contract.Tracked.COLUMN_NAME_URL + " = '" + strURL + "'", null);
                            if (sqlPrice.moveToFirst() && sqlStock.moveToFirst()) {
                                final String[] prices = sqlPrice.getColumnNames();
                                final String[] stocks = sqlStock.getColumnNames();
                                do {
                                    for (String loop2 : prices) {
                                        comparePrice = sqlPrice.getString(sqlPrice.getColumnIndex(loop2));
                                        Log.e("Price in Loop", comparePrice);
                                    }//end for loop
                                    for (String loop2 : stocks) {
                                        compareStock = sqlStock.getString(sqlStock.getColumnIndex(loop2));
                                        Log.e("Stock in Loop", compareStock);
                                    }//end for loop
                                }
                                while (sqlPrice.moveToNext() && sqlStock.moveToNext());//end do while
                            }//end if
                            Log.e("New Price", newPrice.text());
                            Log.e("Compared Price", comparePrice);
                            for (Element link : newPrice) {
                                if (!newPrice.text().equalsIgnoreCase(comparePrice)) {
                                    db.execSQL("UPDATE " + Contract.Tracked.TABLE_NAME + " SET " + Contract.Tracked.COLUMN_NAME_OLDPRICE + " = '" + Contract.Tracked.COLUMN_NAME_PRICE + "', " + Contract.Tracked.COLUMN_NAME_PRICE + " = '" + newPrice.text() + "' WHERE " + Contract.Tracked.COLUMN_NAME_URL + " = '" + strURL + "';");
                                    update = true;
                                    Log.e("Price Difference", "SQL Executed");
                                }//end if price
                                if (!newStock.text().equalsIgnoreCase(compareStock)) {
                                    db.execSQL("UPDATE " + Contract.Tracked.TABLE_NAME + " SET " + Contract.Tracked.COLUMN_NAME_OLDSTOCK + " = '" + Contract.Tracked.COLUMN_NAME_STOCK + "', " + Contract.Tracked.COLUMN_NAME_STOCK + " = '" + newStock.text() + "' WHERE " + Contract.Tracked.COLUMN_NAME_URL + " = '" + strURL + "';");
                                    update = true;
                                    Log.e("Stock Difference", "SQL Executed");
                                }//end if stock
                            }//end for
                            sqlPrice.close();
                            sqlStock.close();
                        }//end else if
                    }//end URL FOR
                }while(crsUrl.moveToNext());
            }//end URL IF
            crsUrl.close();
        } catch (IOException e) {
            e.printStackTrace();
        }//end try catch
        return update;
    }//end compareItem

}//end Parser class