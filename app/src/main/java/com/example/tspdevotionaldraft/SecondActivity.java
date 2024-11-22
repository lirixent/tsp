package com.example.tspdevotionaldraft;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;

import java.util.Calendar;

public class SecondActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "DevotionPrefs";
    private static final String TIMER_SET_KEY = "TimerSet";
    private static final int ALARM_REQUEST_CODE = 100;
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

        // Set up the choose sound button to let the user pick an alarm sound
        setSoundButton.setOnClickListener(v -> {
            // You can implement sound selection here if necessary (RingtoneManager, etc.)
            // For simplicity, using default sound for now
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
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {
                    setAlarm(selectedHour, selectedMinute);
                    setTimerSet(true);
                    Toast.makeText(SecondActivity.this, "Alarm set successfully!", Toast.LENGTH_SHORT).show();
                },
                hour, minute, true // true for 24-hour time format
        );
        timePickerDialog.show();
    }

    private void setAlarm(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // API level 31 and above
                if (alarmManager.canScheduleExactAlarms()) {
                    // Schedule exact alarm
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), getPendingIntent());
                } else {
                    // Handle case where exact alarms cannot be scheduled
                    Toast.makeText(this, "Exact alarms cannot be scheduled. Please allow the app to schedule alarms.", Toast.LENGTH_LONG).show();
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), getPendingIntent());
                }
            } else {
                // For devices with lower than API level 31, just set the alarm as normal
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), getPendingIntent());
            }
        }
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("alarmSound", alarmSoundUri.toString());
        return PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
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
