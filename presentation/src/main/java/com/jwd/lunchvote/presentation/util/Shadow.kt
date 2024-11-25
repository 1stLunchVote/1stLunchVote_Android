package com.jwd.lunchvote.presentation.util

import android.graphics.BlurMaskFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 내부 그림자를 적용하는 Modifier
 * @param color 색상
 * @param shape 모양
 * @param offsetX 그림자 X축 오프셋
 * @param offsetY 그림자 Y축 오프셋
 * @param blur 그림자 블러 정도
 * @param spread 그림자 확산 정도
 */
@Composable
internal fun Modifier.outerShadow(
  color: Color,
  shape: Shape,
  offsetX: Dp = 0.dp,
  offsetY: Dp = 4.dp,
  blur: Dp = 4.dp,
  spread: Dp = 0.dp,
): Modifier = this.drawBehind {
  val shadowSize = Size(size.width + spread.toPx(), size.height + spread.toPx())
  val shadowOutline = shape.createOutline(shadowSize, layoutDirection, this)

  val paint = Paint().apply {
    this.color = color.copy(alpha = 0.64f)
  }

  if (blur.toPx() > 0) {
    paint.asFrameworkPaint().apply {
      maskFilter = BlurMaskFilter(blur.toPx(), BlurMaskFilter.Blur.NORMAL)
    }
  }

  drawIntoCanvas { canvas ->
    canvas.save()
    canvas.translate(offsetX.toPx(), offsetY.toPx())
    canvas.drawOutline(shadowOutline, paint)
    canvas.restore()
  }
}

/**
 * 내부 그림자를 적용하는 Modifier
 * @param color 색상
 * @param shape 모양
 * @param offsetX 그림자 X축 오프셋
 * @param offsetY 그림자 Y축 오프셋
 * @param blur 그림자 블러 정도
 */
@Composable
internal fun Modifier.innerShadow(
  color: Color,
  shape: Shape,
  offsetX: Dp = 0.dp,
  offsetY: Dp = 0.dp,
  blur: Dp = 4.dp,
): Modifier = this.drawWithContent {
  drawContent()

  val rect = Rect(Offset.Zero, size)
  val paint = Paint().apply {
    this.color = color
    this.isAntiAlias = true
  }

  val shadowOutline = shape.createOutline(size, layoutDirection, this)

  drawIntoCanvas { canvas ->
    canvas.saveLayer(rect, paint)
    canvas.drawOutline(shadowOutline, paint)

    val frameworkPaint = paint.asFrameworkPaint()
    frameworkPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
    if (blur.toPx() > 0) {
      frameworkPaint.maskFilter = BlurMaskFilter(blur.toPx(), BlurMaskFilter.Blur.NORMAL)
    }
    paint.color = Color.Black

    canvas.translate(offsetX.toPx(), offsetY.toPx())
    canvas.drawOutline(shadowOutline, paint)
    canvas.restore()
  }
}