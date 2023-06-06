package com.jwd.lunchvote.ui.lounge

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.jwd.lunchvote.R
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.core.ui.theme.buttonTextStyle
import com.jwd.lunchvote.core.ui.theme.colorNeutral90
import com.jwd.lunchvote.core.ui.theme.colorOutlineVariant
import com.jwd.lunchvote.model.ChatUIModel
import com.jwd.lunchvote.model.MemberUIModel
import com.jwd.lunchvote.ui.lounge.LoungeContract.*
import com.jwd.lunchvote.widget.ChatBubble
import com.jwd.lunchvote.widget.LunchVoteTopBar

@Composable
fun LoungeRoute(
    viewModel: LoungeViewModel = hiltViewModel(),
    popBackStack: () -> Unit
){
    val loungeState : LoungeState by viewModel.viewState.collectAsStateWithLifecycle()

    val snackBarHostState = remember { SnackbarHostState() }

    LoungeScreen(
        loungeState = loungeState,
        snackBarHostState = snackBarHostState,
        popBackStack = popBackStack,
        onEditChat = { viewModel.sendEvent(LoungeEvent.OnEditChat(it)) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoungeScreen(
    loungeState: LoungeState,
    snackBarHostState: SnackbarHostState,
    popBackStack: () -> Unit = {},
    onEditChat: (String) -> Unit = {},
){
    Scaffold(
        topBar = {
            LunchVoteTopBar(
                title = stringResource(id = R.string.lounge_topbar_title),
                popBackStack = popBackStack
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        bottomBar = {
            if (loungeState.memberList.isNotEmpty()){
                LoungeBottomBar(loungeState = loungeState, onEditChat = onEditChat)
            }
        }
    ) { padding ->
        if (loungeState.loungeId == null){
            LoungeLoadingScreen(
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
                loungeState = loungeState
            )
        }
    }
}

@Composable
private fun LoungeContent(
    modifier: Modifier,
    loungeState: LoungeState
){
    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(16.dp))

        LoungeMemberList(memberList = loungeState.memberList)

        Spacer(modifier = Modifier.height(24.dp))

        LoungeChatList(chatList = loungeState.chatList, memberList = loungeState.memberList)
    }
}

@Composable
private fun LoungeChatList(
    chatList: List<ChatUIModel> = emptyList(),
    memberList: List<MemberUIModel> = emptyList()
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        items(chatList) { chat ->
            // 채팅방 생성, 참가 메시지
            if (chat.messageType == 1) {
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
                    isReady = memberList.find { it.uid == chat.sender }?.isReady ?: false
                )
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
private fun LoungeChatListPreview(){
    LunchVoteTheme {
        LoungeChatList(chatList = chatList)
    }
}

@Composable
private fun LoungeMemberList(
    memberList: List<MemberUIModel> = emptyList()
){
    val memberLimit = 6
    // 멤버 최대 6명
    // 4명이하 경우 -> memberList 보여주고 초대 서클, 빈 서클 추가
    // 5명인 경우 -> 초대 서클 추가
    // 6명인 경우 memberList 만
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(horizontal = 32.dp)
    ){
        items(memberList){ item ->
            Surface(
                shape = CircleShape,
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.background,
                // 일단 그림자 효과 안둠
                border = BorderStroke(width = 2.dp, color = if (item.isReady) Color.Red else MaterialTheme.colorScheme.outline)
            ) {
                AsyncImage(
                    model = item.profileImage,
                    contentDescription = null,
                    contentScale = ContentScale.Crop
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
                        modifier = Modifier.padding(8.dp)
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
    }
}


@Composable
private fun LoungeLoadingScreen(
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
            text = stringResource(id = R.string.lounge_create_loading),
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LoungeBottomBar(
    loungeState: LoungeState,
    onEditChat: (String) -> Unit = {}
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
                contentColor = if (loungeState.allReady) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onBackground,
                color = if (loungeState.allReady) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.background,
                border = BorderStroke(width = 2.dp, color = Color.Black),
            ) {
                Text(
                    text = stringResource(id = if (loungeState.isOwner) R.string.lounge_start_btn else R.string.lounge_ready_btn),
                    style = buttonTextStyle,
                    modifier = Modifier
                        .clickable { }
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
                onClick = { /*TODO*/ },
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

@Preview(showBackground = true)
@Composable
private fun LoungeMemberListPreview(){
    LunchVoteTheme {
        LoungeMemberList(memberList = listOf(
            MemberUIModel("test", "http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_640x640.jpg", false)
        ))
    }
}


@Preview(showBackground = true)
@Composable
private fun LoungeScreenPreview(){
    LunchVoteTheme {
        LoungeScreen(
            loungeState = LoungeState(loungeId = "1234", chatList = chatList, memberList = listOf(
                MemberUIModel("test", "http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_640x640.jpg", false),
            )),
            snackBarHostState = remember { SnackbarHostState() },
        )
    }
}

private val chatList = listOf(
    ChatUIModel(
        messageType = 1,
        content = "채팅방이 생성되었습니다.",
        isMine = true,
        sender = "sender",
        createdAt = "",
        profileImage = ""
    ),
    ChatUIModel(
        messageType = 0,
        content = "안녕하세요",
        isMine = false,
        sender = "sender",
        createdAt = "",
        profileImage = ""
    ),
    ChatUIModel(
        messageType = 0,
        content = "안녕하세요",
        isMine = true,
        sender = "sender",
        createdAt = "",
        profileImage = ""
    ),
)