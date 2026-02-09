package com.hush.app.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cabin
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.DirectionsTransit
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.Water
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Waves
import androidx.compose.ui.graphics.vector.ImageVector

object SoundIcons {
    fun getIcon(soundId: String): ImageVector {
        return when (soundId) {
            "white_noise" -> Icons.Filled.GraphicEq
            "pink_noise" -> Icons.Filled.GraphicEq
            "brown_noise" -> Icons.Filled.GraphicEq
            "blue_noise" -> Icons.Filled.GraphicEq
            "rain" -> Icons.Filled.WaterDrop
            "ocean" -> Icons.Filled.Waves
            "forest" -> Icons.Filled.Forest
            "wind" -> Icons.Filled.Air
            "fireplace" -> Icons.Filled.LocalFireDepartment
            "thunder" -> Icons.Filled.Thunderstorm
            "birds" -> Icons.Filled.WbSunny
            "crickets" -> Icons.Filled.Nightlight
            "city" -> Icons.Filled.LocationCity
            "cafe" -> Icons.Filled.Coffee
            "fan" -> Icons.Filled.Air
            "ac" -> Icons.Filled.Cloud
            "train" -> Icons.Filled.DirectionsTransit
            "airplane" -> Icons.Filled.FlightTakeoff
            else -> Icons.Filled.GraphicEq
        }
    }
}
