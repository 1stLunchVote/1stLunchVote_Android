package com.jwd.lunchvote.presentation.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jwd.lunchvote.presentation.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.util.outerShadow

@Composable
internal fun LikeDislike(
  like: Int,
  dislike: Int,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    Like(
      amount = like,
      modifier = Modifier.height(20.dp)
    )
    Dislike(
      amount = dislike,
      modifier = Modifier.height(20.dp)
    )
  }
}

@Composable
private fun Like(
  amount: Int,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .background(MaterialTheme.colorScheme.secondary, MaterialTheme.shapes.extraLarge)
      .padding(horizontal = 6.dp),
    horizontalArrangement = Arrangement.spacedBy(4.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(12.dp)
        .outerShadow(
          color = MaterialTheme.colorScheme.onSecondary,
          shape = CircleShape,
          offsetY = 0.dp,
          blur = 8.dp
        )
        .border(2.dp, MaterialTheme.colorScheme.onSecondary, CircleShape)
        .background(MaterialTheme.colorScheme.secondary, CircleShape)
    )
    Text(
      text = "$amount".padStart(2, '0'),
      style = MaterialTheme.typography.labelLarge,
      color = MaterialTheme.colorScheme.onSecondary
    )
  }
}

@Composable
private fun Dislike(
  amount: Int,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .background(MaterialTheme.colorScheme.error, MaterialTheme.shapes.extraLarge)
      .padding(horizontal = 6.dp),
    horizontalArrangement = Arrangement.spacedBy(4.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(12.dp)
        .background(MaterialTheme.colorScheme.errorContainer, CircleShape)
    ) {
      HorizontalDivider(
        color = MaterialTheme.colorScheme.error,
        thickness = 2.dp,
        modifier = Modifier
          .align(Alignment.Center)
          .rotate(45f)
      )
    }
    Text(
      text = "$amount".padStart(2, '0'),
      style = MaterialTheme.typography.labelLarge,
      color = MaterialTheme.colorScheme.onError
    )
  }
}

@Preview
@Composable
private fun Preview() {
  LunchVoteTheme {
    LikeDislike(0, 0)
  }
}