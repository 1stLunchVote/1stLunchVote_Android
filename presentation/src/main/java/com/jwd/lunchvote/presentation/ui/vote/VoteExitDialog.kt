package com.jwd.lunchvote.presentation.ui.vote

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jwd.lunchvote.presentation.widget.LunchVoteDialog

@Composable
fun VoteExitDialog(
  modifier: Modifier = Modifier,
  popBackStack: () -> Unit = {},
  navigateToHome: () -> Unit = {},
) {
  LunchVoteDialog(
    title = "투표를 종료하시겠습니까?",
    dismissText = "취소",
    onDismissRequest = popBackStack,
    confirmText = "나가기",
    onConfirmation = navigateToHome,
    modifier = modifier,
    icon = {
      Icon(
        Icons.Rounded.Warning,
        "Warning",
        modifier = Modifier.size(28.dp)
      )
    },
    content = {
      Text("방을 나갈 경우 무효표 처리되며, 다시 참여할 수 없습니다.")
    }
  )
}