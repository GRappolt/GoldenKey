package com.roachcitysoftware.goldenkey;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
//    private static final String TAG = BootReceiver.class.getSimpleName();
    private static final long DEFAULT_INTERVAL = AlarmManager.INTERVAL_FIFTEEN_MINUTES;

    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        PendingIntent operation = PendingIntent.getService(context, -1,
                new Intent(context, ReminderService.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);

        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime(), DEFAULT_INTERVAL, operation);
        // clk: Not using AlarmManager.RTC and corresponding System.currentTimeMillis()
        // Avoid basing your alarm on clock time if possible, if only need interval
        //  https://developer.android.com/training/scheduling/alarms.html
    }
}
