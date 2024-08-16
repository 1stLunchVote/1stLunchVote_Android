package com.jwd.lunchvote.presentation.screen.vote.second

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.model.FoodUIModel
import com.jwd.lunchvote.presentation.model.MemberUIModel
import com.jwd.lunchvote.presentation.model.UserUIModel
import com.jwd.lunchvote.presentation.screen.vote.second.SecondVoteContract.SecondVoteDialog
import com.jwd.lunchvote.presentation.screen.vote.second.SecondVoteContract.SecondVoteEvent
import com.jwd.lunchvote.presentation.screen.vote.second.SecondVoteContract.SecondVoteSideEffect
import com.jwd.lunchvote.presentation.screen.vote.second.SecondVoteContract.SecondVoteState
import com.jwd.lunchvote.presentation.util.LocalSnackbarChannel
import com.jwd.lunchvote.presentation.util.clickableWithoutEffect
import com.jwd.lunchvote.presentation.widget.HorizontalProgressBar
import com.jwd.lunchvote.presentation.widget.LoadingScreen
import com.jwd.lunchvote.presentation.widget.LunchVoteDialog
import com.jwd.lunchvote.presentation.widget.LunchVoteTopBar
import com.jwd.lunchvote.presentation.widget.MemberProgress
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SecondVoteRoute(
  popBackStack: () -> Unit,
  navigateToVoteResult: (String) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: SecondVoteViewModel = hiltViewModel(),
  snackbarChannel: Channel<String> = LocalSnackbarChannel.current,
  context: Context = LocalContext.current
) {
  val state by viewModel.viewState.collectAsStateWithLifecycle()
  val dialog by viewModel.dialogState.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel.sideEffect) {
    viewModel.sideEffect.collectLatest {
      when(it) {
        is SecondVoteSideEffect.PopBackStack -> popBackStack()
        is SecondVoteSideEffect.NavigateToVoteResult -> navigateToVoteResult(it.loungeId)
        is SecondVoteSideEffect.ShowSnackbar -> snackbarChannel.send(it.message.asString(context))
      }
    }
  }

  BackHandler { viewModel.sendEvent(SecondVoteEvent.OnClickBackButton) }

  LaunchedEffect(Unit) { viewModel.sendEvent(SecondVoteEvent.ScreenInitialize) }

  when (dialog) {
    is SecondVoteDialog.ExitDialog -> ExitDialog(
      onDismissRequest = { viewModel.sendEvent(SecondVoteEvent.OnClickCancelButtonInExitDialog) },
      onConfirmation = { viewModel.sendEvent(SecondVoteEvent.OnClickConfirmButtonInExitDialog) }
    )
    null -> Unit
  }

  if (state.calculating) LoadingScreen(message = stringResource(R.string.second_vote_calculating_message))
  else SecondVoteScreen(
    state = state,
    modifier = modifier,
    onEvent = viewModel::sendEvent
  )
}

@Composable
private fun SecondVoteScreen(
  state: SecondVoteState,
  modifier: Modifier = Modifier,
  onEvent: (SecondVoteEvent) -> Unit = {}
) {
  Screen(
    modifier = modifier,
    topAppBar = {
      LunchVoteTopBar(
        title = stringResource(R.string.second_vote_title),
        navIconVisible = false
      )
      if (state.lounge.timeLimit != null) {
        key(state.lounge.timeLimit) {
          HorizontalProgressBar(
            timeLimitSecond = state.lounge.timeLimit,
            modifier = Modifier.fillMaxWidth(),
            onProgressComplete = { onEvent(SecondVoteEvent.OnVoteFinish) }
          )
        }
      }
    },
    scrollable = false
  ) {
    if (state.finished) SecondVoteWaitingScreen(state = state, onEvent = onEvent)
    else SecondVotingScreen(state = state, onEvent = onEvent)
  }
}

@Composable
private fun SecondVotingScreen(
  state: SecondVoteState,
  modifier: Modifier = Modifier,
  onEvent: (SecondVoteEvent) -> Unit = {}
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(start = 32.dp, top = 16.dp, end = 32.dp, bottom = 24.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    SecondVoteInformationRow(
      memberList = state.memberList,
      modifier = Modifier.fillMaxWidth()
    )
    SecondVoteBallot(
      userName = state.user.name,
      foodList = state.foodList,
      selectedFood = state.selectedFood,
      onClickFood = { onEvent(SecondVoteEvent.OnClickFood(it)) },
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
    )
    Button(
      onClick = { onEvent(SecondVoteEvent.OnClickFinishButton) },
      modifier = Modifier.align(Alignment.CenterHorizontally),
      enabled = state.selectedFood != null
    ) {
      Text(text = stringResource(R.string.second_vote_finish_button))
    }
  }
}

@Composable
private fun SecondVoteInformationRow(
  memberList: List<MemberUIModel>,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.End
  ) {
    MemberProgress(memberList.map { it.status })
  }
}

