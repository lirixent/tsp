package com.example.tspdevotionaldraft;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ThirdActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "DevotionalPrefs";
    private static final String PREF_CURRENT_DAY = "CurrentDay";
    private static final String ASSET_PATH = "file:///android_asset/Devotional/day";

    private WebView webView;
    private Button nextButton, previousButton;
    private int currentDay = 1; // Start from Day 1

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third); // Set the layout for this activity

        // Initialize views
        webView = findViewById(R.id.webView);
        nextButton = findViewById(R.id.nextButton);
        previousButton = findViewById(R.id.previousButton);

        // Load stored preferences for the current day
        loadPreferences();
        loadHtmlPage(currentDay);  // Load HTML page for the current day

        // Set button listeners
        nextButton.setOnClickListener(v -> {
            if (currentDay < 90) {  // Ensure we don’t go beyond Day 90
                currentDay++;
                loadHtmlPage(currentDay);
                savePreferences();  // Save updated current day
            } else {
                Toast.makeText(this, "You have completed all days!", Toast.LENGTH_SHORT).show();
            }
        });

        previousButton.setOnClickListener(v -> {
            if (currentDay > 1) {  // Ensure we don’t go below Day 1
                currentDay--;
                loadHtmlPage(currentDay);
                savePreferences();  // Save updated current day
            } else {
                Toast.makeText(this, "This is the first day!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Load the HTML page for the given day from the assets folder.
     */
    private void loadHtmlPage(int day) {
        String fileName = ASSET_PATH + day + ".html";

        // Enable JavaScript if required by the HTML
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient());  // Open links in WebView
        webView.loadUrl(fileName);  // Load HTML file from assets
    }

    /**
     * Load the current day from SharedPreferences.
     */
    private void loadPreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        currentDay = prefs.getInt(PREF_CURRENT_DAY, 1);  // Default to Day 1 if no preference found
    }

    /**
     * Save the current day to SharedPreferences.
     */
    private void savePreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(PREF_CURRENT_DAY, currentDay);  // Save the current day
        editor.apply();  // Commit the changes
    }
}
