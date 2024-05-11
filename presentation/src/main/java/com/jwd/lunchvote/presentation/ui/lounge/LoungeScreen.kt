package com.jwd.lunchvote.presentation.ui.lounge

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.core.ui.theme.buttonTextStyle
import com.jwd.lunchvote.core.ui.theme.colorNeutral90
import com.jwd.lunchvote.core.ui.theme.colorOutlineVariant
import com.jwd.lunchvote.core.ui.util.circleShadow
import com.jwd.lunchvote.core.ui.util.modifyIf
import com.jwd.lunchvote.domain.entity.type.MessageType
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.model.LoungeChatUIModel
import com.jwd.lunchvote.presentation.model.MemberUIModel
import com.jwd.lunchvote.presentation.model.type.MemberStatusUIType
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
import kotlinx.coroutines.flow.debounce

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

  val isOwner = state.user.id == state.memberList.find { it.status == MemberStatusUIType.OWNER }?.userId
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
      onClickInvite = { onEvent(LoungeEvent.OnClickInviteButton) },
      modifier = Modifier.fillMaxWidth()
    )
    LoungeChatList(
      userId = state.user.id,
      chatList = state.chatList,
      memberList = state.memberList,
      onClickProfile = { onEvent(LoungeEvent.OnClickMember(it)) },
      modifier = Modifier.fillMaxWidth()
    )
//    if (state.memberList.isNotEmpty()) {
//      LoungeBottomBar(
//        state = state,
//        onEditChat = { onEvent(LoungeEvent.OnChatChanged(it)) },
//        onSendChat = { onEvent(LoungeEvent.OnClickSendChatButton) },
//        onClickReadyStart = { onEvent(LoungeEvent.OnClickReadyButton) }
//      )
//    }
  }
}

@Composable
private fun MemberRow(
  memberList: List<MemberUIModel>,
  onClickMember: (MemberUIModel) -> Unit,
  onClickInvite: () -> Unit,
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
        onClick = onClickInvite
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
  onClickProfile: (MemberUIModel) -> Unit,
  modifier: Modifier = Modifier
) {
  val lazyListState = rememberLazyListState()

  LazyColumn(
    modifier = modifier.padding(24.dp),
    state = lazyListState,
    verticalArrangement = Arrangement.spacedBy(16.dp),
    reverseLayout = true
  ) {
    items(chatList) {chat ->
      ChatBubble(
        chat = chat,
        member = memberList.find { it.userId == chat.userId } ?: MemberUIModel(),
        isMine = chat.userId == userId,
        onClickProfile = onClickProfile
      )
    }
  }
}

