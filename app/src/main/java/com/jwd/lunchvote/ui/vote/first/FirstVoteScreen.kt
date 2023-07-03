package com.jwd.lunchvote.ui.vote.first

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.graphics.PathParser
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.R
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.domain.entity.FoodStatus
import com.jwd.lunchvote.model.FoodUIModel
import com.jwd.lunchvote.ui.vote.first.FirstVoteContract.FirstVoteState
import com.jwd.lunchvote.ui.vote.first.FirstVoteContract.FirstVoteEvent
import com.jwd.lunchvote.ui.vote.first.FirstVoteContract.FirstVoteReduce
import com.jwd.lunchvote.ui.vote.first.FirstVoteContract.FirstVoteSideEffect
import com.jwd.lunchvote.util.UiText
import com.jwd.lunchvote.widget.FoodItem
import com.jwd.lunchvote.widget.LikeDislike
import com.jwd.lunchvote.widget.LunchVoteTextField
import com.jwd.lunchvote.widget.ProgressTopBar
import com.jwd.lunchvote.widget.StepProgress
import com.jwd.lunchvote.widget.TextFieldType
import kotlinx.coroutines.flow.collectLatest

@Composable
fun FirstVoteRoute(
    navigateToSecondVote: () -> Unit,
    popBackStack: (String) -> Unit,
    viewModel: FirstVoteViewModel = hiltViewModel(),
    context: Context = LocalContext.current
) {
    val firstVoteState by viewModel.viewState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collectLatest {
            when(it) {
                is FirstVoteSideEffect.PopBackStack -> popBackStack(UiText.StringResource(R.string.exit_from_first_vote).asString(context))
                is FirstVoteSideEffect.NavigateToSecondVote -> navigateToSecondVote()
            }
        }
    }

    FirstVoteScreen(
        firstVoteState = firstVoteState,
        typeSearchKeyword = { search -> viewModel.sendEvent(FirstVoteEvent.TypeSearchKeyword(search)) },
        navigateToSecondVote = navigateToSecondVote,
        popBackStack = popBackStack
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun FirstVoteScreen(
    firstVoteState: FirstVoteState,
    typeSearchKeyword: (String) -> Unit = {},
    navigateToSecondVote: () -> Unit = {},
    popBackStack: (String) -> Unit = {},
    context: Context = LocalContext.current
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProgressTopBar(stringResource(R.string.first_vote_title)) {
                popBackStack(UiText.StringResource(R.string.exit_from_first_vote).asString(context))
            }
            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
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
            Spacer(Modifier.height(16.dp))
            LunchVoteTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                text = firstVoteState.searchKeyword,
                hintText = stringResource(R.string.first_vote_hint_text),
                onTextChanged = typeSearchKeyword,
                textFieldType = TextFieldType.Search
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                items(firstVoteState.foodList) {

                }
                item { FoodItem(
                    food = FoodUIModel(
                        foodId = 0L,
                        imageUrl = "",
                        name = "햄버거",
                        status = FoodStatus.DISLIKE
                    )
                ) }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun FirstVoteScreenPreview() {
    LunchVoteTheme {
        Surface {
            FirstVoteScreen(
                FirstVoteState(
                    totalMember = 3,
                    endedMember = 1
                )
            )
        }
    }
}