package com.jwd.lunchvote.presentation.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.jwd.lunchvote.presentation.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.util.conditional

@Composable
internal fun Screen(
  modifier: Modifier = Modifier,
  topAppBar: @Composable (ColumnScope.() -> Unit)? = null,
  bottomAppBar: @Composable (ColumnScope.() -> Unit)? = null,
  actions: @Composable (ColumnScope.() -> Unit)? = null,
  scrollable: Boolean = true,
  content: @Composable ColumnScope.() -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(LocalConfiguration.current.screenHeightDp.dp),
    contentAlignment = Alignment.TopCenter
  ) {
    Column(
      modifier = Modifier
        .widthIn(max = 1024.dp)
        .fillMaxHeight(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      if (topAppBar != null) {
        Column(
          modifier = Modifier.zIndex(1f)
        ) { topAppBar() }
      }
      Column(
        modifier = Modifier
          .weight(1f)
          .conditional(scrollable) { verticalScroll(rememberScrollState()) }
          .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
      ) { content() }
      if (bottomAppBar != null) {
        Column(
          modifier = Modifier.zIndex(1f)
        ) { bottomAppBar() }
      }
    }
    if (actions != null) {
      Column(
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(end = 32.dp, bottom = 48.dp)
      ) { actions() }
    }
  }
}

@Composable
internal fun ScreenPreview(
  content: @Composable () -> Unit
) {
  LunchVoteTheme {
    Surface {
      content()
    }
  }
}