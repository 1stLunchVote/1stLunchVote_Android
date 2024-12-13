package com.jwd.lunchvote.presentation.widget

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.jwd.lunchvote.presentation.R

@Composable
internal fun NetworkLostDialog(
  modifier: Modifier = Modifier,
  onClickQuitButton: () -> Unit = {}
) {
  Dialog(
    title = stringResource(R.string.network_lost_dialog_title),
    onDismissRequest = onClickQuitButton,
    modifier = modifier,
    icon = {
      Icon(
        imageVector = Icons.Rounded.Warning,
        contentDescription = "Warning"
      )
    },
    iconColor = MaterialTheme.colorScheme.error,
    body = stringResource(R.string.network_lost_dialog_body),
    canDismiss = false,
    buttons = {
      DialogButton(
        text = stringResource(R.string.network_lost_dialog_quit_button),
        onClick = onClickQuitButton,
        color = MaterialTheme.colorScheme.error
      )
    }
  )
}

@Preview
@Composable
private fun Preview() {
  ScreenPreview {
    NetworkLostDialog()
    Screen {}
  }
}