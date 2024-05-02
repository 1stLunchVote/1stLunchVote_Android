package com.jwd.lunchvote.presentation.ui.setting

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.presentation.ui.setting.SettingContract.SettingEvent
import com.jwd.lunchvote.presentation.ui.setting.SettingContract.SettingSideEffect
import com.jwd.lunchvote.presentation.ui.setting.SettingContract.SettingState
import com.jwd.lunchvote.presentation.util.clickableWithoutEffect
import com.jwd.lunchvote.presentation.widget.LunchVoteTopBar
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SettingRoute(
  popBackStack: () -> Unit,
  navigateToLogin: () -> Unit,
  showSnackBar: suspend (String) -> Unit,
  viewModel: SettingViewModel = hiltViewModel(),
  context: Context = LocalContext.current
) {
  val state by viewModel.viewState.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel.sideEffect) {
    viewModel.sideEffect.collectLatest {
      when(it) {
        is SettingSideEffect.PopBackStack -> popBackStack()
        is SettingSideEffect.ShowSnackBar -> showSnackBar(it.message.asString(context))
      }
    }
  }

  LaunchedEffect(Unit) {
    val appVersion = try {
      val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        context.packageManager.getPackageInfo(context.packageName, PackageManager.PackageInfoFlags.of(0))
      } else {
        context.packageManager.getPackageInfo(context.packageName, 0)
      }
      packageInfo.versionName
    } catch (e: PackageManager.NameNotFoundException) {
      "버전 정보를 가져올 수 없습니다."
    }

    viewModel.handleEvents(SettingEvent.ScreenInitialize(appVersion))
  }

  SettingScreen(
    state = state,
    onClickBackButton = { viewModel.handleEvents(SettingEvent.OnClickBackButton) },
    onClickEditProfileButton = { viewModel.handleEvents(SettingEvent.OnClickEditProfileButton) },
    onClickAlertSettingButton = { viewModel.handleEvents(SettingEvent.OnClickAlertSettingButton) },
    onClickContactButton = { viewModel.handleEvents(SettingEvent.OnClickContactButton) },
    onClickNoticeButton = { viewModel.handleEvents(SettingEvent.OnClickNoticeButton) },
    onClickSuggestButton = { viewModel.handleEvents(SettingEvent.OnClickSuggestButton) },
    onClickLogoutButton = { viewModel.handleEvents(SettingEvent.OnClickLogoutButton) }
  )
}

@Composable
private fun SettingScreen(
  state: SettingState,
  modifier: Modifier = Modifier,
  onClickBackButton: () -> Unit = {},
  onClickEditProfileButton: () -> Unit = {},
  onClickAlertSettingButton: () -> Unit = {},
  onClickContactButton: () -> Unit = {},
  onClickNoticeButton: () -> Unit = {},
  onClickSuggestButton: () -> Unit = {},
  onClickLogoutButton: () -> Unit = {}
) {
  Screen(
    modifier = modifier,
    topAppBar = {
      LunchVoteTopBar(
        title = "설정",
        navIconVisible = true,
        popBackStack = onClickBackButton
      )
    }
  ) {
    SettingBlock(name = "내 정보") {
      SettingItem(name = "프로필 수정") { onClickEditProfileButton() }
    }
    SettingBlock(name = "서비스 이용") {
      SettingItem(name = "알림 설정") { onClickAlertSettingButton() }
      SettingItem(name = "1:1 문의") { onClickContactButton() }
    }
    SettingBlock(name = "서비스 이용") {
      SettingItem(name = "공지사항 및 이용약관") { onClickNoticeButton() }
      SettingItem(name = "개선 제안하기") { onClickSuggestButton() }
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "앱 버전",
          style = MaterialTheme.typography.titleMedium
        )
        Text(
          text = state.appVersion,
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.outline
        )
      }
    }
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 16.dp),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = "로그아웃",
        modifier = Modifier
          .clickableWithoutEffect(onClickLogoutButton)
          .padding(horizontal = 12.dp, vertical = 8.dp),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.error,
        textDecoration = TextDecoration.Underline
      )
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
  modifier: Modifier = Modifier,
  onClickItem: () -> Unit
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .clickableWithoutEffect(onClickItem)
      .padding(vertical = 12.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = name,
      style = MaterialTheme.typography.titleMedium
    )
    Icon(
      Icons.AutoMirrored.Rounded.KeyboardArrowRight,
      null
    )
  }
}

@Preview
@Composable
private fun Preview() {
  ScreenPreview {
    SettingScreen(
      SettingState(
        appVersion = "1.0.0"
      )
    )
  }
}