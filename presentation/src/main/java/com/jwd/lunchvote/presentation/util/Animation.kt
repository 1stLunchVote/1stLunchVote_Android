package com.jwd.lunchvote.presentation.util

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import kotlinx.coroutines.delay

@Composable
fun FlickerAnimation(
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit
) {
  var isVisible by remember { mutableStateOf(true) }
  val infiniteTransition = rememberInfiniteTransition(label = "flicking")
  val alpha by infiniteTransition.animateFloat(
    initialValue = if (isVisible) 0f else 1f,
    targetValue = if (isVisible) 1f else 0f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 240, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "flicker"
  )

  Box(modifier = modifier.alpha(alpha)) { content() }

  LaunchedEffect(Unit) {
    while (true) {
      isVisible = !isVisible
      delay(2000)
    }
  }
}