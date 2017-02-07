package com.roachcitysoftware.goldenkey;

/**
 * Implementation of database helper class used to create and access Grand.db.
 * Created by GeorgeR on 9/8/2015.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class GrandDbHelper extends SQLiteOpenHelper {
//    private static final String TAG = GrandDbHelper.class.getSimpleName();

    public GrandDbHelper (Context context) {
        super(context, GrandContract.DB_NAME, null, GrandContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String sql = String.format("create table %s (%s integer primary key autoincrement, %s text)",
                GrandContract.TABLE_1, GrandContract.BlessingsColumn.ID,
                GrandContract.BlessingsColumn.BLESSING);
        db.execSQL(sql);
        sql = String.format("create table %s (%s integer primary key autoincrement, %s integer, %s text, %s text)",
                GrandContract.TABLE_2, GrandContract.HistoryColumn.ID, GrandContract.HistoryColumn.DATE_TIME,
                GrandContract.HistoryColumn.EVENT_TYPE, GrandContract.HistoryColumn.EXTRA_DATA);
         db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        // Typically you do ALTER TABLE ...
        // db.execSQL("drop table if exists " + GrandContract.TABLE_1);
        // Rename table with blessing list to save contents
        db.execSQL("alter table " + GrandContract.TABLE_1 + " rename to oldBlessings");
        onCreate(db);
        // Drop new empty table
        db.execSQL("drop table if exists " + GrandContract.TABLE_1);
        // Replace by renaming saved table (will need new logic if table format ever changes)
        db.execSQL("alter table oldBlessings rename to " + GrandContract.TABLE_1);
    }
}
