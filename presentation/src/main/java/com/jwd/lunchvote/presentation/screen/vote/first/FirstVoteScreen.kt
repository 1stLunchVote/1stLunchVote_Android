package com.jwd.lunchvote.presentation.screen.vote.first

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.model.FoodItem
import com.jwd.lunchvote.presentation.model.FoodItem.Status.DISLIKE
import com.jwd.lunchvote.presentation.model.FoodItem.Status.LIKE
import com.jwd.lunchvote.presentation.model.FoodUIModel
import com.jwd.lunchvote.presentation.model.MemberUIModel
import com.jwd.lunchvote.presentation.model.TemplateUIModel
import com.jwd.lunchvote.presentation.screen.template.add_template.AddTemplateContract.AddTemplateEvent
import com.jwd.lunchvote.presentation.screen.vote.first.FirstVoteContract.FirstVoteDialog
import com.jwd.lunchvote.presentation.screen.vote.first.FirstVoteContract.FirstVoteEvent
import com.jwd.lunchvote.presentation.screen.vote.first.FirstVoteContract.FirstVoteSideEffect
import com.jwd.lunchvote.presentation.screen.vote.first.FirstVoteContract.FirstVoteState
import com.jwd.lunchvote.presentation.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.util.LocalSnackbarChannel
import com.jwd.lunchvote.presentation.widget.FoodGrid
import com.jwd.lunchvote.presentation.widget.FoodItem
import com.jwd.lunchvote.presentation.widget.Gap
import com.jwd.lunchvote.presentation.widget.HorizontalProgressBar
import com.jwd.lunchvote.presentation.widget.LikeDislike
import com.jwd.lunchvote.presentation.widget.LoadingScreen
import com.jwd.lunchvote.presentation.widget.LunchVoteDialog
import com.jwd.lunchvote.presentation.widget.LunchVoteTextField
import com.jwd.lunchvote.presentation.widget.LunchVoteTopBar
import com.jwd.lunchvote.presentation.widget.MemberProgress
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import com.jwd.lunchvote.presentation.widget.SearchIcon
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
  val dialog by viewModel.dialogState.collectAsStateWithLifecycle()

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

  when (dialog) {
    is FirstVoteDialog.ExitDialog -> ExitDialog(
      onDismissRequest = { viewModel.sendEvent(FirstVoteEvent.OnClickCancelButtonInExitDialog) },
      onConfirmation = { viewModel.sendEvent(FirstVoteEvent.OnClickConfirmButtonInExitDialog) }
    )
    is FirstVoteDialog.SelectTemplateDialog -> SelectTemplateDialog(
      templateList = (dialog as FirstVoteDialog.SelectTemplateDialog).templateList,
      template = state.template,
      onDismissRequest = { viewModel.sendEvent(FirstVoteEvent.OnClickCancelButtonInSelectTemplateDialog) },
      onTemplateChange = { viewModel.sendEvent(FirstVoteEvent.OnTemplateChangeInSelectTemplateDialog(it)) },
      onConfirmation = { viewModel.sendEvent(FirstVoteEvent.OnClickApplyButtonInSelectTemplateDialog) }
    )
    null -> Unit
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
      LunchVoteTopBar(
        title = stringResource(R.string.first_vote_title),
        navIconVisible = false
      )
      if (state.lounge.timeLimit != null) {
        key(state.lounge.timeLimit) {
          HorizontalProgressBar(
            timeLimitSecond = state.lounge.timeLimit,
            modifier = Modifier.fillMaxWidth(),
            onProgressComplete = { onEvent(FirstVoteEvent.OnVoteFinish) }
          )
        }
      }
    },
    actions = {
      if (state.finished) {
        FloatingActionButton(
          onClick = { onEvent(FirstVoteEvent.OnClickReVoteButton) },
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
          Text(
            text = stringResource(R.string.first_vote_re_vote_button),
            modifier = Modifier.padding(horizontal = 24.dp)
          )
        }
      } else {
        if (state.foodItemList.count { it.status == LIKE } >= (state.lounge.minLikeFoods ?: 0)
          && state.foodItemList.count { it.status == DISLIKE } >= (state.lounge.minDislikeFoods ?: 0)) {
          FloatingActionButton(
            onClick = { onEvent(FirstVoteEvent.OnClickFinishButton) },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
          ) {
            Text(
              text = stringResource(R.string.first_vote_finish_button),
              modifier = Modifier.padding(horizontal = 24.dp)
            )
          }
        }
      }
    },
    scrollable = false
  ) {
    if (state.finished) {
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
    } else {
      InformationRow(
        like = state.foodItemList.count { it.status == LIKE },
        dislike = state.foodItemList.count { it.status == DISLIKE },
        memberList = state.memberList,
        modifier = Modifier.fillMaxWidth()
      )
      FoodGrid(
        searchKeyword = state.searchKeyword,
        filteredFoodList = state.foodItemList.filter { it.food.name.contains(state.searchKeyword) },
        onSearchKeywordChange = { onEvent(FirstVoteEvent.OnSearchKeywordChange(it)) },
        onClickFoodItem = { onEvent(FirstVoteEvent.OnClickFoodItem(it)) },
      )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectTemplateDialog(
  templateList: List<TemplateUIModel>,
  template: TemplateUIModel?,
  modifier: Modifier = Modifier,
  onDismissRequest: () -> Unit = {},
  onTemplateChange: (TemplateUIModel) -> Unit = {},
  onConfirmation: () -> Unit = {}
) {
  LunchVoteDialog(
    title = stringResource(R.string.select_template_dialog_title),
    dismissText = stringResource(R.string.select_template_dialog_dismiss_button),
    onDismissRequest = onDismissRequest,
    confirmText = stringResource(R.string.select_template_dialog_confirm_button),
    onConfirmation = onConfirmation,
    confirmEnabled = template != null,
    modifier = modifier
  ) {
    Text(text = stringResource(R.string.select_template_dialog_body))
    Gap(height = 16.dp)

    var expended by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
      expanded = expended,
      onExpandedChange = { expended = it },
    ) {
      if (templateList.isEmpty()) {
        OutlinedTextField(
          value = stringResource(R.string.select_template_dialog_no_template_text),
          onValueChange = { },
          modifier = Modifier
            .fillMaxWidth()
            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
          readOnly = true,
          trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expended) },
          singleLine = true
        )
      } else {
        OutlinedTextField(
          value = template?.name ?: stringResource(R.string.select_template_dialog_hint_text),
          onValueChange = { },
          modifier = Modifier
            .fillMaxWidth()
            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
          readOnly = true,
          trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expended) },
          singleLine = true
        )
        ExposedDropdownMenu(
          expanded = expended,
          onDismissRequest = { expended = false }
        ) {
          templateList.forEach { template ->
            DropdownMenuItem(
              text = { Text(text = template.name) },
              onClick = {
                onTemplateChange(template)
                expended = false
              }
            )
          }
        }
      }
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

@Preview
@Composable
private fun SelectTemplateDialogPreview() {
  LunchVoteTheme {
    SelectTemplateDialog(
      templateList = listOf(
        TemplateUIModel(name = "템플릿1"),
        TemplateUIModel(name = "템플릿2"),
        TemplateUIModel(name = "템플릿3")
      ), template = null
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