@Composable
private fun SecondVoteBallot(
  userName: String,
  foodList: List<FoodUIModel>,
  selectedFood: FoodUIModel?,
  modifier: Modifier = Modifier,
  onClickFood: (FoodUIModel) -> Unit = {}
) {
  Column(
    modifier = modifier
      .border(1.dp, MaterialTheme.colorScheme.onBackground)
      .padding(start = 20.dp, top = 30.dp, end = 20.dp, bottom = 20.dp)
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(30.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = stringResource(R.string.second_vote_paper_title, userName),
      style = MaterialTheme.typography.titleLarge
    )
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
      foodList.forEach { food ->
        SecondVoteTile(
          food = food,
          selected = selectedFood == food,
          modifier = Modifier.fillMaxWidth(),
          onClick = { onClickFood(food) }
        )
      }
    }
  }
}

@Composable
private fun SecondVoteTile(
  food: FoodUIModel,
  selected: Boolean,
  modifier: Modifier = Modifier,
  onClick: () -> Unit = {}
) {
  Row(
    modifier = modifier
      .height(80.dp)
      .border(1.dp, MaterialTheme.colorScheme.outline)
      .clickableWithoutEffect(onClick),
    verticalAlignment = Alignment.CenterVertically
  ) {
    CoilImage(
      imageModel = { food.imageUrl },
      modifier = Modifier.size(80.dp),
      imageOptions = ImageOptions(
        contentScale = ContentScale.Crop
      ),
      previewPlaceholder = R.drawable.ic_food_image_temp
    )
    VerticalDivider(
      color = MaterialTheme.colorScheme.outline
    )
    Text(
      text = food.name,
      style = MaterialTheme.typography.titleMedium,
      modifier = Modifier
        .weight(1f)
        .padding(horizontal = 16.dp)
    )
    VerticalDivider(
      color = MaterialTheme.colorScheme.outline
    )
    Box(
      modifier = Modifier.size(80.dp),
      contentAlignment = Alignment.Center
    ) {
      if (selected) Image(
        painter = painterResource(R.drawable.ic_second_voted),
        contentDescription = "selected",
        modifier = Modifier.size(48.dp)
      )
    }
  }
}

@Composable
private fun SecondVoteWaitingScreen(
  state: SecondVoteState,
  modifier: Modifier = Modifier,
  onEvent: (SecondVoteEvent) -> Unit = {}
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
        text = stringResource(R.string.second_vote_waiting_title),
        style = MaterialTheme.typography.titleLarge
      )
      MemberProgress(state.memberList.map { it.status })
      Text(
        text = stringResource(R.string.second_vote_waiting_body, state.memberList.count { it.status == MemberUIModel.Status.VOTED }),
        style = MaterialTheme.typography.bodyMedium
      )
    }
    Button(
      onClick = { onEvent(SecondVoteEvent.OnClickReVoteButton) },
      modifier = Modifier.align(Alignment.CenterHorizontally)
    ) {
      Text(text = stringResource(R.string.second_vote_re_vote_button))
    }
  }
}

@Composable
private fun ExitDialog(
  modifier: Modifier = Modifier,
  onDismissRequest: () -> Unit = {},
  onConfirmation: () -> Unit = {}
) {
  LunchVoteDialog(
    title = stringResource(R.string.vote_exit_dialog_title),
    dismissText = stringResource(R.string.vote_exit_dialog_dismiss_button),
    onDismissRequest = onDismissRequest,
    confirmText = stringResource(R.string.vote_exit_dialog_confirm_button),
    onConfirmation = onConfirmation,
    modifier = modifier,
    icon = {
      Icon(
        Icons.Rounded.Warning,
        contentDescription = null,
        modifier = Modifier.size(28.dp)
      )
    }
  ) {
    Text(text = stringResource(R.string.vote_exit_dialog_body))
  }
}

@Preview
@Composable
private fun Preview() {
  ScreenPreview {
    SecondVoteScreen(
      SecondVoteState(
        user = UserUIModel(name = "조유리"),
        memberList = listOf(
          MemberUIModel(status = MemberUIModel.Status.VOTING),
          MemberUIModel(status = MemberUIModel.Status.VOTED),
          MemberUIModel(status = MemberUIModel.Status.VOTED),
          MemberUIModel(status = MemberUIModel.Status.VOTING),
          MemberUIModel(status = MemberUIModel.Status.VOTED),
          MemberUIModel(status = MemberUIModel.Status.VOTING)
        ),
        foodList = List(5) {
          FoodUIModel(name = "음식 $it")
        },
        selectedFood = FoodUIModel(name = "음식 2")
      )
    )
  }
}

@Preview
@Composable
private fun ExitDialogPreview() {
  LunchVoteTheme {
    ExitDialog()
  }
}