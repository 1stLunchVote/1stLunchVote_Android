package com.jwd.lunchvote.presentation.screen.lounge.setting

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.screen.lounge.setting.LoungeSettingContract.LoungeSettingEvent
import com.jwd.lunchvote.presentation.screen.lounge.setting.LoungeSettingContract.LoungeSettingSideEffect
import com.jwd.lunchvote.presentation.screen.lounge.setting.LoungeSettingContract.LoungeSettingState
import com.jwd.lunchvote.presentation.screen.lounge.setting.LoungeSettingContract.MaxMembersDialogEvent
import com.jwd.lunchvote.presentation.screen.lounge.setting.LoungeSettingContract.MinDislikeFoodsDialogEvent
import com.jwd.lunchvote.presentation.screen.lounge.setting.LoungeSettingContract.MinLikeFoodsDialogEvent
import com.jwd.lunchvote.presentation.screen.lounge.setting.LoungeSettingContract.SecondVoteCandidatesDialogEvent
import com.jwd.lunchvote.presentation.screen.lounge.setting.LoungeSettingContract.TimeLimitDialogEvent
import com.jwd.lunchvote.presentation.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.util.LocalSnackbarChannel
import com.jwd.lunchvote.presentation.util.clickableWithoutEffect
import com.jwd.lunchvote.presentation.util.conditional
import com.jwd.lunchvote.presentation.widget.Dialog
import com.jwd.lunchvote.presentation.widget.DialogButton
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import com.jwd.lunchvote.presentation.widget.TopBar
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

  LaunchedEffect(viewModel.sideEffect) {
    viewModel.sideEffect.collectLatest {
      when (it) {
        is LoungeSettingSideEffect.PopBackStack -> popBackStack()
        is LoungeSettingSideEffect.ShowSnackbar -> snackbarChannel.send(it.message.asString(context))
      }
    }
  }

  LaunchedEffect(Unit) { viewModel.sendEvent(LoungeSettingEvent.ScreenInitialize) }

  state.timeLimitDialogState?.let { dialogState ->
    TimeLimitDialog(
      timeLimit = dialogState.timeLimit,
      initialValue = state.lounge.timeLimit,
      onEvent = viewModel::sendEvent
    )
  }
  state.maxMembersDialogState?.let { dialogState ->
    MaxMembersDialog(
      maxMembers = dialogState.maxMembers,
      initialValue = state.lounge.maxMembers,
      onEvent = viewModel::sendEvent
    )
  }
  state.secondVoteCandidatesDialogState?.let { dialogState ->
    SecondVoteCandidatesDialog(
      secondVoteCandidates = dialogState.secondVoteCandidates,
      initialValue = state.lounge.secondVoteCandidates,
      onEvent = viewModel::sendEvent
    )
  }
  state.minLikeFoodsDialogState?.let { dialogState ->
    MinLikeFoodsDialog(
      minLikeFoods = dialogState.minLikeFoods ?: VoteConfig.DEFAULT_MIN_LIKE_FOODS,
      initialValue = state.lounge.minLikeFoods ?: VoteConfig.DEFAULT_MIN_LIKE_FOODS,
      onEvent = viewModel::sendEvent
    )
  }
  state.minDislikeFoodsDialogState?.let { dialogState ->
    MinDislikeFoodsDialog(
      minDislikeFoods = dialogState.minDislikeFoods ?: VoteConfig.DEFAULT_MIN_DISLIKE_FOODS,
      initialValue = state.lounge.minDislikeFoods ?: VoteConfig.DEFAULT_MIN_DISLIKE_FOODS,
      onEvent = viewModel::sendEvent
    )
  }

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
      TopBar(
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
        .conditional(enabled.not()) { alpha(0.75f) },
      horizontalArrangement = Arrangement.spacedBy(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(
        modifier = Modifier
          .weight(1f)
          .conditional(enabled.not()) { alpha(0.5f) },
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
        color = MaterialTheme.colorScheme.outline
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
  initialValue: Int?,
  modifier: Modifier = Modifier,
  onEvent: (TimeLimitDialogEvent) -> Unit = {}
) {
  Dialog(
    title = stringResource(R.string.lounge_setting_time_limit_title),
    onDismissRequest = { onEvent(TimeLimitDialogEvent.OnClickCancelButton) },
    modifier = modifier,
    icon = {
      Icon(
        imageVector = Icons.Rounded.Build,
        contentDescription = "Build"
      )
    },
    iconColor = MaterialTheme.colorScheme.tertiary,
    body = stringResource(R.string.lounge_setting_time_limit_description),
    canDismiss = false,
    content = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterHorizontally),
          verticalAlignment = Alignment.CenterVertically
        ) {
          FilledIconButton(
            onClick = { onEvent(TimeLimitDialogEvent.OnClickDecreaseButton) },
            enabled = timeLimit != 10
          ) {
            Text(
              text = stringResource(R.string.minus),
              style = MaterialTheme.typography.titleSmall
            )
          }
          Text(
            text = stringResource(
              when (timeLimit) {
                10 -> R.string.ls_time_limit_dialog_time_preset_10sec
                20 -> R.string.ls_time_limit_dialog_time_preset_20sec
                30 -> R.string.ls_time_limit_dialog_time_preset_30sec
                60 -> R.string.ls_time_limit_dialog_time_preset_60sec
                90 -> R.string.ls_time_limit_dialog_time_preset_90sec
                120 -> R.string.ls_time_limit_dialog_time_preset_120sec
                else -> R.string.ls_time_limit_dialog_time_preset_unlimited
              }
            ),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
          )
          FilledIconButton(
            onClick = { onEvent(TimeLimitDialogEvent.OnClickIncreaseButton) },
            enabled = timeLimit != null
          ) {
            Text(
              text = stringResource(R.string.plus),
              style = MaterialTheme.typography.titleSmall
            )
          }
        }
        if (timeLimit == 10) {
          Text(
            text = stringResource(R.string.ls_time_limit_dialog_warning),
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelMedium
          )
        }
      }
    },
    buttons = {
      DialogButton(
        text = stringResource(R.string.ls_dialog_cancel_text),
        onClick = { onEvent(TimeLimitDialogEvent.OnClickCancelButton) },
        isDismiss = true
      )
      DialogButton(
        text = stringResource(R.string.ls_dialog_apply_text),
        onClick = { onEvent(TimeLimitDialogEvent.OnClickConfirmButton) },
        enabled = timeLimit != initialValue
      )
    }
  )
}

