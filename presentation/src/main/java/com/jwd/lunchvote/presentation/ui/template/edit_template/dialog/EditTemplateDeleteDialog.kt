package com.jwd.lunchvote.presentation.ui.template.edit_template.dialog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.widget.LunchVoteDialog

@Composable
fun EditTemplateDeleteDialog(
  modifier: Modifier = Modifier,
  onDismissRequest: () -> Unit = {},
  onConfirmation: () -> Unit = {}
) {
  LunchVoteDialog(
    title = stringResource(R.string.edit_template_delete_title),
    dismissText = stringResource(R.string.edit_template_delete_cancel_button),
    onDismissRequest = onDismissRequest,
    confirmText = stringResource(R.string.edit_template_delete_confirm_button),
    onConfirmation = onConfirmation,
    modifier = modifier,
    icon = {
      Icon(
        Icons.Outlined.Delete,
        null,
        modifier = Modifier.size(28.dp)
      )
    },
    content = {
      Text(
        stringResource(R.string.edit_template_delete_content),
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
      )
    }
  )
}

@Preview
@Composable
private fun EditTemplateDeleteDialogPreview() {
  LunchVoteTheme {
    EditTemplateDeleteDialog()
  }
}