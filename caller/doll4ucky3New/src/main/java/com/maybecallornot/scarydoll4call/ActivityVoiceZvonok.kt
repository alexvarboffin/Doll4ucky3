package com.maybecallornot.scarydoll4call

import android.app.KeyguardManager
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.maybecallornot.scarydoll4call.databinding.ActivityGolosovoyZvonokBinding
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import java.text.SimpleDateFormat
import java.util.*


class ActivityVoiceZvonok : AppCompatActivity() {

    private lateinit var binding: ActivityGolosovoyZvonokBinding
    private var playerRing:MediaPlayer? = null
    private var playerCall:MediaPlayer? = null
    private var vibrator:Vibrator? = null

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            val win: Window = window
            win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
            win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)

        }
        super.onCreate(savedInstanceState)
        binding = ActivityGolosovoyZvonokBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val cpuWakelock = (getSystemService(POWER_SERVICE) as PowerManager).newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "app:wake")
        cpuWakelock.acquire(5000)

        val attr = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
            .setLegacyStreamType(AudioManager.STREAM_RING)
            .build()
        playerRing = MediaPlayer.create(this,
            R.raw.zvonok, attr, AudioManager.AUDIO_SESSION_ID_GENERATE)
        playerRing?.isLooping = true
        playerRing?.start()
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

        val delay = 0
        val vibrate = 1000
        val sleep = 1000
        val start = 0
        val vibratePattern = longArrayOf(delay.toLong(), vibrate.toLong(), sleep.toLong())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator!!.vibrate(VibrationEffect.createWaveform(vibratePattern, start))
        } else {
            vibrator!!.vibrate(vibratePattern, start)
        }

        binding.KnopkaZelenaya.setOnClickListener {
            vibrator?.cancel()
            playerRing?.release()
            playerCall?.release()
            playerCall = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                        .build()
                )
                setDataSource(applicationContext, Uri.parse("android.resource://"
                        + packageName + "/" + R.raw.golos))
                prepare()
                start()
            }
            val startTime = System.currentTimeMillis()
            val timer = Timer()
            timer.scheduleAtFixedRate(object:TimerTask(){
                override fun run() {
                    runOnUiThread {
                        val callTime = System.currentTimeMillis() - startTime
                        binding.textoviyTimer.text = SimpleDateFormat("mm:ss",
                            Locale.getDefault()).format(callTime)
                    }
                }
            },0, 1000)
            binding.KnopkaZelenaya.isVisible = false
            binding.number.isVisible = false
            binding.KnopkaKrasnaya.setOnClickListener {
                timer.cancel()
                vibrator?.cancel()
                playerRing?.release()
                playerCall?.release()
                showAdsInter()
            }
        }

        binding.KnopkaKrasnaya.setOnClickListener {
            vibrator?.cancel()
            playerRing?.release()
            playerCall?.release()
            showAdsInter()
        }

        cpuWakelock.release()

    }

    private fun showAdsInter() {
        if (CacheAds.mInterstitialAd!=null) {
            CacheAds.mInterstitialAd?.fullScreenContentCallback =
                object: FullScreenContentCallback(){
                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                        finish()
                    }
                    override fun onAdDismissedFullScreenContent() {
                        CacheAds.loadInter(this@ActivityVoiceZvonok)
                        finish()
                    }
                    override fun onAdClicked() {
                        CacheAds.loadInter(this@ActivityVoiceZvonok)
                    }
                }
            CacheAds.mInterstitialAd?.show(this)
        }
        else finish()
    }


    override fun onDestroy() {
        vibrator?.cancel()
        playerRing?.release()
        playerCall?.release()
        showAdsInter()
        super.onDestroy()
    }
}