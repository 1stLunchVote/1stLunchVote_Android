package com.jwd.lunchvote.presentation.screen.tips

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.screen.tips.TipsContract.TipsEvent
import com.jwd.lunchvote.presentation.screen.tips.TipsContract.TipsSideEffect
import com.jwd.lunchvote.presentation.screen.tips.TipsContract.TipsState
import com.jwd.lunchvote.presentation.util.LocalSnackbarChannel
import com.jwd.lunchvote.presentation.widget.LunchVoteTopBar
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun TipsRoute(
  popBackStack: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: TipsViewModel = hiltViewModel(),
  snackbarChannel: Channel<String> = LocalSnackbarChannel.current,
  context: Context = LocalContext.current
) {
  val state by viewModel.viewState.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel.sideEffect) {
    viewModel.sideEffect.collectLatest {
      when(it) {
        is TipsSideEffect.PopBackStack -> popBackStack()
        is TipsSideEffect.ShowSnackbar -> snackbarChannel.send(it.message.asString(context))
      }
    }
  }

  TipsScreen(
    state = state,
    modifier = modifier,
    onEvent = viewModel::sendEvent
  )
}

@Composable
private fun TipsScreen(
  state: TipsState,
  modifier: Modifier = Modifier,
  onEvent: (TipsEvent) -> Unit = {}
) {
  Screen(
    modifier = modifier,
    topAppBar = {
      LunchVoteTopBar(
        title = "게임 방법",
        popBackStack = { onEvent(TipsEvent.OnClickBackButton) }
      )
    },
    scrollable = false
  ) {
    TabRow(
      selectedTabIndex = state.tabIndex,
      modifier = Modifier.fillMaxWidth(),
      contentColor = MaterialTheme.colorScheme.onBackground
    ) {
      val tabs = listOf("투표 대기방", "1차 투표", "2차 투표")
      tabs.forEachIndexed { index, tab ->
        Tab(
          text = { Text(tab) },
          selected = state.tabIndex == index,
          onClick = { onEvent(TipsEvent.OnClickTab(index)) }
        )
      }
    }

    val tipsModifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState())
      .padding(24.dp)
      .weight(1f)

    when (state.tabIndex) {
      0 -> LoungeTips(modifier = tipsModifier)
      1 -> FirstVoteTips(modifier = tipsModifier)
      2 -> SecondVoteTips(modifier = tipsModifier)
    }
  }
}

@Composable
private fun LoungeTips(
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(8.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = "[방장]",
      style = MaterialTheme.typography.titleMedium
    )
    val loungeTipsOwner = listOf(
      "메인 메뉴에서 [투표 방 만들기] 버튼을 클릭하여 새로운 투표 방을 생성할 수 있습니다." to R.drawable.img_lounge_tip_1,
      "투표 대기방 상단의 아이콘은 현재 참여중인 멤버를 나타내며, [+] 버튼을 눌러 방 참여 링크를 복사할 수 있습니다." to R.drawable.img_lounge_tip_2,
      "복사한 링크를 함께 투표할 친구에게 보내주세요!" to null,
      "친구가 참여하기 시작하면, 나머지 친구들이 모두 참여할 때까지 채팅을 통해 대화를 나눌 수 있습니다." to R.drawable.img_lounge_tip_3,
      "모든 참여자가 준비완료되었을 경우, [시작] 버튼을 눌러 투표를 시작할 수 있습니다." to R.drawable.img_lounge_tip_4
    )
    loungeTipsOwner.forEachIndexed { index, tip ->
      Tip(
        index = index,
        text = tip.first,
        modifier = Modifier.fillMaxWidth(),
        imageResource = tip.second
      )
    }

    Text(
      text = "[참여자]",
      style = MaterialTheme.typography.titleMedium
    )
    val loungeTipsMember = listOf(
      "메인 메뉴에서 [투표 방 참여하기] 버튼을 클릭하여 투표 방에 참여할 수 있습니다." to R.drawable.img_lounge_tip_5,
      "방장에게 전송받은 참여 링크를 입력합니다." to R.drawable.img_lounge_tip_6,
      "투표 방에 참여하면, 나머지 친구들이 모두 참여할 때까지 채팅을 통해 대화를 나눌 수 있습니다." to R.drawable.img_lounge_tip_7,
      "투표를 시작할 준비가 되었다면, [준비] 버튼을 눌러 준비 완료 상태로 대기합니다." to R.drawable.img_lounge_tip_8
    )
    loungeTipsMember.forEachIndexed { index, tip ->
      Tip(
        index = index,
        text = tip.first,
        modifier = Modifier.fillMaxWidth(),
        imageResource = tip.second
      )
    }
  }
}

