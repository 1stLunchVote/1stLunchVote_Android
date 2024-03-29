package com.jwd.lunchvote.ui.lounge

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
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.jwd.lunchvote.R
import com.jwd.lunchvote.core.ui.theme.buttonTextStyle
import com.jwd.lunchvote.core.ui.theme.colorNeutral90
import com.jwd.lunchvote.core.ui.theme.colorOutlineVariant
import com.jwd.lunchvote.core.ui.util.circleShadow
import com.jwd.lunchvote.core.ui.util.modifyIf
import com.jwd.lunchvote.domain.entity.type.MessageType
import com.jwd.lunchvote.model.MemberUIModel
import com.jwd.lunchvote.ui.lounge.LoungeContract.*
import com.jwd.lunchvote.widget.ChatBubble
import com.jwd.lunchvote.widget.LunchVoteTopBar
import com.jwd.lunchvote.widget.VoteExitDialog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LoungeRoute(
    navigateToMember: (MemberUIModel, String, Boolean) -> Unit,
    navigateToFirstVote: (String) -> Unit,
    viewModel: LoungeViewModel = hiltViewModel(),
    popBackStack: (String) -> Unit,
    clipboardManager: ClipboardManager = LocalClipboardManager.current
){
    val loungeState : LoungeState by viewModel.viewState.collectAsStateWithLifecycle()

    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.sideEffect){
        viewModel.sideEffect.collectLatest {
            when(it){
                is LoungeSideEffect.ShowSnackBar -> {
                    snackBarHostState.showSnackbar(it.message)
                }
                is LoungeSideEffect.PopBackStack -> popBackStack(it.message)
                is LoungeSideEffect.CopyToClipboard -> {
                    clipboardManager.setText(AnnotatedString(it.loungeId))
                    snackBarHostState.showSnackbar("초대 코드가 복사되었습니다.")
                }
                is LoungeSideEffect.NavigateToVote -> {
                    navigateToFirstVote(it.loungeId)
                }
            }
        }
    }

    LaunchedEffect(Unit){
        viewModel.error.collectLatest {
            snackBarHostState.showSnackbar("문제가 발생하였습니다.")
        }
    }

    // 다이얼로그가 보이지 않는 상황 or 키보드 안보일 때 뒤로가기 버튼 누르면 다이얼로그 띄움
    BackHandler(loungeState.exitDialogShown.not() && WindowInsets.isImeVisible.not()) {
        viewModel.sendEvent(LoungeEvent.OnTryExit)
    }

    if (loungeState.exitDialogShown){
        VoteExitDialog(
            onDismiss = { viewModel.sendEvent(LoungeEvent.OnClickExit(false)) },
            onExit = { viewModel.sendEvent(LoungeEvent.OnClickExit(true)) },
            isOwner = loungeState.isOwner
        )
    }

    LoungeScreen(
        loungeState = loungeState,
        snackBarHostState = snackBarHostState,
        navigateToMember = navigateToMember,
        onEventAction = viewModel::sendEvent
    )
}

