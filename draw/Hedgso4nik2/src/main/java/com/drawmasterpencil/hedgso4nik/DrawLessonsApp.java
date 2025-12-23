package com.drawmasterpencil.hedgso4nik;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class DrawLessonsApp extends Application {

    private static final String TAG = "DrawLessonsApp";


    @Override
    public void onCreate() {
        super.onCreate();

        // Admob
        MobileAds.initialize(getApplicationContext(), initializationStatus ->
                Log.d(TAG, "MobileAds.initialize: "+initializationStatus));
    }
}
