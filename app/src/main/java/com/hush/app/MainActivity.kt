package com.hush.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.hush.app.ads.HushBannerAd
import com.hush.app.audio.PlaybackService
import com.hush.app.audio.SoundEngine
import com.hush.app.billing.BillingManager
import com.hush.app.ui.screens.HomeScreen
import com.hush.app.ui.screens.HomeViewModel
import com.hush.app.ui.theme.HushTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var billingManager: BillingManager

    @Inject
    lateinit var soundEngine: SoundEngine

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* granted or not, we continue */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        billingManager.initialize()
        requestNotificationPermission()

        setContent {
            HushTheme {
                val viewModel: HomeViewModel = hiltViewModel()

                HomeScreen(
                    viewModel = viewModel,
                    onUpgradeClick = { billingManager.launchPurchaseFlow(this) },
                    adBanner = { HushBannerAd() }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Start foreground service if sounds are playing
        if (soundEngine.isPlaying) {
            startPlaybackService()
        }
    }

    override fun onStop() {
        super.onStop()
        // Start foreground service to keep audio playing in background
        if (soundEngine.isPlaying) {
            startPlaybackService()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        billingManager.destroy()
        if (!soundEngine.isPlaying) {
            stopPlaybackService()
        }
    }

    private fun startPlaybackService() {
        val soundNames = soundEngine.activeSounds.joinToString(" · ") { it.sound.name }
        val intent = Intent(this, PlaybackService::class.java).apply {
            putExtra("sound_names", soundNames)
        }
        ContextCompat.startForegroundService(this, intent)
    }

    private fun stopPlaybackService() {
        val intent = Intent(this, PlaybackService::class.java).apply {
            action = PlaybackService.ACTION_STOP
        }
        startService(intent)
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
