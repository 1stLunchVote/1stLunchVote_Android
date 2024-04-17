package com.jwd.lunchvote.presentation.widget

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ColumnScope.Gap(
  modifier: Modifier = Modifier,
  height: Dp = 0.dp,
  minHeight: Dp = height,
) {
  if (height == 0.dp) Spacer(modifier.weight(1f))
  if (minHeight != 0.dp) Spacer(modifier.height(minHeight))
}

@Composable
fun RowScope.Gap(
  modifier: Modifier = Modifier,
  width: Dp = 0.dp,
  minWidth: Dp = 0.dp,
) {
  if (width == 0.dp) Spacer(modifier.weight(1f))
  if (minWidth != 0.dp) Spacer(modifier.width(minWidth))
}