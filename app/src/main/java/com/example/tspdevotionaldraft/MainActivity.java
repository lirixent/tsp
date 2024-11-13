package com.example.tspdevotionaldraft;

import android.view.View;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowInsets;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity {

    private static final long DELAY_MILLIS = 5000; // 5-second delay for splash screen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize AdMob SDK (error handling is optional but can be added if required)
        MobileAds.initialize(this, initializationStatus -> {
            // You can add any logic here after AdMob has finished initializing, e.g. loading ads
        });

        // Enable Edge-to-Edge UI for modern devices
        enableEdgeToEdgeUI();

        // Set the layout for the activity (Splash screen layout)
        setContentView(R.layout.activity_main);

        // Start the SecondActivity after the delay
        new Handler().postDelayed(() -> {
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
     * Enable Edge-to-Edge UI for modern devices with system bar insets.
     */
    private void enableEdgeToEdgeUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11 (API 30) and above - Use InsetsController for hiding system bars
            getWindow().getInsetsController().hide(WindowInsets.Type.statusBars()); // Hide status bar
            getWindow().getInsetsController().hide(WindowInsets.Type.navigationBars()); // Hide navigation bar (optional)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Android 4.4 (API 19) to Android 10 (API 29) - Use WindowInsetsController for fullscreen
            // Ensure full-screen experience on older Android versions
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    /**
     * Adjust the padding for the system bars (status bar, navigation bar) to ensure UI elements are not covered.
     */
    private void adjustPaddingForInsets() {
        // Adjust padding based on system bars (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            // Get system bar insets (left, top, right, bottom)
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets; // Return the insets to keep the default behavior
        });
    }
}
