package com.hush.app.audio

import com.hush.app.data.ActiveSound
import com.hush.app.data.Sound
import com.hush.app.data.SoundSourceType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundEngine @Inject constructor() {

    companion object {
        const val MAX_SIMULTANEOUS_SOUNDS = 5
    }

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val activeGenerators = mutableMapOf<String, Any>() // NoiseGenerator or SyntheticSoundGenerator
    private val activeSoundsMap = mutableMapOf<String, ActiveSound>()

    val activeSounds: List<ActiveSound>
        get() = activeSoundsMap.values.toList()

    val isPlaying: Boolean
        get() = activeGenerators.isNotEmpty()

    fun toggleSound(sound: Sound, initialVolume: Float = 0.7f): Boolean {
        return if (activeSoundsMap.containsKey(sound.id)) {
            stopSound(sound.id)
            false
        } else {
            if (activeSoundsMap.size >= MAX_SIMULTANEOUS_SOUNDS) {
                return false // at capacity
            }
            startSound(sound, initialVolume)
            true
        }
    }

    fun startSound(sound: Sound, volume: Float = 0.7f) {
        if (activeSoundsMap.containsKey(sound.id)) return
        if (activeSoundsMap.size >= MAX_SIMULTANEOUS_SOUNDS) return

        val generator = createGenerator(sound.sourceType)
        activeGenerators[sound.id] = generator
        activeSoundsMap[sound.id] = ActiveSound(sound, volume)

        when (generator) {
            is NoiseGenerator -> {
                generator.setVolume(volume)
                generator.start(scope)
            }
            is SyntheticSoundGenerator -> {
                generator.setVolume(volume)
                generator.start(scope)
            }
        }
    }

    fun stopSound(soundId: String) {
        val generator = activeGenerators.remove(soundId)
        activeSoundsMap.remove(soundId)

        when (generator) {
            is NoiseGenerator -> generator.stop()
            is SyntheticSoundGenerator -> generator.stop()
        }
    }

    fun setVolume(soundId: String, volume: Float) {
        val activeSound = activeSoundsMap[soundId] ?: return
        activeSoundsMap[soundId] = activeSound.copy(volume = volume)

        when (val generator = activeGenerators[soundId]) {
            is NoiseGenerator -> generator.setVolume(volume)
            is SyntheticSoundGenerator -> generator.setVolume(volume)
        }
    }

    fun isSoundActive(soundId: String): Boolean = activeSoundsMap.containsKey(soundId)

    fun pauseAll() {
        val currentSounds = activeSoundsMap.toMap()
        stopAll()
        // Store for resume - the activeSoundsMap is cleared by stopAll but
        // we keep the references in a separate structure
        _pausedSounds.clear()
        _pausedSounds.putAll(currentSounds)
    }

    fun resumeAll() {
        _pausedSounds.forEach { (_, activeSound) ->
            startSound(activeSound.sound, activeSound.volume)
        }
        _pausedSounds.clear()
    }

    fun stopAll() {
        activeGenerators.values.forEach { generator ->
            when (generator) {
                is NoiseGenerator -> generator.stop()
                is SyntheticSoundGenerator -> generator.stop()
            }
        }
        activeGenerators.clear()
        activeSoundsMap.clear()
    }

    private val _pausedSounds = mutableMapOf<String, ActiveSound>()
    val hasPausedSounds: Boolean get() = _pausedSounds.isNotEmpty()

    private fun createGenerator(sourceType: SoundSourceType): Any {
        return when (sourceType) {
            SoundSourceType.NOISE_WHITE -> NoiseGenerator(NoiseType.WHITE)
            SoundSourceType.NOISE_PINK -> NoiseGenerator(NoiseType.PINK)
            SoundSourceType.NOISE_BROWN -> NoiseGenerator(NoiseType.BROWN)
            SoundSourceType.NOISE_BLUE -> NoiseGenerator(NoiseType.BLUE)
            else -> SyntheticSoundGenerator(sourceType)
        }
    }
}
