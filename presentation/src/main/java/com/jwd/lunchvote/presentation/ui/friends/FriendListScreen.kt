package com.jwd.lunchvote.presentation.ui.friends

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.model.MemberUIModel
import com.jwd.lunchvote.presentation.model.UserUIModel
import com.jwd.lunchvote.presentation.ui.friends.FriendListContract.FriendListEvent
import com.jwd.lunchvote.presentation.ui.friends.FriendListContract.FriendListSideEffect
import com.jwd.lunchvote.presentation.ui.friends.FriendListContract.FriendListState
import com.jwd.lunchvote.presentation.ui.template.edit_template.EditTemplateContract.EditTemplateEvent
import com.jwd.lunchvote.presentation.util.LocalSnackbarChannel
import com.jwd.lunchvote.presentation.widget.Gap
import com.jwd.lunchvote.presentation.widget.LunchVoteDialog
import com.jwd.lunchvote.presentation.widget.LunchVoteTextField
import com.jwd.lunchvote.presentation.widget.LunchVoteTopBar
import com.jwd.lunchvote.presentation.widget.MemberProfile
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun FriendListRoute(
  popBackStack: () -> Unit,
  navigateToFriendRequest: () -> Unit,
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
        is FriendListSideEffect.NavigateToFriendRequest -> navigateToFriendRequest()
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
          IconButton(
            onClick = { onEvent(FriendListEvent.OnClickFriendRequestButton) }
          ) {
            Icon(
              Icons.Rounded.Notifications,
              contentDescription = "friend request"
            )
          }
        }
      )
    },
    scrollable = false
  ) {
    Box(
      modifier = Modifier.fillMaxSize()
    ) {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        FriendItemGroup(
          title = "투표 진행 중",
          friendList = state.friendList
        ) { friend ->
          FriendItem(
            friend = friend,
            online = true,
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 24.dp)
          )
        }
        FriendItemGroup(
          title = "미접속",
          friendList = state.friendList
        ) { friend ->
          FriendItem(
            friend = friend,
            online = false,
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 24.dp)
          )
        }
      }
      FloatingActionButton(
        onClick = { onEvent(FriendListEvent.OnClickRequestButton) },
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(end = 32.dp, bottom = 48.dp),
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
      ) {
        Icon(
          imageVector = Icons.Outlined.Add,
          contentDescription = "add friend"
        )
      }
    }
  }
}

private fun LazyListScope.FriendItemGroup(
  title: String,
  friendList: List<UserUIModel>,
  content: @Composable LazyItemScope.(UserUIModel) -> Unit
) {
  item {
    HorizontalDivider(thickness = 2.dp)
  }
  item {
    Text(
      text = title,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp, vertical = 4.dp),
      color = MaterialTheme.colorScheme.outlineVariant,
      style = MaterialTheme.typography.labelMedium
    )
  }
  items(friendList) { friend ->
    content(friend)
  }
}

@Composable
private fun FriendItem(
  friend: UserUIModel,
  modifier: Modifier,
  online: Boolean = true
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.spacedBy(24.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    MemberProfile(
      member = MemberUIModel(
        userProfile = friend.profileImage,
        type = if (online) MemberUIModel.Type.READY else MemberUIModel.Type.DEFAULT
      ),
      onClick = {}
    )
    Text(
      text = friend.name,
      style = MaterialTheme.typography.titleSmall
    )
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