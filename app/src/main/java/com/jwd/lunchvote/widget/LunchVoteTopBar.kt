package com.jwd.lunchvote.widget

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LunchVoteTopBar(
  title: String,
  navIconVisible: Boolean = true,
  popBackStack: () -> Unit = {},
  actions: @Composable RowScope.() -> Unit = { }
){
  CenterAlignedTopAppBar(
    title = { Text(text = title) },
    navigationIcon = {
      if (navIconVisible){
        IconButton(onClick = popBackStack) {
          Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "navigation_back",
          )
        }
      }
    },
    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
      containerColor = MaterialTheme.colorScheme.background
    ),
    actions = actions,
  )
}

@Preview
@Composable
fun LunchVoteTopBarPreview(){
  LunchVoteTheme {
    LunchVoteTopBar(
      title = "투표 대기방",
      popBackStack = {  },
      actions = {
        IconButton(onClick = {  }) {
          Icon(
            imageVector = Icons.Outlined.Delete,
            contentDescription = "delete",
          )
        }
      }
    )
  }
}