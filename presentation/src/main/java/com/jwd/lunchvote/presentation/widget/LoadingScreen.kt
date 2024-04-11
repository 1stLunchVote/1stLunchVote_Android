package com.jwd.lunchvote.presentation.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme

@Composable
fun LoadingScreen(
  modifier: Modifier = Modifier,
  message: String = ""
) {
  Column(
    modifier = modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    CircularProgressIndicator()
    if (message.isNotBlank()) {
      Text(
        message,
        modifier = Modifier.padding(top = 16.dp),
        style = MaterialTheme.typography.titleMedium
      )
    }
  }
}

@Preview
@Composable
fun LoadingScreenPreview() {
  LunchVoteTheme {
    LoadingScreen()
  }
}