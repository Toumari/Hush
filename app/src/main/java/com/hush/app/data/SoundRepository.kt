package com.hush.app.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundRepository @Inject constructor() {

    val allSounds: List<Sound> = listOf(
        // Noise - Free
        Sound("white_noise", "White Noise", SoundCategory.NOISE, false, SoundSourceType.NOISE_WHITE),
        Sound("pink_noise", "Pink Noise", SoundCategory.NOISE, false, SoundSourceType.NOISE_PINK),
        Sound("brown_noise", "Brown Noise", SoundCategory.NOISE, false, SoundSourceType.NOISE_BROWN),
        // Noise - Premium
        Sound("blue_noise", "Blue Noise", SoundCategory.NOISE, true, SoundSourceType.NOISE_BLUE),

        // Nature - Free
        Sound("rain", "Rain", SoundCategory.NATURE, false, SoundSourceType.SYNTHETIC_RAIN),
        Sound("ocean", "Ocean Waves", SoundCategory.NATURE, false, SoundSourceType.SYNTHETIC_OCEAN),
        Sound("forest", "Forest", SoundCategory.NATURE, false, SoundSourceType.SYNTHETIC_FOREST),
        Sound("wind", "Wind", SoundCategory.NATURE, false, SoundSourceType.SYNTHETIC_WIND),
        Sound("fireplace", "Fireplace", SoundCategory.NATURE, false, SoundSourceType.SYNTHETIC_FIREPLACE),
        // Nature - Premium
        Sound("thunder", "Thunder", SoundCategory.NATURE, true, SoundSourceType.SYNTHETIC_THUNDER),
        Sound("birds", "Birds", SoundCategory.NATURE, true, SoundSourceType.SYNTHETIC_BIRDS),
        Sound("crickets", "Crickets", SoundCategory.NATURE, true, SoundSourceType.SYNTHETIC_CRICKETS),

        // Ambient - Premium
        Sound("city", "City", SoundCategory.AMBIENT, true, SoundSourceType.SYNTHETIC_CITY),
        Sound("cafe", "Cafe", SoundCategory.AMBIENT, true, SoundSourceType.SYNTHETIC_CAFE),

        // Mechanical - Premium
        Sound("fan", "Fan", SoundCategory.MECHANICAL, true, SoundSourceType.SYNTHETIC_FAN),
        Sound("ac", "AC", SoundCategory.MECHANICAL, true, SoundSourceType.SYNTHETIC_AC),

        // Transport - Premium
        Sound("train", "Train", SoundCategory.TRANSPORT, true, SoundSourceType.SYNTHETIC_TRAIN),
        Sound("airplane", "Airplane", SoundCategory.TRANSPORT, true, SoundSourceType.SYNTHETIC_AIRPLANE)
    )

    fun getSoundsByCategory(): Map<SoundCategory, List<Sound>> {
        return allSounds.groupBy { it.category }
    }

    fun getSoundById(id: String): Sound? {
        return allSounds.find { it.id == id }
    }

    fun getFreeSounds(): List<Sound> = allSounds.filter { !it.isPremium }

    fun getPremiumSounds(): List<Sound> = allSounds.filter { it.isPremium }
}
