package com.lirixgroup.tspdevotionaldraft;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class ThirdActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "DevotionalPrefs";
    private static final String PREF_CURRENT_DAY = "CurrentDay";
    private static final String ASSET_PATH = "file:///android_asset/Devotional/day";

    private WebView webView;
    private Button nextButton, previousButton;
    private int currentDay = 1; // Start from Day 1

    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        // Initialize views
        webView = findViewById(R.id.webView);
        nextButton = findViewById(R.id.nextButton);
        previousButton = findViewById(R.id.previousButton);

        // Load stored preferences for the current day
        loadPreferences();
        loadHtmlPage(currentDay);

        // Load the first interstitial ad
        loadInterstitialAd();

        // Set button listeners
        nextButton.setOnClickListener(v -> {
            if (currentDay < 90) {
                if (interstitialAd != null) {
                    interstitialAd.show(ThirdActivity.this);
                    interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            loadInterstitialAd();
                            currentDay++;
                            loadHtmlPage(currentDay);
                            savePreferences();
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                            currentDay++;
                            loadHtmlPage(currentDay);
                            savePreferences();
                        }
                    });
                } else {
                    currentDay++;
                    loadHtmlPage(currentDay);
                    savePreferences();
                }
            } else {
                Toast.makeText(this, "You have completed all days!", Toast.LENGTH_SHORT).show();
            }
        });

        previousButton.setOnClickListener(v -> {
            if (currentDay > 1) {
                currentDay--;
                loadHtmlPage(currentDay);
                savePreferences();
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

        // Configure WebView settings for security
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Enable JavaScript if necessary
        webSettings.setAllowFileAccess(false); // Disable file access
        webSettings.setAllowContentAccess(false); // Disable content access
        webSettings.setGeolocationEnabled(false); // Disable geolocation

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl(fileName);
    }

    /**
     * Load the current day from SharedPreferences.
     */
    private void loadPreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        currentDay = prefs.getInt(PREF_CURRENT_DAY, 1); // Default to Day 1 if no preference found
    }

    /**
     * Save the current day to SharedPreferences.
     */
    private void savePreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(PREF_CURRENT_DAY, currentDay);
        editor.apply();
    }

    /**
     * Load an interstitial ad.
     */
    private void loadInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, "ca-app-pub-6768873008958793/6280549531", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd ad) {
                        interstitialAd = ad;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                        interstitialAd = null;
                        Log.d("AdLoad", "Failed to load interstitial ad: " + adError.getMessage());
                    }
                });
    }
}
