package com.jwd.lunchvote.presentation.widget

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jwd.lunchvote.presentation.theme.LunchVoteTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
  title: String,
  modifier: Modifier = Modifier,
  navIconVisible: Boolean = true,
  popBackStack: () -> Unit = {},
  actions: @Composable RowScope.() -> Unit = {}
) {
  CenterAlignedTopAppBar(
    title = { Text(text = title) },
    modifier = modifier.fillMaxWidth(),
    navigationIcon = {
      if (navIconVisible) {
        IconButton(popBackStack) {
          Icon(
            Icons.AutoMirrored.Rounded.ArrowBack,
            contentDescription = "Navigate Up",
          )
        }
      }
    },
    actions = actions
  )
}

@Preview
@Composable
private fun Preview() {
  LunchVoteTheme {
    TopBar(title = "투표 대기방", actions = {
      IconButton({}) {
        Icon(
          imageVector = Icons.Rounded.Delete,
          contentDescription = "delete",
        )
      }
    })
  }
}