@Composable
private fun LoungeScreen(
    loungeState: LoungeState,
    snackBarHostState: SnackbarHostState,
    navigateToMember: (MemberUIModel, String, Boolean) -> Unit = { _, _, _ -> },
    onEventAction: (LoungeEvent) -> Unit = {}
){
    Scaffold(
        topBar = {
            LunchVoteTopBar(
                title = stringResource(id = R.string.lounge_topbar_title),
                popBackStack = { onEventAction(LoungeEvent.OnTryExit) }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        bottomBar = {
            if (loungeState.memberList.isNotEmpty()){
                LoungeBottomBar(
                    loungeState = loungeState,
                    onEditChat = { onEventAction(LoungeEvent.OnEditChat(it)) },
                    onSendChat = { onEventAction(LoungeEvent.OnSendChat)},
                    onClickReadyStart = { onEventAction(LoungeEvent.OnReady)}
                )
            }
        }
    ) { padding ->
        if (loungeState.loungeId == null){
            LoungeLoadingScreen(
                isOwner = loungeState.isOwner,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        }
        else {
            LoungeContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                loungeState = loungeState,
                navigateToMember = navigateToMember,
                onClickInvite = { onEventAction(LoungeEvent.OnClickInvite)},
                onScrolled = { onEventAction(LoungeEvent.OnScrolled(it))}
            )
        }
    }
}

@Composable
private fun LoungeContent(
    modifier: Modifier,
    loungeState: LoungeState,
    navigateToMember: (MemberUIModel, String, Boolean) -> Unit = {_, _, _ -> },
    onClickInvite: () -> Unit = {},
    onScrolled: (Int) -> Unit = {}
){
    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(16.dp))

        LoungeMemberList(
            memberList = loungeState.memberList,
            navigateToMember = { navigateToMember(it, loungeState.loungeId ?: return@LoungeMemberList, loungeState.isOwner) },
            onClickInvite = onClickInvite
        )

        Spacer(modifier = Modifier.height(24.dp))

        LoungeChatList(
            loungeState = loungeState,
            navigateToMember = {
                navigateToMember(it, loungeState.loungeId ?: return@LoungeChatList, loungeState.isOwner)
            },
            onScrolled = onScrolled
        )
    }
}

@Composable
private fun LoungeChatList(
    loungeState: LoungeState,
    listState: LazyListState = rememberLazyListState(),
    navigateToMember: (MemberUIModel) -> Unit = {},
    onScrolled: (Int) -> Unit = {}
) {
    LaunchedEffect(loungeState.chatList.size){
        if (loungeState.chatList.isEmpty()) return@LaunchedEffect

        // 마지막으로 메시지 온 것이 내 것일 때(내가 직전에 보냈을 때 포함) 스크롤
        if (loungeState.chatList.first().isMine){
            listState.scrollToItem(0)
        }
    }

    LaunchedEffect(listState){
        // 스크롤 포지션 복구
        if (loungeState.scrollIndex > 0) {
            listState.scrollToItem(loungeState.scrollIndex)
        }

        // 현재 스크롤 포지션 저장
        snapshotFlow { listState.firstVisibleItemIndex }
            .debounce(500L)
            .collectLatest(onScrolled)
    }

    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        reverseLayout = true
    ) {
        item {Spacer(modifier = Modifier.height(0.dp)) }

        items(loungeState.chatList) { chat ->
            // 채팅방 생성, 참가 메시지
            if (chat.messageType != MessageType.NORMAL) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = colorNeutral90,
                    contentColor = Color.White,
                ) {
                    Text(
                        text = chat.content,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier
                            .padding(horizontal = 48.dp)
                            .padding(vertical = 5.dp)
                    )
                }
            } else {
                // 일반 메시지
                ChatBubble(
                    message = chat.content,
                    profileImage = chat.profileImage,
                    isMine = chat.isMine,
                    isReady = loungeState.memberList.find { it.uid == chat.sender }?.isReady ?: false,
                    sendStatus = chat.sendStatus,
                    navigateToMember = {
                        navigateToMember(
                            loungeState.memberList.find { it.uid == chat.sender } ?: return@ChatBubble,
                        )
                    }
                )
            }
        }

    }
}


