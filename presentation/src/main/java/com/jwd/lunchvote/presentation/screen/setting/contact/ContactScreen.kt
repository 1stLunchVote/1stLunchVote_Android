package com.jwd.lunchvote.presentation.screen.setting.contact

import android.content.Context
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.presentation.model.ContactReplyUIModel
import com.jwd.lunchvote.presentation.model.ContactUIModel
import com.jwd.lunchvote.presentation.screen.setting.contact.ContactContract.Companion.DELETE_DIALOG
import com.jwd.lunchvote.presentation.screen.setting.contact.ContactContract.ContactEvent
import com.jwd.lunchvote.presentation.screen.setting.contact.ContactContract.ContactSideEffect
import com.jwd.lunchvote.presentation.screen.setting.contact.ContactContract.ContactState
import com.jwd.lunchvote.presentation.util.INITIAL_DATE_TIME
import com.jwd.lunchvote.presentation.util.LocalSnackbarChannel
import com.jwd.lunchvote.presentation.util.clickableWithoutEffect
import com.jwd.lunchvote.presentation.widget.LunchVoteDialog
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
  val dialog by viewModel.dialogState.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel.sideEffect) {
    viewModel.sideEffect.collectLatest {
      when(it) {
        is ContactSideEffect.PopBackStack -> popBackStack()
        is ContactSideEffect.OpenDeleteDialog -> viewModel.setDialogState(DELETE_DIALOG)
        is ContactSideEffect.CloseDialog -> viewModel.setDialogState("")
        is ContactSideEffect.ShowSnackbar -> snackbarChannel.send(it.message.asString(context))
      }
    }
  }

  when(dialog) {
    DELETE_DIALOG -> DeleteDialog(
      onDismissRequest = { viewModel.sendEvent(ContactEvent.OnClickCancelButtonDeleteDialog) },
      onConfirmation = { viewModel.sendEvent(ContactEvent.OnClickDeleteButtonDeleteDialog) }
    )
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
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      ContactBox(contact = state.contact)
      if (state.reply != null) {
        ReplyBox(reply = state.reply)
      } else {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(4.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "담당자가 최대한 빠르게 확인 후 답변드리겠습니다.",
            color = MaterialTheme.colorScheme.outline,
            style = MaterialTheme.typography.labelMedium
          )
          Text(
            text = "* 휴무일에는 답변이 어려운 점 양해 부탁드립니다.",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.labelSmall
          )
        }
      }
      Text(
        text = "문의 삭제",
        modifier = Modifier
          .clickableWithoutEffect { onEvent(ContactEvent.OnClickDeleteButton) }
          .padding(vertical = 12.dp),
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.error,
        textDecoration = TextDecoration.Underline
      )
    }
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
      .border(1.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(16.dp))
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
      .border(1.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(16.dp))
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
    HorizontalDivider()
    Text(
      text = reply.content,
      modifier = Modifier.fillMaxWidth()
    )
  }
}

@Composable
private fun DeleteDialog(
  modifier: Modifier = Modifier,
  onDismissRequest: () -> Unit = {},
  onConfirmation: () -> Unit = {}
) {
  LunchVoteDialog(
    title = "문의 삭제",
    dismissText = "취소",
    onDismissRequest = onDismissRequest,
    confirmText = "삭제",
    onConfirmation = onConfirmation,
    modifier = modifier,
    icon = {
      Icon(
        Icons.Outlined.Delete,
        contentDescription = null,
        modifier = Modifier.size(28.dp)
      )
    },
    content = {
      Text(
        text = "문의를 삭제하시겠습니까?",
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
      )
    }
  )
}

@Preview
@Composable
private fun Preview() {
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