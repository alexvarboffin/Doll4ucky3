package com.drawfanatteberad.mortalfived.activities;

import static com.drawfanatteberad.mortalfived.utlis.AppConstants.EXTRA_STEPS_NUMBER;
import static com.drawfanatteberad.mortalfived.utlis.AppConstants.EXTRA_TASK;

import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import com.drawfanatteberad.mortalfived.R;
import com.drawfanatteberad.mortalfived.base.BaseActivity;
import com.drawfanatteberad.mortalfived.databinding.ActivityThemeTitleBinding;
import com.drawfanatteberad.mortalfived.ui.activities.ActivityMain;
import com.drawfanatteberad.mortalfived.utlis.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class ThemeTitleActivity extends BaseActivity {

    private final String TAG = "StepActivity";

    private String taskName;
    private int stepsNumber;

    private AssetManager assetManager;

    private ActivityThemeTitleBinding binding;
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityThemeTitleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setCurrentActivity(this);

        mContext = getApplicationContext();
        assetManager = mContext.getAssets();

        taskName = getIntent().getStringExtra(EXTRA_TASK);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        binding.lToolbar.tvTitleToolbar.setText(taskName);
        binding.lToolbar.tvAllApps.setOnClickListener(v -> openDeveloperPageOnPlayMarket());
        //binding.lToolbar.actionRateApp.setOnClickListener(v -> Utils.openAppOnPlayMarket(ThemeTitleActivity.this));

        stepsNumber = getIntent().getIntExtra(EXTRA_STEPS_NUMBER, 1);

        binding.btnNext.setOnClickListener(v -> {
            startTaskActivity(taskName, stepsNumber);
            finish();
        });

        String labelStepNumber = getString(R.string.steps) + " " + stepsNumber;
        binding.tvLessonStepsNumber.setText(labelStepNumber);
        setTaskIcon();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAds();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    private void setTaskIcon() {
        String lastImage = (stepsNumber < 10) ? "0" + stepsNumber + ".png" : stepsNumber + ".png";

        try {
            InputStream is = assetManager.open(taskName + "/" + lastImage);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            binding.imvIcon.setImageBitmap(bitmap);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startTaskActivity(String task, int nSteps) {
        Intent nextActivityIntent = new Intent();

        nextActivityIntent.setClass(mContext, DrawLessonStepsActivity.class);
        nextActivityIntent.putExtra(EXTRA_STEPS_NUMBER, nSteps);
        nextActivityIntent.putExtra(EXTRA_TASK, task);

        startActivity(nextActivityIntent);
    }
}