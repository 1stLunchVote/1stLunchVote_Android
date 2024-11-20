package com.jwd.lunchvote.presentation.screen.friends

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.model.MemberUIModel
import com.jwd.lunchvote.presentation.model.UserUIModel
import com.jwd.lunchvote.presentation.screen.friends.FriendListContract.FriendListEvent
import com.jwd.lunchvote.presentation.screen.friends.FriendListContract.FriendListSideEffect
import com.jwd.lunchvote.presentation.screen.friends.FriendListContract.FriendListState
import com.jwd.lunchvote.presentation.screen.friends.FriendListContract.RequestDialogEvent
import com.jwd.lunchvote.presentation.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.util.LocalSnackbarChannel
import com.jwd.lunchvote.presentation.util.clickableWithoutEffect
import com.jwd.lunchvote.presentation.widget.DialogButton
import com.jwd.lunchvote.presentation.widget.LazyColumn
import com.jwd.lunchvote.presentation.widget.LunchVoteModal
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
  navigateToLounge: (String) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: FriendListViewModel = hiltViewModel(),
  snackbarChannel: Channel<String> = LocalSnackbarChannel.current,
  context: Context = LocalContext.current
){
  val state by viewModel.viewState.collectAsStateWithLifecycle()
  val loading by viewModel.isLoading.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel.sideEffect){
    viewModel.sideEffect.collectLatest {
      when(it){
        is FriendListSideEffect.PopBackStack -> popBackStack()
        is FriendListSideEffect.NavigateToFriendRequest -> navigateToFriendRequest()
        is FriendListSideEffect.NavigateToLounge -> navigateToLounge(it.loungeId)
        is FriendListSideEffect.ShowSnackbar -> snackbarChannel.send(it.message.asString(context))
      }
    }
  }

  LaunchedEffect(Unit) { viewModel.sendEvent(FriendListEvent.ScreenInitialize) }

  state.requestDialogState?.let { dialogState ->
    RequestDialog(
      friendName = dialogState.friendName,
      onEvent = viewModel::sendEvent
    )
  }

  FriendListScreen(
    state = state,
    modifier = modifier,
    loading = loading,
    onEvent = viewModel::sendEvent
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FriendListScreen(
  state: FriendListState,
  modifier: Modifier = Modifier,
  loading: Boolean = false,
  onEvent: (FriendListEvent) -> Unit = {}
){
  Screen(
    modifier = modifier,
    topAppBar = {
      LunchVoteTopBar(
        title = stringResource(R.string.friend_list_title),
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
    actions = {
      FloatingActionButton(
        onClick = { onEvent(FriendListEvent.OnClickRequestButton) },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
      ) {
        Icon(
          imageVector = Icons.Outlined.Add,
          contentDescription = "add friend"
        )
      }
    },
    scrollable = false
  ) {
    LazyColumn(
      onRefresh = { onEvent(FriendListEvent.ScreenInitialize) },
      modifier = Modifier.fillMaxSize(),
      isRefreshing = loading,
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      friendItemGroup(
        titleId = R.string.friend_list_lounge_group_title,
        friendList = state.joinedFriendList
      ) { friend ->
        FriendItem(
          friend = friend,
          modifier = Modifier.fillMaxWidth(),
          online = true,
          onClickDeleteFriendButton = { onEvent(FriendListEvent.OnClickDeleteFriendButton(friend.id)) },
          onClickJoinButton = { onEvent(FriendListEvent.OnClickJoinButton(friend.id)) }
        )
      }
      friendItemGroup(
        titleId = R.string.friend_list_online_group_title,
        friendList = state.onlineFriendList
      ) { friend ->
        FriendItem(
          friend = friend,
          modifier = Modifier.fillMaxWidth(),
          online = true,
          onClickDeleteFriendButton = { onEvent(FriendListEvent.OnClickDeleteFriendButton(friend.id)) }
        )
      }
      friendItemGroup(
        titleId = R.string.friend_list_offline_group_title,
        friendList = state.offlineFriendList
      ) { friend ->
        FriendItem(
          friend = friend,
          modifier = Modifier.fillMaxWidth(),
          online = false,
          onClickDeleteFriendButton = { onEvent(FriendListEvent.OnClickDeleteFriendButton(friend.id)) },
        )
      }
      item {
        Text(
          text = stringResource(R.string.friend_list_tooltip),
          modifier = Modifier.padding(top = 16.dp),
          color = MaterialTheme.colorScheme.outlineVariant,
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.labelMedium
        )
      }
    }
  }
}

private fun LazyListScope.friendItemGroup(
  titleId: Int,
  friendList: List<UserUIModel>,
  content: @Composable LazyItemScope.(UserUIModel) -> Unit
) {
  item {
    HorizontalDivider(thickness = 2.dp)
  }
  item {
    Text(
      text = stringResource(titleId),
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
  if (friendList.isEmpty()) {
    item {
      Text(
        text = stringResource(R.string.friend_list_no_friend),
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp),
        color = MaterialTheme.colorScheme.outline,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.labelMedium
      )
    }
  }
}

@Composable
private fun FriendItem(
  friend: UserUIModel,
  modifier: Modifier,
  online: Boolean = true,
  onClickDeleteFriendButton: () -> Unit = {},
  onClickJoinButton: (() -> Unit)? = null
) {
  var extended by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 24.dp, vertical = 8.dp)
      .clickableWithoutEffect { extended = !extended },
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(24.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      MemberProfile(
        member = MemberUIModel(
          userProfile = friend.profileImage,
          type = if (online) MemberUIModel.Type.READY else MemberUIModel.Type.DEFAULT
        ),
        onClick = { extended = !extended }
      )
      Text(
        text = friend.name,
        modifier = Modifier.weight(1f),
        style = MaterialTheme.typography.titleSmall
      )
    }
    if (extended) {
      Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.End),
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedButton(onClickDeleteFriendButton) {
          Text(text = stringResource(R.string.friend_list_delete_button))
        }
        if (onClickJoinButton != null) {
          Button(onClickJoinButton) {
            Text(text = stringResource(R.string.friend_list_join_button))
          }
        }
      }
    }
  }
}

@Composable
private fun RequestDialog(
  friendName: String,
  modifier: Modifier = Modifier,
  onEvent: (RequestDialogEvent) -> Unit = {}
) {
  LunchVoteModal(
    title = stringResource(R.string.fl_request_dialog_title),
    onDismissRequest = { onEvent(RequestDialogEvent.OnClickCancelButton) },
    modifier = modifier,
    icon = {
      Icon(
        imageVector = Icons.Rounded.Person,
        contentDescription = "friend request"
      )
    },
    body = stringResource(R.string.fl_request_dialog_body),
    content = {
      LunchVoteTextField(
        text = friendName,
        onTextChange = { onEvent(RequestDialogEvent.OnFriendNameChange(it)) },
        hintText = stringResource(R.string.fl_request_dialog_hint_text)
      )
    },
    buttons = {
      DialogButton(
        text = stringResource(R.string.fl_request_dialog_cancel_text),
        onClick = { onEvent(RequestDialogEvent.OnClickCancelButton) },
        modifier = Modifier.weight(1f),
        isDismiss = true
      )
      DialogButton(
        text = stringResource(R.string.fl_request_dialog_request_text),
        onClick = { onEvent(RequestDialogEvent.OnClickRequestButton) },
        modifier = Modifier.weight(1f),
        enabled = friendName.isNotBlank()
      )
    }
  )
}

@Preview
@Composable
private fun Preview() {
  ScreenPreview {
    FriendListScreen(
      FriendListState(
        onlineFriendList = List(5) {
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