package com.jwd.lunchvote.presentation.ui.vote.first

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.model.FoodStatus
import com.jwd.lunchvote.presentation.model.MemberUIModel
import com.jwd.lunchvote.presentation.ui.vote.first.FirstVoteContract.FirstVoteEvent
import com.jwd.lunchvote.presentation.ui.vote.first.FirstVoteContract.FirstVoteSideEffect
import com.jwd.lunchvote.presentation.ui.vote.first.FirstVoteContract.FirstVoteState
import com.jwd.lunchvote.presentation.widget.FoodItem
import com.jwd.lunchvote.presentation.widget.HorizontalProgressBar
import com.jwd.lunchvote.presentation.widget.LikeDislike
import com.jwd.lunchvote.presentation.widget.LoadingScreen
import com.jwd.lunchvote.presentation.widget.LunchVoteTextField
import com.jwd.lunchvote.presentation.widget.LunchVoteTopBar
import com.jwd.lunchvote.presentation.widget.MemberProgress
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import com.jwd.lunchvote.presentation.widget.TextFieldType
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@Composable
fun FirstVoteRoute(
  popBackStack: () -> Unit,
  navigateToSecondVote: () -> Unit,
  openTemplateDialog: () -> Unit,
  openVoteExitDialog: () -> Unit,
  showSnackBar: suspend (String) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: FirstVoteViewModel = hiltViewModel(),
  context: Context = LocalContext.current
) {
  val state by viewModel.viewState.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel.sideEffect) {
    viewModel.sideEffect.collectLatest {
      when(it) {
        is FirstVoteSideEffect.PopBackStack -> popBackStack()
        is FirstVoteSideEffect.NavigateToSecondVote -> navigateToSecondVote()
        is FirstVoteSideEffect.OpenTemplateDialog -> openTemplateDialog()
        is FirstVoteSideEffect.OpenVoteExitDialog -> openVoteExitDialog()
        is FirstVoteSideEffect.ShowSnackBar -> showSnackBar(it.message.asString(context))
      }
    }
  }

  BackHandler { viewModel.sendEvent(FirstVoteEvent.OnClickBackButton) }

  LaunchedEffect(Unit) { viewModel.sendEvent(FirstVoteEvent.ScreenInitialize) }

  FirstVoteScreen(
    state = state,
    modifier = modifier,
    onEvent = viewModel::sendEvent
  )
}

@Composable
private fun FirstVoteScreen(
  state: FirstVoteState,
  modifier: Modifier = Modifier,
  onEvent: (FirstVoteEvent) -> Unit = {}
) {
  Screen(
    modifier = modifier,
    topAppBar = {
      LunchVoteTopBar(
        title = stringResource(R.string.first_vote_title),
        navIconVisible = false
      )
      HorizontalProgressBar(
        timeLimitSecond = 60,
        modifier = Modifier.fillMaxWidth(),
        onProgressComplete = { onEvent(FirstVoteEvent.OnClickFinishButton) }
      )
    },
    scrollable = false
  ) {
    if (state.finished) FirstVoteWaitingScreen(state = state, onEvent = onEvent)
    else FirstVotingScreen(state = state, onEvent = onEvent)
  }
}

@Composable
private fun FirstVotingScreen(
  state: FirstVoteState,
  modifier: Modifier = Modifier,
  onEvent: (FirstVoteEvent) -> Unit = {}
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(start = 32.dp, top = 16.dp, end = 32.dp, bottom = 24.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    FirstVoteInformationRow(
      like = state.likedFoods.size,
      dislike = state.dislikedFoods.size,
      memberList = state.memberList,
      modifier = Modifier.fillMaxWidth()
    )
    LunchVoteTextField(
      text = state.searchKeyword,
      onTextChange = { onEvent(FirstVoteEvent.OnSearchKeywordChange(it)) },
      hintText = stringResource(R.string.first_vote_hint_text),
      modifier = Modifier.fillMaxWidth(),
      textFieldType = TextFieldType.Search
    )
    LazyVerticalGrid(
      columns = GridCells.Fixed(3),
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f),
      verticalArrangement = Arrangement.spacedBy(8.dp),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      val filteredFoodList = state.foodMap.keys.filter { it.name.contains(state.searchKeyword) }

      items(filteredFoodList) {food ->
        FoodItem(
          food = food,
          status = state.foodMap[food] ?: FoodStatus.DEFAULT,
          onClick = { onEvent(FirstVoteEvent.OnClickFood(food)) }
        )
      }
    }
    Button(
      onClick = { onEvent(FirstVoteEvent.OnClickFinishButton) },
      modifier = Modifier.align(Alignment.CenterHorizontally),
      enabled = state.likedFoods.isNotEmpty() || state.dislikedFoods.isNotEmpty()
    ) {
      Text(text = stringResource(R.string.first_vote_finish_button))
    }
  }
}

@Composable
private fun FirstVoteInformationRow(
  like: Int,
  dislike: Int,
  memberList: List<MemberUIModel>,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    LikeDislike(like, dislike)
    MemberProgress(memberList.map { it.status })
  }
}

@Composable
private fun FirstVoteWaitingScreen(
  state: FirstVoteState,
  modifier: Modifier = Modifier,
  onEvent: (FirstVoteEvent) -> Unit = {}
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(24.dp),
    verticalArrangement = Arrangement.spacedBy(40.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f),
      verticalArrangement = Arrangement.spacedBy(40.dp, alignment = Alignment.CenterVertically),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = stringResource(R.string.first_vote_waiting_title),
        style = MaterialTheme.typography.titleLarge
      )
      MemberProgress(state.memberList.map { it.status })
      Text(
        text = stringResource(R.string.first_vote_waiting_body, state.memberList.count { it.status == MemberUIModel.Status.VOTED }),
        style = MaterialTheme.typography.bodyMedium
      )
    }
    Button(
      onClick = { onEvent(FirstVoteEvent.OnClickReVoteButton) },
      modifier = Modifier.align(Alignment.CenterHorizontally)
    ) {
      Text(text = stringResource(R.string.first_vote_re_vote_button))
    }
  }
}

@Preview
@Composable
private fun Preview1() {
  ScreenPreview {
    FirstVoteScreen(
      FirstVoteState(
        memberList = listOf(
          MemberUIModel(status = MemberUIModel.Status.VOTING),
          MemberUIModel(status = MemberUIModel.Status.VOTED),
          MemberUIModel(status = MemberUIModel.Status.VOTED),
          MemberUIModel(status = MemberUIModel.Status.VOTING),
          MemberUIModel(status = MemberUIModel.Status.VOTED),
          MemberUIModel(status = MemberUIModel.Status.VOTING)
        )
      )
    )
  }
}

@Preview
@Composable
private fun Preview2() {
  ScreenPreview {
    FirstVoteScreen(
      FirstVoteState(
        memberList = listOf(
          MemberUIModel(status = MemberUIModel.Status.VOTING),
          MemberUIModel(status = MemberUIModel.Status.VOTED),
          MemberUIModel(status = MemberUIModel.Status.VOTED),
          MemberUIModel(status = MemberUIModel.Status.VOTING),
          MemberUIModel(status = MemberUIModel.Status.VOTED),
          MemberUIModel(status = MemberUIModel.Status.VOTING)
        ),
        finished = true
      )
    )
  }
}