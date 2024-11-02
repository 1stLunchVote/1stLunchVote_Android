package com.jwd.lunchvote.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun LunchVoteTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = lunchVoteColorScheme(),
    shapes = lunchVoteShapes(),
    typography = nanumSquareTypography,
    content = content
  )
}