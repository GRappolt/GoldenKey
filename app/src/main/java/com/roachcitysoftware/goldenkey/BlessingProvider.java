package com.roachcitysoftware.goldenkey;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class BlessingProvider extends ContentProvider {
    private static final String TAG = BlessingProvider.class.getSimpleName();
    private GrandDbHelper grandDbHelper;
    private ArrayList<BlessingEntry> localBlessings;
    private static long lastID;

    private class BlessingEntry {
        public long ID;
        public String blessing;
    }

    private static final UriMatcher sURIMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(GrandContract.AUTHORITY_1, GrandContract.TABLE_1,
                GrandContract.BLESSING_DIR);
        sURIMatcher.addURI(GrandContract.AUTHORITY_1, GrandContract.TABLE_1
                + "/#", GrandContract.BLESSING_ITEM);
    }

    public BlessingProvider() {
        localBlessings = new ArrayList<>();
     }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        switch (sURIMatcher.match(uri)) {
            case GrandContract.BLESSING_DIR:
                Log.d(TAG, "gotType: " + GrandContract.BLESSING_TYPE_DIR);
                return GrandContract.BLESSING_TYPE_DIR;
            case GrandContract.BLESSING_ITEM:
                Log.d(TAG, "gotType: " + GrandContract.BLESSING_TYPE_ITEM);
                return GrandContract.BLESSING_TYPE_ITEM;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri ret = null;

        // Assert correct uri - can't be for specific ID, not known until inssert() returns!
        if (sURIMatcher.match(uri) != GrandContract.BLESSING_DIR) {
            throw new IllegalArgumentException("Illegal uri: " + uri);
        }

        SQLiteDatabase db = grandDbHelper.getWritableDatabase();
        long rowId = db.insertWithOnConflict(GrandContract.TABLE_1, null,
                values, SQLiteDatabase.CONFLICT_IGNORE);

        // Was insert successful?
        if (rowId != -1) {
            long id = values.getAsLong(GrandContract.BlessingsColumn.ID);
            ret = ContentUris.withAppendedId(uri, id);
            Log.d(TAG, "inserted uri: " + ret);

            // Notify that data for this uri has changed
            // clk: Notify registered observers that a row was updated
            //   Note: Cursor returned by query() registers by calling
            //         setNotificationUri(ContentResolver, Uri)
            //   2nd argument for a ContentObserver is null here
            getContext().getContentResolver().notifyChange(uri, null);
        }
        else {
            Log.d(TAG, "insert failed");
        }

        return ret;
    }

    @Override
    public boolean onCreate() {
        grandDbHelper = new GrandDbHelper(getContext());
        Log.d(TAG, "onCreate");
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void startBuildList () {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables( GrandContract.TABLE_1 );

        String orderBy = GrandContract.DEFAULT_SORT_1;

        SQLiteDatabase db = grandDbHelper.getWritableDatabase();
        Cursor cursor = qb.query(db, null, null, null, null, null, orderBy);

        BlessingEntry blessingRecord = new BlessingEntry();
        long nextID = 0;
        String nextBlessing;
        // PROBLEM: localBlessings is uniinitalized, and I don't know how to initialize it.
        if (cursor.getCount() > 0) {
            boolean live = cursor.moveToFirst();
            while (live) {
                nextID = cursor.getLong(cursor.getColumnIndex(GrandContract.BlessingsColumn.ID));
                blessingRecord.ID = nextID;
                nextBlessing = cursor.getString(cursor.getColumnIndex(GrandContract.BlessingsColumn.BLESSING));
                blessingRecord.blessing = nextBlessing;
                localBlessings.add(blessingRecord);
                Log.d(TAG, "item ID: " + blessingRecord.ID + " text: " + blessingRecord.blessing);
                live = cursor.moveToNext();
             }

        }
        lastID = nextID;
        Log.d(TAG, "startBuildList count: " + cursor.getCount());
    }
}
