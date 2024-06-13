package com.jwd.lunchvote.presentation.ui.vote.second

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.ui.vote.second.SecondVoteContract.SecondVoteDialog
import com.jwd.lunchvote.presentation.ui.vote.second.SecondVoteContract.SecondVoteEvent
import com.jwd.lunchvote.presentation.ui.vote.second.SecondVoteContract.SecondVoteSideEffect
import com.jwd.lunchvote.presentation.ui.vote.second.SecondVoteContract.SecondVoteState
import com.jwd.lunchvote.presentation.widget.LunchVoteDialog
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SecondVoteRoute(
  popBackStack: () -> Unit,
  navigateToVoteResult: (String) -> Unit,
  showSnackBar: suspend (String) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: SecondVoteViewModel = hiltViewModel(),
  context: Context = LocalContext.current
) {
  val state by viewModel.viewState.collectAsStateWithLifecycle()
  val dialog by viewModel.dialogState.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel.sideEffect) {
    viewModel.sideEffect.collectLatest {
      when(it) {
        is SecondVoteSideEffect.PopBackStack -> popBackStack()
        is SecondVoteSideEffect.NavigateToVoteResult -> navigateToVoteResult(it.loungeId)
        is SecondVoteSideEffect.ShowSnackBar -> showSnackBar(it.message.asString(context))
      }
    }
  }

  BackHandler { viewModel.sendEvent(SecondVoteEvent.OnClickBackButton) }

  LaunchedEffect(Unit) { viewModel.sendEvent(SecondVoteEvent.ScreenInitialize) }

  when (dialog) {
    is SecondVoteDialog.ExitDialog -> ExitDialog(
      onDismissRequest = { viewModel.sendEvent(SecondVoteEvent.OnClickCancelButtonInExitDialog) },
      onConfirmation = { viewModel.sendEvent(SecondVoteEvent.OnClickConfirmButtonInExitDialog) }
    )
    null -> Unit
  }

  SecondVoteScreen(
    state = state,
    modifier = modifier,
    onEvent = viewModel::sendEvent
  )
}

@Composable
private fun SecondVoteScreen(
  state: SecondVoteState,
  modifier: Modifier = Modifier,
  onEvent: (SecondVoteEvent) -> Unit = {}
) {

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
private fun Preview() {
  ScreenPreview {
    SecondVoteScreen(
      SecondVoteState()
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