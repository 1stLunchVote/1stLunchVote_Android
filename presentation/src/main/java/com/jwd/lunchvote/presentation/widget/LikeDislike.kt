package com.jwd.lunchvote.presentation.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jwd.lunchvote.presentation.util.circleShadow
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.theme.colorOnSuccess
import com.jwd.lunchvote.presentation.theme.colorSuccess

@Composable
fun LikeDislike(
  like: Int,
  dislike: Int,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    Like(like)
    Dislike(dislike)
  }
}

@Composable
private fun Like(
  amount: Int,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .height(20.dp)
      .background(colorSuccess, MaterialTheme.shapes.extraLarge),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Spacer(Modifier.width(4.dp))
    Image(
      painterResource(R.drawable.ic_like),
      null,
      modifier = Modifier
        .size(10.dp)
        .circleShadow(colorOnSuccess, blurRadius = 6.dp)
    )
    Spacer(Modifier.width(4.dp))
    Text(
      "$amount".padStart(2, '0'),
      style = MaterialTheme.typography.labelLarge,
      color = colorOnSuccess
    )
    Spacer(Modifier.width(4.dp))
  }
}

@Composable
private fun Dislike(
  amount: Int,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .height(20.dp)
      .background(MaterialTheme.colorScheme.error, MaterialTheme.shapes.extraLarge),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Spacer(Modifier.width(4.dp))
    Image(
      painterResource(R.drawable.ic_dislike),
      null,
      modifier = Modifier.size(12.dp)
    )
    Spacer(Modifier.width(4.dp))
    Text(
      "$amount".padStart(2, '0'),
      style = MaterialTheme.typography.labelLarge,
      color = colorOnSuccess
    )
    Spacer(Modifier.width(4.dp))
  }
}

@Preview
@Composable
private fun LikeDislikePreview() {
  LikeDislike(0, 0)
}