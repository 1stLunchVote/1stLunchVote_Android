package com.jwd.lunchvote.presentation.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex

@Composable
fun Screen(
  modifier: Modifier = Modifier,
  topAppBar: @Composable (ColumnScope.() -> Unit)? = null,
  scrollable: Boolean = true,
  content: @Composable ColumnScope.() -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .let {
        if (scrollable) it
          .verticalScroll(rememberScrollState())
          .height(IntrinsicSize.Max)
        else it
      }
      .then(modifier),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    if (topAppBar != null) Column(Modifier.zIndex(1f)) { topAppBar() }
    content()
  }
}