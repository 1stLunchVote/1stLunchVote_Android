package com.jwd.lunchvote.presentation.util

import android.graphics.Paint
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp

internal fun Modifier.glow(
  color: Color
): Modifier = this.drawBehind {
  val glowingColor = color.copy(alpha = 0.5f)
  val paint = Paint().apply {
    isAntiAlias = true
    setShadowLayer(24.dp.toPx(), 0.dp.toPx(), 0.dp.toPx(), glowingColor.toArgb())
  }

  drawContext.canvas.nativeCanvas.apply {
    drawRoundRect(
      /*left*/0f,/*top*/0f,/*right*/size.width,/*bottom*/size.height,
      /*radiusX*/24.dp.toPx(),/*radiusY*/24.dp.toPx(),/*paint*/paint
    )
  }
}