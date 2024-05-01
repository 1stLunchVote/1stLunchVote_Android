package com.jwd.lunchvote.presentation.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

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

internal fun Modifier.clickableWithoutEffect(
  onClick: () -> Unit
): Modifier = composed {
  this.clickable(
    interactionSource = remember { MutableInteractionSource() },
    indication = null,
    onClick = onClick
  )
}