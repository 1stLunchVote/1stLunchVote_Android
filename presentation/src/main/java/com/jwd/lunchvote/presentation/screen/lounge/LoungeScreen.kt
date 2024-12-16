package com.jwd.lunchvote.presentation.screen.lounge

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.domain.entity.Chat
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.domain.entity.Member.Type
import com.jwd.lunchvote.domain.entity.User
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.mapper.asUI
import com.jwd.lunchvote.presentation.model.ChatUIModel
import com.jwd.lunchvote.presentation.model.ChatUIModel.Type.SYSTEM
import com.jwd.lunchvote.presentation.model.MemberUIModel
import com.jwd.lunchvote.presentation.model.MemberUIModel.Type.DEFAULT
import com.jwd.lunchvote.presentation.model.UserUIModel
import com.jwd.lunchvote.presentation.screen.lounge.LoungeContract.ExitDialogEvent
import com.jwd.lunchvote.presentation.screen.lounge.LoungeContract.LoungeEvent
import com.jwd.lunchvote.presentation.screen.lounge.LoungeContract.LoungeSideEffect
import com.jwd.lunchvote.presentation.screen.lounge.LoungeContract.LoungeState
import com.jwd.lunchvote.presentation.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.util.LocalSnackbarChannel
import com.jwd.lunchvote.presentation.modifier.conditional
import com.jwd.lunchvote.presentation.widget.ChatBubble
import com.jwd.lunchvote.presentation.widget.Dialog
import com.jwd.lunchvote.presentation.widget.DialogButton
import com.jwd.lunchvote.presentation.widget.EmptyProfile
import com.jwd.lunchvote.presentation.widget.InviteProfile
import com.jwd.lunchvote.presentation.widget.LoadingScreen
import com.jwd.lunchvote.presentation.widget.MemberProfile
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import com.jwd.lunchvote.presentation.widget.TopBar
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoungeRoute(
  popBackStack: () -> Unit,
  navigateToLoungeSetting: (String) -> Unit,
  navigateToMember: (String, String) -> Unit,
  navigateToFirstVote: (String) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: LoungeViewModel = hiltViewModel(),
  clipboardManager: ClipboardManager = LocalClipboardManager.current,
  snackbarChannel: Channel<String> = LocalSnackbarChannel.current,
  context: Context = LocalContext.current
) {
  val state by viewModel.viewState.collectAsStateWithLifecycle()
  val loading by viewModel.isLoading.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel.sideEffect) {
    viewModel.sideEffect.collectLatest {
      when (it) {
        is LoungeSideEffect.PopBackStack -> popBackStack()
        is LoungeSideEffect.NavigateToLoungeSetting -> navigateToLoungeSetting(it.loungeId)
        is LoungeSideEffect.NavigateToMember -> navigateToMember(it.userId, it.loungeId)
        is LoungeSideEffect.NavigateToVote -> navigateToFirstVote(it.loungeId)
        is LoungeSideEffect.ShowSnackbar -> snackbarChannel.send(it.message.asString(context))
        is LoungeSideEffect.CopyToClipboard -> clipboardManager.setText(AnnotatedString(it.loungeId))
      }
    }
  }

  BackHandler { viewModel.sendEvent(LoungeEvent.OnClickBackButton) }

  state.exitDialogState?.let {
    ExitDialog(
      isOwner = state.isOwner,
      onEvent = viewModel::sendEvent
    )
  }

  if (loading) LoadingScreen(
    message = if (state.isOwner) stringResource(R.string.lounge_create_loading)
    else stringResource(R.string.lounge_join_loading)
  ) else LoungeScreen(
    state = state,
    modifier = modifier,
    onEvent = viewModel::sendEvent
  )
}

