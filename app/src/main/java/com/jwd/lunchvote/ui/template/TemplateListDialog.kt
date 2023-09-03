package com.jwd.lunchvote.ui.template

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.tooling.preview.Preview
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.ui.template.TemplateListContract.TemplateListDialogState
import com.jwd.lunchvote.widget.LunchVoteDialog
import com.jwd.lunchvote.widget.LunchVoteTextField

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TemplateListDialog(
  templateListDialogState: TemplateListDialogState?,
  onClickDismissButton: () -> Unit
) {
  when (templateListDialogState) {
    is TemplateListDialogState.AddTemplate -> {
      var templateName by remember { mutableStateOf("") }
      LunchVoteDialog(
        title = "템플릿 생성",
        dismissText = "취소",
        onDismiss = onClickDismissButton,
        confirmText = "생성",
        onConfirm = {
          templateListDialogState.onClickConfirm(templateName.trim())
          onClickDismissButton()
        },
        confirmEnabled = templateName.isNotBlank(),
        content = {
          LunchVoteTextField(
            text = templateName,
            hintText = "템플릿 이름",
            onTextChanged = { templateName = it },
          )
        }
      )
    }
    else -> {}
  }
}

@Preview(showBackground = false)
@Composable
fun AddTemplateDialogPreview() {
  LunchVoteTheme {
    TemplateListDialog(
      TemplateListDialogState.AddTemplate {}
    ) {}
  }
}