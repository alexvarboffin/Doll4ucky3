package com.trubkuvozmi.pepp3gip

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.ads.MobileAds

class ActivitySplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        MobileAds.initialize(this)
        CacheAds.loadInter(this)

        startActivity(Intent(this, ActivityGlavnaya::class.java))
        finish()
    }

}