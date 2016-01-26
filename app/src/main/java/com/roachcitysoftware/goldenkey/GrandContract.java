package com.roachcitysoftware.goldenkey;

/**
 * Constants used to define database structure for Grand.db.
 * Created by GeorgeR on 9/8/2015.
 */

import android.net.Uri;
import android.provider.BaseColumns;

public class GrandContract {
    public static final String DB_NAME = "grand.db";
    public static final int DB_VERSION = 2;

    public static final String TABLE_1 = "blessings";
    // + " DESC" for descending order
//    public static final String DEFAULT_SORT_1 = BlessingsColumn.ID + " DESC";
    public static final String DEFAULT_SORT_1 = BlessingsColumn.ID;

    public class BlessingsColumn {
        public static final String ID = BaseColumns._ID;
        public static final String BLESSING = "blessing";
    }

    // BlessingProvider specific constants
    public static final String AUTHORITY_1 = "com.roachcitysoftware.goldenkey.BlessingProvider";
    public static final Uri CONTENT_URI_1 = Uri.parse("content://" + AUTHORITY_1 + "/" + TABLE_1);
    public static final int BLESSING_ITEM = 1;
    public static final int BLESSING_DIR = 2;
    public static final String BLESSING_TYPE_ITEM =
            "vnd.android.cursor.item/vnd.com.roachcitysoftware.goldenkey.provider.blessing";
    public static final String BLESSING_TYPE_DIR =
            "vnd.android.cursor.dir/vnd.com.roachcitysoftware.goldenkey.provider.blessing";
}

