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

import java.util.Date;


public class BlessingProvider extends ContentProvider {
//    private static final String TAG = BlessingProvider.class.getSimpleName();
    private GrandDbHelper grandDbHelper;

    private static final UriMatcher sURIMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(GrandContract.AUTHORITY_1, GrandContract.TABLE_1,
                GrandContract.BLESSING_DIR);
        sURIMatcher.addURI(GrandContract.AUTHORITY_1, GrandContract.TABLE_1
                + "/#", GrandContract.BLESSING_ITEM);
        sURIMatcher.addURI(GrandContract.AUTHORITY_1, GrandContract.TABLE_2,
                GrandContract.HISTORY_DIR);
        sURIMatcher.addURI(GrandContract.AUTHORITY_1, GrandContract.TABLE_2
                + "/#", GrandContract.HISTORY_ITEM);
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        String where;
        String table;
        long id;

        switch (sURIMatcher.match(uri)) {
            case GrandContract.BLESSING_DIR:
                // so we count deleted rows
                where = (selection == null) ? "1" : selection;
                table = GrandContract.TABLE_1;
                break;
            case GrandContract.BLESSING_ITEM:
                id = ContentUris.parseId(uri);
                where = GrandContract.BlessingsColumn.ID
                        + "="
                        + id
                        + (TextUtils.isEmpty(selection) ? "" : " and ( "
                        + selection + " )");
                table = GrandContract.TABLE_1;
                break;
            case GrandContract.HISTORY_DIR:
                // so we count deleted rows
                where = (selection == null) ? "1" : selection;
                table = GrandContract.TABLE_2;
                break;
            case GrandContract.HISTORY_ITEM:
                id = ContentUris.parseId(uri);
                where = GrandContract.HistoryColumn.ID
                        + "="
                        + id
                        + (TextUtils.isEmpty(selection) ? "" : " and ( "
                        + selection + " )");
                table = GrandContract.TABLE_2;
                break;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }

        SQLiteDatabase db = grandDbHelper.getReadableDatabase();
        int ret = db.delete(table, where, selectionArgs);

        if(ret>0) {
            // Notify that data for this uri has changed
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return ret;
   }