@Composable
private fun MaxMembersDialog(
  maxMembers: Int,
  initialValue: Int,
  modifier: Modifier = Modifier,
  range: IntRange = 2..6,
  onEvent: (MaxMembersDialogEvent) -> Unit = {}
) {
  Dialog(
    title = stringResource(R.string.lounge_setting_max_members_title),
    onDismissRequest = { onEvent(MaxMembersDialogEvent.OnClickCancelButton) },
    modifier = modifier,
    icon = {
      Icon(
        imageVector = Icons.Rounded.Build,
        contentDescription = "Build"
      )
    },
    iconColor = MaterialTheme.colorScheme.tertiary,
    body = stringResource(R.string.lounge_setting_max_members_description),
    canDismiss = false,
    content = {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
      ) {
        FilledIconButton(
          onClick = { onEvent(MaxMembersDialogEvent.OnClickDecreaseButton) },
          enabled = maxMembers > range.first
        ) {
          Text(
            text = stringResource(R.string.minus),
            style = MaterialTheme.typography.titleSmall
          )
        }
        Text(
          text = stringResource(R.string.lounge_setting_max_members_body, maxMembers),
          modifier = Modifier.weight(1f),
          textAlign = TextAlign.Center
        )
        FilledIconButton(
          onClick = { onEvent(MaxMembersDialogEvent.OnClickIncreaseButton) },
          enabled = maxMembers < range.last
        ) {
          Text(
            text = stringResource(R.string.plus),
            style = MaterialTheme.typography.titleSmall
          )
        }
      }
    },
    buttons = {
      DialogButton(
        text = stringResource(R.string.ls_dialog_cancel_text),
        onClick = { onEvent(MaxMembersDialogEvent.OnClickCancelButton) },
        isDismiss = true
      )
      DialogButton(
        text = stringResource(R.string.ls_dialog_apply_text),
        onClick = { onEvent(MaxMembersDialogEvent.OnClickConfirmButton) },
        enabled = maxMembers != initialValue
      )
    }
  )
}

