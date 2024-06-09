package com.jwd.lunchvote.presentation.widget

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import kotlinx.coroutines.delay

@Composable
fun HorizontalProgressBar(
  timeLimitSecond: Int,
  modifier: Modifier = Modifier,
  onProgressComplete: () -> Unit = {}
) {
  var progress by remember { mutableFloatStateOf(0f) }
  val progressAnimation by animateFloatAsState(
    targetValue = progress,
    animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
    label = "Vote Progress Animation",
  )

  LaunchedEffect(Unit) {
    while (progress < 1f) {
      progress += 1f / timeLimitSecond
      delay(1000)
      if (progress >= 1f) onProgressComplete()
    }
  }

  LinearProgressIndicator(
    progress = { progressAnimation },
    modifier = modifier,
    trackColor = MaterialTheme.colorScheme.outlineVariant,
  )
}

@Preview
@Composable
private fun Preview() {
  LunchVoteTheme {
    Surface {
      HorizontalProgressBar(
        timeLimitSecond = 60,
        modifier = Modifier.fillMaxWidth()
      )
    }
  }
}