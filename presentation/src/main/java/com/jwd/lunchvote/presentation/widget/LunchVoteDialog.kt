package com.jwd.lunchvote.presentation.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme

@Composable
fun LunchVoteDialog(
  title: String,
  dismissText: String,
  onDismiss: () -> Unit,
  confirmText: String,
  onConfirm: () -> Unit,
  modifier: Modifier = Modifier,
  dismissEnabled: Boolean = true,
  confirmEnabled: Boolean = true,
  icon: @Composable (() -> Unit)? = null,
  content: @Composable ColumnScope.() -> Unit,
) {
  Dialog(
    onDismissRequest = onDismiss
  ) {
    Column(
      modifier = modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.background, RoundedCornerShape(28.dp))
        .border(2.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(28.dp)),
    ){
      Column(
        modifier = Modifier.padding(24.dp)
      ) {
        icon?.let {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(30.dp),
            contentAlignment = Alignment.Center
          ) {
            it.invoke()
          }
        }
        Spacer(Modifier.height(16.dp))
        Text(
          title,
          modifier = Modifier.fillMaxWidth(),
          style = MaterialTheme.typography.titleLarge,
          textAlign = if (icon == null) TextAlign.Start else TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        content()
        Spacer(Modifier.height(24.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          Button(
            onClick = onDismiss,
            enabled = dismissEnabled
          ) {
            Text(dismissText)
          }
          Spacer(Modifier.width(8.dp))
          Button(
            onClick = onConfirm,
            enabled = confirmEnabled
          ) {
            Text(confirmText)
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalComposeUiApi::class)
@Preview
@Composable
fun LunchVoteDialogPreview(){
  LunchVoteTheme {
    LunchVoteDialog(
      title = "투표 방 참여하기",
      dismissText = "취소",
      onDismiss = {},
      confirmText = "참여",
      onConfirm = {},
      confirmEnabled = false,
      content = {
        LunchVoteTextField(
          text = "",
          hintText = "초대 코드",
          onTextChanged = {}
        )
      }
    )
  }
}

@Preview
@Composable
fun LunchVoteIconDialogPreview(){
  LunchVoteTheme {
    LunchVoteDialog(
      title = "정말 나가시겠습니까?",
      dismissText = "취소",
      onDismiss = {},
      confirmText = "나가기",
      onConfirm = {},
      icon = {
        Icon(
          imageVector = Icons.Rounded.Warning,
          contentDescription = null,
          modifier = Modifier.size(28.dp)
        )
      },
      content = {
        Text(
          "방장이 방을 나갈 경우, 투표 대기방이 사라지게 됩니다.",
          modifier = Modifier.fillMaxWidth(),
          style = MaterialTheme.typography.bodyMedium
        )
      }
    )
  }
}