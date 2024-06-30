package com.jwd.lunchvote.presentation.ui.friends

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.model.UserUIModel
import com.jwd.lunchvote.presentation.ui.friends.FriendListContract.FriendListEvent
import com.jwd.lunchvote.presentation.ui.friends.FriendListContract.FriendListSideEffect
import com.jwd.lunchvote.presentation.ui.friends.FriendListContract.FriendListState
import com.jwd.lunchvote.presentation.util.LocalSnackbarChannel
import com.jwd.lunchvote.presentation.widget.LunchVoteDialog
import com.jwd.lunchvote.presentation.widget.LunchVoteTextField
import com.jwd.lunchvote.presentation.widget.LunchVoteTopBar
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun FriendListRoute(
  popBackStack: () -> Unit,
  navigateToFriendRequestList: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: FriendListViewModel = hiltViewModel(),
  snackbarChannel: Channel<String> = LocalSnackbarChannel.current,
  context: Context = LocalContext.current
){
  val state by viewModel.viewState.collectAsStateWithLifecycle()
  val dialog by viewModel.dialogState.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel.sideEffect){
    viewModel.sideEffect.collectLatest {
      when(it){
        is FriendListSideEffect.PopBackStack -> popBackStack()
        is FriendListSideEffect.NavigateToFriendRequestList -> navigateToFriendRequestList()
        is FriendListSideEffect.OpenRequestDialog -> viewModel.setDialogState(FriendListContract.REQUEST_DIALOG)
        is FriendListSideEffect.CloseDialog -> viewModel.setDialogState("")
        is FriendListSideEffect.ShowSnackbar -> snackbarChannel.send(it.message.asString(context))
      }
    }
  }

  LaunchedEffect(Unit) { viewModel.sendEvent(FriendListEvent.ScreenInitialize) }

  when (dialog) {
    FriendListContract.REQUEST_DIALOG -> {
      RequestDialog(
        friendName = state.friendName ?: "",
        onDismissRequest = { viewModel.sendEvent(FriendListEvent.OnClickCancelButtonRequestDialog) },
        onFriendNameChange = { viewModel.sendEvent(FriendListEvent.OnFriendNameChange(it)) },
        onConfirmation = { viewModel.sendEvent(FriendListEvent.OnClickConfirmButtonRequestDialog) }
      )
    }
  }

  FriendListScreen(
    state = state,
    modifier = modifier,
    onEvent = viewModel::sendEvent
  )
}

@Composable
private fun FriendListScreen(
  state: FriendListState,
  modifier: Modifier = Modifier,
  onEvent: (FriendListEvent) -> Unit = {}
){
  Screen(
    modifier = modifier,
    topAppBar = {
      LunchVoteTopBar(
        title = "친구 목록",
        popBackStack = { onEvent(FriendListEvent.OnClickBackButton) },
        actions = {

        }
      )
    }
  ) {

  }
}

@Composable
private fun RequestDialog(
  friendName: String,
  modifier: Modifier = Modifier,
  onDismissRequest: () -> Unit = {},
  onFriendNameChange: (String) -> Unit = {},
  onConfirmation: () -> Unit = {},
) {
  LunchVoteDialog(
    title = "친구 초대하기",
    dismissText = "취소",
    onDismissRequest = onDismissRequest,
    confirmText = "친구 초대",
    onConfirmation = onConfirmation,
    modifier = modifier,
    confirmEnabled = friendName.isNotBlank()
  ) {
    LunchVoteTextField(
      text = friendName,
      onTextChange = onFriendNameChange,
      hintText = "친구의 닉네임을 입력해주세요"
    )
  }
}

@Preview
@Composable
private fun Preview() {
  ScreenPreview {
    FriendListScreen(
      FriendListState(
        friendList = List(5) {
          UserUIModel(
            name = "친구 $it"
          )
        }
      )
    )
  }
}

@Preview
@Composable
private fun JoinDialogPreview() {
  LunchVoteTheme {
    RequestDialog("닉네임")
  }
}