@Composable
private fun LoungeScreen(
  state: LoungeState,
  modifier: Modifier = Modifier,
  onEvent: (LoungeEvent) -> Unit = {}
) {
  Screen(
    modifier = modifier.padding(start = 24.dp, bottom = 24.dp, end = 24.dp),
    topAppBar = {
      TopBar(
        title = stringResource(R.string.lounge_title),
        popBackStack = { onEvent(LoungeEvent.OnClickBackButton) },
        actions = {
          IconButton(
            onClick = { onEvent(LoungeEvent.OnClickSettingButton) }
          ) {
            Icon(
              Icons.Rounded.Settings,
              contentDescription = "Settings"
            )
          }
        }
      )
    },
    scrollable = false
  ) {
    MemberRow(
      maxMembers = state.lounge.maxMembers,
      memberList = state.memberList,
      onClickMember = { onEvent(LoungeEvent.OnClickMember(it)) },
      onClickInviteButton = { onEvent(LoungeEvent.OnClickInviteButton) },
      modifier = Modifier.fillMaxWidth()
    )
    ChatList(
      userId = state.user.id,
      chatList = state.chatList,
      memberArchive = state.memberArchive,
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f),
      onClickMember = { onEvent(LoungeEvent.OnClickMember(it)) }
    )
    LoungeBottomBar(
      text = state.text,
      isOwner = state.isOwner,
      isReady = state.memberList.find { it.userId == state.user.id }?.type == MemberUIModel.Type.READY,
      memberList = state.memberList,
      onTextChange = { onEvent(LoungeEvent.OnTextChange(it)) },
      onClickSendChatButton = { onEvent(LoungeEvent.OnClickSendChatButton) },
      onClickActionButton = { onEvent(LoungeEvent.OnClickActionButton) }
    )
  }
}

@Composable
private fun MemberRow(
  maxMembers: Int,
  memberList: List<MemberUIModel>,
  onClickMember: (MemberUIModel) -> Unit,
  onClickInviteButton: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier.padding(top = 16.dp, bottom = 8.dp),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    memberList.forEach { member ->
      MemberProfile(
        member = member,
        onClick = onClickMember
      )
    }
    if (memberList.size < maxMembers) {
      InviteProfile(
        onClick = onClickInviteButton
      )
    }
    repeat(maxMembers - memberList.size - 1) {
      EmptyProfile()
    }
  }
}

@Composable
private fun ChatList(
  userId: String,
  chatList: List<ChatUIModel>,
  memberArchive: List<MemberUIModel>,
  modifier: Modifier = Modifier,
  onClickMember: (MemberUIModel) -> Unit
) {
  val lazyListState = rememberLazyListState()

  LazyColumn(
    modifier = modifier.padding(vertical = 8.dp),
    state = lazyListState,
    verticalArrangement = Arrangement.Top,
    horizontalAlignment = Alignment.CenterHorizontally,
    reverseLayout = true
  ) {
    itemsIndexed(chatList) { index, chat ->
      val isSameUserWithPrevious = index < chatList.size - 1
        && chatList[index + 1].type != SYSTEM
        && chat.userId == chatList[index + 1].userId

      ChatBubble(
        chat = chat,
        modifier = Modifier.padding(top = if (isSameUserWithPrevious) 4.dp else 16.dp),
        isMine = chat.userId == userId,
        member = memberArchive.find { it.userId == chat.userId },
        previousChat = chatList.getOrNull(index + 1),
        nextChat = chatList.getOrNull(index - 1),
        onClickMember = onClickMember
      )
    }
  }
}

@Composable
private fun LoungeBottomBar(
  text: String,
  isOwner: Boolean,
  isReady: Boolean,
  modifier: Modifier = Modifier,
  memberList: List<MemberUIModel> = emptyList(),
  onTextChange: (String) -> Unit,
  onClickSendChatButton: () -> Unit,
  onClickActionButton: () -> Unit,
) {
  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Button(
      onClick = onClickActionButton,
      modifier = Modifier
        .size(44.dp)
        .conditional(isOwner.not() && isReady.not()) { alpha(0.64f) },
      enabled = isOwner.not() || (memberList.size > 1 && memberList.any { it.type == DEFAULT }.not()),
      shape = MaterialTheme.shapes.small,
      contentPadding = PaddingValues(6.dp)
    ) {
      if (isOwner) {
        Icon(
          imageVector = Icons.Rounded.PlayArrow,
          contentDescription = "Play"
        )
      } else {
        Icon(
          imageVector = Icons.Rounded.Check,
          contentDescription = "Ready"
        )
      }
    }
    LoungeChatField(
      text = text,
      modifier = Modifier.fillMaxWidth(),
      onTextChange = onTextChange,
      onClickSendChatButton = onClickSendChatButton
    )
  }
}

