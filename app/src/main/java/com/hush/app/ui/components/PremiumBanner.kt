package com.hush.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.hush.app.ui.theme.HushAccent
import com.hush.app.ui.theme.HushAccentGlow
import com.hush.app.ui.theme.HushBorder
import com.hush.app.ui.theme.HushPremiumGold
import com.hush.app.ui.theme.HushTextPrimary
import com.hush.app.ui.theme.HushTextSecondary

@Composable
fun PremiumBanner(
    onUpgradeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(HushAccentGlow)
            .border(1.dp, HushBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onUpgradeClick)
            .padding(16.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.AutoAwesome,
            contentDescription = null,
            tint = HushPremiumGold,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Unlock All Sounds",
                style = MaterialTheme.typography.titleMedium,
                color = HushTextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Remove ads + 10 premium sounds for \$2.99",
                style = MaterialTheme.typography.bodySmall,
                color = HushTextSecondary
            )
        }
        Text(
            text = "Upgrade",
            style = MaterialTheme.typography.labelLarge,
            color = HushAccent,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, HushAccent, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun PremiumRequiredDialog(
    onUpgrade: () -> Unit,
    onDismiss: () -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(com.hush.app.ui.theme.HushSurface)
                .border(1.dp, HushBorder, RoundedCornerShape(20.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.AutoAwesome,
                contentDescription = null,
                tint = HushPremiumGold,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Premium Sound",
                style = MaterialTheme.typography.headlineMedium,
                color = HushTextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "This sound is part of the premium collection. Upgrade to unlock all 18 sounds and remove ads.",
                style = MaterialTheme.typography.bodyMedium,
                color = HushTextSecondary,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            androidx.compose.material3.TextButton(
                onClick = onUpgrade,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(HushAccent)
            ) {
                Text(
                    text = "Upgrade for \$2.99",
                    color = com.hush.app.ui.theme.HushSurface,
                    style = MaterialTheme.typography.labelLarge
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text(
                    text = "Maybe Later",
                    color = HushTextSecondary,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
