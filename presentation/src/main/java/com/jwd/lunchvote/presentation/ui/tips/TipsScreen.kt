package com.jwd.lunchvote.presentation.ui.tips

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.presentation.ui.tips.TipsContract.TipsEvent
import com.jwd.lunchvote.presentation.ui.tips.TipsContract.TipsSideEffect
import com.jwd.lunchvote.presentation.ui.tips.TipsContract.TipsState
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import kotlinx.coroutines.flow.collectLatest

@Composable
fun TipsRoute(
  popBackStack: () -> Unit,
  showSnackBar: suspend (String) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: TipsViewModel = hiltViewModel(),
  context: Context = LocalContext.current
) {
  val state by viewModel.viewState.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel.sideEffect) {
    viewModel.sideEffect.collectLatest {
      when(it) {
        is TipsSideEffect.PopBackStack -> popBackStack()
        is TipsSideEffect.ShowSnackBar -> showSnackBar(it.message.asString(context))
      }
    }
  }

  TipsScreen(
    state = state,
    modifier = modifier,
    onEvent = viewModel::sendEvent
  )
}

@Composable
private fun TipsScreen(
  state: TipsState,
  modifier: Modifier = Modifier,
  onEvent: (TipsEvent) -> Unit = {}
) {
  Screen(
    modifier = modifier,
    topAppBar = {

    }
  ) {

  }
}

@Preview
@Composable
private fun Preview() {
  ScreenPreview {
    TipsScreen(
      TipsState(
        tabIndex = 0
      )
    )
  }
}