package com.lirixgroup.tspdevotionaldraft;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final long DELAY_MILLIS = 5000;

    // Launcher for notification permission
    private final ActivityResultLauncher<String> notificationPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    Toast.makeText(this, "Notification permission granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Notification permission denied. Some features may not work.", Toast.LENGTH_LONG).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main); // Initialize UI before making window changes

        enableEdgeToEdgeUI(); // Call after setContentView
        checkAndRequestNotificationPermission(); // Request notification permission
        scheduleNotificationWorker(); // Schedule background work for notifications

        // Delay transition to SecondActivity
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(MainActivity.this, SecondActivity.class));
            finish();
        }, DELAY_MILLIS);
    }

    // Schedule a notification worker
    private void scheduleNotificationWorker() {
        OneTimeWorkRequest notificationWorkRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setInitialDelay(1, TimeUnit.MINUTES) // Set delay for 1 minute
                .build();

        WorkManager.getInstance(this).enqueueUniqueWork(
                "NotificationWorker", // Unique work name
                ExistingWorkPolicy.REPLACE, // Replace if existing work exists
                notificationWorkRequest
        );
    }

    // Enable edge-to-edge UI
    private void enableEdgeToEdgeUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Check if WindowInsetsController is available
            if (getWindow() != null && getWindow().getDecorView() != null) {
                WindowInsetsController controller = getWindow().getInsetsController();
                if (controller != null) {
                    controller.hide(WindowInsets.Type.systemBars());
                    controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                }
            }
        } else {
            // Fallback for older versions
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            );
        }
    }

    // Check and request notification permission
    private void checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }
}
