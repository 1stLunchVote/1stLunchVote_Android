package com.jwd.lunchvote.presentation.widget

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.dp
import com.jwd.lunchvote.presentation.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.util.conditional
import kotlinx.coroutines.delay

@Composable
fun HorizontalTimer(
  timeLimitSecond: Int,
  modifier: Modifier = Modifier,
  warningRatio: Float = 0.1f,
  onProgressComplete: () -> Unit = {}
) {
  var progress by remember { mutableFloatStateOf(0f) }
  val progressAnimation by animateFloatAsState(
    targetValue = progress,
    animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
    label = "Timer Progress Animation",
  )

  LaunchedEffect(Unit) {
    while (progress < 1f) {
      progress += 1f / timeLimitSecond
      delay(1000)
      if (progress >= 1f) onProgressComplete()
    }
  }

  Row(modifier) {
    Box(
      modifier = Modifier
        .conditional(progressAnimation > 0f, modifierIf = {
          weight(progressAnimation)
        }, modifierElse = {
          width(0.dp)
        })
    )
    Box(
      modifier = Modifier
        .conditional(1f - progressAnimation > warningRatio, modifierIf = {
          background(MaterialTheme.colorScheme.primary)
        }, modifierElse = {
          background(MaterialTheme.colorScheme.error)
        })
        .height(4.dp)
        .conditional(1f - progressAnimation > 0f, modifierIf = {
          weight(1f - progressAnimation)
        }, modifierElse = {
          width(0.dp)
        })
    )
  }
}

@Preview
@Composable
private fun Preview() {
  LunchVoteTheme {
    Surface {
      HorizontalTimer(
        timeLimitSecond = 3,
        modifier = Modifier.fillMaxWidth()
      )
    }
  }
}