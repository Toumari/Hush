package com.hush.app.ui.screens

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hush.app.audio.SoundEngine
import com.hush.app.data.ActiveSound
import com.hush.app.data.Sound
import com.hush.app.data.SoundCategory
import com.hush.app.data.SoundRepository
import com.hush.app.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val soundRepository: SoundRepository,
    private val soundEngine: SoundEngine,
    private val userPreferences: UserPreferences
) : ViewModel() {

    val soundsByCategory: Map<SoundCategory, List<Sound>> = soundRepository.getSoundsByCategory()

    private val _activeSounds = MutableStateFlow<List<ActiveSound>>(emptyList())
    val activeSounds: StateFlow<List<ActiveSound>> = _activeSounds.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _activeSoundIds = MutableStateFlow<Set<String>>(emptySet())
    val activeSoundIds: StateFlow<Set<String>> = _activeSoundIds.asStateFlow()

    val isPremium: StateFlow<Boolean> = userPreferences.isPremium
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    // Timer state
    private val _timerRemainingMinutes = MutableStateFlow<Int?>(null)
    val timerRemainingMinutes: StateFlow<Int?> = _timerRemainingMinutes.asStateFlow()

    private var sleepTimer: CountDownTimer? = null

    // Dialog state
    private val _showTimerDialog = MutableStateFlow(false)
    val showTimerDialog: StateFlow<Boolean> = _showTimerDialog.asStateFlow()

    private val _showPremiumDialog = MutableStateFlow(false)
    val showPremiumDialog: StateFlow<Boolean> = _showPremiumDialog.asStateFlow()

    private val _showMaxSoundsMessage = MutableStateFlow(false)
    val showMaxSoundsMessage: StateFlow<Boolean> = _showMaxSoundsMessage.asStateFlow()

    fun onSoundClick(sound: Sound) {
        // Check premium
        if (sound.isPremium && !isPremium.value) {
            _showPremiumDialog.value = true
            return
        }

        // Check if already active (toggle off)
        if (soundEngine.isSoundActive(sound.id)) {
            soundEngine.stopSound(sound.id)
            updateState()
            return
        }

        // Check max sounds
        if (soundEngine.activeSounds.size >= SoundEngine.MAX_SIMULTANEOUS_SOUNDS) {
            _showMaxSoundsMessage.value = true
            return
        }

        soundEngine.startSound(sound)
        updateState()
    }

    fun onVolumeChange(soundId: String, volume: Float) {
        soundEngine.setVolume(soundId, volume)
        updateState()
    }

    fun onRemoveSound(soundId: String) {
        soundEngine.stopSound(soundId)
        updateState()
    }

    fun onPlayPause() {
        if (soundEngine.isPlaying) {
            soundEngine.pauseAll()
        } else if (soundEngine.hasPausedSounds) {
            soundEngine.resumeAll()
        }
        updateState()
    }

    fun onStopAll() {
        soundEngine.stopAll()
        cancelTimer()
        updateState()
    }

    fun showTimerDialog() {
        _showTimerDialog.value = true
    }

    fun dismissTimerDialog() {
        _showTimerDialog.value = false
    }

    fun setTimer(minutes: Int) {
        cancelTimer()
        _showTimerDialog.value = false
        _timerRemainingMinutes.value = minutes

        sleepTimer = object : CountDownTimer(minutes * 60 * 1000L, 60 * 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                _timerRemainingMinutes.value = (millisUntilFinished / 60000).toInt() + 1
            }

            override fun onFinish() {
                soundEngine.stopAll()
                _timerRemainingMinutes.value = null
                updateState()
            }
        }.start()
    }

    fun cancelTimer() {
        sleepTimer?.cancel()
        sleepTimer = null
        _timerRemainingMinutes.value = null
    }

    fun dismissPremiumDialog() {
        _showPremiumDialog.value = false
    }

    fun dismissMaxSoundsMessage() {
        _showMaxSoundsMessage.value = false
    }

    fun onUpgradeClick() {
        _showPremiumDialog.value = false
        // BillingManager will handle the actual purchase flow
        // For now this is a placeholder that will be connected in MainActivity
    }

    private fun updateState() {
        _activeSounds.value = soundEngine.activeSounds
        _isPlaying.value = soundEngine.isPlaying
        _activeSoundIds.value = soundEngine.activeSounds.map { it.sound.id }.toSet()
    }

    override fun onCleared() {
        super.onCleared()
        sleepTimer?.cancel()
    }
}
