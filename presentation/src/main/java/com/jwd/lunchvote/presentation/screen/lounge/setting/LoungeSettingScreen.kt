package com.jwd.lunchvote.presentation.screen.lounge.setting

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.screen.lounge.setting.LoungeSettingContract.Companion.MAX_MEMBERS_DIALOG
import com.jwd.lunchvote.presentation.screen.lounge.setting.LoungeSettingContract.Companion.MIN_DISLIKE_FOODS_DIALOG
import com.jwd.lunchvote.presentation.screen.lounge.setting.LoungeSettingContract.Companion.MIN_LIKE_FOODS_DIALOG
import com.jwd.lunchvote.presentation.screen.lounge.setting.LoungeSettingContract.Companion.SECOND_VOTE_CANDIDATES_DIALOG
import com.jwd.lunchvote.presentation.screen.lounge.setting.LoungeSettingContract.Companion.TIME_LIMIT_DIALOG
import com.jwd.lunchvote.presentation.screen.lounge.setting.LoungeSettingContract.LoungeSettingEvent
import com.jwd.lunchvote.presentation.screen.lounge.setting.LoungeSettingContract.LoungeSettingSideEffect
import com.jwd.lunchvote.presentation.screen.lounge.setting.LoungeSettingContract.LoungeSettingState
import com.jwd.lunchvote.presentation.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.util.LocalSnackbarChannel
import com.jwd.lunchvote.presentation.util.clickableWithoutEffect
import com.jwd.lunchvote.presentation.widget.LunchVoteDialog
import com.jwd.lunchvote.presentation.widget.LunchVoteTopBar
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kr.co.inbody.config.config.VoteConfig

@Composable
fun LoungeSettingRoute(
  popBackStack: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: LoungeSettingViewModel = hiltViewModel(),
  snackbarChannel: Channel<String> = LocalSnackbarChannel.current,
  context: Context = LocalContext.current
) {
  val state by viewModel.viewState.collectAsStateWithLifecycle()
  val dialog by viewModel.dialogState.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel.sideEffect) {
    viewModel.sideEffect.collectLatest {
      when (it) {
        is LoungeSettingSideEffect.PopBackStack -> popBackStack()
        is LoungeSettingSideEffect.OpenTimeLimitDialog -> viewModel.openDialog(TIME_LIMIT_DIALOG)
        is LoungeSettingSideEffect.OpenMaxMembersDialog -> viewModel.openDialog(MAX_MEMBERS_DIALOG)
        is LoungeSettingSideEffect.OpenSecondVoteCandidatesDialog -> viewModel.openDialog(
          SECOND_VOTE_CANDIDATES_DIALOG
        )
        is LoungeSettingSideEffect.OpenMinLikeFoodsDialog -> viewModel.openDialog(
          MIN_LIKE_FOODS_DIALOG
        )
        is LoungeSettingSideEffect.OpenMinDislikeFoodsDialog -> viewModel.openDialog(
          MIN_DISLIKE_FOODS_DIALOG
        )
        is LoungeSettingSideEffect.CloseDialog -> viewModel.openDialog("")
        is LoungeSettingSideEffect.ShowSnackbar -> snackbarChannel.send(it.message.asString(context))
      }
    }
  }
  
  when (dialog) {
    TIME_LIMIT_DIALOG -> TimeLimitDialog(
      timeLimit = state.lounge.timeLimit,
      onDismissRequest = { viewModel.sendEvent(LoungeSettingEvent.OnClickCancelButtonDialog) },
      onConfirmation = { viewModel.sendEvent(LoungeSettingEvent.OnClickConfirmButtonDialog(it)) }
    )
    MAX_MEMBERS_DIALOG -> MaxMembersDialog(
      maxMembers = state.lounge.maxMembers,
      onDismissRequest = { viewModel.sendEvent(LoungeSettingEvent.OnClickCancelButtonDialog) },
      onConfirmation = { viewModel.sendEvent(LoungeSettingEvent.OnClickConfirmButtonDialog(it)) }
    )
    SECOND_VOTE_CANDIDATES_DIALOG -> SecondVoteCandidatesDialog(
      secondVoteCandidates = state.lounge.secondVoteCandidates,
      onDismissRequest = { viewModel.sendEvent(LoungeSettingEvent.OnClickCancelButtonDialog) },
      onConfirmation = { viewModel.sendEvent(LoungeSettingEvent.OnClickConfirmButtonDialog(it)) }
    )
    MIN_LIKE_FOODS_DIALOG -> MinLikeFoodsDialog(
      minLikeFoods = state.lounge.minLikeFoods ?: VoteConfig.DEFAULT_MIN_LIKE_FOODS,
      onDismissRequest = { viewModel.sendEvent(LoungeSettingEvent.OnClickCancelButtonDialog) },
      onConfirmation = { viewModel.sendEvent(LoungeSettingEvent.OnClickConfirmButtonDialog(it)) }
    )
    MIN_DISLIKE_FOODS_DIALOG -> MinDislikeFoodsDialog(
      minDislikeFoods = state.lounge.minDislikeFoods ?: VoteConfig.DEFAULT_MIN_DISLIKE_FOODS,
      onDismissRequest = { viewModel.sendEvent(LoungeSettingEvent.OnClickCancelButtonDialog) },
      onConfirmation = { viewModel.sendEvent(LoungeSettingEvent.OnClickConfirmButtonDialog(it)) }
    )
  }

  LaunchedEffect(Unit) { viewModel.sendEvent(LoungeSettingEvent.ScreenInitialize) }

  LoungeSettingScreen(
    state = state,
    modifier = modifier,
    onEvent = viewModel::sendEvent
  )
}

