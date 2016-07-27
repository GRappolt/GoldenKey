package com.roachcitysoftware.goldenkey;

/**
 * Constants used to define database structure for Grand.db.
 * Created by GeorgeR on 9/8/2015.
 */

import android.net.Uri;
import android.provider.BaseColumns;

public class GrandContract {
    public static final String DB_NAME = "grand.db";
    public static final int DB_VERSION = 3;

    public static final String TABLE_1 = "blessings";
    // + " DESC" for descending order
//    public static final String DEFAULT_SORT_1 = BlessingsColumn.ID + " DESC";
    public static final String DEFAULT_SORT_1 = BlessingsColumn.ID;

    public class BlessingsColumn {
        public static final String ID = BaseColumns._ID;
        public static final String BLESSING = "blessing";
    }

    public static final String TABLE_2 = "history";
    public static final String DEFAULT_SORT_2 = HistoryColumn.DATE_TIME;

    public class HistoryColumn {
        public static final String ID = BaseColumns._ID;
        public static final String DATE_TIME = "dateTime";
        public static final String EVENT_TYPE = "eventType";
        public static final String EXTRA_DATA = "extraData";
    }

    // BlessingProvider specific constants
    public static final String AUTHORITY_1 = "com.roachcitysoftware.goldenkey.BlessingProvider";
    public static final Uri CONTENT_URI_1 = Uri.parse("content://" + AUTHORITY_1 + "/" + TABLE_1);
    public static final Uri CONTENT_URI_2 = Uri.parse("content://" + AUTHORITY_1 + "/" + TABLE_2);
    public static final int BLESSING_ITEM = 1;
    public static final int BLESSING_DIR = 2;
    public static final int HISTORY_ITEM = 3;
    public static final int HISTORY_DIR = 4;
    public static final String BLESSING_TYPE_ITEM =
            "vnd.android.cursor.item/vnd.com.roachcitysoftware.goldenkey.provider.blessing";
    public static final String BLESSING_TYPE_DIR =
            "vnd.android.cursor.dir/vnd.com.roachcitysoftware.goldenkey.provider.blessing";
    public static final String HISTORY_TYPE_ITEM =
            "vnd.android.cursor.item/vnd.com.roachcitysoftware.goldenkey.provider.history";
    public static final String HISTORY_TYPE_DIR =
            "vnd.android.cursor.dir/vnd.com.roachcitysoftware.goldenkey.provider.history";

}

