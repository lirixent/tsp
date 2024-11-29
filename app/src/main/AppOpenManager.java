import android.app.Activity;
import android.content.Context;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;

public class AppOpenManager {
    private AppOpenAd appOpenAd = null;
    private long loadTime = 0;

    public void loadAd(Context context) {
        AdRequest request = new AdRequest.Builder().build();
        AppOpenAd.load(context, "ca-app-pub-6768873008958793/4729434768", request, new AppOpenAdLoadCallback() {
            @Override
            public void onAdLoaded(AppOpenAd ad) {
                appOpenAd = ad;
                loadTime = System.currentTimeMillis();
                ad.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        appOpenAd = null;
                    }
                });
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                appOpenAd = null;
            }
        });
    }

    public void showAdIfAvailable(Activity activity) {
        if (appOpenAd != null) {
            appOpenAd.show(activity);
        } else {
            loadAd(activity); // Load a new ad if one is not available
        }
    }
}