@Preview
@Composable
private fun Preview() {
  ScreenPreview {
    LoungeScreen(
      LoungeState(
        memberList = List(4) { MemberUIModel() },
        chatList = List(10) { LoungeChatUIModel() }
      )
    )
  }
}
//
//@Composable
//private fun LoungeContent(
//  loungeId: String,
//  isOwner: Boolean,
//  modifier: Modifier = Modifier,
//  navigateToMember: (MemberUIModel, String, Boolean) -> Unit = { _, _, _ -> },
//  onClickInvite: () -> Unit = {},
//  onScrolled: (Int) -> Unit = {}
//) {
//  Column(modifier = modifier) {
//    Spacer(modifier = Modifier.height(16.dp))
//
//    LoungeMemberList(
//      memberList = state.memberList,
//      navigateToMember = {
//        navigateToMember(
//          it,
//          state.loungeId ?: return@LoungeMemberList,
//          isOwner
//        )
//      },
//      onClickInvite = onClickInvite
//    )
//
//    Spacer(modifier = Modifier.height(24.dp))
//
//    LoungeChatList(
//      state = state,
//      navigateToMember = {
//        navigateToMember(it, state.loungeId ?: return@LoungeChatList, isOwner)
//      },
//      onScrolled = onScrolled
//    )
//  }
//}
//
//@Composable
//private fun LoungeChatList(
//  state: LoungeState,
//  listState: LazyListState = rememberLazyListState(),
//  navigateToMember: (MemberUIModel) -> Unit = {},
//  onScrolled: (Int) -> Unit = {}
//) {
//  LaunchedEffect(state.chatList.size) {
//    if (state.chatList.isEmpty()) return@LaunchedEffect
//
//    // 마지막으로 메시지 온 것이 내 것일 때(내가 직전에 보냈을 때 포함) 스크롤
//    if (state.chatList.first().isMine) {
//      listState.scrollToItem(0)
//    }
//  }
//
//  LaunchedEffect(listState) {
//    // 스크롤 포지션 복구
//    if (state.scrollIndex > 0) {
//      listState.scrollToItem(state.scrollIndex)
//    }
//
//    // 현재 스크롤 포지션 저장
//    snapshotFlow { listState.firstVisibleItemIndex }
//      .debounce(500L)
//      .collectLatest(onScrolled)
//  }
//
//  LazyColumn(
//    state = listState,
//    verticalArrangement = Arrangement.spacedBy(16.dp),
//    horizontalAlignment = Alignment.CenterHorizontally,
//    modifier = Modifier
//      .fillMaxWidth()
//      .padding(horizontal = 24.dp),
//    reverseLayout = true
//  ) {
//    item { Spacer(modifier = Modifier.height(0.dp)) }
//
//    items(state.chatList) { chat ->
//      // 채팅방 생성, 참가 메시지
//      if (chat.messageType != MessageType.NORMAL) {
//        Surface(
//          shape = RoundedCornerShape(24.dp),
//          color = colorNeutral90,
//          contentColor = Color.White,
//        ) {
//          Text(
//            text = chat.message,
//            style = MaterialTheme.typography.titleSmall,
//            modifier = Modifier
//              .padding(horizontal = 48.dp)
//              .padding(vertical = 5.dp)
//          )
//        }
//      } else {
//        // 일반 메시지
//        ChatBubble(
//          message = chat.message,
//          profileImage = chat.userProfile,
//          isMine = chat.isMine,
//          isReady = state.memberList.find { it.id == chat.userId }?.status == MemberStatusUIType.READY,
//          sendStatus = chat.sendStatus,
//          navigateToMember = {
//            navigateToMember(
//              state.memberList.find { it.id == chat.userId } ?: return@ChatBubble,
//            )
//          }
//        )
//      }
//    }
//
//  }
//}
//
//
//@Composable
//private fun LoungeMemberList(
//  memberList: List<MemberUIModel> = emptyList(),
//  navigateToMember: (MemberUIModel) -> Unit = {},
//  onClickInvite: () -> Unit = {},
//) {
//  val memberLimit = 6
//  // 멤버 최대 6명
//  // 4명이하 경우 -> memberList 보여주고 초대 서클, 빈 서클 추가
//  // 5명인 경우 -> 초대 서클 추가
//  // 6명인 경우 memberList 만
//  LazyRow(
//    horizontalArrangement = Arrangement.spacedBy(12.dp),
//  ) {
//    item { Spacer(modifier = Modifier.width(20.dp)) }
//
//    items(memberList) { member ->
//      Surface(
//        shape = CircleShape,
//        border = BorderStroke(
//          width = 2.dp,
//          color = if (member.status == MemberStatusUIType.READY) MaterialTheme.colorScheme.primary
//          else MaterialTheme.colorScheme.outline
//        ),
//        modifier = Modifier
//          .size(48.dp)
//          .modifyIf(member.status == MemberStatusUIType.READY) {
//            circleShadow(blurRadius = 16.dp)
//          },
//        color = MaterialTheme.colorScheme.background,
//      ) {
//        // TODO: 나중에 추가햇
////        AsyncImage(
////          model = member.profileImage,
////          contentDescription = null,
////          contentScale = ContentScale.Crop,
////          modifier = Modifier.clickable(enabled = !member.isMine) {
////            navigateToMember(member)
////          },
////        )
//      }
//    }
//
//    if (memberList.size < memberLimit - 1) {
//      item {
//        Surface(
//          shape = CircleShape,
//          modifier = Modifier.size(48.dp),
//          color = MaterialTheme.colorScheme.background,
//          border = BorderStroke(width = 2.dp, color = MaterialTheme.colorScheme.outline)
//        ) {
//          Image(
//            painter = painterResource(id = R.drawable.ic_add),
//            contentDescription = "add_member",
//            modifier = Modifier
//              .clickable(onClick = onClickInvite)
//              .padding(8.dp)
//          )
//        }
//      }
//    }
//
//    if (memberList.size < memberLimit) {
//      items(memberLimit - memberList.size - 1) {
//        Surface(
//          shape = CircleShape,
//          modifier = Modifier
//            .size(48.dp)
//            .drawWithContent {
//              val stroke = Stroke(
//                width = 2f,
//                pathEffect = PathEffect.dashPathEffect(floatArrayOf(30f, 20f), 0f)
//              )
//
//              drawContent()
//              drawCircle(
//                color = colorOutlineVariant,
//                radius = size.minDimension / 2f,
//                style = stroke
//              )
//            },
//          color = MaterialTheme.colorScheme.background
//        ) {}
//      }
//    }
//    item { Spacer(modifier = Modifier.width(20.dp)) }
//  }
//}
//
//
//@Composable
//private fun LoungeLoadingScreen(
//  modifier: Modifier = Modifier,
//  isOwner: Boolean = false
//) {
//  Column(
//    verticalArrangement = Arrangement.Center,
//    horizontalAlignment = Alignment.CenterHorizontally,
//    modifier = modifier,
//  ) {
//    CircularProgressIndicator(
//      modifier = Modifier.size(50.dp)
//    )
//    Spacer(modifier = Modifier.height(16.dp))
//    Text(
//      text = if (isOwner) stringResource(id = R.string.lounge_create_loading)
//      else stringResource(id = R.string.lounge_join_loading),
//      textAlign = TextAlign.Center
//    )
//  }
//}
//
//
//@OptIn(ExperimentalLayoutApi::class)
//@Composable
//private fun LoungeBottomBar(
//  state: LoungeState,
//  onEditChat: (String) -> Unit = {},
//  onSendChat: () -> Unit = {},
//  onClickReadyStart: () -> Unit = {},
//) {
//  val isTyping by rememberUpdatedState(newValue = WindowInsets.isImeVisible && state.chat.isNotBlank())
//
//  Column(modifier = Modifier.fillMaxWidth()) {
//    Divider(
//      thickness = 2.dp, color = Color.Black,
//      modifier = Modifier
//        .fillMaxWidth()
//    )
//    Row(
//      modifier = Modifier
//        .fillMaxWidth()
//        .padding(16.dp),
//      verticalAlignment = Alignment.CenterVertically,
//      horizontalArrangement = Arrangement.spacedBy(8.dp)
//    ) {
//      Surface(
//        shape = RoundedCornerShape(100.dp),
//        contentColor = if (state.isReady || state.allReady) MaterialTheme.colorScheme.onPrimaryContainer
//        else MaterialTheme.colorScheme.onBackground,
//        color = if (state.isReady || state.allReady) MaterialTheme.colorScheme.primaryContainer
//        else MaterialTheme.colorScheme.background,
//        border = BorderStroke(width = 2.dp, color = Color.Black),
//      ) {
//        Text(
//          text = stringResource(id = if (isOwner) R.string.lounge_start_button else R.string.lounge_ready_button),
//          style = buttonTextStyle,
//          modifier = Modifier
//            .clickable {
//              onClickReadyStart()
//            }
//            .padding(horizontal = 16.dp, vertical = 12.dp)
//        )
//      }
//
//      // 키보드 포커스드 상태일 때 border 색상 변경
//      Surface(
//        shape = RoundedCornerShape(24.dp),
//        contentColor = MaterialTheme.colorScheme.onBackground,
//        color = MaterialTheme.colorScheme.background,
//        border = BorderStroke(
//          width = 2.dp,
//          color = if (isTyping) MaterialTheme.colorScheme.primary else Color.Black
//        ),
//        modifier = Modifier.weight(1f),
//      ) {
//        BasicTextField(
//          value = state.chat,
//          onValueChange = onEditChat,
//          textStyle = MaterialTheme.typography.bodyLarge,
//          modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
//          maxLines = 2,
//        )
//      }
//
//
//      OutlinedIconButton(
//        onClick = onSendChat,
//        border = BorderStroke(
//          width = 2.dp,
//          color = if (state.chat.isNotBlank()) Color.Black else colorNeutral90
//        ),
//        enabled = state.chat.isNotBlank(),
//        modifier = Modifier
//          .size(40.dp)
//          .padding(5.dp),
//        colors = IconButtonDefaults.outlinedIconButtonColors(
//          disabledContentColor = colorNeutral90,
//        )
//      ) {
//        Icon(
//          painterResource(id = R.drawable.ic_arrow_up),
//          contentDescription = "send"
//        )
//      }
//    }
//  }
//}