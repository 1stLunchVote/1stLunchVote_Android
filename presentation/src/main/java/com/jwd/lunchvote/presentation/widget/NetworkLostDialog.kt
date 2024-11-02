package com.jwd.lunchvote.presentation.widget

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.jwd.lunchvote.presentation.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.R

@Composable
fun NetworkLostDialog(
  modifier: Modifier = Modifier,
  onDismissRequest: () -> Unit = {}
  ) {
  LunchVoteDialog(
    title = stringResource(R.string.network_lost_dialog_title),
    modifier = modifier,
    dismissText = stringResource(R.string.network_lost_dialog_dismiss),
    onDismissRequest = onDismissRequest,
    dismissOnBackPress = false,
    dismissOnClickOutside = false
  ) {
    Text(
      text = stringResource(R.string.network_lost_dialog_body)
    )
  }
}

@Preview
@Composable
private fun Preview() {
  LunchVoteTheme {
    NetworkLostDialog()
  }
}