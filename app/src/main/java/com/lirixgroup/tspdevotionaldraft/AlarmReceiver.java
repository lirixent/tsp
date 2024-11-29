package com.lirixgroup.tspdevotionaldraft;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "alarm_channel";  // Notification channel ID
    private static final int NOTIFICATION_ID = 1;  // Notification ID

    @Override
    public void onReceive(Context context, Intent intent) {
        // Log when the alarm goes off
        Log.d("AlarmReceiver", "Alarm received!");

        // Create a notification channel (for Android Oreo and above)
        createNotificationChannel(context);

        // Show notification to the user
        showNotification(context);
    }

    /**
     * Show a notification to the user when the alarm goes off.
     */
    private void showNotification(Context context) {
        // Create an intent to open ThirdActivity when the notification is clicked
        Intent notificationIntent = new Intent(context, ThirdActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // Create a PendingIntent to trigger the notification
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Build the notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_alarm)  // Replace with your own icon
                .setContentTitle("Devotional Alarm")
                .setContentText("It's time for your devotional!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)  // Automatically dismiss the notification when clicked
                .setContentIntent(pendingIntent);  // When clicked, the activity will open

        // If running on Android Oreo or later, check if permission is granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Check if notification permission is granted
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with showing the notification
                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                notificationManagerCompat.notify(NOTIFICATION_ID, notificationBuilder.build());
            } else {
                // Request permission (this should be done in an Activity, not here)
                Log.d("AlarmReceiver", "Notification permission is required.");
                // Normally, this request should be triggered by an Activity (MainActivity or similar).
                Intent permissionRequestIntent = new Intent(context, MainActivity.class);
                permissionRequestIntent.putExtra("requestNotificationPermission", true);
                permissionRequestIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(permissionRequestIntent);
            }
        } else {
            // No need to check permission for versions lower than API 33
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(NOTIFICATION_ID, notificationBuilder.build());
        }
    }

    /**
     * Create a notification channel for Android Oreo and above (API level 26+).
     */
    private void createNotificationChannel(Context context) {
        // Only create the channel if it doesn't already exist
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null && notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                // Create a notification channel
                CharSequence name = "Devotional Alarm Channel";
                String description = "Channel for devotional alarms";
                int importance = NotificationManager.IMPORTANCE_HIGH;

                android.app.NotificationChannel channel = new android.app.NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);

                // Register the notification channel
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
