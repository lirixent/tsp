package com.example.tspdevotionaldraft;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

public class NotificationWorker extends Worker {

    private static final String CHANNEL_ID = "alarm_channel";  // Notification channel ID
    private static final int NOTIFICATION_ID = 1;  // Notification ID

    public NotificationWorker(Context context, WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @Override
    public Result doWork() {
        // Log when the worker starts
        Log.d("NotificationWorker", "Alarm worker started!");

        // Show the notification
        showNotification(getApplicationContext());

        // Play the alarm sound
        playAlarmSound(getApplicationContext());

        // Return success to indicate the work was completed successfully
        return Result.success();
    }

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
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                    == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with showing the notification
                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                notificationManagerCompat.notify(NOTIFICATION_ID, notificationBuilder.build());
            } else {
                // Request permission (this should be done in an Activity, not here)
                Log.d("NotificationWorker", "Notification permission is required.");
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

    private void playAlarmSound(Context context) {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmSound != null) {
            Ringtone ringtone = RingtoneManager.getRingtone(context, alarmSound);
            ringtone.play();
        }
    }
}
