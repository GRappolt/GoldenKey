package com.roachcitysoftware.goldenkey;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ReminderReceiver extends BroadcastReceiver {
    private static final String TAG = ReminderReceiver.class.getSimpleName();

    public ReminderReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get selector
        int selector = intent.getIntExtra("Target", 0);

        // Send selected notification
        switch (selector){
            case ReminderService.PRACTICE:
                // Build and send Practice notification
                break;
            case ReminderService.BUILD_LIST:
                // Build and send Build List notification
                break;
            default:
                // Handle unexpected event (throw exception?)
        }
         Log.d(TAG, "onReceive");
    }
}