    @Override
    public String getType(Uri uri) {
        switch (sURIMatcher.match(uri)) {
            case GrandContract.BLESSING_DIR:
                return GrandContract.BLESSING_TYPE_DIR;
            case GrandContract.BLESSING_ITEM:
                return GrandContract.BLESSING_TYPE_ITEM;
            case GrandContract.HISTORY_DIR:
                return GrandContract.HISTORY_TYPE_DIR;
            case GrandContract.HISTORY_ITEM:
                return GrandContract.HISTORY_TYPE_ITEM;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri ret = null;

        // Assert correct uri - can't be for specific ID, not known until insert() returns!
        if ((sURIMatcher.match(uri) != GrandContract.BLESSING_DIR) &&
                (sURIMatcher.match(uri) != GrandContract.HISTORY_DIR)){
            throw new IllegalArgumentException("Illegal uri: " + uri);
        }

        SQLiteDatabase db = grandDbHelper.getWritableDatabase();
        long rowId;
        if (sURIMatcher.match(uri) == GrandContract.BLESSING_DIR)
            rowId = db.insertWithOnConflict(GrandContract.TABLE_1, null, values,
                    SQLiteDatabase.CONFLICT_IGNORE);
        else        // GrandContract.HISTORY_DIR
            rowId = db.insertWithOnConflict(GrandContract.TABLE_2, null, values,
                    SQLiteDatabase.CONFLICT_IGNORE);

        // Was insert successful?
        if (rowId != -1) {
              ret = ContentUris.withAppendedId(uri, rowId);

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
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String orderBy;

        switch (sURIMatcher.match(uri)) {
            case GrandContract.BLESSING_DIR:
                qb.setTables( GrandContract.TABLE_1 );
                orderBy = (TextUtils.isEmpty(sortOrder)) ? GrandContract.DEFAULT_SORT_1
                        : sortOrder;
                break;
            case GrandContract.BLESSING_ITEM:
                qb.setTables( GrandContract.TABLE_1 );
                qb.appendWhere(GrandContract.BlessingsColumn.ID + "="
                        + uri.getLastPathSegment());
                orderBy = (TextUtils.isEmpty(sortOrder)) ? GrandContract.DEFAULT_SORT_1
                        : sortOrder;
                break;
            case GrandContract.HISTORY_DIR:
                qb.setTables( GrandContract.TABLE_2 );
                orderBy = (TextUtils.isEmpty(sortOrder)) ? GrandContract.DEFAULT_SORT_2
                        : sortOrder;
                break;
            case GrandContract.HISTORY_ITEM:
                qb.setTables( GrandContract.TABLE_2 );
                qb.appendWhere(GrandContract.HistoryColumn.ID + "="
                        + uri.getLastPathSegment());
                orderBy = (TextUtils.isEmpty(sortOrder)) ? GrandContract.DEFAULT_SORT_2
                        : sortOrder;
                break;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }


        SQLiteDatabase db = grandDbHelper.getReadableDatabase();
        Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

        // register for uri changes
        // clk: So the Cursor returned to CursorLoader after it does a query
        //   will know of changes and CursorLoader will know to re-query
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        String where;
        String table;
        long id;

        switch (sURIMatcher.match(uri)) {
            case GrandContract.BLESSING_DIR:
                // so we count updated rows
                where = selection;
                table = GrandContract.TABLE_1;
                break;
            case GrandContract.BLESSING_ITEM:
                id = ContentUris.parseId(uri);
                where = GrandContract.BlessingsColumn.ID
                        + "="
                        + id
                        + (TextUtils.isEmpty(selection) ? "" : " and ( "
                        + selection + " )");
                table = GrandContract.TABLE_1;
                break;
            case GrandContract.HISTORY_DIR:
                // so we count updated rows
                where = selection;
                table = GrandContract.TABLE_2;
                break;
            case GrandContract.HISTORY_ITEM:
                id = ContentUris.parseId(uri);
                where = GrandContract.HistoryColumn.ID
                        + "="
                        + id
                        + (TextUtils.isEmpty(selection) ? "" : " and ( "
                        + selection + " )");
                table = GrandContract.TABLE_2;
                break;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }

        SQLiteDatabase db = grandDbHelper.getReadableDatabase();
        int ret = db.update(table, values, where, selectionArgs);

        if(ret>0) {
            // Notify that data for this uri has changed
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return ret;
    }

    public boolean onAdd (String text)
    {
        text = text.trim();
        if (text.isEmpty()) {
            return false;
        }
        ContentValues entry = new ContentValues();
        entry.clear();
        entry.put(GrandContract.BlessingsColumn.BLESSING, text);
        Uri result = insert(GrandContract.CONTENT_URI_1, entry);
        if (result != null) {
            return true;
        } else {
            return false;
        }
    }

    public long onAddEvent (String eventType, String extraData)
    {
        eventType = eventType.trim();
        if (eventType.isEmpty()) {
            return -1;
        }
        extraData = extraData.trim();

        ContentValues entry = new ContentValues();
        entry.clear();
        java.util.Date present = new Date();
        long now = present.getTime();
        entry.put(GrandContract.HistoryColumn.DATE_TIME, now);
        entry.put(GrandContract.HistoryColumn.EVENT_TYPE, eventType);
        entry.put(GrandContract.HistoryColumn.EXTRA_DATA, extraData);

        Uri result = insert(GrandContract.CONTENT_URI_2, entry);
        if (result != null) {
            return ContentUris.parseId(result);
        } else {
            return -1;
        }
    }

    public void onUpdateEvent (long eventId, String eventType, String extraData)
    {
        if (eventId == -1){
            return;
        }
        eventType = eventType.trim();
        if (eventType.isEmpty()) {
            return;
        }
        extraData = extraData.trim();

        ContentValues entry = new ContentValues();
        entry.clear();
        java.util.Date present = new Date();
        long now = present.getTime();
        entry.put(GrandContract.HistoryColumn.DATE_TIME, now);
        entry.put(GrandContract.HistoryColumn.EVENT_TYPE, eventType);
        entry.put(GrandContract.HistoryColumn.EXTRA_DATA, extraData);

        Uri target = ContentUris.withAppendedId(GrandContract.CONTENT_URI_2, eventId);
        int upDateCount = update(target, entry, null, null);
    }
}
