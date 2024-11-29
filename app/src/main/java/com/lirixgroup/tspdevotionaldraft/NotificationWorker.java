package com.lirixgroup.tspdevotionaldraft;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class NotificationWorker extends Worker {

    private static final String CHANNEL_ID = "devotion_alarm_channel";
    private static final String CHANNEL_NAME = "Devotion Alarm Notifications";

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Get the alarm sound URI passed from the SecondActivity (via SharedPreferences)
        String soundUriString = getInputData().getString("alarmSound");
        Uri alarmSoundUri = (soundUriString != null) ? Uri.parse(soundUriString) : RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        // Show the notification
        showNotification(alarmSoundUri);

        // Return success once the task is complete
        return Result.success();
    }

    private void showNotification(Uri soundUri) {
        // Get the NotificationManager system service
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

// Create an Intent to launch ThirdActivity when the notification is clicked
        Intent intent = new Intent(getApplicationContext(), ThirdActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Wrap the intent in a PendingIntent
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0, // Request code
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE // Ensure compatibility with Android 12+
        );


        // Create a notification channel for Android 8.0 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        // Build the notification
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_alarm) // Your alarm icon here
                .setContentTitle("Devotion Reminder")
                .setContentText("It's time for your devotion!")
                .setSound(soundUri) // Set the sound for the notification
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true) // Automatically remove the notification when tapped
                .build();

        // Issue the notification
        notificationManager.notify(0, notification);
    }
}