@Composable
private fun LoungeChatField(
  text: String,
  modifier: Modifier = Modifier,
  onTextChange: (String) -> Unit,
  onClickSendChatButton: () -> Unit
) {
  BasicTextField(
    value = text,
    onValueChange = onTextChange,
    modifier = modifier,
    textStyle = MaterialTheme.typography.bodyMedium,
    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
    keyboardActions = KeyboardActions(onSend = { if (text.isNotBlank()) onClickSendChatButton() }),
    singleLine = true
  ) { innerTextField ->
    Row(
      modifier = modifier
        .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f), MaterialTheme.shapes.small)
        .padding(start = 12.dp, top = 6.dp, end = 6.dp, bottom = 6.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .weight(1f)
          .height(20.dp),
        contentAlignment = Alignment.CenterStart
      ) {
        innerTextField()
        if (text.isEmpty()) {
          Text(
            text = stringResource(R.string.lounge_chat_field_text),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            style = MaterialTheme.typography.bodyMedium
          )
        }
      }
      Button(
        onClick = onClickSendChatButton,
        modifier = Modifier.size(32.dp),
        enabled = text.isNotBlank(),
        shape = MaterialTheme.shapes.extraSmall,
        contentPadding = PaddingValues(6.dp),
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Rounded.Send,
          contentDescription = "Send"
        )
      }
    }
  }
}

@Composable
private fun ExitDialog(
  isOwner: Boolean,
  modifier: Modifier = Modifier,
  onEvent: (ExitDialogEvent) -> Unit = {}
) {
  Dialog(
    title = stringResource(R.string.l_exit_dialog_title),
    onDismissRequest = { onEvent(ExitDialogEvent.OnClickCancelButton) },
    modifier = modifier,
    icon = {
      Icon(
        imageVector = Icons.Rounded.Warning,
        contentDescription = "Warning"
      )
    },
    iconColor = MaterialTheme.colorScheme.error,
    body = if (isOwner) stringResource(R.string.l_exit_dialog_body_owner)
      else stringResource(R.string.l_exit_dialog_body_not_owner),
    buttons = {
      DialogButton(
        text = stringResource(R.string.l_exit_dialog_cancel_button),
        onClick = { onEvent(ExitDialogEvent.OnClickCancelButton) },
        color = MaterialTheme.colorScheme.onSurface,
        isDismiss = true
      )
      DialogButton(
        text = stringResource(R.string.l_exit_dialog_exit_button),
        onClick = { onEvent(ExitDialogEvent.OnClickExitButton) },
        color = MaterialTheme.colorScheme.error
      )
    }
  )
}

@Preview
@Composable
private fun Preview() {
  val user1 = User("1", "", "김철수", "", 0L, 0L)
  val user2 = User("2", "", "김영희", "", 0L, 0L)
  val user3 = User("3", "", "김영수", "", 0L, 0L)
  val member1 = Member.Builder("", user1).owner().build()
  val member2 = Member.Builder("", user2).build().copy(type = Type.READY)
  val member3 = Member.Builder("", user3).build()

  val chatBuilder = Chat.Builder("")

  ScreenPreview {
    LoungeScreen(
      LoungeState(
        user = UserUIModel(id = "1"),
        memberList = listOf(member1.asUI(), member2.asUI(), member3.asUI()),
        memberArchive = listOf(member1.asUI(), member2.asUI(), member3.asUI()),
        chatList = listOf(
          chatBuilder
            .setUserId(user3.id)
            .exile()
            .build()
            .asUI(),
          chatBuilder
            .setUserId(user3.id)
            .setMessage("안녕하세요")
            .build()
            .asUI(),
          chatBuilder
            .setUserId(user3.id)
            .join()
            .build()
            .asUI(),
          chatBuilder
            .setUserId(user1.id)
            .setMessage("안녕하세요")
            .build()
            .asUI(),
          chatBuilder
            .setUserId(user2.id)
            .setMessage("반갑습니다")
            .build()
            .asUI(),
          chatBuilder
            .setUserId(user2.id)
            .setMessage("안녕하세요")
            .build()
            .asUI(),
          chatBuilder
            .setUserId(user2.id)
            .join()
            .build()
            .asUI(),
          chatBuilder
            .create()
            .build()
            .asUI()
        )
      )
    )
  }
}

@Preview
@Composable
private fun ExitDialogOwnerPreview() {
  LunchVoteTheme {
    ExitDialog(
      isOwner = true
    )
  }
}

@Preview
@Composable
private fun ExitDialogMemberPreview() {
  LunchVoteTheme {
    ExitDialog(
      isOwner = false
    )
  }
}