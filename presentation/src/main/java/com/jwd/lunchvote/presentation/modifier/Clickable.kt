package com.jwd.lunchvote.presentation.modifier

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

/**
 * Ripple 효과 없는 clicable Modifier
 * @param enabled 클릭 가능 여부
 * @param onClick 클릭 시 실행할 동작
 */
internal fun Modifier.clickableWithoutEffect(
  enabled: Boolean = true,
  onClick: () -> Unit
): Modifier = composed {
  this.clickable(
    interactionSource = remember { MutableInteractionSource() },
    indication = null,
    onClick = onClick,
    enabled = enabled
  )
}

/**
 * Ripple 효과 없는 clicable Modifier
 * @param onClick 클릭 시 실행할 동작
 */
internal fun Modifier.clickableWithoutEffect(
  onClick: () -> Unit
): Modifier = composed {
  this.clickable(
    interactionSource = remember { MutableInteractionSource() },
    indication = null,
    onClick = onClick
  )
}