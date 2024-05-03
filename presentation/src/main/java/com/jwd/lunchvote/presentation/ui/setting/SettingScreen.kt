package com.jwd.lunchvote.presentation.ui.setting

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.ui.setting.SettingContract.SettingEvent
import com.jwd.lunchvote.presentation.ui.setting.SettingContract.SettingSideEffect
import com.jwd.lunchvote.presentation.ui.setting.SettingContract.SettingState
import com.jwd.lunchvote.presentation.util.UiText
import com.jwd.lunchvote.presentation.util.clickableWithoutEffect
import com.jwd.lunchvote.presentation.widget.LunchVoteTopBar
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SettingRoute(
  popBackStack: () -> Unit,
  navigateToProfile: () -> Unit,
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
        is SettingSideEffect.NavigateToProfile -> navigateToProfile()
        is SettingSideEffect.NavigateToLogin -> navigateToLogin()
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
      UiText.StringResource(R.string.setting_app_version_failed).asString(context)
    }

    viewModel.handleEvents(SettingEvent.ScreenInitialize(appVersion))
  }

  SettingScreen(
    state = state,
    onClickBackButton = { viewModel.handleEvents(SettingEvent.OnClickBackButton) },
    onClickProfileButton = { viewModel.handleEvents(SettingEvent.OnClickProfileButton) },
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
  onClickProfileButton: () -> Unit = {},
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
        title = stringResource(R.string.setting_title),
        navIconVisible = true,
        popBackStack = onClickBackButton
      )
    }
  ) {
    SettingBlock(name = stringResource(R.string.setting_my_info)) {
      SettingItem(
        name = stringResource(R.string.setting_my_profile),
        onClickItem = onClickProfileButton
      )
    }
    SettingBlock(name = stringResource(R.string.setting_service)) {
      SettingItem(
        name = stringResource(R.string.setting_alert),
        onClickItem = onClickAlertSettingButton
      )
      SettingItem(
        name = stringResource(R.string.setting_contact),
        onClickItem = onClickContactButton
      )
    }
    SettingBlock(name = stringResource(R.string.setting_app_config)) {
      SettingItem(
        name = stringResource(R.string.setting_notice_and_terms),
        onClickItem = onClickNoticeButton
      )
      SettingItem(
        name = stringResource(R.string.setting_suggest),
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
          text = stringResource(R.string.setting_app_version),
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
        text = stringResource(R.string.setting_logout),
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