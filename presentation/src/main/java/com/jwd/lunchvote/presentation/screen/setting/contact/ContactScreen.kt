package com.jwd.lunchvote.presentation.screen.setting.contact

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.presentation.screen.setting.contact.ContactContract.ContactEvent
import com.jwd.lunchvote.presentation.screen.setting.contact.ContactContract.ContactSideEffect
import com.jwd.lunchvote.presentation.screen.setting.contact.ContactContract.ContactState
import com.jwd.lunchvote.presentation.util.LocalSnackbarChannel
import com.jwd.lunchvote.presentation.widget.LunchVoteTopBar
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ContactRoute(
  popBackStack: () -> Unit,
  viewModel: ContactViewModel = hiltViewModel(),
  snackbarChannel: Channel<String> = LocalSnackbarChannel.current,
  context: Context = LocalContext.current
) {
  val state by viewModel.viewState.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel.sideEffect) {
    viewModel.sideEffect.collectLatest {
      when(it) {
        is ContactSideEffect.PopBackStack -> popBackStack()
        is ContactSideEffect.ShowSnackbar -> snackbarChannel.send(it.message.asString(context))
      }
    }
  }

  LaunchedEffect(Unit) { viewModel.handleEvents(ContactEvent.ScreenInitialize) }

  ContactScreen(
    state = state,
    onEvent = viewModel::sendEvent
  )
}

@Composable
private fun ContactScreen(
  state: ContactState,
  modifier: Modifier = Modifier,
  onEvent: (ContactEvent) -> Unit = {}
) {
  Screen(
    modifier = modifier,
    topAppBar = {
      LunchVoteTopBar(
        title = "1:1 문의",
        navIconVisible = true,
        popBackStack = { onEvent(ContactEvent.OnClickBackButton) }
      )
    }
  ) {
  }
}

@Preview
@Composable
private fun Preview() {
  ScreenPreview {
    ContactScreen(
      ContactState(
        text = "Hello world!"
      )
    )
  }
}