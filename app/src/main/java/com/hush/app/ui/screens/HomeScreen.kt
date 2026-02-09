package com.hush.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TimerOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hush.app.data.SoundCategory
import com.hush.app.ui.components.CategorySection
import com.hush.app.ui.components.MixerBottomSheet
import com.hush.app.ui.components.PremiumBanner
import com.hush.app.ui.components.PremiumRequiredDialog
import com.hush.app.ui.components.SleepTimerDialog
import com.hush.app.ui.theme.HushAccent
import com.hush.app.ui.theme.HushBackground
import com.hush.app.ui.theme.HushTextPrimary
import com.hush.app.ui.theme.HushTextSecondary

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onUpgradeClick: () -> Unit,
    adBanner: @Composable () -> Unit
) {
    val activeSounds by viewModel.activeSounds.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val activeSoundIds by viewModel.activeSoundIds.collectAsStateWithLifecycle()
    val isPremium by viewModel.isPremium.collectAsStateWithLifecycle()
    val timerRemaining by viewModel.timerRemainingMinutes.collectAsStateWithLifecycle()
    val showTimerDialog by viewModel.showTimerDialog.collectAsStateWithLifecycle()
    val showPremiumDialog by viewModel.showPremiumDialog.collectAsStateWithLifecycle()
    val showMaxSounds by viewModel.showMaxSoundsMessage.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(showMaxSounds) {
        if (showMaxSounds) {
            Toast.makeText(context, "Maximum of 5 sounds at once", Toast.LENGTH_SHORT).show()
            viewModel.dismissMaxSoundsMessage()
        }
    }

    // Dialogs
    if (showTimerDialog) {
        SleepTimerDialog(
            currentTimerMinutes = timerRemaining,
            onSetTimer = { viewModel.setTimer(it) },
            onCancelTimer = { viewModel.cancelTimer(); viewModel.dismissTimerDialog() },
            onDismiss = { viewModel.dismissTimerDialog() }
        )
    }

    if (showPremiumDialog) {
        PremiumRequiredDialog(
            onUpgrade = {
                viewModel.dismissPremiumDialog()
                onUpgradeClick()
            },
            onDismiss = { viewModel.dismissPremiumDialog() }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = HushBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = if (activeSounds.isNotEmpty()) 140.dp else 80.dp)
            ) {
                // Top bar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = "hush",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            letterSpacing = (-1).sp
                        ),
                        color = HushTextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    // Timer button
                    IconButton(onClick = { viewModel.showTimerDialog() }) {
                        Icon(
                            imageVector = if (timerRemaining != null) Icons.Filled.TimerOff else Icons.Filled.Timer,
                            contentDescription = "Sleep Timer",
                            tint = if (timerRemaining != null) HushAccent else HushTextSecondary
                        )
                    }
                }

                // Timer indicator
                if (timerRemaining != null) {
                    Text(
                        text = "Timer: ${timerRemaining} min remaining",
                        style = MaterialTheme.typography.bodySmall,
                        color = HushAccent,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Premium banner (only for free users)
                if (!isPremium) {
                    PremiumBanner(
                        onUpgradeClick = onUpgradeClick,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Sound categories
                val categoryOrder = listOf(
                    SoundCategory.NOISE,
                    SoundCategory.NATURE,
                    SoundCategory.AMBIENT,
                    SoundCategory.MECHANICAL,
                    SoundCategory.TRANSPORT
                )

                categoryOrder.forEach { category ->
                    val sounds = viewModel.soundsByCategory[category]
                    if (sounds != null) {
                        CategorySection(
                            category = category,
                            sounds = sounds,
                            activeSoundIds = activeSoundIds,
                            isPremium = isPremium,
                            onSoundClick = { viewModel.onSoundClick(it) }
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                // Ad banner at bottom (for free users)
                if (!isPremium) {
                    adBanner()
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Mixer bottom sheet
            MixerBottomSheet(
                activeSounds = activeSounds,
                isPlaying = isPlaying,
                onPlayPause = { viewModel.onPlayPause() },
                onStopAll = { viewModel.onStopAll() },
                onVolumeChange = { id, vol -> viewModel.onVolumeChange(id, vol) },
                onRemoveSound = { viewModel.onRemoveSound(it) },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
