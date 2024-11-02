package com.jwd.lunchvote.presentation.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.jwd.lunchvote.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.R

@Composable
fun KakaoLoginButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  size: LoginButtonSize = LoginButtonSize.Small,
  density: Density = LocalDensity.current
) {
  val containerHeightDp = density.run { (90 * size.scale).toDp() }
  val containerRadiusDp = density.run { (12 * size.scale).toDp() }
  val paddingDp = density.run { (32 * size.scale).toDp() }
  val symbolHeightDp = density.run { (30 * size.scale).toDp() }
  val labelSizeSp = density.run { (30 * size.scale).toSp() }

  val containerColor = Color(0xFFFEE500)
  val symbolColor = Color(0xFF000000)
  val labelColor = Color(0xFF000000).copy(alpha = 0.8f)

  Row(
    modifier = modifier
      .height(containerHeightDp)
      .clip(RoundedCornerShape(containerRadiusDp))
      .background(containerColor)
      .clickable(enabled = enabled, onClick = onClick)
      .padding(horizontal = paddingDp)
      .semantics { contentDescription = "Kakao Login" },
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      painter = painterResource(R.drawable.ic_kakao_symbol),
      contentDescription = "Kakao Symbol",
      modifier = Modifier.size(symbolHeightDp),
      tint = symbolColor
    )
    Spacer(Modifier.weight(1f))
    Text(
      stringResource(R.string.login_kakao_button),
      color = labelColor,
      fontSize = labelSizeSp
    )
    Spacer(Modifier.weight(1f))
  }
}

@Composable
fun GoogleLoginButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  size: LoginButtonSize = LoginButtonSize.Small,
  density: Density = LocalDensity.current
) {
  val containerHeightDp = density.run { (90 * size.scale).toDp() }
  val containerRadiusDp = density.run { (12 * size.scale).toDp() }
  val paddingDp = density.run { (8 * size.scale).toDp() }
  val symbolHeightDp = density.run { (74 * size.scale).toDp() }
  val symbolRadiusDp = density.run { (8 * size.scale).toDp() }
  val symbolPaddingDp = density.run { (16 * size.scale).toDp() }
  val labelSizeSp = density.run { (30 * size.scale).toSp() }

  val containerColor = Color(0xFF4285F4)
  val labelColor = Color(0xFFFFFFFF)

  Row(
    modifier = modifier
      .height(containerHeightDp)
      .clip(RoundedCornerShape(containerRadiusDp))
      .background(containerColor)
      .clickable(enabled = enabled, onClick = onClick)
      .padding(horizontal = paddingDp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(symbolHeightDp)
        .clip(RoundedCornerShape(symbolRadiusDp))
        .background(Color.White)
        .padding(symbolPaddingDp)
    ) {
      Image(
        painter = painterResource(R.drawable.ic_google_symbol),
        contentDescription = "Kakao Symbol",
        modifier = Modifier.size(symbolHeightDp)
      )
    }
    Spacer(Modifier.weight(1f))
    Text(
      stringResource(R.string.login_google_button),
      color = labelColor,
      fontSize = labelSizeSp
    )
    Spacer(Modifier.weight(1f))
  }
}

enum class LoginButtonSize(val scale: Float) {
  Small(1f),
  Medium(1.5f),
  Big(2f)
}

@Preview(showBackground = true)
@Composable
private fun KakaoLoginButtonPreview() {
  com.jwd.lunchvote.theme.LunchVoteTheme {
    Column(
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      KakaoLoginButton(
        onClick = {}, modifier = Modifier.width(200.dp), size = LoginButtonSize.Small
      )
      KakaoLoginButton(
        onClick = {}, modifier = Modifier.width(300.dp), size = LoginButtonSize.Medium
      )
      KakaoLoginButton(
        onClick = {}, modifier = Modifier.width(400.dp), size = LoginButtonSize.Big
      )
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun GoogleLoginButtonPreview() {
  com.jwd.lunchvote.theme.LunchVoteTheme {
    Column(
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      GoogleLoginButton(
        onClick = {}, modifier = Modifier.width(200.dp), size = LoginButtonSize.Small
      )
      GoogleLoginButton(
        onClick = {}, modifier = Modifier.width(300.dp), size = LoginButtonSize.Medium
      )
      GoogleLoginButton(
        onClick = {}, modifier = Modifier.width(400.dp), size = LoginButtonSize.Big
      )
    }
  }
}