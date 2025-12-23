package com.drawfanatteberad.mortalfived.activities;

import static com.drawfanatteberad.mortalfived.utlis.AppConstants.EXTRA_STEPS_NUMBER;
import static com.drawfanatteberad.mortalfived.utlis.AppConstants.EXTRA_TASK;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.drawfanatteberad.mortalfived.R;
import com.drawfanatteberad.mortalfived.base.BaseActivity;
import com.drawfanatteberad.mortalfived.databinding.ActivityDrawLessonStepsBinding;
import com.drawfanatteberad.mortalfived.utlis.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class DrawLessonStepsActivity extends BaseActivity {

    private final String TAG = "Lesson";

    private int step = 1;
    private int stepsNumber;
    private String lessonName;
    private String strStep;

    private AssetManager assetManager;
    private ActivityDrawLessonStepsBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDrawLessonStepsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setCurrentActivity(this);

        Context mContext = getApplicationContext();
        assetManager = mContext.getAssets();

        lessonName = getIntent().getStringExtra(EXTRA_TASK);
        stepsNumber = getIntent().getIntExtra(EXTRA_STEPS_NUMBER, 1);
        strStep = calcNextStepFileName(step);

        initToolbar();
        initViews();
        setNextStep();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAds();
        loadInterstitial(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        Log.d(TAG, "onSupportNavigateUp; mStep = "+ step);
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed; mStep = "+ step);
        showInterstitial();
        finish();
    }

    @Override
    protected void onSaveInstanceState (@NonNull Bundle outState) {
        super.onSaveInstanceState (outState);
        // Write the variable with the key in the Bundle
        outState.putInt("Step", step);
        outState.putBoolean("Interstitial", isInterShown);
        Log.d(TAG, "onSaveInstanceState; mStep = "+ step +"; isInterShown = "+isInterShown);
    }

    @Override
    protected void onRestoreInstanceState (Bundle savedInstanceState) {
        super.onRestoreInstanceState (savedInstanceState);
        // Restore our variable by key
        step = savedInstanceState.getInt("Step");
        isInterShown = savedInstanceState.getBoolean("Interstitial");
        // We can also set the default value after the key, separated by commas
        Log.d(TAG, "onRestoreInstanceState; mStep = "+"; isInterShown = "+isInterShown);

        strStep = calcNextStepFileName(step);
        setNextStep();
    }


    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        binding.lToolbar.tvTitleToolbar.setText(lessonName);
        binding.lToolbar.tvAllApps.setOnClickListener(v -> openDeveloperPageOnPlayMarket());
        //binding.lToolbar.actionRateApp.setOnClickListener(v -> Utils.openAppOnPlayMarket(DrawLessonStepsActivity.this));
    }

    private void initViews() {
        binding.btnPrevious.setOnClickListener(v -> {
            if (--step < 1) {
                step = 1;
            }

            strStep = calcNextStepFileName(step);
            setNextStep();
        });

        binding.btnNext.setOnClickListener(v -> {
            if (++step >= stepsNumber + 1) {
                step = stepsNumber;

            } else {
                updateOrdinaryStep();
            }

            strStep = calcNextStepFileName(step);
            setNextStep();
        });
    }

    private void updateOrdinaryStep() {
        binding.btnNext.setBackgroundResource(R.drawable.napravo);
    }

    private void updateTitle() {
        String lesson = step + "/" + stepsNumber;
        binding.lToolbar.tvTitleToolbar.setText(lesson);
    }

    private String calcNextStepFileName(int step) {
        return (step < 10) ? "0" + step : "" + step;
    }

    private void setNextStep() {
        //set hardness icon
        try {
            InputStream is = assetManager.open(lessonName + "/" + strStep + ".png");
            Bitmap imStep = BitmapFactory.decodeStream(is);
            binding.imvIcon.setImageBitmap(imStep);

        } catch (IOException e) {
            Log.w(TAG, e.getMessage());
        }

        updateTitle();

        // disable previous arrow at first step
        if (step == 1) {
            binding.btnPrevious.setVisibility(View.INVISIBLE);

        } else {
            binding.btnPrevious.setVisibility(View.VISIBLE);
        }

        // disable forward arrow at last step
        if (step == stepsNumber) {
            binding.btnNext.setVisibility(View.INVISIBLE);
        }
        else {
            binding.btnNext.setVisibility(View.VISIBLE);
        }
    }
}