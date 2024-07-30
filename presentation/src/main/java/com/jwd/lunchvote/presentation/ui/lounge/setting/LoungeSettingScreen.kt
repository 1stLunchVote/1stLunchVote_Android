package com.jwd.lunchvote.presentation.ui.lounge.setting

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.presentation.ui.lounge.setting.LoungeSettingContract.LoungeSettingEvent
import com.jwd.lunchvote.presentation.ui.lounge.setting.LoungeSettingContract.LoungeSettingSideEffect
import com.jwd.lunchvote.presentation.ui.lounge.setting.LoungeSettingContract.LoungeSettingState
import com.jwd.lunchvote.presentation.util.LocalSnackbarChannel
import com.jwd.lunchvote.presentation.util.clickableWithoutEffect
import com.jwd.lunchvote.presentation.widget.LunchVoteTopBar
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest

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
        is LoungeSettingSideEffect.OpenTimeLimitDialog -> viewModel.openDialog(LoungeSettingContract.TIME_LIMIT_DIALOG)
        is LoungeSettingSideEffect.OpenMaxMembersDialog -> viewModel.openDialog(LoungeSettingContract.MAX_MEMBERS_DIALOG)
        is LoungeSettingSideEffect.OpenSecondVoteCandidatesDialog -> viewModel.openDialog(LoungeSettingContract.SECOND_VOTE_CANDIDATES_DIALOG)
        is LoungeSettingSideEffect.OpenMinLikeFoodsDialog -> viewModel.openDialog(LoungeSettingContract.MIN_LIKE_FOODS_DIALOG)
        is LoungeSettingSideEffect.OpenMinDislikeFoodsDialog -> viewModel.openDialog(LoungeSettingContract.MIN_DISLIKE_FOODS_DIALOG)
        is LoungeSettingSideEffect.CloseDialog -> viewModel.openDialog("")
        is LoungeSettingSideEffect.ShowSnackbar -> snackbarChannel.send(it.message.asString(context))
      }
    }
  }
  
  when (dialog) {
    LoungeSettingContract.TIME_LIMIT_DIALOG -> {

    }
    LoungeSettingContract.MAX_MEMBERS_DIALOG -> {

    }
    LoungeSettingContract.SECOND_VOTE_CANDIDATES_DIALOG -> {

    }
    LoungeSettingContract.MIN_LIKE_FOODS_DIALOG -> {

    }
    LoungeSettingContract.MIN_DISLIKE_FOODS_DIALOG -> {

    }
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
        title = "투표 설정",
        popBackStack = { onEvent(LoungeSettingEvent.OnClickBackButton) }
      )
    },
    scrollable = false
  ) {
    LazyColumn {
      item {
        SettingBlock(
          name = "시간 설정",
          modifier = Modifier.fillMaxWidth()
        ) {
          SettingItem(
            name = "투표 제한 시간",
            description = "1차, 2차 투표 각각의 제한 시간을 설정합니다.\n *최소 10초, 최대 2분, 무제한 가능*",
            value = "60초",
            onClickItem = {}
          )
        }
      }
      item {
        SettingBlock(
          name = "인원 설정",
          modifier = Modifier.fillMaxWidth()
        ) {
          SettingItem(
            name = "투표 최대 인원 수",
            description = "투표에 참여할 최대 인원 수를 설정합니다.\n *최소 1명, 최대 6명*",
            value = "6명",
            onClickItem = {}
          )
        }
      }
      item {
        SettingBlock(
          name = "상세 설정",
          modifier = Modifier.fillMaxWidth()
        ) {
          SettingItem(
            name = "2차 투표 후보 수",
            description = "2차 투표에 후보로 등장할 음식의 개수를 설정합니다.\n *최소 2개, 최대 10개*",
            value = "5개",
            onClickItem = {}
          )
          SettingItem(
            name = "최소 선호 음식 수",
            description = "다음 투표로 넘어가기 위해 투표해야하는 선호 음식의 개수를 설정합니다.\n *최소 1개, 최대 5개*",
            value = "5개",
            enabled = false,
            warningText = "투표 제한 시간이 무제한이어야 합니다.",
            onClickItem = {}
          )
          SettingItem(
            name = "최소 비선호 음식 수",
            description = "다음 투표로 넘어가기 위해 투표해야하는 비선호 음식의 개수를 설정합니다.\n *최소 0개, 최대 5개*",
            value = "5개",
            enabled = false,
            warningText = "투표 제한 시간이 무제한이어야 합니다.",
            onClickItem = {}
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
        text = if (enabled) value else "-",
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

@Preview
@Composable
private fun Preview() {
  ScreenPreview {
    LoungeSettingScreen(
      LoungeSettingState()
    )
  }
}