@file:OptIn(ExperimentalMaterial3Api::class)

package com.jwd.lunchvote.presentation.widget

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefreshIndicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jwd.lunchvote.presentation.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.util.FlickerAnimation

/**
 * pull-to-refresh 기능이 추가된 LazyColumn
 *
 * @param onRefresh pull-to-refresh가 실행될 때 호출되는 콜백
 */
@Composable
fun LazyColumn(
  onRefresh: () -> Unit,
  modifier: Modifier = Modifier,
  isRefreshing: Boolean = false,
  state: LazyListState = rememberLazyListState(),
  refreshState: PullToRefreshState = rememberPullToRefreshState(),
  contentPadding: PaddingValues = PaddingValues(0.dp),
  reverseLayout: Boolean = false,
  verticalArrangement: Arrangement.Vertical = if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
  horizontalAlignment: Alignment.Horizontal = Alignment.Start,
  flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
  userScrollEnabled: Boolean = true,
  content: LazyListScope.() -> Unit,
) {
  LaunchedEffect(isRefreshing) {
    if (isRefreshing) {
      refreshState.animateToThreshold()
    } else {
      refreshState.animateToHidden()
    }
  }

  PullToRefreshBox(
    isRefreshing = isRefreshing,
    onRefresh = onRefresh,
    modifier = modifier,
    state = refreshState,
    indicator = {
      LunchVoteRefreshIndicator(
        state = refreshState,
        isRefreshing = isRefreshing,
        modifier = Modifier.align(Alignment.TopCenter)
      )
    }
  ) {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      state = state,
      contentPadding = contentPadding,
      reverseLayout = reverseLayout,
      verticalArrangement = verticalArrangement,
      horizontalAlignment = horizontalAlignment,
      flingBehavior = flingBehavior,
      userScrollEnabled = userScrollEnabled,
      content = content
    )
  }
}

@Composable
private fun LunchVoteRefreshIndicator(
  state: PullToRefreshState,
  isRefreshing: Boolean,
  modifier: Modifier = Modifier,
  threshold: Dp = 80.dp,
) {
  Box(
    modifier = modifier.pullToRefreshIndicator(
      state = state,
      isRefreshing = isRefreshing,
      containerColor = MaterialTheme.colorScheme.primaryContainer,
      threshold = threshold,
    ),
    contentAlignment = Alignment.Center
  ) {
    Crossfade(
      targetState = isRefreshing,
      animationSpec = tween(durationMillis = 100),
      label = "refreshing"
    ) { refreshing ->
      if (refreshing) {
        FlickerAnimation {
          LunchVoteIcon()
        }
      } else {
        LunchVoteIcon()
      }
    }
  }
}

@Preview
@Composable
private fun Preview() {
  LunchVoteTheme {
    Surface {
      LazyColumn(onRefresh = {}) {
        items(10) {
          Text(
            text = "하이!", modifier = Modifier.fillMaxSize()
          )
        }
      }
    }
  }
}