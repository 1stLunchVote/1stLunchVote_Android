package com.jwd.lunchvote.presentation.util

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.Dp

/**
 * 플리커 애니메이션을 적용하는 Modifier
 *
 * @param durationMillis 애니메이션 지속 시간
 * @param delayMillis 애니메이션 반복 딜레이
 */
@Composable
internal fun Modifier.animateFlicker(
  durationMillis: Int = 240,
  delayMillis: Int = 120,
): Modifier {
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

  return this.alpha(alpha)
}


/**
 * 팝업 애니메이션을 적용하는 Modifier
 *
 * @param maxSize 최대 크기
 * @param durationMillis 애니메이션 지속 시간
 */
@Composable
internal fun Modifier.animatePopUp(
  maxSize: Dp,
  durationMillis: Int = 240,
): Modifier {
  var targetScale by remember { mutableFloatStateOf(0.32f) }
  val scale by animateFloatAsState(
    targetValue = targetScale,
    animationSpec = tween(
      durationMillis = durationMillis,
      easing = LinearEasing
    ),
    label = "popUp",
    finishedListener = {
      targetScale = 0.96f
    }
  )

  LaunchedEffect(Unit) {
    targetScale = 1f
  }

  return Modifier.size(maxSize * scale).then(this)
}

/**
 * 페이드 인 애니메이션을 적용하는 Modifier
 * @param durationMillis 애니메이션 지속 시간
 */
@Composable
internal fun Modifier.animateFadeIn(
  durationMillis: Int = 240,
): Modifier {
  var targetAlpha by remember { mutableFloatStateOf(0f) }
  val alpha by animateFloatAsState(
    targetValue = targetAlpha,
    animationSpec = tween(
      durationMillis = durationMillis,
      easing = LinearEasing
    ),
    label = "fadeIn"
  )

  LaunchedEffect(Unit) {
    targetAlpha = 1f
  }

  return this.alpha(alpha)
}