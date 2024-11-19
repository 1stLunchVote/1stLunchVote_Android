package com.jwd.lunchvote.presentation.screen.setting.contact

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.model.ContactReplyUIModel
import com.jwd.lunchvote.presentation.model.ContactUIModel
import com.jwd.lunchvote.presentation.screen.setting.contact.ContactContract.ContactEvent
import com.jwd.lunchvote.presentation.screen.setting.contact.ContactContract.ContactSideEffect
import com.jwd.lunchvote.presentation.screen.setting.contact.ContactContract.ContactState
import com.jwd.lunchvote.presentation.screen.setting.contact.ContactContract.DeleteDialogEvent
import com.jwd.lunchvote.presentation.util.INITIAL_DATE_TIME
import com.jwd.lunchvote.presentation.util.LocalSnackbarChannel
import com.jwd.lunchvote.presentation.util.clickableWithoutEffect
import com.jwd.lunchvote.presentation.widget.DialogButton
import com.jwd.lunchvote.presentation.widget.Gap
import com.jwd.lunchvote.presentation.widget.LunchVoteModal
import com.jwd.lunchvote.presentation.widget.LunchVoteTopBar
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import java.time.format.DateTimeFormatter
import java.util.Locale

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

  state.deleteDialogState?.let {
    DeleteDialog(onEvent = viewModel::sendEvent)
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
    modifier = modifier.padding(horizontal = 24.dp),
    topAppBar = {
      LunchVoteTopBar(
        title = stringResource(R.string.contact_title),
        navIconVisible = true,
        popBackStack = { onEvent(ContactEvent.OnClickBackButton) }
      )
    }
  ) {
    ContactBox(contact = state.contact)
    Gap(height = 16.dp)
    if (state.reply != null) {
      ReplyBox(reply = state.reply)
    } else {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = stringResource(R.string.contact_notice_1),
          color = MaterialTheme.colorScheme.outline,
          style = MaterialTheme.typography.labelMedium
        )
        Text(
          text = stringResource(R.string.contact_notice_2),
          color = MaterialTheme.colorScheme.error,
          style = MaterialTheme.typography.labelSmall
        )
      }
    }
    Gap(height = 16.dp)
    Text(
      text = stringResource(R.string.contact_delete_button),
      modifier = Modifier
        .clickableWithoutEffect { onEvent(ContactEvent.OnClickDeleteButton) }
        .padding(vertical = 12.dp)
        .align(Alignment.CenterHorizontally),
      style = MaterialTheme.typography.titleSmall,
      color = MaterialTheme.colorScheme.error,
      textDecoration = TextDecoration.Underline
    )
  }
}

@Composable
private fun ContactBox(
  contact: ContactUIModel,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .border(1.dp, MaterialTheme.colorScheme.onBackground, MaterialTheme.shapes.medium)
      .padding(18.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd a hh:mm", Locale.KOREA)

      Text(
        text = contact.title,
        style = MaterialTheme.typography.titleSmall
      )
      Text(
        text = contact.createdAt.format(dateTimeFormatter),
        color = MaterialTheme.colorScheme.outline,
        style = MaterialTheme.typography.labelSmall
      )
    }
    HorizontalDivider()
    Text(
      text = contact.content,
      modifier = Modifier.fillMaxWidth()
    )
  }
}

@Composable
private fun ReplyBox(
  reply: ContactReplyUIModel,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(MaterialTheme.colorScheme.surfaceDim, MaterialTheme.shapes.medium)
      .padding(18.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd a hh:mm", Locale.KOREA)

      Text(
        text = reply.title,
        style = MaterialTheme.typography.titleSmall
      )
      Text(
        text = reply.createdAt.format(dateTimeFormatter),
        color = MaterialTheme.colorScheme.outline,
        style = MaterialTheme.typography.labelSmall
      )
    }
    HorizontalDivider(
      color = MaterialTheme.colorScheme.background,
    )
    Text(
      text = reply.content,
      modifier = Modifier.fillMaxWidth()
    )
  }
}

@Composable
private fun DeleteDialog(
  modifier: Modifier = Modifier,
  onEvent: (DeleteDialogEvent) -> Unit = {}
) {
  LunchVoteModal(
    title = stringResource(R.string.c_delete_dialog_title),
    onDismissRequest = { onEvent(DeleteDialogEvent.OnClickCancelButton) },
    modifier = modifier,
    icon = {
      Icon(
        Icons.Rounded.Delete,
        contentDescription = "Delete"
      )
    },
    iconColor = MaterialTheme.colorScheme.error,
    body = stringResource(R.string.c_delete_dialog_body),
    closable = false,
    buttons = {
      DialogButton(
        text = stringResource(R.string.c_delete_dialog_cancel_button),
        onClick = { onEvent(DeleteDialogEvent.OnClickCancelButton) },
        isDismiss = true,
        color = MaterialTheme.colorScheme.onSurface
      )
      DialogButton(
        text = stringResource(R.string.c_delete_dialog_delete_button),
        onClick = { onEvent(DeleteDialogEvent.OnClickDeleteButton) },
        color = MaterialTheme.colorScheme.error
      )
    }
  )
}

@Preview
@Composable
private fun NoReply() {
  ScreenPreview {
    ContactScreen(
      ContactState(
        contact = ContactUIModel(
          id = "1",
          title = "문의 제목",
          content = "문의 내용",
          createdAt = INITIAL_DATE_TIME
        )
      )
    )
  }
}

@Preview
@Composable
private fun Reply() {
  ScreenPreview {
    ContactScreen(
      ContactState(
        contact = ContactUIModel(
          id = "1",
          title = "문의 제목",
          content = "문의 내용",
          createdAt = INITIAL_DATE_TIME
        ),
        reply = ContactReplyUIModel(
          id = "1",
          title = "답변 제목",
          content = "답변 내용",
          createdAt = INITIAL_DATE_TIME
        )
      )
    )
  }
}

@Preview
@Composable
private fun DeleteDialogPreview() {
  ScreenPreview {
    DeleteDialog()
  }
}