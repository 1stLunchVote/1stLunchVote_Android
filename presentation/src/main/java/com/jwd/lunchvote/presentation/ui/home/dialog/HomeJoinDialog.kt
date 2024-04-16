package com.jwd.lunchvote.presentation.ui.home.dialog

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.ui.home.dialog.HomeJoinContract.HomeJoinEvent
import com.jwd.lunchvote.presentation.ui.home.dialog.HomeJoinContract.HomeJoinSideEffect
import com.jwd.lunchvote.presentation.widget.LunchVoteDialog
import com.jwd.lunchvote.presentation.widget.LunchVoteTextField
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeJoinDialog(
  modifier: Modifier = Modifier,
  popBackStack: () -> Unit = {},
  navigateToLounge: (String) -> Unit = {},
  showSnackBar: suspend (String) -> Unit = {},
  viewModel: HomeJoinViewModel = hiltViewModel(),
  context: Context = LocalContext.current
) {
  val homeJoinState by viewModel.viewState.collectAsStateWithLifecycle()
  val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel.sideEffect) {
    viewModel.sideEffect.collectLatest {
      when (it) {
        is HomeJoinSideEffect.PopBackStack -> popBackStack()
        is HomeJoinSideEffect.NavigateToLounge -> navigateToLounge(it.loungeId)
        is HomeJoinSideEffect.ShowSnackBar -> showSnackBar(it.message.asString(context))
      }
    }
  }

  LunchVoteDialog(
    title = "투표 방 참여하기",
    dismissText = "취소",
    onDismiss = { viewModel.sendEvent(HomeJoinEvent.OnClickDismissButton) },
    confirmText = "참여",
    onConfirm = { viewModel.sendEvent(HomeJoinEvent.OnClickConfirmButton) },
    modifier = modifier,
    confirmEnabled = homeJoinState.loungeId.isNotBlank()
  ) {
    if (isLoading) {
      Text("로딩 중...")
    } else {
      LunchVoteTextField(
        text = homeJoinState.loungeId,
        onTextChange = { viewModel.sendEvent(HomeJoinEvent.OnLoungeIdChange(it)) },
        hintText = "초대 코드"
      )
    }
  }
}

@Preview
@Composable
private fun HomeJoinDialogPreview() {
  LunchVoteTheme {
    HomeJoinDialog()
  }
}