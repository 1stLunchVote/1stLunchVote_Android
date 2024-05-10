package com.jwd.lunchvote.presentation.widget

import android.graphics.Paint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.BeyondBoundsLayout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.toPath
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.model.MemberUIModel
import com.jwd.lunchvote.presentation.model.type.MemberStatusUIType
import com.skydoves.landscapist.coil.CoilImage
import java.lang.Float.max

@Composable
fun MemberProfile(
  member: MemberUIModel,
  modifier: Modifier = Modifier,
  onClick: (MemberUIModel) -> Unit,
) {
  val borderColor = when (member.status) {
    MemberStatusUIType.READY -> MaterialTheme.colorScheme.primary
    else -> MaterialTheme.colorScheme.outline
  }
  val glowingColor = when (member.status) {
    MemberStatusUIType.READY -> MaterialTheme.colorScheme.primary
    else -> Color.Transparent
  }

  Box(
    modifier = modifier
      .size(48.dp)
      .drawBehind {
        val canvasSize = size
        val paint = Paint().apply {
          color = Color.White.toArgb()
          isAntiAlias = true
          setShadowLayer(
            24.dp.toPx(),
            0.dp.toPx(), 0.dp.toPx(),
            glowingColor
              .copy(alpha = 0.5f)
              .toArgb()
          )
        }
        drawContext.canvas.nativeCanvas.apply {
          drawRoundRect(
            0f,            // Left
            0f,            // Top
            canvasSize.width,   // Right
            canvasSize.height,  // Bottom
            24.dp.toPx(),       // Radius X
            24.dp.toPx(),       // Radius Y
            paint               // Paint
          )
        }
      }
      .clip(CircleShape)
      .border(2.dp, borderColor, CircleShape)
      .clickable { onClick(member) }
  ) {
    CoilImage(
      imageModel = { member.userProfile },
      modifier = Modifier.fillMaxSize(),
      previewPlaceholder = R.drawable.ic_food_image_temp
    )
  }
}

@Preview
@Composable
private fun Preview1() {
  LunchVoteTheme {
    Box(
      modifier = Modifier.size(100.dp),
      contentAlignment = Alignment.Center
    ) {
      MemberProfile(
        member = MemberUIModel(
          userProfile = "",
          status = MemberStatusUIType.JOINED
        )
      ) {}
    }
  }
}

@Preview
@Composable
private fun Preview2() {
  LunchVoteTheme {
    Box(
      modifier = Modifier.size(100.dp),
      contentAlignment = Alignment.Center
    ) {
      MemberProfile(
        member = MemberUIModel(
          userProfile = "",
          status = MemberStatusUIType.READY
        )
      ) {}
    }
  }
}