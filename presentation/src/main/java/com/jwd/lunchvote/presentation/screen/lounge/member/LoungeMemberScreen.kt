package com.jwd.lunchvote.presentation.screen.lounge.member

import android.content.Context
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.model.MemberUIModel
import com.jwd.lunchvote.presentation.model.UserUIModel
import com.jwd.lunchvote.presentation.screen.lounge.member.LoungeMemberContract.ExileDialogEvent
import com.jwd.lunchvote.presentation.screen.lounge.member.LoungeMemberContract.LoungeMemberEvent
import com.jwd.lunchvote.presentation.screen.lounge.member.LoungeMemberContract.LoungeMemberSideEffect
import com.jwd.lunchvote.presentation.screen.lounge.member.LoungeMemberContract.LoungeMemberState
import com.jwd.lunchvote.presentation.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.util.LocalSnackbarChannel
import com.jwd.lunchvote.presentation.widget.Dialog
import com.jwd.lunchvote.presentation.widget.DialogButton
import com.jwd.lunchvote.presentation.widget.Gap
import com.jwd.lunchvote.presentation.widget.ImageFromUri
import com.jwd.lunchvote.presentation.widget.LoadingScreen
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import com.jwd.lunchvote.presentation.widget.TopBar
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoungeMemberRoute(
  popBackStack: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: LoungeMemberViewModel = hiltViewModel(),
  snackbarChannel: Channel<String> = LocalSnackbarChannel.current,
  context: Context = LocalContext.current
) {
  val state by viewModel.viewState.collectAsStateWithLifecycle()
  val loading by viewModel.isLoading.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel.sideEffect) {
    viewModel.sideEffect.collectLatest {
      when (it) {
        is LoungeMemberSideEffect.PopBackStack -> popBackStack()
        is LoungeMemberSideEffect.ShowSnackbar -> snackbarChannel.send(it.message.asString(context))
      }
    }
  }

  state.exileDialogState?.let {
    ExileDialog(onEvent = viewModel::sendEvent)
  }

  LaunchedEffect(Unit) { viewModel.sendEvent(LoungeMemberEvent.ScreenInitialize) }

  if (loading) LoadingScreen()
  else LoungeMemberScreen(
    state = state,
    modifier = modifier,
    onEvent = viewModel::sendEvent
  )
}

@Composable
private fun LoungeMemberScreen(
  state: LoungeMemberState,
  modifier: Modifier = Modifier,
  onEvent: (LoungeMemberEvent) -> Unit = {}
) {
  Screen(
    modifier = modifier.padding(32.dp),
    topAppBar = {
      TopBar(
        title = stringResource(R.string.lounge_member_title),
        popBackStack = { onEvent(LoungeMemberEvent.OnClickBackButton) }
      )
    }
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(32.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      ImageFromUri(
        uri = state.user.profileImage.toUri(),
        modifier = Modifier
          .size(96.dp)
          .clip(CircleShape)
          .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape)
      )
      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Text(
          text = state.user.name,
          style = MaterialTheme.typography.titleLarge
        )
        Text(
          text = state.user.email,
          style = MaterialTheme.typography.bodyMedium
        )
      }
    }
    Gap(minHeight = 32.dp)
    if (state.me.type == MemberUIModel.Type.OWNER && state.me.userId != state.member.userId) TextButton(
      onClick = { onEvent(LoungeMemberEvent.OnClickExileButton) },
      modifier = Modifier
        .padding(64.dp)
        .align(Alignment.CenterHorizontally)
    ) {
      Text(
        text = stringResource(R.string.lounge_member_exile),
        color = MaterialTheme.colorScheme.error,
        textDecoration = TextDecoration.Underline
      )
    }
  }
}

@Composable
private fun ExileDialog(
  modifier: Modifier = Modifier,
  onEvent: (ExileDialogEvent) -> Unit = {}
) {
  Dialog(
    title = stringResource(R.string.lm_exile_dialog_title),
    onDismissRequest = { onEvent(ExileDialogEvent.OnClickCancelButton) },
    modifier = modifier,
    icon = {
      Icon(
        imageVector = Icons.Rounded.Warning,
        contentDescription = "Warning"
      )
    },
    iconColor = MaterialTheme.colorScheme.error,
    body = stringResource(R.string.lm_exile_dialog_body),
    closable = false,
    buttons = {
      DialogButton(
        text = stringResource(R.string.lm_exile_dialog_cancel_button),
        onClick = { onEvent(ExileDialogEvent.OnClickCancelButton) },
        isDismiss = true,
        color = MaterialTheme.colorScheme.onSurface
      )
      DialogButton(
        text = stringResource(R.string.lm_exile_dialog_exile_button),
        onClick = { onEvent(ExileDialogEvent.OnClickExileButton) },
        color = MaterialTheme.colorScheme.error
      )
    }
  )
}

@Preview
@Composable
private fun Preview() {
  ScreenPreview {
    LoungeMemberScreen(
      LoungeMemberState(
        me = MemberUIModel(
          userId = "123",
          type = MemberUIModel.Type.OWNER,
        ),
        user = UserUIModel(
          id = "234",
          name = "김철수",
          email = "email@email.com"
        )
      )
    )
  }
}

@Preview
@Composable
private fun ExileDialogPreview() {
  LunchVoteTheme {
    ExileDialog()
  }
}