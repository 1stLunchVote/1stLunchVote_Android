package com.jwd.lunchvote.presentation.ui.vote.first

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.jwd.lunchvote.presentation.model.MemberUIModel
import com.jwd.lunchvote.presentation.ui.vote.first.FirstVoteContract.FirstVoteEvent
import com.jwd.lunchvote.presentation.ui.vote.first.FirstVoteContract.FirstVoteSideEffect
import com.jwd.lunchvote.presentation.ui.vote.first.FirstVoteContract.FirstVoteState
import com.jwd.lunchvote.presentation.widget.HorizontalProgressBar
import com.jwd.lunchvote.presentation.widget.LikeDislike
import com.jwd.lunchvote.presentation.widget.LoadingScreen
import com.jwd.lunchvote.presentation.widget.LunchVoteTopBar
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import kotlinx.coroutines.flow.collectLatest

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
  val loading by viewModel.isLoading.collectAsStateWithLifecycle()

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

  if (loading) LoadingScreen()
  else FirstVoteScreen(
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
        modifier = Modifier.fillMaxWidth()
      )
    }
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 32.dp)
        .padding(top = 16.dp)
    ) {

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

  }
}

@Preview
@Composable
private fun Preview() {
  ScreenPreview {
    FirstVoteScreen(
      FirstVoteState()
    )
  }
}