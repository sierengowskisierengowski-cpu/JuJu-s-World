package com.jujusworld.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val JujuColorScheme = darkColorScheme(
    primary          = JujuPink,
    secondary        = JujuPurple,
    tertiary         = JujuYellow,
    background       = JujuNight,
    surface          = Color(0xFF2D1B69),
    onPrimary        = Color.White,
    onSecondary      = Color.White,
    onBackground     = Color.White,
    onSurface        = Color.White,
    primaryContainer = Color(0xFF4A1D96),
    onPrimaryContainer = Color.White,
)

@Composable
fun JujusWorldTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }
    MaterialTheme(
        colorScheme = JujuColorScheme,
        typography  = Typography,
        content     = content
    )
}
