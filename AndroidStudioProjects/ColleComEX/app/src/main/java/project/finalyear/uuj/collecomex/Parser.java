package project.finalyear.uuj.collecomex;
/**
 * Created by Andrew on 17/03/2018.
 */
import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Parser {
    String url = "";
    //String name = "";
    String price = "";
    String stock = "";

    //    public static String itemRetrieve(String url){

    public static ContentValues itemRetrieve(){
        //String text = "";
        //TextView returnView = test;
        ContentValues values = new ContentValues();
        //final StringBuilder builder = new StringBuilder();
            try {
                //String itemName = "";
                System.setProperty("javax.net.ssl.trustStore", "C:/Users/Andrew/AndroidStudioProjects/ColleComEX/wwwamazoncouk.jks");
                Document doc = Jsoup.connect("https://www.amazon.co.uk/dp/B01ET83LCW/?coliid=I27YSKVBTEEMMC&colid=42HF5NOR3X0M&psc=1&ref_=lv_ov_lig_dp_it").get();
                String title = doc.title();
                Elements name = doc.select("span#productTitle");//name
                Elements price = doc.select("span#priceblock_ourprice");
                Elements stock = doc.select("div#availability");
                for (Element link : name) {
                    //builder.append("Name : ").append(name.text()).append("\n").append("Price : ").append(price.text()).append(stock.text());
                    values.put(Contract.Tracked.COLUMN_NAME_TITLE, name.text());
                    values.put(Contract.Tracked.COLUMN_NAME_PRICE, price.text());
                    values.put(Contract.Tracked.COLUMN_NAME_STOCK, stock.text());
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
                //span#productTitle
            }catch (IOException e){
                //fill in at some point
            }//catch
            return values;
        }//end itemRetrieve
}//end Parser class