package com.trubkuvozmi.pepp3gip

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.trubkuvozmi.pepp3gip.databinding.ActivityOboiBinding
import com.google.android.gms.ads.*

class ActivityOboi : AppCompatActivity(), AdapterImages.OnClick {

    private lateinit var binding: ActivityOboiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOboiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = AdapterImages(this, ArrayList<Int>().apply {
            add(R.drawable.walp1)
            add(R.drawable.walp2)
            add(R.drawable.walp3)
            add(R.drawable.walp4)
        }, this)

        binding.UtilizatorKartinok.adapter = adapter

        initBanner()
    }

    override fun onImageClick(image: Int) {
        openWallpaper(image)
    }

    private fun initBanner() {
        binding.adView.visibility = View.GONE
        val adRequest = AdRequest.Builder().build()
        binding.adView.adListener = object: AdListener() {
            override fun onAdLoaded() {
                binding.adView.visibility = View.VISIBLE
            }
        }
        binding.adView.loadAd(adRequest)
    }

    private fun openWallpaper(image: Int){
        val intent = Intent(this, ActivitySetOboi::class.java)
        intent.putExtra("image", image)
        startActivity(intent)
    }
}