@Composable
private fun LoungeSettingScreen(
  state: LoungeSettingState,
  modifier: Modifier = Modifier,
  onEvent: (LoungeSettingEvent) -> Unit = {}
) {
  Screen(
    modifier = modifier,
    topAppBar = {
      LunchVoteTopBar(
        title = stringResource(R.string.lounge_setting_title),
        popBackStack = { onEvent(LoungeSettingEvent.OnClickBackButton) }
      )
    },
    scrollable = false
  ) {
    LazyColumn {
      item {
        SettingBlock(
          name = stringResource(R.string.lounge_setting_time_setting_title),
          modifier = Modifier.fillMaxWidth()
        ) {
          SettingItem(
            name = stringResource(R.string.lounge_setting_time_limit_title),
            description = stringResource(R.string.lounge_setting_time_limit_description),
            value = if (state.lounge.timeLimit != null) stringResource(R.string.lounge_setting_time_limit_body, state.lounge.timeLimit) else stringResource(R.string.lounge_setting_time_limit_body_unlimited),
            onClickItem = { if (state.isOwner) onEvent(LoungeSettingEvent.OnClickTimeLimitItem) }
          )
        }
      }
      item {
        SettingBlock(
          name = stringResource(R.string.lounge_setting_member_setting_title),
          modifier = Modifier.fillMaxWidth()
        ) {
          SettingItem(
            name = stringResource(R.string.lounge_setting_max_members_title),
            description = stringResource(R.string.lounge_setting_max_members_description),
            value = stringResource(R.string.lounge_setting_max_members_body, state.lounge.maxMembers),
            onClickItem = { if (state.isOwner) onEvent(LoungeSettingEvent.OnClickMaxMembersItem) }
          )
        }
      }
      item {
        SettingBlock(
          name = stringResource(R.string.lounge_setting_detail_setting_title),
          modifier = Modifier.fillMaxWidth()
        ) {
          SettingItem(
            name = stringResource(R.string.lounge_setting_second_vote_candidates_title),
            description = stringResource(R.string.lounge_setting_second_vote_candidates_description),
            value = stringResource(R.string.lounge_setting_second_vote_candidates_body, state.lounge.secondVoteCandidates),
            onClickItem = { if (state.isOwner) onEvent(LoungeSettingEvent.OnClickSecondVoteCandidatesItem) }
          )
          SettingItem(
            name = stringResource(R.string.lounge_setting_min_like_foods_title),
            description = stringResource(R.string.lounge_setting_min_like_foods_description),
            value = if (state.lounge.minLikeFoods != null) stringResource(R.string.lounge_setting_min_like_foods_body, state.lounge.minLikeFoods) else stringResource(R.string.hyphen),
            enabled = state.lounge.minLikeFoods != null,
            warningText = stringResource(R.string.lounge_setting_min_like_foods_warning),
            onClickItem = { if (state.isOwner) onEvent(LoungeSettingEvent.OnClickMinLikeFoodsItem) }
          )
          SettingItem(
            name = stringResource(R.string.lounge_setting_min_dislike_foods_title),
            description = stringResource(R.string.lounge_setting_min_dislike_foods_description),
            value = if (state.lounge.minDislikeFoods != null) stringResource(R.string.lounge_setting_min_dislike_foods_body, state.lounge.minDislikeFoods) else stringResource(R.string.hyphen),
            enabled = state.lounge.minDislikeFoods != null,
            warningText = stringResource(R.string.lounge_setting_min_dislike_foods_warning),
            onClickItem = { if (state.isOwner) onEvent(LoungeSettingEvent.OnClickMinDislikeFoodsItem) }
          )
        }
      }
    }
  }
}

