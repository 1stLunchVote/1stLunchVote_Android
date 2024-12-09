package com.jwd.lunchvote.presentation.screen.vote.first

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.model.FoodItem
import com.jwd.lunchvote.presentation.model.FoodItem.Status.DISLIKE
import com.jwd.lunchvote.presentation.model.FoodItem.Status.LIKE
import com.jwd.lunchvote.presentation.model.FoodUIModel
import com.jwd.lunchvote.presentation.model.MemberUIModel
import com.jwd.lunchvote.presentation.model.TemplateUIModel
import com.jwd.lunchvote.presentation.screen.vote.first.FirstVoteContract.ExitDialogEvent
import com.jwd.lunchvote.presentation.screen.vote.first.FirstVoteContract.FirstVoteEvent
import com.jwd.lunchvote.presentation.screen.vote.first.FirstVoteContract.FirstVoteSideEffect
import com.jwd.lunchvote.presentation.screen.vote.first.FirstVoteContract.FirstVoteState
import com.jwd.lunchvote.presentation.screen.vote.first.FirstVoteContract.InformationDialogEvent
import com.jwd.lunchvote.presentation.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.util.LocalSnackbarChannel
import com.jwd.lunchvote.presentation.widget.Dialog
import com.jwd.lunchvote.presentation.widget.DialogButton
import com.jwd.lunchvote.presentation.widget.DropDownMenu
import com.jwd.lunchvote.presentation.widget.FAB
import com.jwd.lunchvote.presentation.widget.FoodGrid
import com.jwd.lunchvote.presentation.widget.HorizontalTimer
import com.jwd.lunchvote.presentation.widget.LikeDislike
import com.jwd.lunchvote.presentation.widget.LoadingScreen
import com.jwd.lunchvote.presentation.widget.LunchVoteIcon
import com.jwd.lunchvote.presentation.widget.MemberProgress
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import com.jwd.lunchvote.presentation.widget.TopBar
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun FirstVoteRoute(
  popBackStack: () -> Unit,
  navigateToSecondVote: (String) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: FirstVoteViewModel = hiltViewModel(),
  snackbarChannel: Channel<String> = LocalSnackbarChannel.current,
  context: Context = LocalContext.current
) {
  val state by viewModel.viewState.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel.sideEffect) {
    viewModel.sideEffect.collectLatest {
      when(it) {
        is FirstVoteSideEffect.PopBackStack -> popBackStack()
        is FirstVoteSideEffect.NavigateToSecondVote -> navigateToSecondVote(it.loungeId)
        is FirstVoteSideEffect.ShowSnackbar -> snackbarChannel.send(it.message.asString(context))
      }
    }
  }

  BackHandler { viewModel.sendEvent(FirstVoteEvent.OnClickBackButton) }

  LaunchedEffect(Unit) { viewModel.sendEvent(FirstVoteEvent.ScreenInitialize) }

  state.informationDialogState?.let { dialogState ->
    InformationDialog(
      templateList = dialogState.templateList,
      template = dialogState.selectedTemplate,
      onEvent = viewModel::sendEvent
    )
  }
  state.exitDialogState?.let {
    ExitDialog(onEvent = viewModel::sendEvent)
  }

  if (state.calculating) LoadingScreen(message = stringResource(R.string.first_vote_calculating_message))
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
    modifier = modifier
      .padding(horizontal = 24.dp)
      .padding(top = 16.dp),
    topAppBar = {
      TopBar(
        title = stringResource(R.string.first_vote_title),
        navIconVisible = false
      )
      if (state.lounge.timeLimit != null) {
        key(state.lounge.timeLimit) {
          HorizontalTimer(
            timeLimitSecond = state.lounge.timeLimit,
            modifier = Modifier.fillMaxWidth(),
            onProgressComplete = { onEvent(FirstVoteEvent.OnVoteFinish) }
          )
        }
      }
    },
    actions = {
      if (state.finished) {
        FAB(
          text = stringResource(R.string.first_vote_re_vote_button),
          onClick = { onEvent(FirstVoteEvent.OnClickReVoteButton) }
        )
      } else {
        if (state.foodItemList.count { it.status == LIKE } >= (state.lounge.minLikeFoods ?: 0)
          && state.foodItemList.count { it.status == DISLIKE } >= (state.lounge.minDislikeFoods ?: 0)) {
          FAB(
            text = stringResource(R.string.first_vote_finish_button),
            onClick = { onEvent(FirstVoteEvent.OnClickFinishButton) }
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
          text = stringResource(R.string.first_vote_waiting_title),
          style = MaterialTheme.typography.titleLarge
        )
        MemberProgress(state.memberList.map { it.status })
        Text(
          text = stringResource(R.string.first_vote_waiting_body, state.memberList.count { it.status == MemberUIModel.Status.VOTED }),
          style = MaterialTheme.typography.bodyMedium
        )
      }
    } else {
      Box(
        modifier = Modifier.fillMaxSize()
      ) {
        InformationRow(
          like = state.foodItemList.count { it.status == LIKE },
          dislike = state.foodItemList.count { it.status == DISLIKE },
          memberList = state.memberList,
          modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, MaterialTheme.shapes.small)
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.background)
            .border(2.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.small)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .zIndex(1f),
        )
        FoodGrid(
          searchKeyword = state.searchKeyword,
          filteredFoodList = state.foodItemList.filter { it.food.name.contains(state.searchKeyword) },
          onSearchKeywordChange = { onEvent(FirstVoteEvent.OnSearchKeywordChange(it)) },
          onClickFoodItem = { onEvent(FirstVoteEvent.OnClickFoodItem(it)) },
          topPadding = 72.dp,
          bottomPadding = 104.dp
        )
      }
    }
  }
}

