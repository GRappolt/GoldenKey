package com.roachcitysoftware.goldenkey;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ReminderReceiver extends BroadcastReceiver {
    private static final String TAG = ReminderReceiver.class.getSimpleName();
    public static final int PRACTICE_NOTIFICATION_ID = 901;
    public static final int BUILD_LIST_NOTIFICATION_ID = 902;

    public ReminderReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int notificationId;
        PendingIntent operation;
        String content;

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        // Get selector
        int selector = intent.getIntExtra("Target", 0);

        // Set notification-specific variables
        switch (selector){
            case ReminderService.PRACTICE:
                notificationId = PRACTICE_NOTIFICATION_ID;
                operation = PendingIntent.getActivity(context, -1,
                        new Intent(context, PracticeActivity.class),
                        PendingIntent.FLAG_ONE_SHOT);
                content = "Practice now to feel really great.";
                break;
            case ReminderService.BUILD_LIST:
                notificationId = BUILD_LIST_NOTIFICATION_ID;
                operation = PendingIntent.getActivity(context, -1,
                        new Intent(context, BuildListActivity.class),
                        PendingIntent.FLAG_ONE_SHOT);
                content = "It’s fun to think of good things and add them to your list.";
                break;
            default:
                // Handle unexpected event (throw exception?)
                Log.d(TAG, "onReceive error - invalid Target");
                return;
        }

        // Build and send notification
        Notification notification = new Notification.Builder(context)
                .setContentTitle("Golden Key")
                .setContentText(content)
                .setSmallIcon(R.mipmap.gk_icon)
                .setContentIntent(operation)
                .setAutoCancel(true)
                .build(); // clk: getNotification() was deprecated in API level 16
        notificationManager.notify(notificationId, notification);

         Log.d(TAG, "onReceive");
    }
}
