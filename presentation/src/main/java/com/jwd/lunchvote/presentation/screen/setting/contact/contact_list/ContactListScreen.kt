package com.jwd.lunchvote.presentation.screen.setting.contact.contact_list

import android.content.Context
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.presentation.model.ContactUIModel
import com.jwd.lunchvote.presentation.screen.setting.contact.contact_list.ContactListContract.ContactListEvent
import com.jwd.lunchvote.presentation.screen.setting.contact.contact_list.ContactListContract.ContactListSideEffect
import com.jwd.lunchvote.presentation.screen.setting.contact.contact_list.ContactListContract.ContactListState
import com.jwd.lunchvote.presentation.util.LocalSnackbarChannel
import com.jwd.lunchvote.presentation.util.clickableWithoutEffect
import com.jwd.lunchvote.presentation.widget.LunchVoteTopBar
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ContactListRoute(
  popBackStack: () -> Unit,
  navigateToAddContact: () -> Unit,
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
        is ContactListSideEffect.NavigateToAddContact -> navigateToAddContact()
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
    modifier = modifier.padding(horizontal = 24.dp),
    topAppBar = {
      LunchVoteTopBar(
        title = "1:1 문의",
        navIconVisible = true,
        popBackStack = { onEvent(ContactListEvent.OnClickBackButton) }
      )
    },
    actions = {
      FloatingActionButton(
        onClick = { onEvent(ContactListEvent.OnClickAddButton) },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
      ) {
        Icon(
          imageVector = Icons.Outlined.Add,
          contentDescription = "add contact"
        )
      }
    },
    scrollable = false
  ) {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = if (state.contactList.isNotEmpty()) Arrangement.spacedBy(16.dp) else Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      items(state.contactList) { contact ->
        ContactItem(
          contact = contact,
          hasReply = state.hasReplyOf[contact] ?: false,
          modifier = Modifier.fillMaxWidth()
        ) { onEvent(ContactListEvent.OnClickContact(contact)) }
      }
      if (state.contactList.isEmpty()) {
        item {
          Text(
            text = "문의 내역이 없습니다.\n문의가 필요한 경우, + 버튼을 눌러 작성해주세요.",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.outline,
            style = MaterialTheme.typography.labelLarge
          )
        }
      }
    }
  }
}

@Composable
private fun ContactItem(
  contact: ContactUIModel,
  hasReply: Boolean,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd a hh:mm", Locale.KOREA)

  Column(
    modifier = modifier
      .border(1.dp, MaterialTheme.colorScheme.onBackground, MaterialTheme.shapes.medium)
      .clickableWithoutEffect(onClick)
      .padding(start = 18.dp, top = 18.dp, end = 14.dp, bottom = 8.dp),
    verticalArrangement = Arrangement.spacedBy(4.dp),
  ) {
    Text(
      text = contact.title,
      style = MaterialTheme.typography.titleSmall
    )
    Text(
      text = contact.createdAt.format(dateTimeFormatter),
      color = MaterialTheme.colorScheme.outline,
      style = MaterialTheme.typography.labelSmall
    )
    FilterChip(
      selected = hasReply,
      onClick = {},
      label = {
        Text(
          text = if (hasReply) "답변 완료" else "답변 대기중",
        )
      },
      modifier = Modifier.align(Alignment.End),
      enabled = false
    )
  }
}

@Preview
@Composable
private fun Preview() {
  ScreenPreview {
    ContactListScreen(
      ContactListState(
        contactList = listOf(
          ContactUIModel(
            title = "문의 제목1"
          ),
          ContactUIModel(
            title = "문의 제목2"
          )
        ),
        hasReplyOf = mapOf(
          ContactUIModel(
            title = "문의 제목1"
          ) to true,
          ContactUIModel(
            title = "문의 제목2"
          ) to false
        )
      )
    )
  }
}