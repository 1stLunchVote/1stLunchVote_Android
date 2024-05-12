package com.jwd.lunchvote.presentation.ui.lounge

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.model.LoungeChatUIModel
import com.jwd.lunchvote.presentation.model.MemberUIModel
import com.jwd.lunchvote.presentation.model.UserUIModel
import com.jwd.lunchvote.presentation.model.type.MemberStatusUIType
import com.jwd.lunchvote.presentation.model.type.MessageUIType
import com.jwd.lunchvote.presentation.ui.lounge.LoungeContract.LoungeEvent
import com.jwd.lunchvote.presentation.ui.lounge.LoungeContract.LoungeSideEffect
import com.jwd.lunchvote.presentation.ui.lounge.LoungeContract.LoungeState
import com.jwd.lunchvote.presentation.util.UiText
import com.jwd.lunchvote.presentation.widget.ChatBubble
import com.jwd.lunchvote.presentation.widget.EmptyProfile
import com.jwd.lunchvote.presentation.widget.InviteProfile
import com.jwd.lunchvote.presentation.widget.LoadingScreen
import com.jwd.lunchvote.presentation.widget.LunchVoteTopBar
import com.jwd.lunchvote.presentation.widget.MemberProfile
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import com.jwd.lunchvote.presentation.widget.VoteExitDialog
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoungeRoute(
  popBackStack: () -> Unit,
  navigateToMember: (MemberUIModel, String, Boolean) -> Unit,
  navigateToFirstVote: (String) -> Unit,
  showSnackBar: suspend (String) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: LoungeViewModel = hiltViewModel(),
  clipboardManager: ClipboardManager = LocalClipboardManager.current,
  context: Context = LocalContext.current
) {
  val state by viewModel.viewState.collectAsStateWithLifecycle()
  val loading by viewModel.isLoading.collectAsStateWithLifecycle()
  val dialog by viewModel.dialogState.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel.sideEffect) {
    viewModel.sideEffect.collectLatest {
      when (it) {
        is LoungeSideEffect.PopBackStack -> popBackStack()
        is LoungeSideEffect.NavigateToMember -> navigateToMember(it.member, it.loungeId, it.isOwner)
        is LoungeSideEffect.NavigateToVote -> navigateToFirstVote(it.loungeId)
        is LoungeSideEffect.OpenVoteExitDialog -> viewModel.openDialog(LoungeContract.VOTE_EXIT_DIALOG)
        is LoungeSideEffect.CloseDialog -> viewModel.openDialog("")
        is LoungeSideEffect.ShowSnackBar -> showSnackBar(it.message.asString(context))
        is LoungeSideEffect.CopyToClipboard -> {
          clipboardManager.setText(AnnotatedString(it.loungeId))
          showSnackBar(UiText.DynamicString("초대 코드가 복사되었습니다.").asString(context))
        }
      }
    }
  }

  BackHandler { viewModel.sendEvent(LoungeEvent.OnClickBackButton) }

  val isOwner =
    state.user.id == state.memberList.find { it.status == MemberStatusUIType.OWNER }?.userId
  when (dialog) {
    LoungeContract.VOTE_EXIT_DIALOG -> VoteExitDialog(
      isOwner = isOwner,
      onDismissRequest = { viewModel.sendEvent(LoungeEvent.OnClickCancelButtonVoteExitDialog) },
      onConfirmation = { viewModel.sendEvent(LoungeEvent.OnClickConfirmButtonVoteExitDialog) }
    )
  }

  if (loading) LoadingScreen(
    message = if (isOwner) stringResource(R.string.lounge_create_loading)
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
    modifier = modifier,
    topAppBar = {
      LunchVoteTopBar(
        title = stringResource(R.string.lounge_topbar_title),
        popBackStack = { onEvent(LoungeEvent.OnClickBackButton) }
      )
    },
    scrollable = false
  ) {
    MemberRow(
      memberList = state.memberList,
      onClickMember = { onEvent(LoungeEvent.OnClickMember(it)) },
      onClickInviteButton = { onEvent(LoungeEvent.OnClickInviteButton) },
      modifier = Modifier.fillMaxWidth()
    )
    LoungeChatList(
      userId = state.user.id,
      chatList = state.chatList,
      memberList = state.memberList,
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f),
      onClickMember = { onEvent(LoungeEvent.OnClickMember(it)) }
    )
    LoungeBottomBar(
      text = state.text,
      isOwner = state.user.id == state.memberList.find { it.status == MemberStatusUIType.OWNER }?.userId,
      onTextChanged = { onEvent(LoungeEvent.OnTextChanged(it)) },
      onClickSendChatButton = { onEvent(LoungeEvent.OnClickSendChatButton) },
      onClickReadyButton = { onEvent(LoungeEvent.OnClickReadyButton) }
    )
  }
}

