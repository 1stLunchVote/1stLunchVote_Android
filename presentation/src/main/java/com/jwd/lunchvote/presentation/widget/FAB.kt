package com.jwd.lunchvote.presentation.widget

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun FAB(
  text: String,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  FloatingActionButton(
    onClick = onClick,
    modifier = modifier,
    containerColor = MaterialTheme.colorScheme.primary,
    contentColor = MaterialTheme.colorScheme.onPrimary
  ) {
    Text(
      text = text,
      modifier = Modifier.padding(horizontal = 24.dp)
    )
  }
}