package com.hush.app.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hush.app.ui.theme.HushAccent
import com.hush.app.ui.theme.HushAccentDim
import com.hush.app.ui.theme.HushBorder
import com.hush.app.ui.theme.HushTextSecondary

@Composable
fun VolumeSlider(
    label: String,
    volume: Float,
    onVolumeChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = HushTextSecondary,
            modifier = Modifier.width(80.dp)
        )
        Slider(
            value = volume,
            onValueChange = onVolumeChange,
            valueRange = 0f..1f,
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(
                thumbColor = HushAccent,
                activeTrackColor = HushAccent,
                inactiveTrackColor = HushBorder,
                activeTickColor = HushAccentDim,
                inactiveTickColor = HushBorder
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "${(volume * 100).toInt()}%",
            style = MaterialTheme.typography.labelSmall,
            color = HushTextSecondary,
            modifier = Modifier.width(32.dp)
        )
    }
}
