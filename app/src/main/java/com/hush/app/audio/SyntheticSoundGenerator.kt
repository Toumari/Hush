package com.hush.app.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.hush.app.data.SoundSourceType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Random
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Generates synthetic nature-like sounds using filtered noise and amplitude modulation.
 * Each sound type uses different filter parameters and modulation patterns to create
 * distinctive textures that approximate real environmental sounds.
 */
class SyntheticSoundGenerator(private val sourceType: SoundSourceType) {

    companion object {
        private const val SAMPLE_RATE = 44100
        private const val BUFFER_SIZE = 4096
    }

    private var audioTrack: AudioTrack? = null
    private var playbackJob: Job? = null
    private var volume: Float = 0.7f
    private val random = Random()

    // Filter state
    private var lpState = 0.0
    private var hpState = 0.0
    private var bpState1 = 0.0
    private var bpState2 = 0.0
    private var phase = 0.0
    private var modPhase = 0.0
    private var brownState = 0.0
    private var sampleCounter = 0L

    // Extended state for improved multi-layer sounds
    private var lpState2 = 0.0
    private var lpState3 = 0.0
    private var hpState2 = 0.0
    private var brownState2 = 0.0
    private var brownState3 = 0.0
    private var phase2 = 0.0
    private var modPhase2 = 0.0
    private var modPhase3 = 0.0
    private var envelopeState = 0.0
    private var dripTimer = 0.0
    private var dripEnv = 0.0

