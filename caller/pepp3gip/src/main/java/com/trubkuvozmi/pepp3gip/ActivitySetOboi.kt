package com.trubkuvozmi.pepp3gip

import android.app.WallpaperManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.trubkuvozmi.pepp3gip.databinding.ActivityUstanoviOboiBinding
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback


class ActivitySetOboi : AppCompatActivity() {

    private lateinit var binding: ActivityUstanoviOboiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUstanoviOboiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent?.extras?.let{
            if (it.containsKey("image")){
                val image = it.getInt("image")
                Glide.with(this).load(image).into(binding.kartinka)
                val wallpaperManager = WallpaperManager.getInstance(applicationContext)
                binding.knopkaUstanoviOboi.setOnClickListener {
                    CacheAds.rewardedAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            wallpaperManager.setResource(image)
                            finish()
                        }
                        override fun onAdDismissedFullScreenContent() {
                            wallpaperManager.setResource(image)
                            finish()
                            CacheAds.loadRewarded(this@ActivitySetOboi)
                        }
                    }
                    if (CacheAds.rewardedAd!=null)
                        CacheAds.rewardedAd?.show(this) {}
                    else {
                        wallpaperManager.setResource(image)
                        finish()
                    }
                }
            }
        }
    }
}