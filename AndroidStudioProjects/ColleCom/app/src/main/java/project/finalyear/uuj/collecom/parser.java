package project.finalyear.uuj.collecom;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by Andrew on 04/03/2018.
 */

public class parser {

    String url = "";
    //String name = "";
    String price = "";
    String stock = "";

    public static void itemRetrieve(String url){
        try {
            String itemName;
            Document doc = Jsoup.connect("https://www.amazon.co.uk/gp/product/B077GTMBFN/ref=s9u_cartx_gw_i1?ie=UTF8&fpl=fresh&pd_rd_i=B077GTMBFN&pd_rd_r=b615c0f2-1fc4-11e8-b2a8-3bfa5c4f60e1&pd_rd_w=72hOc&pd_rd_wg=2FaIj&pf_rd_m=A3P5ROKL5A1OLE&pf_rd_s=&pf_rd_r=8RV0RPXHNTKEZXSCB9D3&pf_rd_t=36701&pf_rd_p=f3df9628-2b30-4071-ac96-e7086fd86f1d&pf_rd_i=desktop").get();
            Elements taglinks = doc.select("span#productTitle");//name
            for (Element link : taglinks) {
                itemName = link.text();
            }//for
        }catch (IOException e){
            //fill in at some point
        }
    }
// https://www.amazon.co.uk/gp/product/B077GTMBFN/ref=s9u_cartx_gw_i1?ie=UTF8&fpl=fresh&pd_rd_i=B077GTMBFN&pd_rd_r=b615c0f2-1fc4-11e8-b2a8-3bfa5c4f60e1&pd_rd_w=72hOc&pd_rd_wg=2FaIj&pf_rd_m=A3P5ROKL5A1OLE&pf_rd_s=&pf_rd_r=8RV0RPXHNTKEZXSCB9D3&pf_rd_t=36701&pf_rd_p=f3df9628-2b30-4071-ac96-e7086fd86f1d&pf_rd_i=desktop
}//end Jsoup class
