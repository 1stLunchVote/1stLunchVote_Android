package com.jwd.lunchvote.presentation.screen.vote.second

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.model.FoodUIModel
import com.jwd.lunchvote.presentation.model.MemberUIModel
import com.jwd.lunchvote.presentation.model.UserUIModel
import com.jwd.lunchvote.presentation.screen.vote.second.SecondVoteContract.ExitDialogEvent
import com.jwd.lunchvote.presentation.screen.vote.second.SecondVoteContract.SecondVoteEvent
import com.jwd.lunchvote.presentation.screen.vote.second.SecondVoteContract.SecondVoteSideEffect
import com.jwd.lunchvote.presentation.screen.vote.second.SecondVoteContract.SecondVoteState
import com.jwd.lunchvote.presentation.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.util.LocalSnackbarChannel
import com.jwd.lunchvote.presentation.util.clickableWithoutEffect
import com.jwd.lunchvote.presentation.widget.Dialog
import com.jwd.lunchvote.presentation.widget.DialogButton
import com.jwd.lunchvote.presentation.widget.FAB
import com.jwd.lunchvote.presentation.widget.HorizontalTimer
import com.jwd.lunchvote.presentation.widget.ImageFromUri
import com.jwd.lunchvote.presentation.widget.LoadingScreen
import com.jwd.lunchvote.presentation.widget.MemberProgress
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import com.jwd.lunchvote.presentation.widget.TopBar
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

  state.exitDialogState?.let {
    ExitDialog(onEvent = viewModel::sendEvent)
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
    modifier = modifier
      .padding(horizontal = 24.dp)
      .padding(top = 16.dp),
    topAppBar = {
      TopBar(
        title = stringResource(R.string.second_vote_title),
        navIconVisible = false
      )
      if (state.lounge.timeLimit != null) {
        key(state.lounge.timeLimit) {
          HorizontalTimer(
            timeLimitSecond = state.lounge.timeLimit,
            modifier = Modifier.fillMaxWidth(),
            onProgressComplete = { onEvent(SecondVoteEvent.OnVoteFinish) }
          )
        }
      }
    },
    actions = {
      if (state.finished) {
        FAB(
          text = stringResource(R.string.second_vote_re_vote_button),
          onClick = { onEvent(SecondVoteEvent.OnClickReVoteButton) }
        )
      } else {
        if (state.selectedFood != null) {
          FAB(
            text = stringResource(R.string.second_vote_finish_button),
            onClick = { onEvent(SecondVoteEvent.OnClickFinishButton) }
          )
        }
      }
    },
    scrollable = false
  ) {
    if (state.finished) {
      Column(
        modifier = Modifier.fillMaxSize(),
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
    }
    else {
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
      ) {
        val scrollState = rememberScrollState()

        InformationRow(
          userName = if (scrollState.value > 200) state.user.name else null,
          memberList = state.memberList,
          modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, MaterialTheme.shapes.small)
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.background)
            .border(2.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.small)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .zIndex(1f)
        )
        SecondVoteBallot(
          userName = state.user.name,
          foodList = state.foodList,
          selectedFood = state.selectedFood,
          onClickFood = { onEvent(SecondVoteEvent.OnClickFood(it)) },
          modifier = Modifier
            .clip(MaterialTheme.shapes.small.copy(bottomStart = CornerSize(0.dp), bottomEnd = CornerSize(0.dp)))
            .verticalScroll(scrollState)
            .fillMaxWidth()
            .padding(top = 64.dp, bottom = 124.dp)
        )
      }
    }
  }
}

@Composable
private fun InformationRow(
  userName: String?,
  memberList: List<MemberUIModel>,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      text = if (userName != null) stringResource(R.string.second_vote_paper_title, userName) else "",
      style = MaterialTheme.typography.titleMedium
    )
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
      .padding(start = 20.dp, top = 30.dp, end = 20.dp, bottom = 20.dp),
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
  onClick: () -> Unit
) {
  Row(
    modifier = modifier
      .height(80.dp)
      .border(1.dp, MaterialTheme.colorScheme.outline)
      .clickableWithoutEffect(onClick),
    verticalAlignment = Alignment.CenterVertically
  ) {
    ImageFromUri(
      uri = food.imageUrl.toUri(),
      modifier = Modifier.size(80.dp)
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
private fun ExitDialog(
  modifier: Modifier = Modifier,
  onEvent: (ExitDialogEvent) -> Unit = {}
) {
  Dialog(
    title = stringResource(R.string.sv_exit_dialog_title),
    onDismissRequest = { onEvent(ExitDialogEvent.OnClickCancelButton) },
    modifier = modifier,
    icon = {
      Icon(
        imageVector = Icons.Rounded.Warning,
        contentDescription = "Warning"
      )
    },
    iconColor = MaterialTheme.colorScheme.error,
    body = stringResource(R.string.sv_exit_dialog_body),
    buttons = {
      DialogButton(
        text = stringResource(R.string.sv_exit_dialog_cancel_button),
        onClick = { onEvent(ExitDialogEvent.OnClickCancelButton) },
        color = MaterialTheme.colorScheme.onSurface,
        isDismiss = true
      )
      DialogButton(
        text = stringResource(R.string.sv_exit_dialog_exit_button),
        onClick = { onEvent(ExitDialogEvent.OnClickExitButton) },
        color = MaterialTheme.colorScheme.error
      )
    }
  )
}

@Preview
@Composable
private fun Default() {
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
        foodList = List(8) {
          FoodUIModel(name = "음식 $it")
        },
        selectedFood = FoodUIModel(name = "음식 2")
      )
    )
  }
}

@Preview
@Composable
private fun FinishVote() {
  ScreenPreview {
    SecondVoteScreen(
      SecondVoteState(
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

@Preview
@Composable
private fun ExitDialogPreview() {
  LunchVoteTheme {
    ExitDialog()
  }
}