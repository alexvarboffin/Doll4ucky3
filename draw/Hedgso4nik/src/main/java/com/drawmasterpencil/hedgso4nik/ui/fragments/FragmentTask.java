package com.drawmasterpencil.hedgso4nik.ui.fragments;

import static com.drawmasterpencil.hedgso4nik.utlis.AppConstants.EXTRA_STEPS_NUMBER;
import static com.drawmasterpencil.hedgso4nik.utlis.AppConstants.EXTRA_TASK;

import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.drawmasterpencil.hedgso4nik.R;
import com.drawmasterpencil.hedgso4nik.adapters.LessonsAdapter;
import com.drawmasterpencil.hedgso4nik.activities.ThemeTitleActivity;
import com.drawmasterpencil.hedgso4nik.interfaces.LessonSelectListener;
import com.drawmasterpencil.hedgso4nik.model.LessonItem;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class FragmentTask extends Fragment implements LessonSelectListener {

    private final String TAG = "FragmentTask";

    private ArrayList<LessonItem> lessonsArray = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        int nSpansCount = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) ? 2 : 3;

        lessonsArray =loadLessons();

        View root = inflater.inflate(R.layout.fragment_lessons, container, false);
        RecyclerView rvTasks = root.findViewById(R.id.rv_lessons);
        rvTasks.setHasFixedSize(true);

        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getContext(), nSpansCount);
        mGridLayoutManager.setSpanCount(nSpansCount);
        rvTasks.setLayoutManager(mGridLayoutManager);

        LessonsAdapter mAdapter = new LessonsAdapter(getContext(), lessonsArray);
        mAdapter.setClickListener(this);
        rvTasks.setAdapter(mAdapter);


        // banner ads
//        List<String> testDeviceIds = Arrays.asList("7274BD221814EF3EAB38177CF87FB26E");
//        RequestConfiguration configuration =
//                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
//        MobileAds.setRequestConfiguration(configuration);

        // Change your Id
        RequestConfiguration configuration = new RequestConfiguration.Builder()
                .setTestDeviceIds(Arrays.asList("7274BD221814EF3EAB38177CF87FB26E")).build();
        MobileAds.setRequestConfiguration(configuration);

        // Ad Banner
        AdRequest adRequest = new AdRequest.Builder().build();
        AdView mAdView = root.findViewById(R.id.adView);
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

        return root;
    }

    @Override
    public void onLessonClicked(View view, int position) {
        startStepActivity(lessonsArray.get(position).getTaskName(), lessonsArray.get(position).getSteps());
    }


    private ArrayList<LessonItem> loadLessons() {
        ArrayList<LessonItem> lessonsArray = new ArrayList<>();
        try {
            AssetManager manager = requireContext().getAssets();

            InputStream is = manager.open("uroki.json");
            int size = is.available();
            byte[] buffer = new byte[size];

            final int read = is.read(buffer);
            is.close();
            Log.i(TAG, "read = "+read);

            String strJsonLessons = new String(buffer, StandardCharsets.UTF_8);

            // parse json string
            JSONObject jsonLessons = new JSONObject(strJsonLessons);
            JSONArray jsonArrayLessons = jsonLessons.getJSONArray("lessons");

            for (int i = 0; i < jsonArrayLessons.length(); i++) {
                JSONObject mJsonTaskObject = jsonArrayLessons.getJSONObject(i);

                int id = mJsonTaskObject.getInt("id");
                String taskName = mJsonTaskObject.getString("lesson_en");
                int stepsNumber = mJsonTaskObject.getInt("steps");

                lessonsArray.add(new LessonItem(id, taskName, stepsNumber));
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return lessonsArray;
    }

    private void startStepActivity(String strTask, int nSteps) {
        Intent nextActivityIntent = new Intent();

        nextActivityIntent.setClass(getContext(), ThemeTitleActivity.class);
        nextActivityIntent.putExtra(EXTRA_STEPS_NUMBER, nSteps);
        nextActivityIntent.putExtra(EXTRA_TASK, strTask);

        startActivity(nextActivityIntent);
    }
}