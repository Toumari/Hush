package com.hush.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.hush.app.ui.theme.HushAccent
import com.hush.app.ui.theme.HushAccentGlow
import com.hush.app.ui.theme.HushBorder
import com.hush.app.ui.theme.HushSurface
import com.hush.app.ui.theme.HushTextPrimary
import com.hush.app.ui.theme.HushTextSecondary

data class TimerOption(val label: String, val minutes: Int)

private val timerOptions = listOf(
    TimerOption("15 min", 15),
    TimerOption("30 min", 30),
    TimerOption("45 min", 45),
    TimerOption("1 hour", 60),
    TimerOption("1.5 hours", 90),
    TimerOption("2 hours", 120),
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SleepTimerDialog(
    currentTimerMinutes: Int?,
    onSetTimer: (Int) -> Unit,
    onCancelTimer: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedMinutes by remember { mutableIntStateOf(currentTimerMinutes ?: 30) }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(HushSurface)
                .border(1.dp, HushBorder, RoundedCornerShape(20.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Sleep Timer",
                style = MaterialTheme.typography.headlineMedium,
                color = HushTextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (currentTimerMinutes != null) "Timer active: $currentTimerMinutes min remaining"
                else "Stop playback after a set time",
                style = MaterialTheme.typography.bodyMedium,
                color = HushTextSecondary
            )
            Spacer(modifier = Modifier.height(20.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                timerOptions.forEach { option ->
                    val isSelected = selectedMinutes == option.minutes
                    Text(
                        text = option.label,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isSelected) HushAccent else HushTextSecondary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) HushAccentGlow else HushSurface)
                            .border(
                                1.dp,
                                if (isSelected) HushAccent else HushBorder,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedMinutes = option.minutes }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(
                onClick = { onSetTimer(selectedMinutes) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(HushAccent)
            ) {
                Text(
                    text = "Set Timer",
                    color = HushSurface,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            if (currentTimerMinutes != null) {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = onCancelTimer) {
                    Text(
                        text = "Cancel Timer",
                        color = HushTextSecondary,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}
