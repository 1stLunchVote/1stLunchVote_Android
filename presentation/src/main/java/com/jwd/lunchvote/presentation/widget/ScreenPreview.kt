package com.jwd.lunchvote.presentation.widget

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.jwd.lunchvote.theme.LunchVoteTheme

@Composable
fun ScreenPreview(
  content: @Composable () -> Unit
) {
  com.jwd.lunchvote.theme.LunchVoteTheme {
    Surface {
      content()
    }
  }
}