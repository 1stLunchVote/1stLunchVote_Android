package com.jwd.lunchvote.presentation.screen.friends.request

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.jwd.lunchvote.presentation.model.FriendUIModel
import com.jwd.lunchvote.presentation.model.MemberUIModel
import com.jwd.lunchvote.presentation.model.UserUIModel
import com.jwd.lunchvote.presentation.screen.friends.request.FriendRequestContract.FriendRequestEvent
import com.jwd.lunchvote.presentation.screen.friends.request.FriendRequestContract.FriendRequestSideEffect
import com.jwd.lunchvote.presentation.screen.friends.request.FriendRequestContract.FriendRequestState
import com.jwd.lunchvote.presentation.util.LocalSnackbarChannel
import com.jwd.lunchvote.presentation.widget.LazyColumn
import com.jwd.lunchvote.presentation.widget.MemberProfile
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import com.jwd.lunchvote.presentation.widget.TopBar
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
  val loading by viewModel.isLoading.collectAsStateWithLifecycle()

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
    loading = loading,
    onEvent = viewModel::sendEvent
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FriendRequestScreen(
  state: FriendRequestState,
  modifier: Modifier = Modifier,
  loading: Boolean = false,
  onEvent: (FriendRequestEvent) -> Unit = {}
){
  Screen(
    modifier = modifier,
    topAppBar = {
      TopBar(
        title = stringResource(R.string.friend_request_title),
        popBackStack = { onEvent(FriendRequestEvent.OnClickBackButton) }
      )
    },
    scrollable = false
  ) {
    LazyColumn(
      onRefresh = { onEvent(FriendRequestEvent.ScreenInitialize) },
      modifier = Modifier.fillMaxSize(),
      isRefreshing = loading,
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      val requestList = state.requestSenderMap.entries.toList().sortedByDescending { it.key.createdAt }

      if (requestList.isEmpty()) {
        item {
          Text(
            text = stringResource(R.string.friend_request_no_request),
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp),
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelMedium
          )
        }
      } else {
        items(requestList) { (request, friend) ->
          FriendRequestItem(
            request = request,
            friend = friend,
            onClickAcceptButton = { onEvent(FriendRequestEvent.OnClickAcceptRequestButton(request)) },
            onClickRejectButton = { onEvent(FriendRequestEvent.OnClickRejectRequestButton(request)) }
          )
        }
      }
    }
  }
}

@Composable
private fun FriendRequestItem(
  request: FriendUIModel,
  friend: UserUIModel,
  onClickAcceptButton: () -> Unit,
  onClickRejectButton: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 24.dp, vertical = 8.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd a hh:mm", Locale.KOREA)

    Text(
      text = request.createdAt.format(dateTimeFormatter),
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
      OutlinedButton(onClickRejectButton) {
        Text(text = stringResource(R.string.friend_request_reject_button))
      }
      Button(onClickAcceptButton) {
        Text(text = stringResource(R.string.friend_request_accept_button))
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
        requestSenderMap = mapOf(
          FriendUIModel(
            id = "1",
            userId = "1"
          ) to UserUIModel(
            id = "1",
            name = "김철수",
            profileImage = ""
          ),
          FriendUIModel(
            id = "2",
            userId = "2"
          ) to UserUIModel(
            id = "2",
            name = "이영희",
            profileImage = ""
          )
        )
      )
    )
  }
}