package com.maybecallornot.scarydoll4call

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.ads.MobileAds

@SuppressLint("CustomSplashScreen")
class ActivitySplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        MobileAds.initialize(this)
        CacheAds.loadInter(this)
        //startMain(this);
    }


    private fun startMain(context: Activity) {
        context.startActivity(Intent(context, ActivityGlavnaya::class.java))
        context.finish()
    }
}