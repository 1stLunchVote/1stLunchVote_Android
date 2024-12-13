package com.jwd.lunchvote.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun lunchVoteColorScheme() = MaterialTheme.colorScheme.copy(
    primary = Color(0xFFEB5530),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFCCBC),
    onPrimaryContainer = Color(0xFF3C0800),
    secondary = Color(0xFF4AA96C),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFCDD2),
    onSecondaryContainer = Color(0xFF400F0F),
    tertiary = Color(0xFFFFC107),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFECB3),
    onTertiaryContainer = Color(0xFF3B1F1E),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    background = Color.White,
    onBackground = Color(0xFF201A19),
    surface = Color.White,
    onSurface = Color(0xFF201A19),
    outline = Color(0xFF7D7D7D),
    outlineVariant = Color(0xFFC8C8C8)
)