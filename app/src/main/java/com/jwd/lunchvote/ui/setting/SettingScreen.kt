package com.jwd.lunchvote.ui.setting

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.ui.setting.SettingContract.SettingEvent
import com.jwd.lunchvote.ui.setting.SettingContract.SettingSideEffect
import com.jwd.lunchvote.ui.setting.SettingContract.SettingState
import com.jwd.lunchvote.widget.LunchVoteTopBar
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SettingRoute(
  popBackStack: (String) -> Unit,
  viewModel: SettingViewModel = hiltViewModel()
) {
  val settingState by viewModel.viewState.collectAsStateWithLifecycle()
  val loading by viewModel.isLoading.collectAsStateWithLifecycle()
//  val settingDialogState by viewModel.dialogState.collectAsStateWithLifecycle()

  val snackBarHostState = remember { SnackbarHostState() }

  LaunchedEffect(viewModel.sideEffect) {
    viewModel.sideEffect.collectLatest {
      when(it) {
        is SettingSideEffect.PopBackStack -> popBackStack(it.message)
        is SettingSideEffect.ShowSnackBar -> snackBarHostState.showSnackbar(it.message)
      }
    }
  }

  if (loading) Dialog({}) { CircularProgressIndicator() }
  SettingScreen(
    snackBarHostState = snackBarHostState,
    settingState = settingState,
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
fun SettingScreen(
  snackBarHostState: SnackbarHostState,
  settingState: SettingState,
  onClickBackButton: () -> Unit = {},
  onClickEditProfileButton: () -> Unit = {},
  onClickAlertSettingButton: () -> Unit = {},
  onClickContactButton: () -> Unit = {},
  onClickNoticeButton: () -> Unit = {},
  onClickSuggestButton: () -> Unit = {},
  onClickLogoutButton: () -> Unit = {},
  context: Context = LocalContext.current
) {
  Scaffold(
    snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      LunchVoteTopBar(
        title = "설정",
        navIconVisible = true,
        popBackStack = onClickBackButton
      )
      SettingBlock(
        name = "내 정보"
      ) {
        SettingItem(
          name = "프로필 수정",
          onClickItem = onClickEditProfileButton
        )
      }
      SettingBlock(
        name = "서비스 이용"
      ) {
        SettingItem(
          name = "알림 설정",
          onClickItem = onClickAlertSettingButton
        )
        SettingItem(
          name = "1:1 문의",
          onClickItem = onClickContactButton
        )
      }
      SettingBlock(
        name = "서비스 이용"
      ) {
        SettingItem(
          name = "공지사항 및 이용약관",
          onClickItem = onClickNoticeButton
        )
        SettingItem(
          name = "개선 제안하기",
          onClickItem = onClickSuggestButton
        )
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            "앱 버전",
            style = MaterialTheme.typography.titleMedium
          )
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
          Text(
            appVersion,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.outline
          )
        }
      }
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          "로그아웃",
          modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
          ) { onClickLogoutButton() },
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.error,
          textDecoration = TextDecoration.Underline
        )
      }
    }
  }
}

@Composable
fun SettingBlock(
  name: String,
  items: @Composable ColumnScope.() -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 24.dp, vertical = 16.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    Text(
      name,
      style = MaterialTheme.typography.titleSmall,
      color = MaterialTheme.colorScheme.outline
    )
    items()
  }
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(2.dp)
      .background(MaterialTheme.colorScheme.outlineVariant)
  )
}

@Composable
fun SettingItem(
  name: String,
  onClickItem: () -> Unit = {}
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 12.dp)
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null
      ) { onClickItem() },
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      name,
      style = MaterialTheme.typography.titleMedium
    )
    Icon(
      imageVector = Icons.Rounded.KeyboardArrowRight,
      contentDescription = null
    )
  }
}

@Preview
@Composable
fun SettingScreenPreview() {
  LunchVoteTheme {
    SettingScreen(
      snackBarHostState = remember { SnackbarHostState() },
      settingState = SettingState()
    )
  }
}