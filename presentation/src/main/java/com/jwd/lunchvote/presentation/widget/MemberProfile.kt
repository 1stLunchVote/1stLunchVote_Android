package com.jwd.lunchvote.presentation.widget

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.jwd.lunchvote.presentation.model.MemberUIModel
import com.jwd.lunchvote.presentation.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.util.conditional
import com.jwd.lunchvote.presentation.util.outerShadow

@Composable
fun MemberProfile(
  member: MemberUIModel,
  modifier: Modifier = Modifier,
  onClick: (MemberUIModel) -> Unit,
) {
  val borderColor = when (member.type) {
    MemberUIModel.Type.READY -> MaterialTheme.colorScheme.primary
    MemberUIModel.Type.OWNER -> MaterialTheme.colorScheme.primary
    else -> MaterialTheme.colorScheme.outline
  }

  Box(
    modifier = modifier
      .size(48.dp)
      .conditional(member.type == MemberUIModel.Type.READY || member.type == MemberUIModel.Type.OWNER) {
        outerShadow(
          color = MaterialTheme.colorScheme.primary,
          shape = CircleShape,
          offsetY = 0.dp,
          blur = 8.dp
        )
      }
      .clip(CircleShape)
      .border(2.dp, borderColor, CircleShape)
      .clickable { onClick(member) }
  ) {
    ImageFromUri(
      uri = member.userProfile.toUri(),
      modifier = Modifier.fillMaxSize()
    )
  }
}

@Composable
fun EmptyProfile(
  modifier: Modifier = Modifier,
  density: Density = LocalDensity.current
) {
  val color = MaterialTheme.colorScheme.outlineVariant
  val stroke = density.run {
    Stroke(
      width = 2.dp.toPx(),
      pathEffect = PathEffect.dashPathEffect(
        intervals = floatArrayOf(8.dp.toPx(), 8.dp.toPx()),
        phase = 0f
      )
    )
  }

  Box(
    modifier = modifier
      .size(48.dp)
      .drawBehind {
        drawCircle(
          color = color, style = stroke
        )
      }
  )
}

@Composable
fun InviteProfile(
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Box(
    modifier = modifier
      .size(48.dp)
      .clip(CircleShape)
      .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape)
      .clickable { onClick() },
    contentAlignment = Alignment.Center
  ) {
    Icon(
      Icons.Rounded.Add,
      contentDescription = "Invite",
      modifier = Modifier.size(32.dp),
      tint = MaterialTheme.colorScheme.outline
    )
  }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
  LunchVoteTheme {
    Row(
      modifier = Modifier.padding(24.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      MemberProfile(
        member = MemberUIModel()
      ) {}
      MemberProfile(
        member = MemberUIModel(
          type = MemberUIModel.Type.READY
        )
      ) {}
      EmptyProfile()
      InviteProfile { }
    }
  }
}