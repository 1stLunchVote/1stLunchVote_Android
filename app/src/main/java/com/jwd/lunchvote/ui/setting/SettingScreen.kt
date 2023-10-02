package com.jwd.lunchvote.ui.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
    onClickBackButton = { viewModel.handleEvents(SettingEvent.OnClickBackButton) }
  )
}

@Composable
fun SettingScreen(
  snackBarHostState: SnackbarHostState,
  settingState: SettingState,
  onClickBackButton: () -> Unit = {}
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
    }
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