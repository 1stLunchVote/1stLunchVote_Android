package com.jwd.lunchvote.presentation.modifier

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * 조건부 Modifier
 * @param c 조건
 * @param modifierIf 조건이 참일 때 적용할 Modifier
 * @param modifierElse 조건이 거짓일 때 적용할 Modifier
 */
@Composable
internal fun Modifier.conditional(
  c: Boolean,
  modifierIf: @Composable Modifier.() -> Modifier,
  modifierElse: @Composable Modifier.() -> Modifier
): Modifier = if (c) this.modifierIf() else this.modifierElse()

/**
 * 조건부 Modifier
 * @param c 조건
 * @param modifierIf 조건이 참일 때 적용할 Modifier
 */
@Composable
internal fun Modifier.conditional(
  c: Boolean,
  modifierIf: @Composable Modifier.() -> Modifier
): Modifier = if (c) this.modifierIf() else this