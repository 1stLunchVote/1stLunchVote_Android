package com.jwd.lunchvote.presentation.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jwd.lunchvote.presentation.R

@Composable
internal fun LunchVoteIcon(
  modifier: Modifier = Modifier,
  size: Dp = 24.dp
) {
  Image(
    painter = painterResource(R.drawable.ic_logo),
    contentDescription = "App Icon",
    modifier = modifier.size(size)
  )
}

@Preview
@Composable
private fun Preview() {
  LunchVoteIcon()
}