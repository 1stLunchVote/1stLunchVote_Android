package com.jwd.lunchvote.ui.vote.first

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.R
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.domain.entity.FoodStatus
import com.jwd.lunchvote.model.FoodUIModel
import com.jwd.lunchvote.ui.vote.first.FirstVoteContract.FirstVoteEvent
import com.jwd.lunchvote.ui.vote.first.FirstVoteContract.FirstVoteSideEffect
import com.jwd.lunchvote.ui.vote.first.FirstVoteContract.FirstVoteState
import com.jwd.lunchvote.util.UiText
import com.jwd.lunchvote.widget.FoodItem
import com.jwd.lunchvote.widget.LikeDislike
import com.jwd.lunchvote.widget.LunchVoteTextField
import com.jwd.lunchvote.widget.ProgressTopBar
import com.jwd.lunchvote.widget.StepProgress
import com.jwd.lunchvote.widget.TextFieldType
import com.jwd.lunchvote.widget.VoteExitDialog
import kotlinx.coroutines.flow.collectLatest

@Composable
fun FirstVoteRoute(
    navigateToSecondVote: () -> Unit,
    popBackStack: (String) -> Unit,
    viewModel: FirstVoteViewModel = hiltViewModel(),
    context: Context = LocalContext.current
) {
    val firstVoteState by viewModel.viewState.collectAsStateWithLifecycle()

//    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collectLatest {
            when(it) {
                is FirstVoteSideEffect.PopBackStack -> popBackStack(UiText.StringResource(R.string.exit_from_vote).asString(context))
                is FirstVoteSideEffect.NavigateToSecondVote -> navigateToSecondVote()
            }
        }
    }

    BackHandler() {
        viewModel.sendEvent(FirstVoteEvent.OnTryExit)
    }

    if (firstVoteState.voteExitDialogShown){
        VoteExitDialog(
            onDismiss = { viewModel.sendEvent(FirstVoteEvent.OnClickExitDialog(false)) },
            onExit = { viewModel.sendEvent(FirstVoteEvent.OnClickExitDialog(true)) }
        )
    }

    FirstVoteScreen(
        firstVoteState = firstVoteState,
        onClickFood = { food -> viewModel.sendEvent(FirstVoteEvent.OnClickFood(food)) },
        setSearchKeyword = { search -> viewModel.sendEvent(FirstVoteEvent.SetSearchKeyword(search)) },
        navigateToSecondVote = navigateToSecondVote
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun FirstVoteScreen(
    firstVoteState: FirstVoteState,
    onClickFood: (FoodUIModel) -> Unit = {},
    setSearchKeyword: (String) -> Unit = {},
    navigateToSecondVote: () -> Unit = {},
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProgressTopBar(
                    title = stringResource(R.string.first_vote_title),
                    onProgressComplete = { navigateToSecondVote() }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .padding(top = 16.dp, start = 24.dp, end = 24.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LikeDislike(firstVoteState.likeList.size, firstVoteState.dislikeList.size)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (i in 1..firstVoteState.totalMember) {
                        StepProgress(i <= firstVoteState.endedMember)
                    }
                }
            }
            LunchVoteTextField(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .padding(horizontal = 24.dp, vertical = 0.dp),
                text = firstVoteState.searchKeyword,
                hintText = stringResource(R.string.first_vote_hint_text),
                onTextChanged = setSearchKeyword,
                textFieldType = TextFieldType.Search
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .weight(1f, false)
                    .padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                items(firstVoteState.foodList.filter { it.name.contains(firstVoteState.searchKeyword) }) {food ->
                    FoodItem(food) { onClickFood(food) }
                }
            }
            Button(
                onClick = navigateToSecondVote,
                modifier = Modifier.padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 24.dp),
                enabled = firstVoteState.likeList.isNotEmpty() && firstVoteState.dislikeList.isNotEmpty()
            ) {
                Text("투표 완료")
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun FirstVoteScreenPreview() {
    LunchVoteTheme {
        Surface {
            FirstVoteScreen(
                FirstVoteState(
                    foodList = List(20) {
                        FoodUIModel(
                            foodId = it.toLong(),
                            imageUrl = "",
                            name = "음식명",
                            status = FoodStatus.DEFAULT
                        )
                    },
                    totalMember = 3,
                    endedMember = 1
                )
            )
        }
    }
}