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


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        String where;

        switch (sURIMatcher.match(uri)) {
            case GrandContract.BLESSING_DIR:
                // so we count deleted rows
                where = (selection == null) ? "1" : selection;
                break;
            case GrandContract.BLESSING_ITEM:
                long id = ContentUris.parseId(uri);
                where = GrandContract.BlessingsColumn.ID
                        + "="
                        + id
                        + (TextUtils.isEmpty(selection) ? "" : " and ( "
                        + selection + " )");
                break;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }

        SQLiteDatabase db = grandDbHelper.getReadableDatabase();
        int ret = db.delete(GrandContract.TABLE_1, where, selectionArgs);

        if(ret>0) {
            // Notify that data for this uri has changed
            getContext().getContentResolver().notifyChange(uri, null);
        }
        Log.d(TAG, "deleted records: " + ret);
        return ret;
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

        // Assert correct uri - can't be for specific ID, not known until insert() returns!
        if (sURIMatcher.match(uri) != GrandContract.BLESSING_DIR) {
            throw new IllegalArgumentException("Illegal uri: " + uri);
        }

        SQLiteDatabase db = grandDbHelper.getWritableDatabase();
        long rowId = db.insertWithOnConflict(GrandContract.TABLE_1, null,
                values, SQLiteDatabase.CONFLICT_IGNORE);

        // Was insert successful?
        if (rowId != -1) {
              ret = ContentUris.withAppendedId(uri, rowId);
            Log.d(TAG, "db rowID: " + rowId);

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
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables( GrandContract.TABLE_1 );

        switch (sURIMatcher.match(uri)) {
            case GrandContract.BLESSING_DIR:
                break;
            case GrandContract.BLESSING_ITEM:
                qb.appendWhere(GrandContract.BlessingsColumn.ID + "="
                        + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }

        String orderBy = (TextUtils.isEmpty(sortOrder)) ? GrandContract.DEFAULT_SORT_1
                : sortOrder;

        SQLiteDatabase db = grandDbHelper.getReadableDatabase();
        Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

        // register for uri changes
        // clk: So the Cursor returned to CursorLoader after it does a query
        //   will know of changes and CursorLoader will know to re-query
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        Log.d(TAG, "queried records: "+cursor.getCount());
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        String where;

        switch (sURIMatcher.match(uri)) {
            case GrandContract.BLESSING_DIR:
                // so we count updated rows
                where = selection;
                break;
            case GrandContract.BLESSING_ITEM:
                long id = ContentUris.parseId(uri);
                where = GrandContract.BlessingsColumn.ID
                        + "="
                        + id
                        + (TextUtils.isEmpty(selection) ? "" : " and ( "
                        + selection + " )");
                break;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }

        SQLiteDatabase db = grandDbHelper.getReadableDatabase();
        int ret = db.update(GrandContract.TABLE_1, values, where, selectionArgs);

        if(ret>0) {
            // Notify that data for this uri has changed
            getContext().getContentResolver().notifyChange(uri, null);
        }
        Log.d(TAG, "updated records: " + ret);
        return ret;
    }

    public boolean onAdd (String text)
    {
        ContentValues entry = new ContentValues();
        entry.clear();
        entry.put(GrandContract.BlessingsColumn.BLESSING, text);
        Uri result = insert(GrandContract.CONTENT_URI_1, entry);
        if (result != null) {
            Log.d(TAG, "onAdd success - text: " + text);
            return true;
        } else {
            Log.d(TAG, "onAdd failed");
            return false;
        }
    }
}
