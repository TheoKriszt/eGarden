package fr.kriszt.theo.egarden;

import android.provider.BaseColumns;

/**
 * Created by wizehunt on 26/02/18.
 */

public final class Database {




    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private Database() {}

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_SUBTITLE = "subtitle";
    }





}
