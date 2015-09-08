package com.roachcitysoftware.goldenkey;

/**
 * Constants used to define database structure for Grand.db.
 * Created by GeorgeR on 9/8/2015.
 */

import android.net.Uri;
import android.provider.BaseColumns;

public class GrandContract {
    public static final String DB_NAME = "grand.db";
    public static final int DB_VERSION = 1;

    public static final String TABLE_1 = "blessings";
    public static final String DEFAULT_SORT_1 = BlessingsColumn.ID + " DESC";

    public class BlessingsColumn {
        public static final String ID = BaseColumns._ID;
        public static final String BLESSING = "blessing";
    }
}