@Composable
private fun SettingBlock(
  name: String,
  modifier: Modifier = Modifier,
  items: @Composable ColumnScope.() -> Unit
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 24.dp, vertical = 16.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    Text(
      text = name,
      style = MaterialTheme.typography.titleSmall,
      color = MaterialTheme.colorScheme.outline
    )
    items()
  }
  HorizontalDivider(thickness = 2.dp)
}

@Composable
private fun SettingItem(
  name: String,
  description: String,
  value: String,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  warningText: String? = null,
  onClickItem: () -> Unit
) {
  Column(
    modifier = modifier
      .clickableWithoutEffect(
        enabled = enabled, onClick = onClickItem
      )
      .padding(vertical = 12.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .let { if (enabled) it else it.alpha(0.75f) },
      horizontalArrangement = Arrangement.spacedBy(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(
        modifier = Modifier
          .weight(1f)
          .let { if (enabled) it else it.alpha(0.5f) },
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Text(
          text = name, style = MaterialTheme.typography.titleMedium
        )
        Text(
          text = description,
          color = MaterialTheme.colorScheme.outline,
          style = MaterialTheme.typography.labelSmall
        )
      }
      Text(
        text = if (enabled) value else stringResource(R.string.hyphen),
        color = MaterialTheme.colorScheme.outline,
        style = MaterialTheme.typography.bodyLarge
      )
    }
    if (enabled.not()) {
      Text(
        text = warningText ?: "",
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.labelSmall
      )
    }
  }
}

@Composable
private fun TimeLimitDialog(
  timeLimit: Int?,
  modifier: Modifier = Modifier,
  onDismissRequest: () -> Unit = {},
  onConfirmation: (Int?) -> Unit = {},
) {
  val timePreset = listOf(R.string.lounge_setting_time_limit_dialog_time_preset_0, R.string.lounge_setting_time_limit_dialog_time_preset_1, R.string.lounge_setting_time_limit_dialog_time_preset_2, R.string.lounge_setting_time_limit_dialog_time_preset_3, R.string.lounge_setting_time_limit_dialog_time_preset_4, R.string.lounge_setting_time_limit_dialog_time_preset_5, R.string.lounge_setting_time_limit_dialog_time_preset_6)
  var value by remember {
    mutableIntStateOf(
      when (timeLimit) {
        10 -> 0
        20 -> 1
        30 -> 2
        60 -> 3
        90 -> 4
        120 -> 5
        else -> 6
      }
    )
  }

  LunchVoteDialog(
    title = stringResource(R.string.lounge_setting_time_limit_title),
    modifier = modifier,
    dismissText = stringResource(R.string.lounge_setting_dialog_dismiss_text),
    onDismissRequest = onDismissRequest,
    confirmText = stringResource(R.string.lounge_setting_dialog_confirm_text),
    onConfirmation = {
       onConfirmation(
        when (value) {
          0 -> 10
          1 -> 20
          2 -> 30
          3 -> 60
          4 -> 90
          5 -> 120
          else -> null
        }
      )    }
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Text(
        text = stringResource(R.string.lounge_setting_time_limit_description),
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodySmall
      )
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
      ) {
        FilledIconButton(
          onClick = { value-- },
          enabled = value > 0
        ) {
          Text(
            text = stringResource(R.string.minus),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
          )
        }
        Text(
          text = stringResource(timePreset[value]),
          modifier = Modifier.weight(1f),
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.bodyLarge
        )
        FilledIconButton(
          onClick = { value++ },
          enabled = value < timePreset.size - 1
        ) {
          Text(
            text = stringResource(R.string.plus),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
          )
        }
      }
      if (value == 0) {
        Text(
          text = stringResource(R.string.lounge_setting_time_limit_dialog_warning),
          modifier = Modifier.fillMaxWidth(),
          color = MaterialTheme.colorScheme.error,
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.labelMedium
        )
      }
    }
  }
}

