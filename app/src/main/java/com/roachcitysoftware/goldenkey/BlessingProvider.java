package com.roachcitysoftware.goldenkey;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class BlessingProvider extends ContentProvider {
    private static final String TAG = BlessingProvider.class.getSimpleName();
    private GrandDbHelper grandDbHelper;

    private static final UriMatcher sURIMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(GrandContract.AUTHORITY_1, GrandContract.TABLE_1,
                GrandContract.BLESSING_DIR);
        sURIMatcher.addURI(GrandContract.AUTHORITY_1, GrandContract.TABLE_1
                + "/#", GrandContract.BLESSING_ITEM);
    }

    public BlessingProvider() {
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
}
