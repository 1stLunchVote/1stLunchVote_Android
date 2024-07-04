package com.jwd.lunchvote.presentation.ui.friends.request

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.jwd.lunchvote.presentation.model.FriendUIModel
import com.jwd.lunchvote.presentation.model.MemberUIModel
import com.jwd.lunchvote.presentation.model.UserUIModel
import com.jwd.lunchvote.presentation.ui.friends.request.FriendRequestContract.*
import com.jwd.lunchvote.presentation.util.LocalSnackbarChannel
import com.jwd.lunchvote.presentation.widget.LunchVoteTopBar
import com.jwd.lunchvote.presentation.widget.MemberProfile
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun FriendRequestRoute(
  popBackStack: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: FriendRequestViewModel = hiltViewModel(),
  snackbarChannel: Channel<String> = LocalSnackbarChannel.current,
  context: Context = LocalContext.current
){
  val state by viewModel.viewState.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel.sideEffect){
    viewModel.sideEffect.collectLatest {
      when(it){
        is FriendRequestSideEffect.PopBackStack -> popBackStack()
        is FriendRequestSideEffect.ShowSnackbar -> snackbarChannel.send(it.message.asString(context))
      }
    }
  }

  LaunchedEffect(Unit) { viewModel.sendEvent(FriendRequestEvent.ScreenInitialize) }

  FriendRequestScreen(
    state = state,
    modifier = modifier,
    onEvent = viewModel::sendEvent
  )
}

@Composable
private fun FriendRequestScreen(
  state: FriendRequestState,
  modifier: Modifier = Modifier,
  onEvent: (FriendRequestEvent) -> Unit = {}
){
  Screen(
    modifier = modifier,
    topAppBar = {
      LunchVoteTopBar(
        title = "받은 친구 신청",
        popBackStack = { onEvent(FriendRequestEvent.OnClickBackButton) }
      )
    },
    scrollable = false
  ) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(state.friendRequestList) { friendRequest ->
        FriendRequestItem(
          friendRequest = friendRequest,
          friend = state.userById[friendRequest.userId]!!,
          onClickAcceptButton = { onEvent(FriendRequestEvent.OnClickAcceptRequestButton) },
          onClickRejectButton = { onEvent(FriendRequestEvent.OnClickRejectRequestButton) }
        )
      }
    }
  }
}

@Composable
private fun FriendRequestItem(
  friendRequest: FriendUIModel,
  friend: UserUIModel,
  onClickAcceptButton: () -> Unit,
  onClickRejectButton: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 8.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd a hh:mm", Locale.KOREA)

    Text(
      text = friendRequest.createdAt.format(dateTimeFormatter),
      modifier = Modifier.padding(bottom = 8.dp),
      color = MaterialTheme.colorScheme.outlineVariant,
      style = MaterialTheme.typography.labelLarge
    )
    Row(
      modifier = modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(24.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      MemberProfile(
        member = MemberUIModel(
          userProfile = friend.profileImage,
          type = MemberUIModel.Type.DEFAULT
        ),
        onClick = {}
      )
      Text(
        text = friend.name,
        modifier = Modifier.weight(1f),
        style = MaterialTheme.typography.titleSmall
      )
    }
    Row(
      modifier = modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.End),
      verticalAlignment = Alignment.CenterVertically
    ) {
      OutlinedButton(
        onClick = onClickRejectButton
      ) {
        Text(
          text = "거절"
        )
      }
      Button(
        onClick = onClickAcceptButton
      ) {
        Text(
          text = "수락"
        )
      }
    }
  }
}

@Preview
@Composable
private fun Preview() {
  ScreenPreview {
    FriendRequestScreen(
      FriendRequestState(
        friendRequestList = List(3) {
          FriendUIModel(
            userId = "$it"
          )
        },
        userById = mapOf(
          "0" to UserUIModel(
            name = "친구 1",
            profileImage = ""
          ),
          "1" to UserUIModel(
            name = "친구 2",
            profileImage = ""
          ),
          "2" to UserUIModel(
            name = "친구 3",
            profileImage = ""
          )
        )
      )
    )
  }
}