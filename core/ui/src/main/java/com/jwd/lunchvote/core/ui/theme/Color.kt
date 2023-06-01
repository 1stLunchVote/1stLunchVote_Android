package com.jwd.lunchvote.core.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val colorPrimary = Color(0xFFEB5530)
val colorOnPrimary = Color.White
val colorPrimaryContainer = Color(0xFFFFCCBC)
val colorOnPrimaryContainer = Color(0xFF3C0800)
val colorSecondary = Color(0xFFF44336)
val colorOnSecondary = Color.White
val colorSecondaryContainer = Color(0xFFFFCDD2)
val colorOnSecondaryContainer = Color(0xFF400F0F)
val colorTertiary = Color(0xFFFFC107)
val colorOnTertiary = Color.White
val colorTertiaryContainer = Color(0xFFFFECB3)
val colorOnTertiaryContainer = Color(0xFF3B1F1E)
val colorError = Color(0xFFBA1A1A)
val colorOnError = Color.White
val colorBackground = Color.White
val colorOnBackground = Color(0xFF201A19)
val colorOutline = Color(0xFF7D7D7D)
val colorDisabled = Color(0xFFC8C8C8)

@Composable
fun lunchVoteColorScheme() = MaterialTheme.colorScheme.copy(
    primary = colorPrimary,
    onPrimary = colorOnPrimary,
    primaryContainer = colorPrimaryContainer,
    onPrimaryContainer = colorOnPrimaryContainer,
    secondary = colorSecondary,
    onSecondary = colorOnSecondary,
    secondaryContainer = colorSecondaryContainer,
    onSecondaryContainer = colorOnSecondaryContainer,
    tertiary = colorTertiary,
    onTertiary = colorOnTertiary,
    tertiaryContainer = colorTertiaryContainer,
    onTertiaryContainer = colorOnTertiaryContainer,
    error = colorError,
    onError = colorOnError,
    background = colorBackground,
    onBackground = colorOnBackground,
    outline = colorOutline,
)