    fun start(scope: CoroutineScope) {
        if (audioTrack != null) return

        val bufferSize = AudioTrack.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        ).coerceAtLeast(BUFFER_SIZE * 2)

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
            val buffer = ShortArray(BUFFER_SIZE)
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
        resetState()
    }

    fun setVolume(vol: Float) {
        volume = vol.coerceIn(0f, 1f)
    }

    private fun resetState() {
        lpState = 0.0
        hpState = 0.0
        bpState1 = 0.0
        bpState2 = 0.0
        phase = 0.0
        modPhase = 0.0
        brownState = 0.0
        sampleCounter = 0L
        lpState2 = 0.0
        lpState3 = 0.0
        hpState2 = 0.0
        brownState2 = 0.0
        brownState3 = 0.0
        phase2 = 0.0
        modPhase2 = 0.0
        modPhase3 = 0.0
        envelopeState = 0.0
        dripTimer = 0.0
        dripEnv = 0.0
    }

    private fun generateBuffer(buffer: ShortArray) {
        when (sourceType) {
            SoundSourceType.SYNTHETIC_RAIN -> generateRain(buffer)
            SoundSourceType.SYNTHETIC_OCEAN -> generateOcean(buffer)
            SoundSourceType.SYNTHETIC_FOREST -> generateForest(buffer)
            SoundSourceType.SYNTHETIC_WIND -> generateWind(buffer)
            SoundSourceType.SYNTHETIC_FIREPLACE -> generateFireplace(buffer)
            SoundSourceType.SYNTHETIC_THUNDER -> generateThunder(buffer)
            SoundSourceType.SYNTHETIC_BIRDS -> generateBirds(buffer)
            SoundSourceType.SYNTHETIC_CRICKETS -> generateCrickets(buffer)
            SoundSourceType.SYNTHETIC_CITY -> generateCity(buffer)
            SoundSourceType.SYNTHETIC_CAFE -> generateCafe(buffer)
            SoundSourceType.SYNTHETIC_FAN -> generateFan(buffer)
            SoundSourceType.SYNTHETIC_AC -> generateAC(buffer)
            SoundSourceType.SYNTHETIC_TRAIN -> generateTrain(buffer)
            SoundSourceType.SYNTHETIC_AIRPLANE -> generateAirplane(buffer)
            else -> generateRain(buffer) // fallback
        }
    }

    // Rain: Three-layer approach — steady wash + mid patter + individual drip transients
    private fun generateRain(buffer: ShortArray) {
        for (i in buffer.indices) {
            sampleCounter++
            modPhase += 1.0 / SAMPLE_RATE

            // Layer 1: Steady low-frequency rain wash (brown-ish filtered noise)
            brownState += random.nextGaussian() * 120
            brownState = brownState.coerceIn(-10000.0, 10000.0)
            brownState *= 0.9992
            // Gentle slow swell on the wash
            val washMod = 0.8 + 0.2 * sin(2 * PI * modPhase * 0.07)
            val wash = brownState * washMod

            // Layer 2: Mid/high frequency patter (bandpass filtered noise with faster modulation)
            val white = random.nextGaussian() * 2500
            lpState += 0.18 * (white - lpState)  // LP at ~1.2 kHz
            hpState += 0.025 * (lpState - hpState) // HP at ~175 Hz
            val patter = lpState - hpState
            // Irregular intensity modulation — two incommensurate sine waves
            val patterMod = 0.6 + 0.25 * sin(2 * PI * modPhase * 0.23) +
                    0.15 * sin(2 * PI * modPhase * 0.37)

            // Layer 3: Sporadic drip transients (sharp filtered clicks at random intervals)
            dripTimer -= 1.0 / SAMPLE_RATE
            if (dripTimer <= 0) {
                dripEnv = 1.0
                // Next drip in 0.05 to 0.4 seconds (irregular)
                dripTimer = 0.05 + random.nextDouble() * 0.35
            }
            dripEnv *= 0.9985 // fast exponential decay (~50ms)
            val dripNoise = random.nextGaussian() * 4000
            lpState2 += 0.35 * (dripNoise - lpState2) // higher cutoff for bright drip
            val drip = lpState2 * dripEnv

            // Mix layers
            val sample = wash * 0.40 + patter * patterMod * 0.45 + drip * 0.15
            buffer[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
    }

    // Ocean: Two overlapping wave cycles at different periods + foam/hiss layer + deep undertow
    private fun generateOcean(buffer: ShortArray) {
        for (i in buffer.indices) {
            sampleCounter++
            modPhase += 1.0 / SAMPLE_RATE

            // Deep undertow rumble (very low-frequency brown noise)
            brownState += random.nextGaussian() * 100
            brownState = brownState.coerceIn(-8000.0, 8000.0)
            brownState *= 0.9997

            // Wave 1: ~8 second period — primary wave swell
            val wave1Phase = modPhase * 0.125
            // Asymmetric wave shape: slow rise, sharper fall (like a real wave cresting)
            val wave1Raw = sin(2 * PI * wave1Phase)
            val wave1 = 0.3 + 0.7 * (0.5 + 0.5 * wave1Raw).let { it * it } // squared for asymmetry

            // Wave 2: ~13 second period — secondary slower swell (interference pattern)
            val wave2Phase = modPhase * 0.077
            val wave2 = 0.5 + 0.5 * sin(2 * PI * wave2Phase)

            // Combined wave envelope
            val waveEnv = wave1 * 0.65 + wave2 * 0.35

            // Body: brown noise shaped by wave envelope
            brownState2 += random.nextGaussian() * 180
            brownState2 = brownState2.coerceIn(-12000.0, 12000.0)
            brownState2 *= 0.9988
            val body = brownState2 * waveEnv

            // Foam/hiss: high-pass filtered noise that appears at wave crests
            val foamGate = if (waveEnv > 0.55) (waveEnv - 0.55) / 0.45 else 0.0
            val foamNoise = random.nextGaussian() * 2000
            lpState += 0.2 * (foamNoise - lpState)
            hpState += 0.04 * (lpState - hpState)
            val foam = (lpState - hpState) * foamGate * foamGate // squared gate for natural onset

            // Receding water hiss (inverse of wave — louder between waves)
            val recedeGate = (1.0 - waveEnv).coerceIn(0.0, 1.0) * 0.3
            val recedeNoise = random.nextGaussian() * 1200
            lpState2 += 0.12 * (recedeNoise - lpState2)
            val recede = lpState2 * recedeGate

            // Mix: deep rumble + wave body + crest foam + receding wash
            val sample = brownState * 0.15 + body * 0.45 + foam * 0.28 + recede * 0.12
            buffer[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
    }

    // Forest: very low-level filtered noise with occasional gentle variations
    private fun generateForest(buffer: ShortArray) {
        for (i in buffer.indices) {
            val white = random.nextGaussian() * 1500
            // Heavy low-pass for rustling
            lpState += 0.03 * (white - lpState)

            // Very slow modulation
            modPhase += 0.05 / SAMPLE_RATE
            val mod = 0.7 + 0.3 * sin(2 * PI * modPhase)

            // Occasional random chirp-like transient
            val chirp = if (random.nextFloat() < 0.0001) {
                sin(phase) * 2000 * random.nextFloat()
            } else 0.0
            phase += 2 * PI * (2000 + random.nextGaussian() * 500) / SAMPLE_RATE

            buffer[i] = (lpState * mod + chirp * 0.3).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            sampleCounter++
        }
    }

    // Wind: heavily low-pass filtered noise with slow sweeping modulation
    private fun generateWind(buffer: ShortArray) {
        for (i in buffer.indices) {
            val white = random.nextGaussian() * 5000
            // Variable cutoff low-pass for whistling effect
            modPhase += 0.08 / SAMPLE_RATE
            val cutoffMod = 0.02 + 0.04 * sin(2 * PI * modPhase)
            lpState += cutoffMod * (white - lpState)

            // Secondary slow modulation for gusts
            val gustPhase = sampleCounter.toDouble() / SAMPLE_RATE * 0.05
            val gust = 0.5 + 0.5 * sin(2 * PI * gustPhase)

            buffer[i] = (lpState * gust).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            sampleCounter++
        }
    }

    // Fireplace: warm low roar + mid-frequency flicker + sharp crackle pops + ember hiss
    private fun generateFireplace(buffer: ShortArray) {
        for (i in buffer.indices) {
            sampleCounter++
            modPhase += 1.0 / SAMPLE_RATE

            // Layer 1: Deep warm roar (heavy low-pass brown noise, very steady)
            brownState += random.nextGaussian() * 80
            brownState = brownState.coerceIn(-6000.0, 6000.0)
            brownState *= 0.9994
            val roar = brownState

            // Layer 2: Mid-frequency flickering (bandpass noise with slow irregular modulation)
            val midNoise = random.nextGaussian() * 2000
            lpState += 0.08 * (midNoise - lpState)
            hpState += 0.012 * (lpState - hpState)
            val flicker = lpState - hpState
            // Irregular flicker intensity — simulates flame dancing
            val flickerMod = 0.5 + 0.3 * sin(2 * PI * modPhase * 0.4) +
                    0.2 * sin(2 * PI * modPhase * 1.1)

            // Layer 3: Crackle pops (sparse bright transients with fast decay)
            // Two tiers: small frequent crackles + rare big pops
            val smallCrackle = if (random.nextFloat() < 0.003) {
                random.nextGaussian() * 4000 * (0.3 + random.nextFloat() * 0.7)
            } else 0.0
            val bigPop = if (random.nextFloat() < 0.0004) {
                random.nextGaussian() * 8000
            } else 0.0
            // Envelope follower for crackle decay
            val crackleInput = smallCrackle + bigPop
            envelopeState = maxOf(envelopeState * 0.997, kotlin.math.abs(crackleInput) / 8000.0)
            lpState2 += 0.4 * (crackleInput - lpState2) // slight smoothing on crackle
            val crackle = lpState2 * envelopeState

            // Layer 4: Gentle ember hiss (very quiet high-frequency continuous)
            val emberNoise = random.nextGaussian() * 600
            lpState3 += 0.25 * (emberNoise - lpState3)
            hpState2 += 0.06 * (lpState3 - hpState2)
            val ember = lpState3 - hpState2

            // Mix: warm roar + dancing flicker + crackle transients + ember hiss
            val sample = roar * 0.35 + flicker * flickerMod * 0.30 + crackle * 0.25 + ember * 0.10
            buffer[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
    }

    // Thunder: deep rumble with occasional louder swells
    private fun generateThunder(buffer: ShortArray) {
        for (i in buffer.indices) {
            brownState += random.nextGaussian() * 200
            brownState = brownState.coerceIn(-14000.0, 14000.0)
            brownState *= 0.9995

            // Very heavy low-pass for deep rumble
            lpState += 0.008 * (brownState - lpState)

            // Slow swell modulation (~20 second period)
            modPhase += 1.0 / SAMPLE_RATE
            val swell = 0.5 + 0.5 * sin(2 * PI * modPhase * 0.05)

            buffer[i] = (lpState * swell).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            sampleCounter++
        }
    }

    // Birds: filtered noise with periodic chirp-like modulations
    private fun generateBirds(buffer: ShortArray) {
        for (i in buffer.indices) {
            // Quiet base noise
            val base = random.nextGaussian() * 400
            lpState += 0.05 * (base - lpState)

            // Chirp generation using simple FM synthesis
            modPhase += 1.0 / SAMPLE_RATE

            val chirpRate = 3.0 // chirps per second
            val chirpPhase = modPhase * chirpRate
            val chirpEnv = (sin(2 * PI * chirpPhase) * 0.5 + 0.5).let { it * it * it }

            // Frequency modulated tone for bird-like sound
            phase += 2 * PI * (2500 + 1500 * sin(2 * PI * chirpPhase * 4.3)) / SAMPLE_RATE
            val chirpSignal = sin(phase) * chirpEnv * 3000

            buffer[i] = (lpState * 0.3 + chirpSignal * 0.7).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            sampleCounter++
        }
    }

    // Crickets: high-frequency pulsing tone
    private fun generateCrickets(buffer: ShortArray) {
        for (i in buffer.indices) {
            modPhase += 1.0 / SAMPLE_RATE

            // Cricket pulse envelope (~7 Hz pulse rate)
            val pulse = (sin(2 * PI * modPhase * 7) * 0.5 + 0.5).let {
                if (it > 0.3) 1.0 else 0.0
            }

            // High-frequency carrier (~4500 Hz)
            phase += 2 * PI * 4500 / SAMPLE_RATE
            val carrier = sin(phase) * 2500

            // Slight noise underneath
            val noise = random.nextGaussian() * 200
            lpState += 0.02 * (noise - lpState)

            buffer[i] = (carrier * pulse * 0.7 + lpState * 0.3).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            sampleCounter++
        }
    }

    // City: broadband noise with low-frequency rumble and occasional events
    private fun generateCity(buffer: ShortArray) {
        for (i in buffer.indices) {
            // Traffic rumble (heavy low-pass brown noise)
            brownState += random.nextGaussian() * 250
            brownState = brownState.coerceIn(-10000.0, 10000.0)
            brownState *= 0.999

            lpState += 0.01 * (brownState - lpState)

            // Mid-range activity noise
            val activity = random.nextGaussian() * 1000
            hpState += 0.08 * (activity - hpState)

            buffer[i] = (lpState * 0.6 + hpState * 0.4).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            sampleCounter++
        }
    }

    // Cafe: mid-range murmur noise with clinking sounds
    private fun generateCafe(buffer: ShortArray) {
        for (i in buffer.indices) {
            // Murmur: bandpass filtered noise
            val white = random.nextGaussian() * 2000
            bpState1 += 0.05 * (white - bpState1)
            bpState2 += 0.01 * (bpState1 - bpState2)
            val murmur = bpState1 - bpState2

            // Occasional clink transient
            val clink = if (random.nextFloat() < 0.0003) {
                sin(phase) * 3000 * random.nextFloat()
            } else 0.0
            phase += 2 * PI * (3000 + random.nextGaussian() * 800) / SAMPLE_RATE

            lpState += 0.2 * (clink - lpState)

            buffer[i] = (murmur * 0.8 + lpState * 0.2).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            sampleCounter++
        }
    }

    // Fan: low-frequency hum with slight wobble
    private fun generateFan(buffer: ShortArray) {
        for (i in buffer.indices) {
            modPhase += 1.0 / SAMPLE_RATE

            // Fundamental hum (~120 Hz motor)
            phase += 2 * PI * 120 / SAMPLE_RATE
            val hum = sin(phase) * 1500

            // Add harmonics
            val harmonic2 = sin(phase * 2) * 600
            val harmonic3 = sin(phase * 3) * 300

            // Wobbly air noise
            val airNoise = random.nextGaussian() * 1500
            lpState += 0.04 * (airNoise - lpState)

            // Slight RPM wobble
            val wobble = 1.0 + 0.02 * sin(2 * PI * modPhase * 0.3)

            val sample = (hum + harmonic2 + harmonic3) * 0.3 * wobble + lpState * 0.7
            buffer[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            sampleCounter++
        }
    }

    // AC: steady broadband noise with compressor hum
    private fun generateAC(buffer: ShortArray) {
        for (i in buffer.indices) {
            // Broadband air noise
            val airNoise = random.nextGaussian() * 2500
            lpState += 0.06 * (airNoise - lpState)

            // Compressor hum (~60 Hz)
            phase += 2 * PI * 60 / SAMPLE_RATE
            val hum = sin(phase) * 800

            // Very slow cycling
            modPhase += 0.02 / SAMPLE_RATE
            val cycle = 0.9 + 0.1 * sin(2 * PI * modPhase)

            val sample = (lpState * 0.85 + hum * 0.15) * cycle
            buffer[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            sampleCounter++
        }
    }

    // Train: rhythmic clacking with low rumble
    private fun generateTrain(buffer: ShortArray) {
        for (i in buffer.indices) {
            modPhase += 1.0 / SAMPLE_RATE

            // Wheel rumble (brown noise base)
            brownState += random.nextGaussian() * 200
            brownState = brownState.coerceIn(-10000.0, 10000.0)
            brownState *= 0.999

            // Rhythmic clack (~2 Hz for rail joints)
            val clackPhase = modPhase * 2.0
            val clackEnv = (sin(2 * PI * clackPhase) * 0.5 + 0.5).let {
                if (it > 0.85) (it - 0.85) / 0.15 else 0.0
            }
            val clack = random.nextGaussian() * 5000 * clackEnv

            lpState += 0.1 * (clack - lpState)

            val sample = brownState * 0.6 + lpState * 0.4
            buffer[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            sampleCounter++
        }
    }

    // Airplane: heavy low-frequency roar with slight variation
    private fun generateAirplane(buffer: ShortArray) {
        for (i in buffer.indices) {
            // Engine roar: pink-ish noise
            val white = random.nextGaussian() * 4000
            lpState += 0.03 * (white - lpState)

            // Engine drone harmonics
            phase += 2 * PI * 85 / SAMPLE_RATE
            val drone = sin(phase) * 1200 + sin(phase * 2.03) * 600 + sin(phase * 3.07) * 300

            // Slow variation
            modPhase += 0.03 / SAMPLE_RATE
            val variation = 0.95 + 0.05 * sin(2 * PI * modPhase)

            val sample = (lpState * 0.65 + drone * 0.35) * variation
            buffer[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            sampleCounter++
        }
    }

    private fun applyVolume(buffer: ShortArray) {
        for (i in buffer.indices) {
            buffer[i] = (buffer[i] * volume).toInt().toShort()
        }
    }
}
