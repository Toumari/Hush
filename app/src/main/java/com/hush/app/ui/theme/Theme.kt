package com.hush.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val HushDarkColorScheme = darkColorScheme(
    primary = HushAccent,
    onPrimary = HushBackground,
    primaryContainer = HushAccentDim,
    onPrimaryContainer = HushTextPrimary,
    secondary = HushAccent,
    onSecondary = HushBackground,
    background = HushBackground,
    onBackground = HushTextPrimary,
    surface = HushSurface,
    onSurface = HushTextPrimary,
    surfaceVariant = HushSurfaceVariant,
    onSurfaceVariant = HushTextSecondary,
    outline = HushBorder,
    outlineVariant = HushBorder,
    error = HushError,
    onError = HushBackground
)

@Composable
fun HushTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = HushBackground.toArgb()
            window.navigationBarColor = HushBackground.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = HushDarkColorScheme,
        typography = HushTypography,
        content = content
    )
}
