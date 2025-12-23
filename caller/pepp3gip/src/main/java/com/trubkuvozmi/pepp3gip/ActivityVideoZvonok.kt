package com.trubkuvozmi.pepp3gip

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.trubkuvozmi.pepp3gip.databinding.ActivityVideoZvonokBinding
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback


class ActivityVideoZvonok : AppCompatActivity() {

    private lateinit var binding: ActivityVideoZvonokBinding
    private val requestCode = 10
    private val permissions =
        mutableListOf (
            Manifest.permission.CAMERA
        ).apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()
    private var playerRing:MediaPlayer? = null
    private val videoArray = arrayOf(R.raw.vidos1,R.raw.vidos2,R.raw.vidos3)
    private var videoId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoZvonokBinding.inflate(layoutInflater)
        setContentView(binding.root)

        videoId = getSharedPreferences("prefs", MODE_PRIVATE).getInt("videoId",0)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, permissions, requestCode)
        }

    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview)
                val attr = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .setLegacyStreamType(AudioManager.STREAM_RING)
                    .build()
                playerRing = MediaPlayer.create(this,
                    R.raw.zvonok, attr, AudioManager.AUDIO_SESSION_ID_GENERATE)
                playerRing?.isLooping = true
                playerRing?.start()
                binding.KnopkaZelenaya.setOnClickListener {
                    playerRing?.release()
                    binding.KnopkaZelenaya.isVisible = false
                    binding.videoView.setVideoURI(Uri.parse("android.resource://"
                            + packageName + "/"+videoArray[videoId]))
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        binding.videoView.setAudioAttributes(AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                            .setLegacyStreamType(AudioManager.STREAM_RING)
                            .build())
                    }
                    binding.videoView.setOnPreparedListener { mediaPlayer ->
                        val videoRatio = mediaPlayer.videoWidth / mediaPlayer.videoHeight.toFloat()
                        val screenRatio = binding.videoView.width / binding.videoView.height.toFloat()
                        val scaleX = videoRatio / screenRatio
                        if (scaleX >= 1f) {
                            binding.videoView.scaleX = scaleX
                        } else {
                            binding.videoView.scaleY = 1f / scaleX
                        }
                        binding.videoView.start()
                    }
                    binding.videoView.isVisible = true
                    binding.viewFinderCall.isVisible = true
                    binding.viewFinder.isVisible = false
                    preview.setSurfaceProvider(binding.viewFinderCall.surfaceProvider)
                }
                binding.KnopkaKrasnaya.setOnClickListener {
                    playerRing?.release()
                    binding.videoView.stopPlayback()
                    showAdsInter()
                }
            } catch(exc: Exception) {
                Log.e("DASD", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = permissions.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == this.requestCode) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun showAdsInter() {
        if (CacheAds.mInterstitialAd!=null) {
            CacheAds.mInterstitialAd?.fullScreenContentCallback =
                object: FullScreenContentCallback(){
                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                        finish()
                    }
                    override fun onAdDismissedFullScreenContent() {
                        CacheAds.loadInter(this@ActivityVideoZvonok)
                        finish()
                    }
                    override fun onAdClicked() {
                        CacheAds.loadInter(this@ActivityVideoZvonok)
                    }
                }
            CacheAds.mInterstitialAd?.show(this)
        }
        else finish()
    }

    override fun onPause() {
        if (videoId<2) videoId++ else videoId = 0
        getSharedPreferences("prefs", MODE_PRIVATE).edit()
            .putInt("videoId", videoId).apply()
        super.onPause()
    }

    override fun onDestroy() {
        playerRing?.release()
        showAdsInter()
        super.onDestroy()
    }


}