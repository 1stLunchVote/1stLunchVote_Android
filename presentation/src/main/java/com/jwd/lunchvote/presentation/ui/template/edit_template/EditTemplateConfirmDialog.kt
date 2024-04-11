package com.jwd.lunchvote.presentation.ui.template.edit_template

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
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
import com.jwd.lunchvote.presentation.widget.LunchVoteDialog

@Composable
fun EditTemplateConfirmDialog(
  modifier: Modifier = Modifier,
  popBackStack: () -> Unit = {}
) {
  LunchVoteDialog(
    title = "템플릿 수정",
    dismissText = "취소",
    onDismiss = popBackStack,
    confirmText = "수정",
    onConfirm = {
      popBackStack()
      // TODO: 수정 완료하기
    },
    modifier = modifier,
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

@Preview
@Composable
private fun EditTemplateConfirmDialogPreview() {
  LunchVoteTheme {
    EditTemplateConfirmDialog()
  }
}