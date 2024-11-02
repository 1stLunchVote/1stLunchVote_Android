package com.jwd.lunchvote.presentation.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.jwd.lunchvote.presentation.theme.LunchVoteTheme

@Composable
fun LunchVoteDialog(
  title: String,
  modifier: Modifier = Modifier,
  dismissText: String,
  onDismissRequest: () -> Unit,
  dismissEnabled: Boolean = true,
  confirmText: String? = null,
  onConfirmation: (() -> Unit)? = null,
  confirmEnabled: Boolean = true,
  icon: @Composable (() -> Unit)? = null,
  dismissOnBackPress: Boolean = true,
  dismissOnClickOutside: Boolean = true,
  content: @Composable ColumnScope.() -> Unit,
) {
  ProvideTextStyle(
    value = MaterialTheme.typography.bodyMedium
  ) {
    Dialog(
      onDismissRequest = onDismissRequest,
      properties = DialogProperties(
        dismissOnBackPress = dismissOnBackPress,
        dismissOnClickOutside = dismissOnClickOutside
      )
    ) {
      Column(
        modifier = modifier
          .fillMaxWidth()
          .background(MaterialTheme.colorScheme.background, RoundedCornerShape(28.dp))
          .border(2.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(28.dp))
          .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        icon?.invoke()
        Gap(height = 16.dp)
        Text(
          title,
          modifier = Modifier.fillMaxWidth(),
          style = MaterialTheme.typography.titleLarge,
          textAlign = if (icon == null) TextAlign.Start else TextAlign.Center
        )
        Gap(height = 16.dp)
        content()
        Gap(height = 24.dp)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.End)
        ) {
          Button(
            onClick = onDismissRequest,
            enabled = dismissEnabled
          ) {
            Text(dismissText)
          }
          if (confirmText != null && onConfirmation != null) {
            Button(
              onClick = onConfirmation, enabled = confirmEnabled
            ) {
              Text(confirmText)
            }
          }
        }
      }
    }
  }
}

@Preview
@Composable
private fun LunchVoteDialogPreview() {
  LunchVoteTheme {
    LunchVoteDialog(title = "투표 방 참여하기",
      dismissText = "취소",
      onDismissRequest = {},
      confirmText = "참여",
      onConfirmation = {},
      confirmEnabled = false,
      content = {
        LunchVoteTextField(text = "", hintText = "초대 코드", onTextChange = {})
      })
  }
}

@Preview
@Composable
private fun LunchVoteIconDialogPreview() {
  LunchVoteTheme {
    LunchVoteDialog(title = "정말 나가시겠습니까?",
      dismissText = "취소",
      onDismissRequest = {},
      confirmText = "나가기",
      onConfirmation = {},
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
      })
  }
}