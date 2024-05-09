package com.jwd.lunchvote.presentation.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.model.MemberUIModel
import com.jwd.lunchvote.presentation.model.type.MemberStatusUIType

@Composable
fun MemberProfileIcon(
  member: MemberUIModel,
  modifier: Modifier = Modifier,
  onClick: (MemberUIModel) -> Unit,
) {
  Box(
    modifier = modifier
      .clip(shape = CircleShape)
  )
}

@Preview
@Composable
private fun Preview1() {
  LunchVoteTheme {
    MemberProfileIcon(
      member = MemberUIModel(
        userProfile = "",
        status = MemberStatusUIType.JOINED
      )
    ) {}
  }
}

@Preview
@Composable
private fun Preview2() {
  LunchVoteTheme {
    MemberProfileIcon(
      member = MemberUIModel(
        userProfile = "",
        status = MemberStatusUIType.READY
      )
    ) {}
  }
}