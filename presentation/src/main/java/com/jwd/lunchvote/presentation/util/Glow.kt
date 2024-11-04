package com.jwd.lunchvote.presentation.util

import android.graphics.Paint
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 백드롭 조명을 적용하는 Modifier
 * @param color 색상
 * @param radius 조명 반지름
 * @param cornerRadius 모양의 모서리 반지름
 */
@Composable
internal fun Modifier.glow(
  color: Color,
  radius: Dp,
  cornerRadius: Dp = 1024.dp,
): Modifier = this.drawBehind {
  val paint = Paint().apply {
    isAntiAlias = true
    setShadowLayer((radius / 4).toPx(), 0.dp.toPx(), 0.dp.toPx(), color.toArgb())
  }

  drawContext.canvas.nativeCanvas.apply {
    drawRoundRect(
      /*left*/0f,
      /*top*/0f,
      /*right*/size.width,
      /*bottom*/size.height,
      /*radiusX*/cornerRadius.toPx(),
      /*radiusY*/cornerRadius.toPx(),
      /*paint*/paint
    )
  }
}