@Composable
private fun SecondVoteCandidatesDialog(
  secondVoteCandidates: Int,
  initialValue: Int,
  modifier: Modifier = Modifier,
  range: IntRange = 2..10,
  onEvent: (SecondVoteCandidatesDialogEvent) -> Unit = {}
) {
  Dialog(
    title = stringResource(R.string.lounge_setting_second_vote_candidates_title),
    onDismissRequest = { onEvent(SecondVoteCandidatesDialogEvent.OnClickCancelButton) },
    modifier = modifier,
    icon = {
      Icon(
        imageVector = Icons.Rounded.Build,
        contentDescription = "Build"
      )
    },
    iconColor = MaterialTheme.colorScheme.tertiary,
    body = stringResource(R.string.lounge_setting_second_vote_candidates_description),
    canDismiss = false,
    content = {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
      ) {
        FilledIconButton(
          onClick = { onEvent(SecondVoteCandidatesDialogEvent.OnClickDecreaseButton) },
          enabled = secondVoteCandidates > range.first
        ) {
          Text(
            text = stringResource(R.string.minus),
            style = MaterialTheme.typography.titleSmall
          )
        }
        Text(
          text = stringResource(R.string.lounge_setting_second_vote_candidates_body, secondVoteCandidates),
          modifier = Modifier.weight(1f),
          textAlign = TextAlign.Center
        )
        FilledIconButton(
          onClick = { onEvent(SecondVoteCandidatesDialogEvent.OnClickIncreaseButton) },
          enabled = secondVoteCandidates < range.last
        ) {
          Text(
            text = stringResource(R.string.plus),
            style = MaterialTheme.typography.titleSmall
          )
        }
      }
    },
    buttons = {
      DialogButton(
        text = stringResource(R.string.ls_dialog_cancel_text),
        onClick = { onEvent(SecondVoteCandidatesDialogEvent.OnClickCancelButton) },
        isDismiss = true
      )
      DialogButton(
        text = stringResource(R.string.ls_dialog_apply_text),
        onClick = { onEvent(SecondVoteCandidatesDialogEvent.OnClickConfirmButton) },
        enabled = secondVoteCandidates != initialValue
      )
    }
  )
}

@Composable
private fun MinLikeFoodsDialog(
  minLikeFoods: Int,
  initialValue: Int,
  modifier: Modifier = Modifier,
  range: IntRange = 1..5,
  onEvent: (MinLikeFoodsDialogEvent) -> Unit = {}
) {
  Dialog(
    title = stringResource(R.string.lounge_setting_min_like_foods_title),
    onDismissRequest = { onEvent(MinLikeFoodsDialogEvent.OnClickCancelButton) },
    modifier = modifier,
    icon = {
      Icon(
        imageVector = Icons.Rounded.Build,
        contentDescription = "Build"
      )
    },
    iconColor = MaterialTheme.colorScheme.tertiary,
    body = stringResource(R.string.lounge_setting_min_like_foods_description),
    canDismiss = false,
    content = {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
      ) {
        FilledIconButton(
          onClick = { onEvent(MinLikeFoodsDialogEvent.OnClickDecreaseButton) },
          enabled = minLikeFoods > range.first
        ) {
          Text(
            text = stringResource(R.string.minus),
            style = MaterialTheme.typography.titleSmall
          )
        }
        Text(
          text = stringResource(R.string.lounge_setting_min_like_foods_body, minLikeFoods),
          modifier = Modifier.weight(1f),
          textAlign = TextAlign.Center
        )
        FilledIconButton(
          onClick = { onEvent(MinLikeFoodsDialogEvent.OnClickIncreaseButton) },
          enabled = minLikeFoods < range.last
        ) {
          Text(
            text = stringResource(R.string.plus),
            style = MaterialTheme.typography.titleSmall
          )
        }
      }
    },
    buttons = {
      DialogButton(
        text = stringResource(R.string.ls_dialog_cancel_text),
        onClick = { onEvent(MinLikeFoodsDialogEvent.OnClickCancelButton) },
        isDismiss = true
      )
      DialogButton(
        text = stringResource(R.string.ls_dialog_apply_text),
        onClick = { onEvent(MinLikeFoodsDialogEvent.OnClickConfirmButton) },
        enabled = minLikeFoods != initialValue
      )
    }
  )
}