@Composable
private fun MaxMembersDialog(
  maxMembers: Int,
  modifier: Modifier = Modifier,
  onDismissRequest: () -> Unit = {},
  onConfirmation: (Int) -> Unit = {},
) {
  var value by remember { mutableIntStateOf(maxMembers) }

  LunchVoteDialog(
    title = stringResource(R.string.lounge_setting_max_members_title),
    modifier = modifier,
    dismissText = stringResource(R.string.lounge_setting_dialog_dismiss_text),
    onDismissRequest = onDismissRequest,
    confirmText = stringResource(R.string.lounge_setting_dialog_confirm_text),
    onConfirmation = { onConfirmation(value) }
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Text(
        text = stringResource(R.string.lounge_setting_max_members_description),
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodySmall
      )
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
      ) {
        FilledIconButton(
          onClick = { value-- },
          enabled = value > 2
        ) {
          Text(
            text = stringResource(R.string.minus),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
          )
        }
        Text(
          text = stringResource(R.string.lounge_setting_max_members_body, value),
          modifier = Modifier.weight(1f),
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.bodyLarge
        )
        FilledIconButton(
          onClick = { value++ },
          enabled = value < 6
        ) {
          Text(
            text = stringResource(R.string.plus),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
          )
        }
      }
    }
  }
}

@Composable
private fun SecondVoteCandidatesDialog(
  secondVoteCandidates: Int,
  modifier: Modifier = Modifier,
  onDismissRequest: () -> Unit = {},
  onConfirmation: (Int) -> Unit = {},
) {
  var value by remember { mutableIntStateOf(secondVoteCandidates) }

  LunchVoteDialog(
    title = stringResource(R.string.lounge_setting_second_vote_candidates_title),
    modifier = modifier,
    dismissText = stringResource(R.string.lounge_setting_dialog_dismiss_text),
    onDismissRequest = onDismissRequest,
    confirmText = stringResource(R.string.lounge_setting_dialog_confirm_text),
    onConfirmation = { onConfirmation(value) }
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Text(
        text = stringResource(R.string.lounge_setting_second_vote_candidates_description),
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodySmall
      )
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
      ) {
        FilledIconButton(
          onClick = { value-- },
          enabled = value > 2
        ) {
          Text(
            text = stringResource(R.string.minus),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
          )
        }
        Text(
          text = stringResource(R.string.lounge_setting_second_vote_candidates_body, value),
          modifier = Modifier.weight(1f),
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.bodyLarge
        )
        FilledIconButton(
          onClick = { value++ },
          enabled = value < 10
        ) {
          Text(
            text = stringResource(R.string.plus),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
          )
        }
      }
    }
  }
}

