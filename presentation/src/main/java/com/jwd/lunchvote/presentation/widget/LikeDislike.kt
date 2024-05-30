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
import com.jwd.lunchvote.core.ui.theme.colorOnSuccess
import com.jwd.lunchvote.core.ui.theme.colorSuccess
import com.jwd.lunchvote.core.ui.util.circleShadow
import com.jwd.lunchvote.presentation.R

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
fun Like(
  amount: Int,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .padding(bottom = 1.dp)
      .height(21.dp)
      .background(colorSuccess, RoundedCornerShape(10.dp)),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Spacer(Modifier.width(4.dp))
    Image(
      painterResource(R.drawable.ic_like),
      null,
      modifier = Modifier
        .size(11.dp)
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
fun Dislike(
  amount: Int,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .padding(bottom = 1.dp)
      .height(21.dp)
      .background(MaterialTheme.colorScheme.error, RoundedCornerShape(10.dp)),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Spacer(Modifier.width(4.dp))
    Image(
      painterResource(R.drawable.ic_dislike),
      null,
      modifier = Modifier.size(13.dp)
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