package com.example.tspdevotionaldraft;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final long DELAY_MILLIS = 5000; // 5-second delay for splash screen
    private static final int PERMISSION_REQUEST_CODE = 1234; // Request code for notification permission

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable Edge-to-Edge UI for modern devices
        enableEdgeToEdgeUI();

        // Set the layout for the activity (Splash screen layout)
        setContentView(R.layout.activity_main);

        // Check for notification permission before proceeding
        checkAndRequestNotificationPermission();

        // Schedule the worker to run after a delay
        scheduleNotificationWorker();

        // Start the SecondActivity after the delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Transition to the SecondActivity after the splash screen delay
            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            startActivity(intent);
            finish(); // Close MainActivity to prevent returning to it
        }, DELAY_MILLIS);

        // Adjust window insets for proper padding (status bar, navigation bar, etc.)
        adjustPaddingForInsets();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Optionally, load or display ads here if needed
    }

    /**
     * Schedule the NotificationWorker to trigger at a specific time
     */
    private void scheduleNotificationWorker() {
        // Define the delay time in minutes (for testing, you can adjust it to your needs)
        long delayInMillis = 60000; // 1 minute (for testing)

        // Create a OneTimeWorkRequest for NotificationWorker
        WorkRequest notificationWorkRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setInitialDelay(delayInMillis, TimeUnit.MILLISECONDS)  // Set the delay before the worker runs
                .build();

        // Enqueue the work
        WorkManager.getInstance(this).enqueue(notificationWorkRequest);
    }

    /**
     * Enable Edge-to-Edge UI for modern devices with system bar insets.
     */
    private void enableEdgeToEdgeUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11 (API 30) and above - Use InsetsController for hiding system bars
            getWindow().getInsetsController().hide(WindowInsets.Type.statusBars()); // Hide status bar
            getWindow().getInsetsController().hide(WindowInsets.Type.navigationBars()); // Hide navigation bar (optional)

            // For immersive mode (full screen + sticky), use InsetsController to make the content edge-to-edge
            getWindow().getInsetsController().setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        } else {
            // Android 4.4 (API 19) to Android 10 (API 29) - Use SYSTEM_UI_FLAG_IMMERSIVE_STICKY for immersive mode
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    /**
     * Adjust the padding for the system bars (status bar, navigation bar) to ensure UI elements are not covered.
     */
    private void adjustPaddingForInsets() {
        // Ensure that the ID exists in your layout (activity_main.xml)
        findViewById(R.id.main).setOnApplyWindowInsetsListener((v, insets) -> {
            v.setPadding(insets.getInsets(WindowInsets.Type.systemBars()).left,
                    insets.getInsets(WindowInsets.Type.systemBars()).top,
                    insets.getInsets(WindowInsets.Type.systemBars()).right,
                    insets.getInsets(WindowInsets.Type.systemBars()).bottom);
            return insets; // Return the insets to keep the default behavior
        });
    }

    /**
     * Check if the app has the notification permission and request it if necessary.
     */
    private void checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Check if notification permission is granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Request the permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
            }
        }
    }

    /**
     * Handle the result of the permission request.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with displaying notifications
                // You can trigger your notifications now
            } else {
                // Permission denied, handle it gracefully (e.g., inform the user)
                // Optionally, you can explain why the permission is necessary
            }
        }
    }
}
