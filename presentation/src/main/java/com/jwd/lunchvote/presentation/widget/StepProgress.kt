package com.jwd.lunchvote.presentation.widget

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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jwd.lunchvote.presentation.model.MemberUIModel
import com.jwd.lunchvote.presentation.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.util.outerShadow

@Composable
internal fun MemberProgress(
  memberStatusList: List<MemberUIModel.Status>,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    memberStatusList.forEach { status ->
      StepProgress(status == MemberUIModel.Status.VOTED)
    }
  }
}

@Composable
private fun StepProgress(
  finished: Boolean,
  modifier: Modifier = Modifier
) {
  if (finished) {
    Box(
      modifier = modifier
        .size(16.dp)
        .outerShadow(
          color = MaterialTheme.colorScheme.primary,
          shape = CircleShape,
          offsetY = 0.dp,
          blur = 8.dp,
          spread = 3.dp
        )
        .background(MaterialTheme.colorScheme.primary, CircleShape)
    )
  } else {
    val outerColor = MaterialTheme.colorScheme.outline
    val innerColor = MaterialTheme.colorScheme.outlineVariant.copy(0.32f)

    Box(
      modifier = modifier
        .size(16.dp)
        .drawBehind {
          drawCircle(color = outerColor, radius = size.width / 2)
          drawCircle(color = innerColor, radius = (size.width / 2) - 4)
        }
    )
  }
}

@Preview(showBackground = true)
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