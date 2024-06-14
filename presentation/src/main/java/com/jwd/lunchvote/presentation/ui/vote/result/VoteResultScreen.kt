package com.jwd.lunchvote.presentation.ui.vote.result

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.presentation.ui.vote.result.VoteResultContract.*
import com.jwd.lunchvote.presentation.widget.LoadingScreen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import kotlinx.coroutines.flow.collectLatest

@Composable
fun VoteResultRoute(
  navigateToHome: () -> Unit,
  showSnackBar: suspend (String) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: VoteResultViewModel = hiltViewModel(),
  context: Context = LocalContext.current
) {
  val state by viewModel.viewState.collectAsStateWithLifecycle()
  val loading by viewModel.isLoading.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.sideEffect.collectLatest {
       when (it) {
        is VoteResultSideEffect.NavigateToHome -> navigateToHome()
        is VoteResultSideEffect.ShowSnackBar -> showSnackBar(it.message.asString(context))
      }
    }
  }

  if (loading) LoadingScreen(
    message = "투표 결과를 집계중입니다..."
  ) else VoteResultScreen(
    state = state,
    modifier = modifier,
    onEvent = viewModel::sendEvent
  )
}

@Composable
private fun VoteResultScreen(
  state: VoteResultState,
  modifier: Modifier = Modifier,
  onEvent: (VoteResultEvent) -> Unit = {}
) {

}

@Preview
@Composable
private fun Preview() {
  ScreenPreview {
    VoteResultScreen(
      VoteResultState()
    )
  }
}
