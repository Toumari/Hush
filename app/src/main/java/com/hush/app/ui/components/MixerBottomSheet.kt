package com.hush.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.hush.app.data.ActiveSound
import com.hush.app.ui.theme.HushAccent
import com.hush.app.ui.theme.HushBorder
import com.hush.app.ui.theme.HushSurface
import com.hush.app.ui.theme.HushTextPrimary
import com.hush.app.ui.theme.HushTextSecondary

@Composable
fun MixerBottomSheet(
    activeSounds: List<ActiveSound>,
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onStopAll: () -> Unit,
    onVolumeChange: (String, Float) -> Unit,
    onRemoveSound: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    if (activeSounds.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(HushSurface)
            .border(
                1.dp,
                HushBorder,
                RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Header row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Play/Pause button
                IconButton(
                    onClick = onPlayPause,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(HushAccent)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = HushSurface,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "${activeSounds.size} sound${if (activeSounds.size != 1) "s" else ""} playing",
                        style = MaterialTheme.typography.titleMedium,
                        color = HushTextPrimary
                    )
                    Text(
                        text = activeSounds.joinToString(" · ") { it.sound.name },
                        style = MaterialTheme.typography.bodySmall,
                        color = HushTextSecondary,
                        maxLines = 1
                    )
                }
            }
            Row {
                IconButton(onClick = onStopAll, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Filled.Stop,
                        contentDescription = "Stop all",
                        tint = HushTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.ExpandMore else Icons.Filled.ExpandLess,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = HushTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Expanded content: volume sliders
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                modifier = Modifier.padding(top = 12.dp)
            ) {
                activeSounds.forEach { activeSound ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        VolumeSlider(
                            label = activeSound.sound.name,
                            volume = activeSound.volume,
                            onVolumeChange = { onVolumeChange(activeSound.sound.id, it) },
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { onRemoveSound(activeSound.sound.id) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = "Remove ${activeSound.sound.name}",
                                tint = HushTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}
