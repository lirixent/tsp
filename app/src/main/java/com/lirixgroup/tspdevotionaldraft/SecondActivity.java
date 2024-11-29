package com.lirixgroup.tspdevotionaldraft;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import android.app.TimePickerDialog;



import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class SecondActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "DevotionPrefs";
    private static final String TIMER_SET_KEY = "TimerSet";
    private static final int REQUEST_CODE_NOTIFICATION_PERMISSION = 1;

    private Button setAlarmButton;
    private Button setSoundButton;

    private Uri alarmSoundUri;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Check if the timer has already been set
        if (isTimerSet()) {
            goToThirdActivity();
        }

        // Initialize the UI components
        setAlarmButton = findViewById(R.id.setAlarmButton);
        setSoundButton = findViewById(R.id.setSoundButton);

        // Default alarm sound
        alarmSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        // Set up the choose sound button to let the user pick an alarm sound (for simplicity, using default sound)
        setSoundButton.setOnClickListener(v -> {
            Toast.makeText(SecondActivity.this, "Alarm sound selected.", Toast.LENGTH_SHORT).show();
        });

        // Set up the set alarm button
        setAlarmButton.setOnClickListener(v -> {
            checkAndRequestNotificationPermission(); // Ensure permissions are checked first
        });
    }

    private boolean isTimerSet() {
        return sharedPreferences.getBoolean(TIMER_SET_KEY, false);
    }

    private void setTimerSet(boolean isSet) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(TIMER_SET_KEY, isSet);
        editor.apply();
    }

    private void showTimePickerDialog() {
        // Get the current time
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Create and show the TimePickerDialog
        new TimePickerDialog(this, (view, selectedHour, selectedMinute) -> {
            setAlarm(selectedHour, selectedMinute);
            setTimerSet(true);
            Toast.makeText(SecondActivity.this, "Alarm set successfully!", Toast.LENGTH_SHORT).show();
        }, hour, minute, true).show();
    }

    private void setAlarm(int hour, int minute) {
        // Save alarm time to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("AlarmHour", hour);
        editor.putInt("AlarmMinute", minute);
        editor.apply();

        // Calculate the delay for the alarm based on the selected time
        scheduleNotificationWithAlarm(hour, minute);
    }

    private void scheduleNotificationWithAlarm(int hour, int minute) {
        // Get the current time
        Calendar now = Calendar.getInstance();

        // Create a calendar instance for the alarm time
        Calendar alarmTime = Calendar.getInstance();
        alarmTime.set(Calendar.HOUR_OF_DAY, hour);
        alarmTime.set(Calendar.MINUTE, minute);
        alarmTime.set(Calendar.SECOND, 0);

        // If the alarm time is before the current time, schedule it for the next day
        if (alarmTime.before(now)) {
            alarmTime.add(Calendar.DAY_OF_MONTH, 1);
        }

        // Calculate the delay in milliseconds
        long delay = alarmTime.getTimeInMillis() - now.getTimeInMillis();

        // Schedule the worker to show the notification at the calculated time
        WorkRequest notificationWorkRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .build();

        WorkManager.getInstance(this).enqueue(notificationWorkRequest);

        Toast.makeText(this, "Notification scheduled for: " + hour + ":" + minute, Toast.LENGTH_SHORT).show();
    }

    private void goToThirdActivity() {
        Intent intent = new Intent(SecondActivity.this, ThirdActivity.class);
        startActivity(intent);
        finish();
    }

    private void checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Check if the POST_NOTIFICATIONS permission is granted (API 33+)
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE_NOTIFICATION_PERMISSION);
            } else {
                showTimePickerDialog();
            }
        } else {
            // If the device is running below API 33, we can proceed without this permission
            showTimePickerDialog();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showTimePickerDialog();
            } else {
                Toast.makeText(this, "Notification permission is required to set an alarm.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