@Composable
private fun MinLikeFoodsDialog(
  minLikeFoods: Int,
  modifier: Modifier = Modifier,
  onDismissRequest: () -> Unit = {},
  onConfirmation: (Int) -> Unit = {},
) {
  var value by remember { mutableIntStateOf(minLikeFoods) }

  LunchVoteDialog(
    title = stringResource(R.string.lounge_setting_min_like_foods_title),
    modifier = modifier,
    dismissText = stringResource(R.string.lounge_setting_dialog_dismiss_text),
    onDismissRequest = onDismissRequest,
    confirmText = stringResource(R.string.lounge_setting_dialog_confirm_text),
    onConfirmation = { onConfirmation(value) }
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Text(
        text = stringResource(R.string.lounge_setting_min_like_foods_description),
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodySmall
      )
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
      ) {
        FilledIconButton(
          onClick = { value-- },
          enabled = value > 1
        ) {
          Text(
            text = stringResource(R.string.minus),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
          )
        }
        Text(
          text = stringResource(R.string.lounge_setting_min_like_foods_body, value),
          modifier = Modifier.weight(1f),
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.bodyLarge
        )
        FilledIconButton(
          onClick = { value++ },
          enabled = value < 5
        ) {
          Text(
            text = stringResource(R.string.plus),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
          )
        }
      }
    }
  }
}

@Composable
private fun MinDislikeFoodsDialog(
  minDislikeFoods: Int,
  modifier: Modifier = Modifier,
  onDismissRequest: () -> Unit = {},
  onConfirmation: (Int) -> Unit = {},
) {
  var value by remember { mutableIntStateOf(minDislikeFoods) }

  LunchVoteDialog(
    title = stringResource(R.string.lounge_setting_min_dislike_foods_title),
    modifier = modifier,
    dismissText = stringResource(R.string.lounge_setting_dialog_dismiss_text),
    onDismissRequest = onDismissRequest,
    confirmText = stringResource(R.string.lounge_setting_dialog_confirm_text),
    onConfirmation = { onConfirmation(value) }
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Text(
        text = stringResource(R.string.lounge_setting_min_dislike_foods_description),
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodySmall
      )
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
      ) {
        FilledIconButton(
          onClick = { value-- },
          enabled = value > 0
        ) {
          Text(
            text = stringResource(R.string.minus),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
          )
        }
        Text(
          text = stringResource(R.string.lounge_setting_min_dislike_foods_body, value),
          modifier = Modifier.weight(1f),
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.bodyLarge
        )
        FilledIconButton(
          onClick = { value++ },
          enabled = value < 5
        ) {
          Text(
            text = stringResource(R.string.plus),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
          )
        }
      }
    }
  }
}

@Preview
@Composable
private fun Preview() {
  ScreenPreview {
    LoungeSettingScreen(
      LoungeSettingState()
    )
  }
}

@Preview
@Composable
private fun TimeLimitDialogPreview() {
  LunchVoteTheme {
    TimeLimitDialog(
      timeLimit = 10
    )
  }
}

@Preview
@Composable
private fun MaxMembersDialogPreview() {
  LunchVoteTheme {
    MaxMembersDialog(
      maxMembers = 3
    )
  }
}

@Preview
@Composable
private fun SecondVoteCandidatesDialogPreview() {
  LunchVoteTheme {
    SecondVoteCandidatesDialog(
      secondVoteCandidates = 5
    )
  }
}

@Preview
@Composable
private fun MinLikeFoodsDialogPreview() {
  LunchVoteTheme {
    MinLikeFoodsDialog(
      minLikeFoods = 3
    )
  }
}

@Preview
@Composable
private fun MinDislikeFoodsDialogPreview() {
  LunchVoteTheme {
    MinDislikeFoodsDialog(
      minDislikeFoods = 2
    )
  }
}