@Composable
private fun LoungeMemberList(
    memberList: List<MemberUIModel> = emptyList(),
    navigateToMember: (MemberUIModel) -> Unit = {},
    onClickInvite: () -> Unit = {},
){
    val memberLimit = 6
    // 멤버 최대 6명
    // 4명이하 경우 -> memberList 보여주고 초대 서클, 빈 서클 추가
    // 5명인 경우 -> 초대 서클 추가
    // 6명인 경우 memberList 만
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ){
        item{ Spacer(modifier = Modifier.width(20.dp)) }

        items(memberList){ item ->
            Surface(
                shape = CircleShape,
                border = BorderStroke(width = 2.dp, color = if (item.isReady) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outline),
                modifier = Modifier
                    .size(48.dp)
                    .modifyIf(item.isReady) {
                        circleShadow(blurRadius = 16.dp)
                    },
                color = MaterialTheme.colorScheme.background,
            ) {
                AsyncImage(
                    model = item.profileImage,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.clickable(enabled = !item.isMine){
                        navigateToMember(item)
                    },
                )
            }
        }

        if (memberList.size < memberLimit - 1) {
            item {
                Surface(
                    shape = CircleShape,
                    modifier = Modifier.size(48.dp),
                    color = MaterialTheme.colorScheme.background,
                    border = BorderStroke(width = 2.dp, color = MaterialTheme.colorScheme.outline)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_add),
                        contentDescription = "add_member",
                        modifier = Modifier
                            .clickable(onClick = onClickInvite)
                            .padding(8.dp)
                    )
                }
            }
        }

        if (memberList.size < memberLimit){
            items(memberLimit - memberList.size - 1){
                Surface(
                    shape = CircleShape,
                    modifier = Modifier
                        .size(48.dp)
                        .drawWithContent {
                            val stroke = Stroke(
                                width = 2f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(30f, 20f), 0f)
                            )

                            drawContent()
                            drawCircle(
                                color = colorOutlineVariant,
                                radius = size.minDimension / 2f,
                                style = stroke
                            )
                        },
                    color = MaterialTheme.colorScheme.background
                ){}
            }
        }
        item{ Spacer(modifier = Modifier.width(20.dp)) }
    }
}


@Composable
private fun LoungeLoadingScreen(
    isOwner: Boolean = false,
    modifier: Modifier = Modifier
){
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(50.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (isOwner) stringResource(id = R.string.lounge_create_loading)
                else stringResource(id = R.string.lounge_join_loading),
            textAlign = TextAlign.Center
        )
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LoungeBottomBar(
    loungeState: LoungeState,
    onEditChat: (String) -> Unit = {},
    onSendChat: () -> Unit = {},
    onClickReadyStart: () -> Unit = {},
){
    val isTyping by rememberUpdatedState(newValue = WindowInsets.isImeVisible && loungeState.currentChat.isNotBlank())

    Column(modifier = Modifier.fillMaxWidth()) {
        Divider(
            thickness = 2.dp, color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(100.dp),
                contentColor = if (loungeState.isReady || loungeState.allReady) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onBackground,
                color = if (loungeState.isReady || loungeState.allReady) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.background,
                border = BorderStroke(width = 2.dp, color = Color.Black),
            ) {
                Text(
                    text = stringResource(id = if (loungeState.isOwner) R.string.lounge_start_btn else R.string.lounge_ready_btn),
                    style = buttonTextStyle,
                    modifier = Modifier
                        .clickable {
                            onClickReadyStart()
                        }
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }

            // 키보드 포커스드 상태일 때 border 색상 변경
            Surface(
                shape = RoundedCornerShape(24.dp),
                contentColor = MaterialTheme.colorScheme.onBackground,
                color = MaterialTheme.colorScheme.background,
                border = BorderStroke(width = 2.dp, color = if (isTyping) MaterialTheme.colorScheme.primary else Color.Black),
                modifier = Modifier.weight(1f),
            ) {
                BasicTextField(
                    value = loungeState.currentChat,
                    onValueChange = onEditChat,
                    textStyle = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                    maxLines = 2,
                )
            }


            OutlinedIconButton(
                onClick = onSendChat,
                border = BorderStroke(width = 2.dp,
                    color = if (loungeState.currentChat.isNotBlank()) Color.Black else colorNeutral90
                ),
                enabled = loungeState.currentChat.isNotBlank(),
                modifier = Modifier
                    .size(40.dp)
                    .padding(5.dp),
                colors = IconButtonDefaults.outlinedIconButtonColors(
                    disabledContentColor = colorNeutral90,
                )
            ) {
                Icon(
                    painterResource(id = R.drawable.ic_arrow_up),
                    contentDescription = "send"
                )
            }
        }
    }
}