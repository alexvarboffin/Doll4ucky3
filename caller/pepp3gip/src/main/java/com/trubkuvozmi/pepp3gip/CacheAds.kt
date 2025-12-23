package com.trubkuvozmi.pepp3gip

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback


object CacheAds {
    var rewardedAd: RewardedAd? = null
    var mInterstitialAd: InterstitialAd? = null

    fun loadInter(context: Activity){
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, context.getString(R.string.reklama_inter), adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.v("DASD", loadAdError.message)
                    mInterstitialAd = null
                }
            })
    }

    fun loadRewarded(context: Activity){
        val adRequest = AdRequest.Builder().build()

        RewardedAd.load(context, context.getString(R.string.reklama_reward),
            adRequest, object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    this@CacheAds.rewardedAd = null
                    if (context is ActivitySplashScreen) startMain(context)
                }

                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    this@CacheAds.rewardedAd = rewardedAd
                    if (context is ActivitySplashScreen) startMain(context)
                }
            })
    }

    private fun startMain(context: Activity) {
        context.startActivity(Intent(context, ActivityGlavnaya::class.java))
        context.finish()
    }


}