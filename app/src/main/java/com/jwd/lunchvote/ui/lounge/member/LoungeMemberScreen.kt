package com.jwd.lunchvote.ui.lounge.member

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.jwd.lunchvote.R
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.core.ui.theme.colorOutline
import com.jwd.lunchvote.ui.lounge.member.LoungeMemberContract.*
import com.jwd.lunchvote.widget.LunchVoteTopBar

@Composable
fun LoungeMemberRoute(
    viewModel: LoungeMemberViewModel = hiltViewModel(),
    popBackStack: () -> Unit
){
    // Todo : 여기서도 게임 시작, 추방 관련 리스너 달고 있어야 함
    val memberState: LoungeMemberState by viewModel.viewState.collectAsStateWithLifecycle()

    LoungeMemberScreen(
        memberState = memberState,
        popBackStack = popBackStack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoungeMemberScreen(
    memberState: LoungeMemberState,
    popBackStack: () -> Unit = {}
){
    Scaffold(
        topBar = {
            LunchVoteTopBar(
                title = stringResource(id = R.string.lounge_member_topbar_title),
                popBackStack = popBackStack
            )
        },
    ) {padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
            ) {
                AsyncImage(
                    model = memberState.profileUrl,
                    contentDescription = "profileImage",
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .border(2.dp, colorOutline, CircleShape)
                )

                Spacer(modifier = Modifier.width(32.dp))

                Text(text = memberState.nickname.ifBlank {
                    stringResource(
                        id = R.string.lounge_member_anonymous_nickname
                    )
                }, modifier = Modifier.padding(top = 20.dp),
                    style = MaterialTheme.typography.titleLarge)
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            if (memberState.isOwner){
                TextButton(onClick = { /*TODO*/ }) {
                    Text(
                        text = stringResource(R.string.lounge_member_exile),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.error,
                            textDecoration = TextDecoration.Underline
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(64.dp))

        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoungeMemberScreenPreview(){
    LunchVoteTheme {
        LoungeMemberScreen(
            memberState = LoungeMemberState("1234", "이동건", null, true)
        )
    }
}