package com.jwd.lunchvote.presentation.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.core.ui.util.circleShadow
import com.jwd.lunchvote.presentation.R

@Composable
fun StepProgress(
  finished: Boolean,
  modifier: Modifier = Modifier
) {
  if (finished) {
    Box(
      modifier = Modifier
        .size(16.dp)
        .background(MaterialTheme.colorScheme.primary, CircleShape)
        .circleShadow(MaterialTheme.colorScheme.primary, blurRadius = 8.dp)
    )
  } else {
    Image(
      painterResource(R.drawable.ic_step_progress),
      contentDescription = null,
      modifier = Modifier.size(16.dp)
    )
  }
}

@Preview
@Composable
private fun StepProgressPreview() {
  LunchVoteTheme {
    Row(
      modifier = Modifier.padding(8.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      StepProgress(true)
      StepProgress(false)
    }
  }
}