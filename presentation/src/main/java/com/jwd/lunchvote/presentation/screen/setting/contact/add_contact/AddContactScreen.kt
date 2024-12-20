package com.jwd.lunchvote.presentation.screen.setting.contact.add_contact

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import com.jwd.lunchvote.presentation.model.ContactUIModel
import com.jwd.lunchvote.presentation.screen.setting.contact.add_contact.AddContactContract.AddContactEvent
import com.jwd.lunchvote.presentation.screen.setting.contact.add_contact.AddContactContract.AddContactSideEffect
import com.jwd.lunchvote.presentation.screen.setting.contact.add_contact.AddContactContract.AddContactState
import com.jwd.lunchvote.presentation.util.LocalSnackbarChannel
import com.jwd.lunchvote.presentation.widget.DropDownMenu
import com.jwd.lunchvote.presentation.widget.Gap
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import com.jwd.lunchvote.presentation.widget.TextField
import com.jwd.lunchvote.presentation.widget.TopBar
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AddContactRoute(
  popBackStack: () -> Unit,
  navigateToContact: (String) -> Unit,
  viewModel: AddContactViewModel = hiltViewModel(),
  snackbarChannel: Channel<String> = LocalSnackbarChannel.current,
  context: Context = LocalContext.current
) {
  val state by viewModel.viewState.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel.sideEffect) {
    viewModel.sideEffect.collectLatest {
      when(it) {
        is AddContactSideEffect.PopBackStack -> popBackStack()
        is AddContactSideEffect.NavigateToContact -> navigateToContact(it.contactId)
        is AddContactSideEffect.ShowSnackbar -> snackbarChannel.send(it.message.asString(context))
      }
    }
  }

  AddContactScreen(
    state = state,
    onEvent = viewModel::sendEvent
  )
}

@Composable
private fun AddContactScreen(
  state: AddContactState,
  modifier: Modifier = Modifier,
  onEvent: (AddContactEvent) -> Unit = {}
) {
  Screen(
    modifier = modifier.padding(horizontal = 24.dp),
    topAppBar = {
      TopBar(
        title = "1:1 문의",
        navIconVisible = true,
        popBackStack = { onEvent(AddContactEvent.OnClickBackButton) }
      )
    }
  ) {
    TextField(
      text = state.title,
      onTextChange = { onEvent(AddContactEvent.OnTitleChange(it)) },
      hintText = "제목",
      modifier = Modifier.fillMaxWidth()
    )
    Gap(height = 16.dp)
    DropDownMenu(
      list = ContactUIModel.Category.entries.toList(),
      selected = state.category,
      onItemSelected = { onEvent(AddContactEvent.OnCategoryChange(it)) },
      getItemName = { it.korean },
      hintText = "카테고리",
      modifier = Modifier.fillMaxWidth(),
      placeholder = "카테고리"
    )
    Gap(height = 16.dp)
    TextField(
      text = state.content,
      onTextChange = { onEvent(AddContactEvent.OnContentChange(it)) },
      hintText = "내용",
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f),
      maxLines = Int.MAX_VALUE
    )
    Gap(height = 16.dp)
    Button(
      onClick = { onEvent(AddContactEvent.OnClickSubmitButton) },
      modifier = Modifier.fillMaxWidth(),
      enabled = state.title.isNotBlank() && state.category != null && state.content.isNotBlank()
    ) {
      Text(
        text = "문의 등록"
      )
    }
    Gap(height = 16.dp)
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
    Gap(height = 24.dp)
  }
}

@Preview
@Composable
private fun Preview() {
  ScreenPreview {
    AddContactScreen(
      AddContactState(
        title = "제목",
        category = ContactUIModel.Category.BUG,
        content = "내용"
      )
    )
  }
}