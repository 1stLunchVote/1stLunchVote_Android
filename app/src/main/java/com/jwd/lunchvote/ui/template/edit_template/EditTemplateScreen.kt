package com.jwd.lunchvote.ui.template.edit_template

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
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.ui.template.add_template.AddTemplateViewModel
import com.jwd.lunchvote.ui.template.edit_template.EditTemplateContract.*
import com.jwd.lunchvote.widget.LunchVoteTopBar
import kotlinx.coroutines.flow.collectLatest

@Composable
fun EditTemplateRoute(
  popBackStack: (String) -> Unit,
  viewModel: EditTemplateViewModel = hiltViewModel()
){
  val editTemplateState : EditTemplateState by viewModel.viewState.collectAsStateWithLifecycle()

  val snackBarHostState = remember { SnackbarHostState() }

  LaunchedEffect(viewModel.sideEffect){
    viewModel.sideEffect.collectLatest {
      when(it){
        is EditTemplateSideEffect.PopBackStack -> popBackStack(it.message)
        is EditTemplateSideEffect.ShowSnackBar -> snackBarHostState.showSnackbar(it.message)
      }
    }
  }

  EditTemplateScreen(
    editTemplateState = editTemplateState,
    snackBarHostState = snackBarHostState,
    onClickBackButton = { viewModel.sendEvent(EditTemplateEvent.OnClickBackButton) }
  )
}

@Composable
private fun EditTemplateScreen(
  editTemplateState: EditTemplateState,
  snackBarHostState: SnackbarHostState,
  onClickBackButton: () -> Unit = {}
) {
  Scaffold(
    snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
  ) { padding ->
    if (editTemplateState.loading) {
      Dialog(onDismissRequest = {  }) { CircularProgressIndicator() }
    } else {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding),
        horizontalAlignment = CenterHorizontally
      ) {
        LunchVoteTopBar(
          title = "템플릿 편집",
          navIconVisible = true,
          popBackStack = onClickBackButton
        )

      }
    }
  }
}

@Preview(showSystemUi = true)
@Composable
fun EditTemplateScreenPreview() {
  LunchVoteTheme {
    EditTemplateScreen(
      editTemplateState = EditTemplateState(),
      snackBarHostState = remember { SnackbarHostState() }
    )
  }
}