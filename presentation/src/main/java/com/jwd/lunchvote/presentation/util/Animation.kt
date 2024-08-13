package com.jwd.lunchvote.presentation.util

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha

/**
 * 플리커 애니메이션을 적용하는 Composable
 *
 * @param modifier Modifier
 * @param durationMillis 애니메이션 지속 시간
 * @param delayMillis 애니메이션 반복 딜레이
 * @param content 애니메이션을 적용할 Composable
 */
@Composable
fun FlickerAnimation(
  modifier: Modifier = Modifier,
  durationMillis: Int = 240,
  delayMillis: Int = 120,
  content: @Composable () -> Unit
) {
  val infiniteTransition = rememberInfiniteTransition(label = "flicking")
  val alpha by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(
        durationMillis = durationMillis,
        delayMillis = delayMillis,
        easing = LinearEasing
      ),
      repeatMode = RepeatMode.Reverse
    ),
    label = "flicker"
  )

  Box(modifier = modifier.alpha(alpha)) { content() }
}