package com.jwd.lunchvote.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun LunchVoteTheme(
    content: @Composable () -> Unit
){
    MaterialTheme(
        content = content,
        colorScheme = lunchVoteColorScheme(),
        typography = nanumSquareTypography
    )
}