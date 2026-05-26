package com.thrivio.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = MascotGreen,
    secondary = XpOrange,
    tertiary = FitnessBlue,
    background = DarkCharcoal,
    surface = SurfaceCardDark,
    outline = BorderOutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = MascotGreen,
    secondary = XpOrange,
    tertiary = FitnessBlue,
    background = LightBackground,
    surface = SurfaceCardLight,
    outline = BorderOutlineLight
)

@Composable
fun ThrivioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ThrivioTypography,
        content = content
    )
}