@Composable
private fun InformationRow(
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
private fun InformationDialog(
  templateList: List<TemplateUIModel>,
  template: TemplateUIModel?,
  modifier: Modifier = Modifier,
  onEvent: (InformationDialogEvent) -> Unit = {}
) {
  Dialog(
    title = stringResource(R.string.fv_information_dialog_title),
    onDismissRequest = { onEvent(InformationDialogEvent.OnClickSkipButton) },
    modifier = modifier,
    icon = { LunchVoteIcon() },
    body = stringResource(R.string.fv_information_dialog_body),
    canDismiss = false,
    content = {
      DropDownMenu(
        list = templateList,
        selected = template,
        onItemSelected = { onEvent(InformationDialogEvent.OnTemplateSelected(it)) },
        getItemName = { it.name },
        hintText = stringResource(R.string.fv_information_dialog_hint_text),
        modifier = Modifier.fillMaxWidth(),
        placeholder = stringResource(R.string.fv_information_dialog_placeholder)
      )
    },
    buttons = {
      DialogButton(
        text = stringResource(R.string.fv_information_dialog_skip_button),
        onClick = { onEvent(InformationDialogEvent.OnClickSkipButton) },
        isDismiss = true
      )
      DialogButton(
        text = stringResource(R.string.fv_information_dialog_apply_button),
        onClick = { onEvent(InformationDialogEvent.OnClickApplyButton) },
        enabled = template != null
      )
    }
  )
}

@Composable
private fun ExitDialog(
  modifier: Modifier = Modifier,
  onEvent: (ExitDialogEvent) -> Unit = {}
) {
  Dialog(
    title = stringResource(R.string.fv_exit_dialog_title),
    onDismissRequest = { onEvent(ExitDialogEvent.OnClickCancelButton) },
    modifier = modifier,
    icon = {
      Icon(
        imageVector = Icons.Rounded.Warning,
        contentDescription = "Warning"
      )
    },
    iconColor = MaterialTheme.colorScheme.error,
    body = stringResource(R.string.fv_exit_dialog_body),
    buttons = {
      DialogButton(
        text = stringResource(R.string.fv_exit_dialog_cancel_button),
        onClick = { onEvent(ExitDialogEvent.OnClickCancelButton) },
        color = MaterialTheme.colorScheme.onSurface,
        isDismiss = true
      )
      DialogButton(
        text = stringResource(R.string.fv_exit_dialog_exit_button),
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
        foodItemList = List(32) {
          FoodItem(
            food = FoodUIModel(name = "${it}번째 음식"),
            status = if (it % 2 == 0) FoodItem.Status.DISLIKE else FoodItem.Status.LIKE
          )
        }
      )
    )
  }
}

@Preview
@Composable
private fun FinishVote() {
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

@Preview
@Composable
private fun InformationDialogPreview() {
  LunchVoteTheme {
    InformationDialog(
      templateList = listOf(
        TemplateUIModel(name = "템플릿1"),
        TemplateUIModel(name = "템플릿2"),
        TemplateUIModel(name = "템플릿3")
      ),
      template = TemplateUIModel(name = "템플릿1")
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