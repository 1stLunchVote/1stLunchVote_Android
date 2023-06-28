package com.jwd.lunchvote.ui.vote.second

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.jwd.lunchvote.R
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.core.ui.util.noRippleClickable
import com.jwd.lunchvote.model.SecondVoteTileUIModel
import com.jwd.lunchvote.widget.ProgressTopBar
import com.jwd.lunchvote.ui.vote.second.SecondVoteContract.*

@Composable
fun SecondVoteRoute(
    viewModel: SecondVoteViewModel = hiltViewModel(),
    popBackStack: () -> Unit
){
    val secondVoteState : SecondVoteState by viewModel.viewState.collectAsStateWithLifecycle()

    // 뒤로가기 버튼 눌렀을 때
    BackHandler() {
        // Todo : 뒤로가기 눌러서 나가도 투표는 그대로 진행된다고 명시할지 고민
        popBackStack()
    }

    SecondVoteScreen(
        state = secondVoteState,
        onClickVote = { viewModel.sendEvent(SecondVoteEvent.OnClickVote(it)) },
        onClickComplete = { viewModel.sendEvent(SecondVoteEvent.OnClickComplete) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SecondVoteScreen(
    state: SecondVoteState = SecondVoteState(),
    onClickVote: (Int) -> Unit = {},
    onClickComplete: () -> Unit = {},
){
    Scaffold(
        topBar = { ProgressTopBar(title = "2차 투표") },
        floatingActionButton = {
            Button(onClick = onClickComplete, enabled = state.voteIndex != -1) {
                Text(text = stringResource(id = R.string.second_vote_fab))
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            SecondVoteContent(state = state, onClickVote = onClickVote)
        }
    }
}

@Composable
private fun SecondVoteContent(
    state: SecondVoteState,
    onClickVote: (Int) -> Unit = {}
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .border(color = MaterialTheme.colorScheme.outline, width = 1.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        val nicknameString = buildAnnotatedString {
            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)){
                append(state.nickname)
            }
            append(" 님의 투표")
        }
        Text(text = nicknameString, style = MaterialTheme.typography.titleLarge.copy(fontSize = 28.sp))

        Spacer(modifier = Modifier.height(30.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ){
            itemsIndexed(state.voteList){ index, item ->
                SecondVoteTile(
                    tile = item,
                    isVoted = state.voteIndex == index,
                    onClickVote = { onClickVote(index) }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun SecondVoteTile(
    tile: SecondVoteTileUIModel,
    isVoted: Boolean,
    onClickVote: () -> Unit = {}
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .noRippleClickable { onClickVote() }
            .border(color = MaterialTheme.colorScheme.outline, width = 1.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        AsyncImage(
            model = tile.foodImg, contentDescription = "food",
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(80.dp)
        )


        Box(
            modifier = Modifier
                .weight(1f)
                .height(80.dp)
                .border(width = 1.dp, color = MaterialTheme.colorScheme.outline),
        ){
            Text(
                text = tile.foodName,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(horizontal = 16.dp),
            )
        }


        Box(
            modifier = Modifier
                .size(80.dp),
        ) {
            if (isVoted){
                Image(
                    painter = painterResource(id = R.drawable.ic_second_voted),
                    contentDescription = "voted",
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SecondVoteScreenPreview(){
    LunchVoteTheme {
        SecondVoteScreen(
            SecondVoteState(nickname = "이동건")
        )
    }
}