package com.jwd.lunchvote.presentation.widget

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.R

@Composable
fun VoteExitDialog(
  isOwner: Boolean,
  modifier: Modifier = Modifier,
  onDismissRequest: () -> Unit = {},
  onConfirmation: () -> Unit = {}
) {
  LunchVoteDialog(
    title = stringResource(R.string.lounge_exit_dialog_title),
    dismissText = stringResource(R.string.lounge_exit_dialog_cancel_button),
    onDismissRequest = onDismissRequest,
    confirmText = stringResource(R.string.lounge_exit_dialog_confirm_button),
    onConfirmation = onConfirmation,
    modifier = modifier,
    icon = {
      Icon(
        imageVector = Icons.Rounded.Warning,
        contentDescription = null,
        modifier = Modifier.size(28.dp)
      )
    }
  ) {
    Text(
      text = if (isOwner) stringResource(R.string.lounge_exit_dialog_owner_content)
      else stringResource(R.string.lounge_exit_dialog_member_content),
      modifier = Modifier.fillMaxWidth()
    )
  }
}

@Preview
@Composable
private fun Preview1() {
  LunchVoteTheme {
    VoteExitDialog(isOwner = true)
  }
}

@Preview
@Composable
private fun Preview2() {
  LunchVoteTheme {
    VoteExitDialog(isOwner = false)
  }
}