package project.finalyear.uuj.collecomex;
/**
 * Created by Andrew on 17/03/2018.
 */
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.messaging.RemoteMessage;

import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class Parser {
    String url = "";
    //String name = "";
    String price = "";
    String stock = "";
    Boolean update = false;
    private static final String TAG = "FCM Service";
    static String folder = "ColleComImages";

    //    public static String itemRetrieve(String url){

    public static ContentValues itemRetrieve(String url){
        //String text = "";
        //TextView returnView = test;
        ContentValues values = new ContentValues();
        //final StringBuilder builder = new StringBuilder();
            try {
                //String itemName = "";
                System.setProperty("javax.net.ssl.trustStore", "C:/Users/Andrew/AndroidStudioProjects/ColleComEX/wwwamazoncouk.jks");
                Document doc = Jsoup.connect(url).get();
                String title = doc.title();
                Elements name = doc.select("span#productTitle");//name
                Elements price = doc.select("span#priceblock_ourprice");
                Elements stock = doc.select("div#availability");
                //Elements image = doc.select("img#landingImage");
                //String src = image.attr("src");
                //Log.e("IMAGE", src);
                Log.e("NAME", name.text());
                Log.e("URL FROM PARSER.JAVA", url);

                for (Element link : name) {
                    //builder.append("Name : ").append(name.text()).append("\n").append("Price : ").append(price.text()).append(stock.text());
                    values.put(Contract.Tracked.COLUMN_NAME_TITLE, name.text());
                    values.put(Contract.Tracked.COLUMN_NAME_PRICE, price.text());
                    values.put(Contract.Tracked.COLUMN_NAME_STOCK, stock.text());
                    values.put(Contract.Tracked.COLUMN_NAME_URL, url);
                    //values.put(Contract.Tracked.COLUMN_NAME_IMAGE, src);
                    //itemName = link.text();
                    //Log.d("Item name", itemName);
                }//for

                //BORUTO VOL. 3
                //Document doc = Jsoup.connect("https://www.amazon.co.uk/gp/product/1421598221/ref=s9u_wish_gw_i5?ie=UTF8&colid=2ATUR6GNVDENM&coliid=IQQ9VRF32N5PW&fpl=fresh&pd_rd_i=1421598221&pd_rd_r=0d6a390c-31ca-11e8-ad28-014ae5dc2f42&pd_rd_w=9qAQO&pd_rd_wg=lgTrH&pf_rd_i=desktop&pf_rd_s=&pf_rd_m=A3P5ROKL5A1OLE&pf_rd_r=K38JDAFF2QV1RE3MDD9Y&pf_rd_t=36701&pf_rd_p=187bec3b-0822-4044-bbe9-441718232b3f").get();
                //Document doc = Jsoup.connect("https://www.amazon.co.uk/Boruto-Vol-Naruto-Next-Generations/dp/1421598221/ref=sr_1_1?ie=UTF8&qid=1522673671&sr=8-1").get();
                //ATELIER
                //Document doc = Jsoup.connect("https://www.amazon.co.uk/dp/B01NAXX4BO/?coliid=I1K8B74ZZSF630&colid=3OICX7TVJ49C3&psc=0&ref_=lv_ov_lig_dp_it").get();
                //DQ7
                //Document doc = Jsoup.connect("https://www.amazon.co.uk/dp/B01ET83LCW/?coliid=I27YSKVBTEEMMC&colid=42HF5NOR3X0M&psc=1&ref_=lv_ov_lig_dp_it").get();
                //MTG Kaladesh
                //Document doc = Jsoup.connect("https://www.amazon.co.uk/Magic-Gathering-14441-Kaladesh-Bundle/dp/B01LDELE0Q/ref=sr_1_1?ie=UTF8&qid=1522766138&sr=8-1").get();
                //span#productTitle
            }catch (IOException e){
                //fill in at some point
            }//catch
            return values;
        }//end itemRetrieve



    public static void compareItem(SQLiteDatabase db, String tableName, String url){
        try {
            System.setProperty("javax.net.ssl.trustStore", "C:/Users/Andrew/AndroidStudioProjects/ColleComEX/wwwamazoncouk.jks");
            Document doc = null;
            doc = Jsoup.connect(url).get();
            Elements newPrice = doc.select("span#priceblock_ourprice");
            Elements newStock = doc.select("div#availability");

            String comparePrice = "";
            String compareStock = "";
            Cursor sqlPrice = db.rawQuery("SELECT "+Contract.Tracked.COLUMN_NAME_PRICE + " FROM " + tableName + " WHERE " + Contract.Tracked.COLUMN_NAME_URL + " = '" + url + "'", null);
            Cursor sqlStock = db.rawQuery("SELECT "+Contract.Tracked.COLUMN_NAME_STOCK + " FROM " + tableName + " WHERE " + Contract.Tracked.COLUMN_NAME_URL + " = '" + url + "'", null);
            if(sqlPrice.moveToFirst()  && sqlStock.moveToFirst()) {
                final String[] prices = sqlPrice.getColumnNames();
                final String[] stocks = sqlStock.getColumnNames();
                do {
                    for (String loop : prices) {
                        comparePrice = String.format(sqlPrice.getString(sqlPrice.getColumnIndex(loop)));
                        Log.e("Price in Loop", comparePrice);
                    }//end for loop
                    for(String loop2 : stocks){
                        compareStock = String.format(sqlStock.getString(sqlStock.getColumnIndex(loop2)));
                        Log.e("Stock in Loop", compareStock);
                    }//end for loop
                }while(sqlPrice.moveToNext() && sqlStock.moveToNext());//end do while
            }//end if

            Log.e("Compared Price", newPrice.text());

            for(Element link : newPrice){
                if(newPrice.text() != comparePrice){
                    db.execSQL("UPDATE "+ tableName+" SET "+Contract.Tracked.COLUMN_NAME_OLDPRICE +" = '"+Contract.Tracked.COLUMN_NAME_PRICE+"', "+Contract.Tracked.COLUMN_NAME_PRICE+" = '"+newPrice.text()+"' WHERE "+ Contract.Tracked.COLUMN_NAME_URL +" = '"+url+"';");
                    //PUSH NOTIFICATION
                    Log.e("Price Difference", "SQL Executed");
                }//end if price
                if(newStock.text() != compareStock){
                    db.execSQL("UPDATE "+ tableName+" SET "+Contract.Tracked.COLUMN_NAME_OLDSTOCK +" = '"+Contract.Tracked.COLUMN_NAME_STOCK+"', "+Contract.Tracked.COLUMN_NAME_STOCK+" = '"+newStock.text()+"' WHERE "+ Contract.Tracked.COLUMN_NAME_URL +" = '"+url+"';");
                    //PUSH NOTIFICATION
                    Log.e("Stock Difference", "SQL Executed");
                }//end if stock
            }//end for
        } catch (IOException e) {
            e.printStackTrace();
        }//end try catch

    }//end compareItem

    private static String getImage(String src) throws IOException{

        //Extract the name of the image from the src attribute
        String name = src.substring(src.lastIndexOf("/")+1);

        /*int indexname = src.lastIndexOf("/");

        if(indexname == src.length()){
            src = src.substring(1, indexname);
        }

        indexname = src.lastIndexOf("/");
        String name = src.substring(indexname, src.length());*/

        //Open a URL Stream
        URL url = new URL(src);
        InputStream in = url.openStream();

        OutputStream out = new BufferedOutputStream(new FileOutputStream(folder + "/" + name));
        String path = out.toString();
        Log.e("Stored Path", path);
        for(int i;(i=in.read()) != -1;){
            out.write(i);
        }
        out.close();
        in.close();
        return path;
    }

}//end Parser class