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
    private static final String TAG = GrandDbHelper.class.getSimpleName();

    public GrandDbHelper (Context context) {
        super(context, GrandContract.DB_NAME, null, GrandContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String sql = String.format("create table %s (%s int primary key, %s text)",
                GrandContract.TABLE_1, GrandContract.BlessingsColumn.ID,
                GrandContract.BlessingsColumn.BLESSING);
        Log.d(TAG, "onCreate with SQL: " + sql);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
}
