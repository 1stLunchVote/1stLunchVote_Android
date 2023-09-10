package com.jwd.lunchvote.ui.vote.first

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jwd.lunchvote.model.TemplateUIModel
import com.jwd.lunchvote.ui.vote.first.FirstVoteContract.FirstVoteDialogState
import com.jwd.lunchvote.widget.LunchVoteDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirstVoteDialog(
  firstVoteDialogState: FirstVoteDialogState?,
  onClickDismissButton: () -> Unit
) {
  when (firstVoteDialogState) {
    is FirstVoteDialogState.SelectTemplateDialog -> {
      var expended by remember { mutableStateOf(false) }
      var chosenTemplate by remember { mutableStateOf<TemplateUIModel?>(null) }

      LunchVoteDialog(
        title = "템플릿을 선택해주세요.",
        dismissText = "건너뛰기",
        onDismiss = onClickDismissButton,
        confirmText = "선택",
        onConfirm = {
          firstVoteDialogState.selectTemplate(chosenTemplate)
          onClickDismissButton()
        },
        confirmEnabled = chosenTemplate != null,
        content = {
          ExposedDropdownMenuBox(
            expanded = expended,
            onExpandedChange = { expended = it }
          ) {
            OutlinedTextField(
              value = chosenTemplate?.name ?: "",
              onValueChange = { },
              modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
              readOnly = true,
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expended) },
              placeholder = { Text("템플릿을 선택해주세요.") }
            )
            ExposedDropdownMenu(
              expanded = expended,
              onDismissRequest = { expended = false }
            ) {
              firstVoteDialogState.templateList.forEach { template ->
                DropdownMenuItem(
                  text = { Text(template.name) },
                  onClick = {
                    chosenTemplate = template
                    expended = false
                  }
                )
              }
            }
          }
        }
      )
    }
    is FirstVoteDialogState.VoteExitDialogState -> {
      LunchVoteDialog(
        title = "투표를 종료하시겠습니까?",
        dismissText = "취소",
        onDismiss = onClickDismissButton,
        confirmText = "나가기",
        onConfirm = firstVoteDialogState.onClickConfirmButton,
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
    else -> {}
  }
}