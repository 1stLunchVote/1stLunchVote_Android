package com.jwd.lunchvote.widget

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme

@Composable
fun LoadingDialog() {
  Dialog(onDismissRequest = { }) {
    CircularProgressIndicator()
  }
}

@Preview
@Composable
fun LoadingDialogPreview() {
  LunchVoteTheme {
    Surface {
      LoadingDialog()
    }
  }
}