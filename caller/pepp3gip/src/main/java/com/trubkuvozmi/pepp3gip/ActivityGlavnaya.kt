package com.trubkuvozmi.pepp3gip

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.ump.ConsentForm
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.trubkuvozmi.pepp3gip.databinding.ActivityHomeBinding
import pl.bclogic.pulsator4droid.library.PulsatorLayout

import java.util.concurrent.TimeUnit


class ActivityGlavnaya : AppCompatActivity() {


    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.knopkaVoiceZvonok.setOnClickListener {
            startActivity(Intent(this, ActivityVoiceZvonok::class.java))
        }

        binding.knopkaVideoZvonok.setOnClickListener {
            startActivity(Intent(this, ActivityVideoZvonok::class.java))
        }
        binding.GruppaTimer.check(R.id.sec10)

        binding.knopkaTimerZvonok.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                && !Settings.canDrawOverlays(this)
            ) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
            } else {
                val timer = when (binding.GruppaTimer.checkedRadioButtonId) {
                    R.id.sec10 -> 10
                    R.id.sec30 -> 30
                    R.id.sec60 -> 60
                    else -> 10
                }

                val uploadWorkRequest: WorkRequest =
                    OneTimeWorkRequestBuilder<WorkerTimer>()
                        .setInitialDelay(timer.toLong(), TimeUnit.SECONDS)
                        .build()
                WorkManager
                    .getInstance(applicationContext)
                    .enqueue(uploadWorkRequest)
                Toast.makeText(
                    this, getString(R.string.s2cheduled),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.knopkaOboi.setOnClickListener {
            CacheAds.loadRewarded(this)
            startActivity(Intent(this, ActivityOboi::class.java))
        }

        binding.knopkaPolicy.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.data = Uri.parse(getString(R.string.silka_policy))
            startActivity(intent)
        }

        binding.knopkaReyting.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.data = Uri.parse(getString(R.string.silka_oceni_nas) + packageName)
            startActivity(intent)
        }

        initBanner()
        initGDPR()


    }

    override fun onResume() {
        super.onResume()
        val pulsator = findViewById(R.id.pulsator) as PulsatorLayout
        pulsator.setCount(3)
        pulsator.setDuration(2200) //single pulse duration
        pulsator.start()
    }

    private fun initBanner() {
        binding.adView.visibility = View.GONE
        val adRequest = AdRequest.Builder().build()
        binding.adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                binding.adView.visibility = View.VISIBLE
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("DASD banner", adError.message)
            }
        }
        binding.adView.loadAd(adRequest)
    }

    private var consentInformation: ConsentInformation? = null
    private var consentForm: ConsentForm? = null

    private fun initGDPR() {
        val params = ConsentRequestParameters.Builder()
            .setTagForUnderAgeOfConsent(false)
            .build()

        consentInformation = UserMessagingPlatform.getConsentInformation(this)
        consentInformation?.requestConsentInfoUpdate(
            this, params, {
                consentInformation!!.requestConsentInfoUpdate(
                    this,
                    params, {
                        if (consentInformation!!.isConsentFormAvailable) {
                            loadForm()
                        }
                    }) {
                    Log.v("DASD 1", it.message)
                }
            },
            {
                Log.v("DASD 2", it.message)
            })
    }

    private fun loadForm() {
        UserMessagingPlatform.loadConsentForm(
            this,
            { consentForm ->
                run {
                    this@ActivityGlavnaya.consentForm = consentForm
                    if (consentInformation?.consentStatus == ConsentInformation.ConsentStatus.REQUIRED) {
                        consentForm.show(this@ActivityGlavnaya) {}
                    }
                }
            }
        ) {
            Log.v("DASD 3", it.message)
        }
    }

}