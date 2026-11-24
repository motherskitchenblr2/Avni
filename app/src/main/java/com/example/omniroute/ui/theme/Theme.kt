package com.example.omniroute.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = OmniCyan,
    onPrimary = OmniBg,
    primaryContainer = OmniCyanGlow,
    onPrimaryContainer = OmniCyan,
    secondary = OmniPurple,
    onSecondary = OmniBg,
    secondaryContainer = OmniPurpleGlow,
    onSecondaryContainer = OmniPurple,
    tertiary = OmniGreen,
    onTertiary = OmniBg,
    background = OmniBg,
    onBackground = OmniTextPrimary,
    surface = OmniSurface,
    onSurface = OmniTextPrimary,
    surfaceVariant = OmniSurfaceVariant,
    onSurfaceVariant = OmniTextSecondary,
    outline = OmniBorder
)

@Composable
fun OmniRouteTheme(
    darkTheme: Boolean = true, // OmniRoute dark cyber tech aesthetic
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = OmniBg.toArgb()
            window.navigationBarColor = OmniBg.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
