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
import com.jwd.lunchvote.presentation.screen.setting.contact.ContactListContract.ContactListEvent
import com.jwd.lunchvote.presentation.screen.setting.contact.ContactListContract.ContactListSideEffect
import com.jwd.lunchvote.presentation.screen.setting.contact.ContactListContract.ContactListState
import com.jwd.lunchvote.presentation.util.LocalSnackbarChannel
import com.jwd.lunchvote.presentation.widget.LunchVoteTopBar
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ContactListRoute(
  popBackStack: () -> Unit,
  navigateToContact: (String) -> Unit,
  viewModel: ContactListViewModel = hiltViewModel(),
  snackbarChannel: Channel<String> = LocalSnackbarChannel.current,
  context: Context = LocalContext.current
) {
  val state by viewModel.viewState.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel.sideEffect) {
    viewModel.sideEffect.collectLatest {
      when(it) {
        is ContactListSideEffect.PopBackStack -> popBackStack()
        is ContactListSideEffect.NavigateToContact -> navigateToContact(it.contact.id)
        is ContactListSideEffect.ShowSnackbar -> snackbarChannel.send(it.message.asString(context))
      }
    }
  }

  LaunchedEffect(Unit) { viewModel.handleEvents(ContactListEvent.ScreenInitialize) }

  ContactListScreen(
    state = state,
    onEvent = viewModel::sendEvent
  )
}

@Composable
private fun ContactListScreen(
  state: ContactListState,
  modifier: Modifier = Modifier,
  onEvent: (ContactListEvent) -> Unit = {}
) {
  Screen(
    modifier = modifier,
    topAppBar = {
      LunchVoteTopBar(
        title = "1:1 문의",
        navIconVisible = true,
        popBackStack = { onEvent(ContactListEvent.OnClickBackButton) }
      )
    }
  ) {
  }
}

@Preview
@Composable
private fun Preview() {
  ScreenPreview {
    ContactListScreen(
      ContactListState()
    )
  }
}