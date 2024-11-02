package com.jwd.lunchvote.presentation.widget

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.jwd.lunchvote.presentation.theme.LunchVoteTheme

@Composable
fun ScreenPreview(
  content: @Composable () -> Unit
) {
  LunchVoteTheme {
    Surface {
      content()
    }
  }
}