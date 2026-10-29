package org.parabellumlinuxproject.getmyip.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PlpNavy = Color(0xFF0D1B2A)
val PlpNavyLight = Color(0xFF1B263B)
val PlpGreen = Color(0xFF1B4332)
val PlpGreenAccent = Color(0xFF74C69D)
val PlpText = Color(0xFFE0E1DD)
val PlpTextSecondary = Color(0xFFA9BCD0)

private val PlpColors = darkColorScheme(
    primary = PlpGreenAccent,
    onPrimary = PlpNavy,
    primaryContainer = PlpGreen,
    onPrimaryContainer = Color.White,
    secondary = PlpTextSecondary,
    onSecondary = PlpNavy,
    background = PlpNavy,
    onBackground = PlpText,
    surface = PlpNavyLight,
    onSurface = PlpText,
    surfaceVariant = PlpNavyLight,
    onSurfaceVariant = PlpTextSecondary,
)

@Composable
fun PlpTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = PlpColors, content = content)
}
