package com.hush.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hush.app.data.Sound
import com.hush.app.ui.icons.SoundIcons
import com.hush.app.ui.theme.HushAccent
import com.hush.app.ui.theme.HushAccentGlow
import com.hush.app.ui.theme.HushBorder
import com.hush.app.ui.theme.HushSurface
import com.hush.app.ui.theme.HushTextMuted
import com.hush.app.ui.theme.HushTextSecondary

@Composable
fun SoundCard(
    sound: Sound,
    isActive: Boolean,
    isLocked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isActive) HushAccentGlow else HushSurface,
        animationSpec = tween(300),
        label = "bg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isActive) HushAccent else HushBorder,
        animationSpec = tween(300),
        label = "border"
    )
    val iconColor by animateColorAsState(
        targetValue = if (isActive) HushAccent else if (isLocked) HushTextMuted else HushTextSecondary,
        animationSpec = tween(300),
        label = "icon"
    )
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1.02f else 1f,
        animationSpec = tween(200),
        label = "scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .width(88.dp)
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = SoundIcons.getIcon(sound.id),
                contentDescription = sound.name,
                tint = iconColor,
                modifier = Modifier.size(32.dp)
            )
            if (isLocked) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Premium",
                    tint = HushTextMuted,
                    modifier = Modifier
                        .size(14.dp)
                        .align(Alignment.TopEnd)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = sound.name,
            style = MaterialTheme.typography.labelMedium,
            color = if (isActive) HushAccent else if (isLocked) HushTextMuted else Color.Unspecified,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