@Composable
private fun FirstVoteTips(
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(8.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    val firstVoteTips = listOf(
      "메뉴를 한 번 터치할 경우 좋아하는 메뉴로 설정할 수 있습니다." to R.drawable.img_first_vote_tip_1,
      "좋아하는 메뉴를 한 번 더 터치할 경우 싫어하는 메뉴로 설정할 수 있습니다. 싫어하는 메뉴는 최대 n개까지 선택할 수 있습니다." to R.drawable.img_first_vote_tip_2,
      "좋아하는 메뉴와 싫어하는 메뉴를 최소 1개씩 선택해야 투표를 완료할 수 있습니다." to R.drawable.img_first_vote_tip_3,
      "1분의 시간이 지나거나, 참여한 인원 전원이 투표했을 경우 1차 투표가 종료되며, 좋아하는 메뉴로 선택된 메뉴 중 아무도 싫어하는 메뉴로 설정하지 않은 메뉴가 2차 투표 대상으로 선정됩니다." to R.drawable.img_first_vote_tip_4
    )
    firstVoteTips.forEachIndexed { index, tip ->
      Tip(
        index = index,
        text = tip.first,
        modifier = Modifier.fillMaxWidth(),
        imageResource = tip.second
      )
    }
  }
}

@Composable
private fun SecondVoteTips(
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(8.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    val secondVoteTips = listOf(
      "1차 투표로 선정된 메뉴 중 가장 먹고 싶은 메뉴 1개를 골라 투표합니다." to R.drawable.img_second_vote_tip_1,
      "가장 많은 참여자가 고른 메뉴 1개가 선정됩니다. 만약 동률이 나올 경우, 참여자가 고른 메뉴 중 1개가 랜덤으로 선정됩니다." to R.drawable.img_second_vote_tip_2,
      "식사 맛있게 하세요!" to null
    )
    secondVoteTips.forEachIndexed { index, tip ->
      Tip(
        index = index,
        text = tip.first,
        modifier = Modifier.fillMaxWidth(),
        imageResource = tip.second
      )
    }
  }
}

@Composable
private fun Tip(
  index: Int,
  text: String,
  modifier: Modifier = Modifier,
  imageResource: Int? = null
) {
  Column(
    modifier = modifier
  ) {
    Row {
      Text(
        text = "${index + 1}.",
        modifier = Modifier.width(24.dp)
      )
      Text(text)
    }
    imageResource?.let {
      Image(
        painter = painterResource(it),
        contentDescription = "${index + 1} Tip",
        modifier = Modifier.fillMaxWidth(),
        contentScale = ContentScale.FillWidth
      )
    }
  }
}

@Preview
@Composable
private fun Preview0() {
  ScreenPreview {
    TipsScreen(
      TipsState(
        tabIndex = 0
      )
    )
  }
}

@Preview
@Composable
private fun Preview1() {
  ScreenPreview {
    TipsScreen(
      TipsState(
        tabIndex = 1
      )
    )
  }
}

@Preview
@Composable
private fun Preview2() {
  ScreenPreview {
    TipsScreen(
      TipsState(
        tabIndex = 2
      )
    )
  }
}