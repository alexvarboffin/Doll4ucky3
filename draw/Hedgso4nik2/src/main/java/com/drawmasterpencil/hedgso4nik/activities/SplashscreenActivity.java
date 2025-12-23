package com.drawmasterpencil.hedgso4nik.activities;

import static com.drawmasterpencil.hedgso4nik.utlis.AppConstants.EXTRA_SHOW_RATE_US_DIALOG;
import static com.drawmasterpencil.hedgso4nik.utlis.AppConstants.PREF_NUMBER_OF_STARTS;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.drawmasterpencil.hedgso4nik.R;
import com.drawmasterpencil.hedgso4nik.databinding.ActivitySplashBinding;

import com.drawmasterpencil.hedgso4nik.ui.activities.ActivityMain;
import com.google.firebase.messaging.FirebaseMessaging;

@SuppressLint("CustomSplashScreen")
public class SplashscreenActivity extends AppCompatActivity {

    private final String TAG = "ActivitySplashscreen";

    private int loadingProgress;

    private SharedPreferences prefs;
    private ActivitySplashBinding binding;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = getApplicationContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        FirebaseMessaging.getInstance()
                .getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching Firebase registration token failed", task.getException());
                    }
                });

        startLoadingBar();
    }

    @Override
    protected void onResume() {
        super.onResume();

        binding.imvLogo.setBackgroundResource(R.drawable.logo_animation_list);
        AnimationDrawable rocketAnimation = (AnimationDrawable) binding.imvLogo.getBackground();
        //binding.imvLogo.setOnClickListener(view -> rocketAnimation.start());
        rocketAnimation.start();
    }

    private void setNumberOfStarts(int starts) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(PREF_NUMBER_OF_STARTS, starts);
        editor.apply();
    }

    public void startLoadingBar() {
        loadingProgress = 0;
        binding.pbLoading.setProgress(loadingProgress);

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (loadingProgress < 200) {
                    loadingProgress += 1;
                   binding.pbLoading.setProgress(loadingProgress / 2);
                    handler.postDelayed(this, 40);

                } else if (loadingProgress == 200) {
                    loadingProgress = 1000;

                    startNextActivity();
                    finish();
                }
            }
        };

        handler.postDelayed(runnable, 100);
    }

    private void startNextActivity() {
        Intent intent = new Intent(context, ActivityMain.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        int startsNumber = prefs.getInt(PREF_NUMBER_OF_STARTS, 0);
        if (startsNumber == 0 || startsNumber == 1) {
            startsNumber++;
            setNumberOfStarts(startsNumber);

        } else if (startsNumber == 2) {
            startsNumber++;
            setNumberOfStarts(startsNumber);
            intent.putExtra(EXTRA_SHOW_RATE_US_DIALOG, true);
        }

        startActivity(intent);
    }
}