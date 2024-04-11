package com.jwd.lunchvote.presentation.ui.template

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.widget.LunchVoteDialog
import com.jwd.lunchvote.presentation.widget.LunchVoteTextField

@Composable
fun TemplateAddDialog(
  popBackStack: () -> Unit = {},
  navigateToAddTemplate: (String) -> Unit = {},
) {
  var templateName by remember { mutableStateOf("") }
  LunchVoteDialog(
    title = "템플릿 생성",
    dismissText = "취소",
    onDismiss = popBackStack,
    confirmText = "생성",
    onConfirm = { navigateToAddTemplate(templateName.trim()) },
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

@Preview(showBackground = false)
@Composable
fun TemplateAddDialogPreview() {
  LunchVoteTheme {
    TemplateAddDialog()
  }
}