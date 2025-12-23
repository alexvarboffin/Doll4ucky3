package com.drawfanatteberad.mortalfived.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.drawfanatteberad.mortalfived.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.Arrays;

public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";

    protected AdRequest adRequest;
    private InterstitialAd mInterstitialAd;

    protected boolean isInterShown;

    protected Context context;
    private Activity currentActivity;


    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void setCurrentActivity(Activity activity) {
        currentActivity = activity;
    }

    // Ad Banner
    protected void loadAds() {

        RequestConfiguration configuration = new RequestConfiguration.Builder()
                .setTestDeviceIds(Arrays.asList("7274BD221814EF3EAB38177CF87FB26E")).build();
        MobileAds.setRequestConfiguration(configuration);

        adRequest = new AdRequest.Builder().build();
        AdView mAdView = findViewById(R.id.adView);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.i(TAG, "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                // Code to be executed when an ad request fails.
                Log.i(TAG, "onAdFailedToLoad, "+adError);
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.i(TAG, "onAdOpened");
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                Log.i(TAG, "onAdClicked");
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
                Log.i(TAG, "onAdClosed");
            }
        });

        mAdView.loadAd(adRequest);
    }

    protected void loadInterstitial(boolean show) {
        Log.i(TAG, "onResume; isInterShown: "+isInterShown);
        if (!isInterShown) {
            InterstitialAd.load(context, getString(R.string.ad_mob_interstitial_id), adRequest, new InterstitialAdLoadCallback() {

                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    // The mInterstitialAd reference will be null until
                    // an ad is loaded.
                    mInterstitialAd = interstitialAd;
                    if (show) {
                        mInterstitialAd.show(currentActivity);
                        isInterShown = true;
                        loadInterstitial(false);
                    }
                    Log.i(TAG, "onInterstitialAdLoaded; show: "+show);
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    // Handle the error
                    Log.i(TAG, loadAdError.getMessage());
                    mInterstitialAd = null;
                }
            });
        }
    }

    protected void showInterstitial() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(currentActivity);

        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }
    }




    protected void openDefaultMailClient() {
        String strEmail = getString(R.string.email_address);
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{ strEmail});
        email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback));
//        email.putExtra(Intent.EXTRA_TEXT, "message");

        //need this to prompts email client only
        email.setType("message/rfc6068"); //rfc822

        startActivity(Intent.createChooser(email, getString(R.string.choose_email_client)));
    }

    protected void openPrivacyUrl() {
        String url = getString(R.string.url_privacy_policy);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    protected void openDeveloperPageOnPlayMarket() {
        String url = getString(R.string.url_google_play_dev);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }
}