@Composable
private fun MemberRow(
  memberList: List<MemberUIModel>,
  onClickMember: (MemberUIModel) -> Unit,
  onClickInviteButton: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier.padding(horizontal = 32.dp, vertical = 16.dp),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    memberList.forEach { member ->
      MemberProfile(
        member = member,
        onClick = onClickMember
      )
    }
    if (memberList.size < 6) {
      InviteProfile(
        onClick = onClickInviteButton
      )
    }
    repeat(6 - memberList.size - 1) {
      EmptyProfile()
    }
  }
}

@Composable
private fun LoungeChatList(
  userId: String,
  chatList: List<LoungeChatUIModel>,
  memberList: List<MemberUIModel>,
  modifier: Modifier = Modifier,
  onClickMember: (MemberUIModel) -> Unit
) {
  val lazyListState = rememberLazyListState()

  LazyColumn(
    modifier = modifier.padding(24.dp),
    state = lazyListState,
    verticalArrangement = Arrangement.spacedBy(16.dp),
    reverseLayout = true
  ) {
    items(chatList) { chat ->
      ChatBubble(
        chat = chat,
        member = memberList.find { it.userId == chat.userId } ?: MemberUIModel(),
        isMine = chat.userId == userId,
        onClickMember = onClickMember
      )
    }
  }
}

@Composable
private fun LoungeBottomBar(
  text: String,
  isOwner: Boolean,
  modifier: Modifier = Modifier,
  onTextChanged: (String) -> Unit,
  onClickSendChatButton: () -> Unit,
  onClickReadyButton: () -> Unit,
) {
  Column {
    HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.onBackground)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      OutlinedButton(
        onClick = onClickReadyButton,
        modifier = Modifier.height(48.dp),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.onBackground),
        contentPadding = PaddingValues(horizontal = 16.dp)
      ) {
        Text(
          text = if (isOwner) stringResource(R.string.lounge_start_button) else stringResource(R.string.lounge_ready_button),
          color = MaterialTheme.colorScheme.onBackground
        )
      }
      ChatTextField(
        text = text,
        onTextChanged = onTextChanged,
        modifier = Modifier
          .height(48.dp)
          .weight(1f)
      )
      OutlinedIconButton(
        onClick = onClickSendChatButton,
        modifier = Modifier.size(48.dp),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.onBackground)
      ) {
        Icon(
          Icons.Rounded.KeyboardArrowUp,
          contentDescription = "Send"
        )
      }
    }
  }
}

@Composable
private fun ChatTextField(
  text: String,
  onTextChanged: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val interactionSource = remember { MutableInteractionSource() }

  BasicTextField(
    value = text,
    onValueChange = onTextChanged,
    modifier = modifier,
    textStyle = MaterialTheme.typography.bodyLarge,
    interactionSource = interactionSource
  ) {
    val isFocus = interactionSource.collectIsFocusedAsState().value

    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(100))
        .border(
          width = 2.dp,
          color = if (isFocus) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
          shape = RoundedCornerShape(100)
        )
        .padding(horizontal = 24.dp),
      contentAlignment = Alignment.CenterStart
    ) {
      it()
    }
  }
}

@Preview
@Composable
private fun Preview() {
  ScreenPreview {
    LoungeScreen(
      LoungeState(
        user = UserUIModel(id = "1"),
        memberList = listOf(
          MemberUIModel(userId = "1", status = MemberStatusUIType.OWNER),
          MemberUIModel(userId = "2", status = MemberStatusUIType.READY),
          MemberUIModel(userId = "3")
        ),
        chatList = listOf(
          LoungeChatUIModel(messageType = MessageUIType.EXIT, userId = "3", userName = "김영희"),
          LoungeChatUIModel(message = "안녕하세요", userId = "3", userName = "김영희"),
          LoungeChatUIModel(messageType = MessageUIType.JOIN, userId = "3", userName = "김영희"),
          LoungeChatUIModel(message = "안녕하세요", userId = "2", userName = "김철수"),
          LoungeChatUIModel(message = "안녕하세요", userId = "1"),
          LoungeChatUIModel(messageType = MessageUIType.JOIN, userId = "2", userName = "김철수"),
          LoungeChatUIModel(messageType = MessageUIType.CREATE)
        )
      )
    )
  }
}