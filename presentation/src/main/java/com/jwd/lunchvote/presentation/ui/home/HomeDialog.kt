package com.jwd.lunchvote.presentation.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.widget.LunchVoteDialog
import com.jwd.lunchvote.presentation.widget.LunchVoteTextField

@Composable
fun HomeJoinDialog(
  onClickDismissButton: () -> Unit,
  onClickConfirmButton: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  var code by remember { mutableStateOf("test") }

  LunchVoteDialog(
    title = "투표 방 참여하기",
    dismissText = "취소",
    onDismiss = onClickDismissButton,
    confirmText = "참여",
    onConfirm = { onClickConfirmButton(code) },
    modifier = modifier,
    confirmEnabled = code.isNotBlank()
  ) {
    LunchVoteTextField(
      text = code,
      hintText = "초대 코드",
      onTextChanged = { code = it },
    )
  }
}

@Preview
@Composable
private fun HomeJoinDialogPreview() {
  LunchVoteTheme {
    HomeJoinDialog(
      onClickDismissButton = {},
      onClickConfirmButton = {}
    )
  }
}