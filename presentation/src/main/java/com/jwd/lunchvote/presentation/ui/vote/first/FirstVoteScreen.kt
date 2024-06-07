package com.jwd.lunchvote.presentation.ui.vote.first

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.model.FoodStatus
import com.jwd.lunchvote.presentation.model.MemberUIModel
import com.jwd.lunchvote.presentation.model.TemplateUIModel
import com.jwd.lunchvote.presentation.ui.vote.first.FirstVoteContract.*
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
import com.jwd.lunchvote.presentation.widget.TextFieldType
import kotlinx.coroutines.flow.collectLatest

@Composable
fun FirstVoteRoute(
  popBackStack: () -> Unit,
  navigateToSecondVote: () -> Unit,
  showSnackBar: suspend (String) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: FirstVoteViewModel = hiltViewModel(),
  context: Context = LocalContext.current
) {
  val state by viewModel.viewState.collectAsStateWithLifecycle()
  val dialog by viewModel.dialogState.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel.sideEffect) {
    viewModel.sideEffect.collectLatest {
      when(it) {
        is FirstVoteSideEffect.PopBackStack -> popBackStack()
        is FirstVoteSideEffect.NavigateToSecondVote -> navigateToSecondVote()
        is FirstVoteSideEffect.ShowSnackBar -> showSnackBar(it.message.asString(context))
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

  if (state.calculating) LoadingScreen(message = stringResource(R.string.first_vote_calculating_message),)
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
        modifier = Modifier.fillMaxWidth(),
        onProgressComplete = { onEvent(FirstVoteEvent.OnVoteFinish) }
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
    title = "투표가 시작되었습니다!",
    dismissText = "건너뛰기",
    onDismissRequest = onDismissRequest,
    confirmText = "적용",
    onConfirmation = onConfirmation,
    confirmEnabled = template != null,
    modifier = modifier
  ) {
    Text(
      text = "음식을 터치하여 호불호를 선택할 수 있습니다.\n" +
        "좋아한다고 투표한 멤버당 1점이 추가되며\n" +
        "싫어한다고 투표한 멤버당 5점이 감점됩니다.\n\n" +
        "템플릿을 골라 빠르게 투표해보세요!"
    )
    Gap(height = 16.dp)

    var expended by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
      expanded = expended,
      onExpandedChange = { expended = it },
    ) {
      if (templateList.isEmpty()) {
        OutlinedTextField(
          value = "템플릿이 없습니다.",
          onValueChange = { },
          modifier = Modifier
            .fillMaxWidth()
            .menuAnchor(),
          readOnly = true,
          trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expended) },
          singleLine = true
        )
      } else {
        OutlinedTextField(
          value = template?.name ?: "템플릿을 선택해주세요",
          onValueChange = { },
          modifier = Modifier
            .fillMaxWidth()
            .menuAnchor(),
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

@Preview
@Composable
private fun SelectTemplateDialogPreview() {
  LunchVoteTheme {
    SelectTemplateDialog(
      templateList = listOf(
        TemplateUIModel(name = "템플릿1"),
        TemplateUIModel(name = "템플릿2"),
        TemplateUIModel(name = "템플릿3")
      ),
      template = null
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