package com.roachcitysoftware.goldenkey;

import android.app.AlarmManager;
import android.app.Service;
import android.content.ContentProviderClient;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;

import java.util.Date;

public class ReminderService extends Service {
    private static final String TAG = ReminderService.class.getSimpleName();
    public static final int PRACTICE = 1;
    public static final int BUILD_LIST = 2;
    public static final int NOTIFY = 3;
    public static final int CANCEL = 4;
    private static long mPracticeReminderDeadline;
    private static long mBuildListReminderDeadline;

    public ReminderService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;        // This is an unbound Service
    }

    @Override
    public void onCreate(){
        super.onCreate();
        // Initialize notification deadlines
        Date dt = new Date();
        long now = dt.getTime();
        mPracticeReminderDeadline = now;
        mBuildListReminderDeadline = now;
        Log.d(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent, flags, startId);
        // Check history, update notification deadlines if needed
        UpdateDeadlines();
        // Check time against deadlines, send notification if needed
        HandleNotifications();
        Log.d(TAG, "onStartCommand");
        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    // Check history, update notification deadlines if needed
    private void UpdateDeadlines (){
        // Access the database
        ContentProviderClient cpc =
                getContentResolver().acquireContentProviderClient(GrandContract.CONTENT_URI_2);
        if (cpc == null){
            Log.d(TAG, "UpdateDeadlines failed - can't get ContentProviderClient");
            return;
        }
        BlessingProvider bp = (BlessingProvider) cpc.getLocalContentProvider();
        if (bp == null){
            Log.d(TAG, "UpdateDeadlines failed - can't get BlessingProvider");
            cpc.release();
            return;
        }
        // Retrieve the last Practice time, update Practice deadline
        Cursor practiceCursor = bp.query(GrandContract.CONTENT_URI_2, null,
                " eventType = '" + GrandContract.PRACTICE_EVENT + "' AND  extraData = 'Done' ",
                null, null);
        if ((practiceCursor != null) && (practiceCursor.moveToFirst())){
            long lastPractice =
                    practiceCursor.getLong(practiceCursor.getColumnIndex(GrandContract.HistoryColumn.DATE_TIME));
            practiceCursor.close();
            long lastDeadline = lastPractice + AlarmManager.INTERVAL_DAY;
            if (lastDeadline > mPracticeReminderDeadline){
                mPracticeReminderDeadline = lastDeadline;
            }
        } else {
            Log.d(TAG, "failed to update mPracticeReminderDeadline");
        }
        // Retrieve the last Build List time, update Build List deadline
        Cursor buildListCursor = bp.query(GrandContract.CONTENT_URI_2, null,
                "eventType = '" + GrandContract.BUILD_LIST_EVENT +"'", null, null);
        if ((buildListCursor != null) && (buildListCursor.moveToFirst())){
            long lastBuild =
                    buildListCursor.getLong(buildListCursor.getColumnIndex(GrandContract.HistoryColumn.DATE_TIME));
            buildListCursor.close();
            long lastBuildDeadline = lastBuild + (7 * AlarmManager.INTERVAL_DAY);
            if (lastBuildDeadline > mBuildListReminderDeadline){
                mBuildListReminderDeadline = lastBuildDeadline;
            }
        } else {
            Log.d(TAG, "failed to update mPracticeReminderDeadline");
        }
        // Release the database
        cpc.release();
    }

    // Check time against deadlines, send notification if needed
    private void HandleNotifications (){
        Date dt = new Date();
        long now = dt.getTime();
        if (now > mPracticeReminderDeadline){
            sendBroadcast(new Intent(
                    "com.roachcitysoftware.goldenkey.action.REMINDER").putExtra(
                    "Target", ReminderService.PRACTICE).putExtra("Action", ReminderService.NOTIFY));
        }
        if (now > mBuildListReminderDeadline){
            sendBroadcast(new Intent(
                    "com.roachcitysoftware.goldenkey.action.REMINDER").putExtra(
                    "Target", ReminderService.BUILD_LIST).putExtra("Action", ReminderService.NOTIFY));
        }
    }
}
