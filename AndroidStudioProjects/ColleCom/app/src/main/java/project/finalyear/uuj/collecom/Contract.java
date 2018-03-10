package project.finalyear.uuj.collecom;

import android.provider.BaseColumns;

/**
 * Created by Andrew on 01/03/2018.
 */

public final class Contract {

        // To prevent someone from accidentally instantiating the contract class,
        // make the constructor private.
        private Contract() {}

        /* Inner class that defines the table contents */
        public static class Tracked implements BaseColumns {
            public static final String TABLE_NAME = "tracked";
            public static final String COLUMN_NAME_TITLE = "title";
            public static final String COLUMN_NAME_IMAGE = "image";
            public static final String COLUMN_NAME_PRICE = "price";
            public static final String COLUMN_NAME_STOCK = "stock";
            public static final String COLUMN_NAME_OLDSTOCK ="oldStock";
            public static final String COLUMN_NAME_OLDPRICE ="oldPrice";
        }//end Tracked class
//test comment
}//end Contract class
