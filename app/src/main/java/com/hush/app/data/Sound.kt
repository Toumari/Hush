package com.hush.app.data

data class Sound(
    val id: String,
    val name: String,
    val category: SoundCategory,
    val isPremium: Boolean,
    val sourceType: SoundSourceType
)

enum class SoundSourceType {
    NOISE_WHITE,
    NOISE_PINK,
    NOISE_BROWN,
    NOISE_BLUE,
    SYNTHETIC_RAIN,
    SYNTHETIC_OCEAN,
    SYNTHETIC_FOREST,
    SYNTHETIC_WIND,
    SYNTHETIC_FIREPLACE,
    SYNTHETIC_THUNDER,
    SYNTHETIC_BIRDS,
    SYNTHETIC_CRICKETS,
    SYNTHETIC_CITY,
    SYNTHETIC_CAFE,
    SYNTHETIC_FAN,
    SYNTHETIC_AC,
    SYNTHETIC_TRAIN,
    SYNTHETIC_AIRPLANE
}
