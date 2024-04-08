package com.jwd.lunchvote.presentation.ui.template.edit_template

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.ui.template.edit_template.EditTemplateContract.EditTemplateDialogState
import com.jwd.lunchvote.presentation.widget.LunchVoteDialog

@Composable
fun EditTemplateDialog(
  editTemplateDialogState: EditTemplateDialogState?,
  onClickDismissButton: () -> Unit
) {
  when (editTemplateDialogState) {
    is EditTemplateDialogState.DeleteTemplateConfirm -> {
      LunchVoteDialog(
        title = "템플릿 삭제",
        dismissText = "취소",
        onDismiss = onClickDismissButton,
        confirmText = "삭제",
        onConfirm = {
          onClickDismissButton()
          editTemplateDialogState.onClickConfirm()
        },
        icon = {
          Icon(
            Icons.Outlined.Delete,
            "Delete",
            modifier = Modifier.size(28.dp)
          )
        },
        content = {
          Text(
            "템플릿을 삭제하시겠습니까?",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
          )
        }
      )
    }
    is EditTemplateDialogState.EditTemplateConfirm -> {
      LunchVoteDialog(
        title = "템플릿 수정",
        dismissText = "취소",
        onDismiss = onClickDismissButton,
        confirmText = "수정",
        onConfirm = {
          onClickDismissButton()
          editTemplateDialogState.onClickConfirm()
        },
        icon = {
          Icon(
            Icons.Outlined.Edit,
            "Edit",
            modifier = Modifier.size(28.dp)
          )
        },
        content = {
          Text(
            "템플릿을 수정하시겠습니까?",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
          )
        }
      )
    }
    else -> {}
  }
}

@Preview(showBackground = false)
@Composable
fun DeleteTemplateDialogPreview1() {
  LunchVoteTheme {
    EditTemplateDialog(
      EditTemplateDialogState.DeleteTemplateConfirm {}
    ) {}
  }
}

@Preview(showBackground = false)
@Composable
fun DeleteTemplateDialogPreview2() {
  LunchVoteTheme {
    EditTemplateDialog(
      EditTemplateDialogState.EditTemplateConfirm {}
    ) {}
  }
}