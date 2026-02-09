package com.hush.app.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Random

enum class NoiseType {
    WHITE, PINK, BROWN, BLUE
}

class NoiseGenerator(private val noiseType: NoiseType) {

    companion object {
        private const val SAMPLE_RATE = 44100
        private const val BUFFER_SIZE_SAMPLES = 4096
    }

    private var audioTrack: AudioTrack? = null
    private var playbackJob: Job? = null
    private var volume: Float = 0.7f
    private val random = Random()

    // Pink noise state (Voss-McCartney algorithm)
    private val pinkRows = IntArray(16)
    private var pinkRunningSum = 0
    private var pinkIndex = 0

    // Brown noise state
    private var brownLast = 0.0

    fun start(scope: CoroutineScope) {
        if (audioTrack != null) return

        val bufferSize = AudioTrack.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        ).coerceAtLeast(BUFFER_SIZE_SAMPLES * 2)

        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setSampleRate(SAMPLE_RATE)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        audioTrack?.play()

        playbackJob = scope.launch(Dispatchers.IO) {
            val buffer = ShortArray(BUFFER_SIZE_SAMPLES)
            while (isActive) {
                generateBuffer(buffer)
                applyVolume(buffer)
                audioTrack?.write(buffer, 0, buffer.size)
            }
        }
    }

    fun stop() {
        playbackJob?.cancel()
        playbackJob = null
        try {
            audioTrack?.stop()
        } catch (_: IllegalStateException) {}
        audioTrack?.release()
        audioTrack = null
        // Reset state
        pinkRows.fill(0)
        pinkRunningSum = 0
        pinkIndex = 0
        brownLast = 0.0
    }

    fun setVolume(vol: Float) {
        volume = vol.coerceIn(0f, 1f)
    }

    private fun generateBuffer(buffer: ShortArray) {
        when (noiseType) {
            NoiseType.WHITE -> generateWhiteNoise(buffer)
            NoiseType.PINK -> generatePinkNoise(buffer)
            NoiseType.BROWN -> generateBrownNoise(buffer)
            NoiseType.BLUE -> generateBlueNoise(buffer)
        }
    }

    private fun generateWhiteNoise(buffer: ShortArray) {
        for (i in buffer.indices) {
            buffer[i] = (random.nextGaussian() * 4000).toInt().toShort()
        }
    }

    private fun generatePinkNoise(buffer: ShortArray) {
        for (i in buffer.indices) {
            pinkIndex = (pinkIndex + 1) % 65536
            var newRandom: Int

            // Voss-McCartney: update rows based on trailing zeros
            val numZeros = Integer.numberOfTrailingZeros(pinkIndex).coerceAtMost(pinkRows.size - 1)
            pinkRunningSum -= pinkRows[numZeros]
            newRandom = (random.nextGaussian() * 500).toInt()
            pinkRunningSum += newRandom
            pinkRows[numZeros] = newRandom

            val white = (random.nextGaussian() * 500).toInt()
            val pink = (pinkRunningSum + white) / 4
            buffer[i] = pink.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
    }

    private fun generateBrownNoise(buffer: ShortArray) {
        for (i in buffer.indices) {
            brownLast += random.nextGaussian() * 200
            // Clamp to prevent drift
            brownLast = brownLast.coerceIn(-16000.0, 16000.0)
            buffer[i] = brownLast.toInt().toShort()
        }
    }

    private fun generateBlueNoise(buffer: ShortArray) {
        // Blue noise: differentiated white noise (high-frequency emphasis)
        var prevSample = 0.0
        for (i in buffer.indices) {
            val white = random.nextGaussian() * 4000
            val blue = white - prevSample
            prevSample = white
            buffer[i] = (blue * 0.5).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
    }

    private fun applyVolume(buffer: ShortArray) {
        for (i in buffer.indices) {
            buffer[i] = (buffer[i] * volume).toInt().toShort()
        }
    }
}