@Composable
private fun MinDislikeFoodsDialog(
  minDislikeFoods: Int,
  initialValue: Int,
  modifier: Modifier = Modifier,
  range: IntRange = 0..5,
  onEvent: (MinDislikeFoodsDialogEvent) -> Unit = {}
) {
  Dialog(
    title = stringResource(R.string.lounge_setting_min_dislike_foods_title),
    onDismissRequest = { onEvent(MinDislikeFoodsDialogEvent.OnClickCancelButton) },
    modifier = modifier,
    icon = {
      Icon(
        imageVector = Icons.Rounded.Build,
        contentDescription = "Build"
      )
    },
    iconColor = MaterialTheme.colorScheme.tertiary,
    body = stringResource(R.string.lounge_setting_min_dislike_foods_description),
    canDismiss = false,
    content = {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
      ) {
        FilledIconButton(
          onClick = { onEvent(MinDislikeFoodsDialogEvent.OnClickDecreaseButton) },
          enabled = minDislikeFoods > range.first
        ) {
          Text(
            text = stringResource(R.string.minus),
            style = MaterialTheme.typography.titleSmall
          )
        }
        Text(
          text = stringResource(R.string.lounge_setting_min_dislike_foods_body, minDislikeFoods),
          modifier = Modifier.weight(1f),
          textAlign = TextAlign.Center
        )
        FilledIconButton(
          onClick = { onEvent(MinDislikeFoodsDialogEvent.OnClickIncreaseButton) },
          enabled = minDislikeFoods < range.last
        ) {
          Text(
            text = stringResource(R.string.plus),
            style = MaterialTheme.typography.titleSmall
          )
        }
      }
    },
    buttons = {
      DialogButton(
        text = stringResource(R.string.ls_dialog_cancel_text),
        onClick = { onEvent(MinDislikeFoodsDialogEvent.OnClickCancelButton) },
        isDismiss = true
      )
      DialogButton(
        text = stringResource(R.string.ls_dialog_apply_text),
        onClick = { onEvent(MinDislikeFoodsDialogEvent.OnClickConfirmButton) },
        enabled = minDislikeFoods != initialValue
      )
    }
  )
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
      timeLimit = 10,
      initialValue = VoteConfig.DEFAULT_TIME_LIMIT
    )
  }
}

@Preview
@Composable
private fun MaxMembersDialogPreview() {
  LunchVoteTheme {
    MaxMembersDialog(
      maxMembers = 3,
      initialValue = VoteConfig.DEFAULT_MAX_MEMBERS
    )
  }
}

@Preview
@Composable
private fun SecondVoteCandidatesDialogPreview() {
  LunchVoteTheme {
    SecondVoteCandidatesDialog(
      secondVoteCandidates = 5,
      initialValue = VoteConfig.DEFAULT_SECOND_VOTE_CANDIDATES
    )
  }
}

@Preview
@Composable
private fun MinLikeFoodsDialogPreview() {
  LunchVoteTheme {
    MinLikeFoodsDialog(
      minLikeFoods = 3,
      initialValue = VoteConfig.DEFAULT_MIN_LIKE_FOODS
    )
  }
}

@Preview
@Composable
private fun MinDislikeFoodsDialogPreview() {
  LunchVoteTheme {
    MinDislikeFoodsDialog(
      minDislikeFoods = 2,
      initialValue = VoteConfig.DEFAULT_MIN_DISLIKE_FOODS
    )
  }
}