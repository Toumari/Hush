package com.hush.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hush.app.data.Sound
import com.hush.app.data.SoundCategory
import com.hush.app.ui.theme.HushTextMuted

@Composable
fun CategorySection(
    category: SoundCategory,
    sounds: List<Sound>,
    activeSoundIds: Set<String>,
    isPremium: Boolean,
    onSoundClick: (Sound) -> Unit,
    modifier: Modifier = Modifier
) {
    val allPremium = sounds.all { it.isPremium }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            Text(
                text = category.displayName,
                style = MaterialTheme.typography.titleMedium
            )
            if (allPremium && !isPremium) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Premium",
                    tint = HushTextMuted,
                    modifier = Modifier
                        .padding(start = 6.dp)
                        .height(14.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 20.dp)
        ) {
            items(sounds, key = { it.id }) { sound ->
                SoundCard(
                    sound = sound,
                    isActive = activeSoundIds.contains(sound.id),
                    isLocked = sound.isPremium && !isPremium,
                    onClick = { onSoundClick(sound) }
                )
            }
        }
    }
}
