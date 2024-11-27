package com.jwd.lunchvote.presentation.widget

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.compose.Balloon
import com.skydoves.balloon.compose.BalloonWindow
import com.skydoves.balloon.compose.rememberBalloonBuilder
import com.skydoves.balloon.compose.setBackgroundColor

@Composable
fun TooltipBox(
  text: String,
  visible: Boolean,
  modifier: Modifier = Modifier,
  color: Color = MaterialTheme.colorScheme.primary,
  lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
  content: @Composable () -> Unit
) {
  var balloonWindow: BalloonWindow? by remember { mutableStateOf(null) }
  val builder = rememberBalloonBuilder {
    setLifecycleOwner(lifecycleOwner)

    setWidth(BalloonSizeSpec.WRAP)
    setHeight(BalloonSizeSpec.WRAP)
    setPaddingHorizontal(6)
    setPaddingVertical(4)
    setMarginHorizontal(4)
    setCornerRadius(4f)
    setBackgroundColor(color)

    setArrowSize(4)
    setArrowPosition(0.5f)
    setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)

    setBalloonAnimation(BalloonAnimation.OVERSHOOT)

    setDismissWhenClicked(false)
    setDismissWhenTouchOutside(false)
    setDismissWhenTouchMargin(false)
  }

  LaunchedEffect(visible) {
    if (visible) {
      balloonWindow?.showAlignTop(yOff = -8)
    } else {
      balloonWindow?.dismiss()
    }
  }

  Balloon(
    modifier = modifier,
    builder = builder,
    onBalloonWindowInitialized = { balloonWindow = it },
    balloonContent = {
      Text(
        text = text,
        color = contentColorFor(color),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.labelSmall
      )
    }
  ) {
    content()
  }
}