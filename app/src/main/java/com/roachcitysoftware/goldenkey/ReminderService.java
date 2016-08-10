package com.roachcitysoftware.goldenkey;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ReminderService extends Service {
    private static final String TAG = ReminderService.class.getSimpleName();
    public static final int PRACTICE = 1;
    public static final int BUILD_LIST = 2;

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
        Log.d(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent, flags, startId);
        // Check history, update notification deadlines if needed
        // Check time against deadlines, send notification if needed
        Log.d(TAG, "onStartCommand");
        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}
