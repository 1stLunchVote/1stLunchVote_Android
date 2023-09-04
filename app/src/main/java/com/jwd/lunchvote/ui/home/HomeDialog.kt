package com.jwd.lunchvote.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.tooling.preview.Preview
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.ui.home.HomeContract.HomeDialogState
import com.jwd.lunchvote.ui.home.HomeContract.HomeState
import com.jwd.lunchvote.widget.LunchVoteDialog
import com.jwd.lunchvote.widget.LunchVoteTextField

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HomeDialog(
  homeDialogState: HomeDialogState?,
  onClickDismissButton: () -> Unit = {}
) {
  when (homeDialogState) {
    is HomeDialogState.JoinDialog -> {
      var code by remember { mutableStateOf("KqND4zmJ59") }

      LunchVoteDialog(
        title = "투표 방 참여하기",
        dismissText = "취소",
        onDismiss = onClickDismissButton,
        confirmText = "참여",
        onConfirm = {
          homeDialogState.confirm(code)
          onClickDismissButton()
        },
        confirmEnabled = code.isNotBlank(),
        content = {
          LunchVoteTextField(
            text = code,
            hintText = "초대 코드",
            onTextChanged = { code = it },
          )
        }
      )
    }
    else -> {}
  }
}

@Preview(showBackground = false)
@Composable
fun JoinDialogPreview() {
  LunchVoteTheme {
    HomeDialog(
      HomeDialogState.JoinDialog {}
    )
  }
}